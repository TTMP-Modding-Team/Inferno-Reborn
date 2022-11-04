package ttmp.infernoreborn.event;

import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.player.ClientPlayerEntity;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.client.resources.I18n;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.HandSide;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockRayTraceResult;
import net.minecraft.util.math.EntityRayTraceResult;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.util.math.vector.Matrix4f;
import net.minecraft.util.text.IFormattableTextComponent;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.Style;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.RenderGameOverlayEvent;
import net.minecraftforge.event.AttachCapabilitiesEvent;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.event.entity.player.ItemTooltipEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import org.lwjgl.opengl.GL11;
import ttmp.infernoreborn.capability.Caps;
import ttmp.infernoreborn.capability.ClientPlayerCapability;
import ttmp.infernoreborn.capability.ClientPlayerCapability.ActiveShield;
import ttmp.infernoreborn.capability.SimpleTickingTaskHandler;
import ttmp.infernoreborn.capability.TickingTaskHandler;
import ttmp.infernoreborn.client.color.ColorUtils;
import ttmp.infernoreborn.contents.ability.holder.ClientAbilityHolder;
import ttmp.infernoreborn.contents.sigil.Sigil;
import ttmp.infernoreborn.contents.sigil.holder.SigilHolder;
import ttmp.infernoreborn.contents.tile.crucible.CrucibleTile;
import ttmp.infernoreborn.shield.ShieldSkin;

import java.text.DecimalFormat;
import java.util.List;
import java.util.Set;

import static ttmp.infernoreborn.InfernoReborn.MODID;

@Mod.EventBusSubscriber(modid = MODID, value = Dist.CLIENT)
public final class ClientEventHandlers{
	private ClientEventHandlers(){}

	private static final ResourceLocation CLIENT_PLAYER_CAPS = new ResourceLocation(MODID, "client_caps");
	private static final DecimalFormat FUCK = new DecimalFormat("#.#");

	@SubscribeEvent
	public static void attachEntityCapabilities(AttachCapabilitiesEvent<Entity> event){
		Entity e = event.getObject();
		if(e instanceof PlayerEntity&&e.level.isClientSide()){
			event.addCapability(CLIENT_PLAYER_CAPS, new ClientPlayerCapability((PlayerEntity)e));
		}
	}

	@SuppressWarnings("ConstantConditions")
	@SubscribeEvent
	public static void onTextRender(RenderGameOverlayEvent.Text event){
		debugAbilities(event);
		ClientPlayerEntity player = Minecraft.getInstance().player;
		if(player!=null){
			ClientPlayerCapability shield = player.getCapability(Caps.clientPlayerShield).orElse(null);
			if(shield!=null){
				event.getRight().add(shield.shields.size()+" shields");
				for(ActiveShield s : shield.shields){
					event.getRight().add(s.getSkin().id+(s.isDown() ? " (DOWN): " : ": ")+s.getDurability()+" / "+s.getMaxDurability());
				}
			}
			if(Minecraft.getInstance().hitResult!=null&&Minecraft.getInstance().hitResult.getType()==RayTraceResult.Type.BLOCK){
				BlockRayTraceResult hitResult = (BlockRayTraceResult)Minecraft.getInstance().hitResult;
				TileEntity be = Minecraft.getInstance().level.getBlockEntity(hitResult.getBlockPos());
				if(be instanceof CrucibleTile){
					CrucibleTile crucible = (CrucibleTile)be;
					event.getLeft().add("Heat: "+crucible.getHeat()+", Stir: "+crucible.getManualStirPower()+", ClientStir: "+crucible.clientStir);
				}
			}
		}
	}

	private static void debugAbilities(RenderGameOverlayEvent.Text event){
		RayTraceResult hitResult = Minecraft.getInstance().hitResult;
		if(hitResult==null||hitResult.getType()!=RayTraceResult.Type.ENTITY) return;
		EntityRayTraceResult result = (EntityRayTraceResult)hitResult;
		Entity entity = result.getEntity();
		if(!(entity instanceof LivingEntity)) return;

		LivingEntity living = (LivingEntity)entity;
		event.getLeft().add(entity.getDisplayName().getString()+" "+living.getHealth()+" / "+living.getMaxHealth());

		living.getAttributes().getSyncableAttributes().stream()
				.map(a -> a.getValue()+" "+I18n.get(a.getAttribute().getDescriptionId()))
				.forEach(event.getRight()::add);

		ClientAbilityHolder h = ClientAbilityHolder.of(entity);
		if(h==null) return;
		h.getAbilities().stream()
				.map(ability -> ability.getName().getString())
				.forEach(event.getLeft()::add);
	}

	@SubscribeEvent
	public static void onClientTick(TickEvent.ClientTickEvent event){
		if(event.phase!=TickEvent.Phase.START) return;
		if(Minecraft.getInstance().level==null) return;
		TickingTaskHandler h = TickingTaskHandler.of(Minecraft.getInstance().level);
		if(h instanceof SimpleTickingTaskHandler) ((SimpleTickingTaskHandler)h).update();
	}

	@SubscribeEvent
	public static void onItemTooltip(ItemTooltipEvent event){
		ItemStack stack = event.getItemStack();
		SigilHolder h = SigilHolder.of(stack);
		if(h==null||h.isEmpty()) return;
		List<ITextComponent> list = event.getToolTip();

		Set<Sigil> sigils = h.getSigils();
		IFormattableTextComponent text = sigils.size()==1 ?
				new TranslationTextComponent("tooltip.infernoreborn.sigil_engraved") :
				new TranslationTextComponent("tooltip.infernoreborn.sigil_engraved.s", sigils.size());
		text.withStyle(Style.EMPTY.withColor(ColorUtils.SIGIL_TEXT_COLOR))
				.append(new TranslationTextComponent("tooltip.infernoreborn.sigil_engraved.points", h.getTotalPoint(), h.getMaxPoints())
						.withStyle(TextFormatting.GOLD));
		int idx = 1;
		list.add(idx++, text);
		if(Screen.hasAltDown()){
			for(Sigil sigil : sigils){
				list.add(idx++, new TranslationTextComponent(
						"tooltip.infernoreborn.sigil_engraved.entry",
						sigil.getName(),
						sigil.getPoint()
				).withStyle(TextFormatting.GOLD));
			}
		}else text.append(new TranslationTextComponent("tooltip.infernoreborn.sigil_engraved.collapsed"));
	}

	@SubscribeEvent
	public static void afterRenderGameOverlay(RenderGameOverlayEvent.Post event){
		if(event.getType()!=RenderGameOverlayEvent.ElementType.ALL) return;
		Minecraft mc = Minecraft.getInstance();
		PlayerEntity player = mc.getCameraEntity() instanceof PlayerEntity ?
				(PlayerEntity)mc.getCameraEntity() : null;
		if(player==null) return;
		//noinspection ConstantConditions
		ClientPlayerCapability cps = player.getCapability(Caps.clientPlayerShield).orElse(null);
		//noinspection ConstantConditions
		if(cps==null||cps.shields.isEmpty()) return;

		RenderSystem.enableBlend();
		RenderSystem.blendFuncSeparate(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA, 1, 0);
		RenderSystem.disableDepthTest();

		MatrixStack matrixStack = event.getMatrixStack();

		boolean renderNumber = Screen.hasAltDown()||Screen.hasShiftDown()||Screen.hasControlDown();

		int screenWidth = mc.getWindow().getGuiScaledWidth();
		int screenHeight = mc.getWindow().getGuiScaledHeight();
		int horizontalCenter = screenWidth/2;
		int hotbarEnd = player.getMainArm()==HandSide.RIGHT&&!player.getOffhandItem().isEmpty() ?
				horizontalCenter-91-29 :
				horizontalCenter-91-1;

		int renderStart = 0;
		int i = 0;
		int y = 0;

		do{
			int width = 0;
			int maxHeight = 0;
			for(; i<cps.shields.size(); i++){
				ShieldSkin skin = cps.shields.get(i).getSkin();
				if(width+skin.textureWidth>hotbarEnd-10)
					break;
				width += skin.textureWidth;
				width += 4;
				maxHeight = Math.max(maxHeight, skin.textureHeight);
			}
			int w2 = 0;
			for(int j = i-1; j>=renderStart; j--){
				ActiveShield shield = cps.shields.get(j);
				draw(matrixStack, hotbarEnd-5-w2, screenHeight-5-y-maxHeight/2, shield);
				if(renderNumber){
					String s = FUCK.format(shield.getDurability());
					int sw = mc.font.width(s)+1;
					// jesus christ
					//noinspection IntegerDivisionInFloatingPointContext
					mc.font.drawShadow(matrixStack, s,
							hotbarEnd-5-w2-shield.getSkin().textureWidth+shield.getSkin().textureWidth/2-sw/2,
							screenHeight-5-y-maxHeight+maxHeight/2-(mc.font.lineHeight+1)/2,
							0xFFFFFFFF);
					RenderSystem.enableBlend();
				}
				w2 += shield.getSkin().textureWidth+4;
			}
			renderStart = i-1;
			y += maxHeight+4;
		}while(i<cps.shields.size());

		RenderSystem.enableDepthTest();
	}

	private static void draw(MatrixStack matrixStack, int x, int y, ActiveShield shield){
		matrixStack.pushPose();
		ShieldSkin skin = shield.getSkin();
		matrixStack.translate(x-skin.textureWidth, y-skin.textureHeight/2.0, 0);
		matrixStack.scale(skin.textureWidth, skin.textureHeight, 1);
		Minecraft.getInstance().textureManager.bind(skin.texture);
		if(shield.isDown()){
			drawShieldPortion(matrixStack, 0, shield.getDurabilityPortion(), 2);
			drawShieldPortion(matrixStack, shield.getDurabilityPortion(), 1, 1);
		}else{
			long anim = shield.getAnimationTime();
			int mode;
			if(shield.isIncrease()){
				if(shield.getDurability()>=shield.getMaxDurability())
					mode = anim<=50||(anim>=100&&anim<=150) ? 3 : 0;
				else mode = anim<=50 ? 3 : 0;
			}else mode = anim<=50||(anim>=100&&anim<=150) ? 2 : 0;

			if(shield.getDurability()>=shield.getMaxDurability()){
				drawShieldPortion(matrixStack, 0, 1, mode);
			}else{
				drawShieldPortion(matrixStack, 0, shield.getDurabilityPortion(), mode);
				drawShieldPortion(matrixStack, shield.getDurabilityPortion(), 1, 1);
			}
		}
		matrixStack.popPose();
	}

	/**
	 * @param from [0(bottom) ~ 1(top)]
	 * @param to   [0(bottom) ~ 1(top)]
	 * @param mode 0: Normal, 1: Gray, 2: Red, 3: Glow
	 */
	private static void drawShieldPortion(MatrixStack matrixStack, double from, double to, int mode){
		float fromf = 1-(float)from;
		float tof = 1-(float)to;
		float v = mode==0 ? 0 : mode==1 ? 0.25f : mode==2 ? 0.5f : 0.75f;
		float tov = tof*.25f+v;
		float fromv = fromf*.25f+v;
		float a = mode==3 ? 1 : mode==1 ? 0.5f : 0.8f;

		Tessellator tessellator = Tessellator.getInstance();
		BufferBuilder wr = tessellator.getBuilder();
		wr.begin(GL11.GL_QUADS, DefaultVertexFormats.POSITION_COLOR_TEX);
		Matrix4f matrix = matrixStack.last().pose();
		wr.vertex(matrix, 0, fromf, 0).color(1, 1, 1, a).uv(0, fromv).endVertex();
		wr.vertex(matrix, 1, fromf, 0).color(1, 1, 1, a).uv(1, fromv).endVertex();
		wr.vertex(matrix, 1, tof, 0).color(1, 1, 1, a).uv(1, tov).endVertex();
		wr.vertex(matrix, 0, tof, 0).color(1, 1, 1, a).uv(0, tov).endVertex();
		tessellator.end();
	}
}
