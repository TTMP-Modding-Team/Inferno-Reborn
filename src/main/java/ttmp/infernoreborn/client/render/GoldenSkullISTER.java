package ttmp.infernoreborn.client.render;

import com.mojang.blaze3d.matrix.MatrixStack;
import net.minecraft.client.renderer.IRenderTypeBuffer;
import net.minecraft.client.renderer.model.ItemCameraTransforms;
import net.minecraft.client.renderer.tileentity.ItemStackTileEntityRenderer;
import net.minecraft.client.renderer.tileentity.SkullTileEntityRenderer;
import net.minecraft.item.ItemStack;
import ttmp.infernoreborn.contents.block.GoldenSkullBlock;

public class GoldenSkullISTER extends ItemStackTileEntityRenderer{
	@Override public void renderByItem(ItemStack stack,
	                                   ItemCameraTransforms.TransformType transformType,
	                                   MatrixStack matrixStack,
	                                   IRenderTypeBuffer buffer,
	                                   int combinedLight,
	                                   int combinedOverlay){
		SkullTileEntityRenderer.renderSkull(null, 180, GoldenSkullBlock.TYPE, null, 0, matrixStack, buffer, combinedLight);
	}
}
