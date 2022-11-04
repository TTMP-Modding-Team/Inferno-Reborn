package ttmp.infernoreborn.mixin;

import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.client.renderer.ItemRenderer;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.item.ItemStack;
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

@Mixin(ItemRenderer.class)
public abstract class MixinItemRenderer{
	private static final long SIGIL_CYCLE_TIME = 500;

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

		Tessellator t = Tessellator.getInstance();
		BufferBuilder bb = t.getBuilder();

		Minecraft.getInstance().getTextureManager().bind(sigil.getSigilTextureLocation());
		bb.begin(GL11.GL_QUADS, DefaultVertexFormats.POSITION_TEX);
		bb.vertex(x+1, y+1, 0).uv(0, 0).endVertex();
		bb.vertex(x+1, y+9, 0).uv(0, 1).endVertex();
		bb.vertex(x+9, y+9, 0).uv(1, 1).endVertex();
		bb.vertex(x+9, y+1, 0).uv(1, 0).endVertex();
		t.end();

		RenderSystem.enableDepthTest();
	}
}
