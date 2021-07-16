package ttmp.infernoreborn.contents.effect;

import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.gui.AbstractGui;
import net.minecraft.client.gui.DisplayEffectsScreen;
import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.potion.Effect;
import net.minecraft.potion.EffectInstance;
import net.minecraft.potion.EffectType;
import net.minecraft.util.math.vector.Matrix4f;
import org.lwjgl.opengl.GL11;

public class BloodFrenzyEffect extends Effect{
	public BloodFrenzyEffect(EffectType effectType, int color){
		super(effectType, color);
	}

	@Override public void renderInventoryEffect(EffectInstance effect, DisplayEffectsScreen<?> gui, MatrixStack mStack, int x, int y, float z){
		draw(effect, mStack, x+6, y+7, z, 1);
	}
	@Override public void renderHUDEffect(EffectInstance effect, AbstractGui gui, MatrixStack mStack, int x, int y, float z, float alpha){
		draw(effect, mStack, x+3, y+3, z, alpha);
	}

	private static void draw(EffectInstance effect, MatrixStack matrixStack, int x, int y, float z, float alpha){
		int a = (int)(alpha*255);
		RenderSystem.disableTexture();
		Matrix4f matrix = matrixStack.last().pose();
		for(int i = 0, j = Math.min(4, effect.getAmplifier()); i<=j; i++){
			Tessellator t = Tessellator.getInstance();
			BufferBuilder b = t.getBuilder();
			b.begin(GL11.GL_QUADS, DefaultVertexFormats.POSITION_COLOR);
			b.vertex(matrix, x+13, y+17-2*i, z).color(0xa0, 0, 0, a).endVertex();
			b.vertex(matrix, x+17, y+17-2*i, z).color(0xa0, 0, 0, a).endVertex();
			b.vertex(matrix, x+17, y+16-2*i, z).color(0xa0, 0, 0, a).endVertex();
			b.vertex(matrix, x+13, y+16-2*i, z).color(0xa0, 0, 0, a).endVertex();
			t.end();
		}
		RenderSystem.enableTexture();
	}
}
