package ttmp.infernoreborn.event;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.resources.I18n;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.EntityRayTraceResult;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.util.text.IFormattableTextComponent;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.Style;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.RenderGameOverlayEvent;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.event.entity.player.ItemTooltipEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import ttmp.infernoreborn.capability.SimpleTickingTaskHandler;
import ttmp.infernoreborn.capability.TickingTaskHandler;
import ttmp.infernoreborn.client.color.ColorUtils;
import ttmp.infernoreborn.contents.ability.holder.ClientAbilityHolder;
import ttmp.infernoreborn.contents.sigil.Sigil;
import ttmp.infernoreborn.contents.sigil.holder.SigilHolder;

import java.util.List;
import java.util.Set;

import static ttmp.infernoreborn.InfernoReborn.MODID;

@Mod.EventBusSubscriber(modid = MODID, value = Dist.CLIENT)
public final class ClientEventHandlers{
	private ClientEventHandlers(){}

	@SubscribeEvent
	public static void onTextRender(RenderGameOverlayEvent.Text event){
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
		event.getLeft().add("Applied Generator Scheme: "+(h.getAppliedInfernalType()!=null ? h.getAppliedInfernalType().getId() : "None"));
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
}
