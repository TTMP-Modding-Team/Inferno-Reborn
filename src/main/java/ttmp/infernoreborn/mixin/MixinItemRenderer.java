package ttmp.infernoreborn.mixin;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.IVertexBuilder;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.renderer.ItemRenderer;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.model.RenderMaterial;
import net.minecraft.client.renderer.texture.MissingTextureSprite;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.inventory.container.PlayerContainer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import org.lwjgl.opengl.GL11;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import ttmp.infernoreborn.api.sigil.Sigil;
import ttmp.infernoreborn.api.sigil.SigilHolder;
import ttmp.infernoreborn.contents.ModItems;
import ttmp.infernoreborn.contents.item.SigilItem;

import javax.annotation.Nullable;
import java.util.HashMap;
import java.util.Map;

import static ttmp.infernoreborn.api.InfernoRebornApi.MODID;

@Mixin(ItemRenderer.class)
public abstract class MixinItemRenderer{
	private static final long SIGIL_CYCLE_TIME = 500;
	private static final Map<Sigil, RenderMaterial> overlayRenderMaterial = new HashMap<>();

	private static final ResourceLocation missingno = new ResourceLocation(MODID, "sigil/missingno");
	private static final RenderMaterial missingnoSigil = new RenderMaterial(PlayerContainer.BLOCK_ATLAS, missingno);

	@Inject(method = "renderGuiItemDecorations(Lnet/minecraft/client/gui/FontRenderer;Lnet/minecraft/item/ItemStack;IILjava/lang/String;)V", at = {
			@At(value = "HEAD")
	})
	public void renderGuiItemDecorationsCallback(FontRenderer font, ItemStack stack, int x, int y, @Nullable String text, CallbackInfo ci){
		if(stack.isEmpty()) return;
		if(stack.getItem()==ModItems.SIGIL.get()||stack.getItem()==ModItems.BODY_SIGIL.get()){
			Sigil sigil = SigilItem.getSigil(stack);
			if(sigil!=null) renderSigilMark(sigil, x, y);
		}else{
			SigilHolder h = SigilHolder.of(stack);
			if(h!=null&&!h.getSigils().isEmpty()){
				Sigil[] sigils = h.getSigils().toArray(new Sigil[0]);
				Sigil sigil = sigils[(int)(System.currentTimeMillis()/SIGIL_CYCLE_TIME%sigils.length)];
				renderSigilMark(sigil, x, y);
			}
		}
	}

	private static void renderSigilMark(Sigil sigil, int x, int y){
		RenderSystem.disableDepthTest();
		RenderSystem.enableBlend();

		RenderMaterial m = overlayRenderMaterial.computeIfAbsent(sigil,
				s -> new RenderMaterial(PlayerContainer.BLOCK_ATLAS, s.getSigilTextureLocation()));
		TextureAtlasSprite sprite = m.sprite();
		if(sprite.getName().equals(MissingTextureSprite.getLocation())){
			sprite = missingnoSigil.sprite();
		}

		Tessellator t = Tessellator.getInstance();
		Minecraft.getInstance().getTextureManager().bind(PlayerContainer.BLOCK_ATLAS);
		t.getBuilder().begin(GL11.GL_QUADS, DefaultVertexFormats.POSITION_TEX);
		IVertexBuilder vb = sprite.wrap(t.getBuilder());
		vb.vertex(x+1, y+1, 0).uv(sprite.getU0(), sprite.getV0()).endVertex();
		vb.vertex(x+1, y+9, 0).uv(sprite.getU0(), sprite.getV1()).endVertex();
		vb.vertex(x+9, y+9, 0).uv(sprite.getU1(), sprite.getV1()).endVertex();
		vb.vertex(x+9, y+1, 0).uv(sprite.getU1(), sprite.getV0()).endVertex();
		t.end();

		RenderSystem.enableDepthTest();
	}
}
