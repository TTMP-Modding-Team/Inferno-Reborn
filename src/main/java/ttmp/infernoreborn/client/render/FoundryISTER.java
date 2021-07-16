package ttmp.infernoreborn.client.render;

import com.mojang.blaze3d.matrix.MatrixStack;
import net.minecraft.block.Block;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.IRenderTypeBuffer;
import net.minecraft.client.renderer.model.ItemCameraTransforms;
import net.minecraft.client.renderer.tileentity.ItemStackTileEntityRenderer;
import net.minecraft.item.ItemStack;
import net.minecraftforge.client.model.data.EmptyModelData;
import ttmp.infernoreborn.contents.ModBlocks;
import ttmp.infernoreborn.contents.block.FoundryBlock;

public class FoundryISTER extends ItemStackTileEntityRenderer{
	public static final FoundryISTER INSTANCE = new FoundryISTER();

	@Override public void renderByItem(ItemStack stack,
	                                   ItemCameraTransforms.TransformType transformType,
	                                   MatrixStack matrixStack,
	                                   IRenderTypeBuffer buffer,
	                                   int combinedLight,
	                                   int combinedOverlay){
		matrixStack.pushPose();
		matrixStack.scale(.5f, .5f, .5f);
		draw(ModBlocks.FOUNDRY.get(), matrixStack, buffer, combinedLight, combinedOverlay, 0, 0, 0);
		for(FoundryBlock.ProxyBlock b : FoundryBlock.proxyBlocks()){
			draw(b, matrixStack, buffer, combinedLight, combinedOverlay, b.proxyX, b.proxyY, b.proxyZ);
		}
		matrixStack.popPose();
	}

	private static void draw(Block block,
	                         MatrixStack matrixStack,
	                         IRenderTypeBuffer buffer,
	                         int combinedLight,
	                         int combinedOverlay,
	                         int x,
	                         int y,
	                         int z){
		matrixStack.pushPose();
		matrixStack.translate(1-x, y, z);
		Minecraft.getInstance().getBlockRenderer().renderBlock(block.defaultBlockState(),
				matrixStack,
				buffer,
				combinedLight,
				combinedOverlay,
				EmptyModelData.INSTANCE);
		matrixStack.popPose();
	}
}
