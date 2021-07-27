package ttmp.infernoreborn.client.render;

import com.mojang.blaze3d.matrix.MatrixStack;
import net.minecraft.block.BlockState;
import net.minecraft.block.SkullBlock;
import net.minecraft.block.WallSkullBlock;
import net.minecraft.client.renderer.IRenderTypeBuffer;
import net.minecraft.client.renderer.tileentity.SkullTileEntityRenderer;
import net.minecraft.client.renderer.tileentity.TileEntityRenderer;
import net.minecraft.client.renderer.tileentity.TileEntityRendererDispatcher;
import net.minecraft.util.Direction;
import ttmp.infernoreborn.contents.block.GoldenSkullBlock;
import ttmp.infernoreborn.contents.tile.GoldenSkullTile;

public class GoldenSkullTileEntityRenderer extends TileEntityRenderer<GoldenSkullTile>{
	public GoldenSkullTileEntityRenderer(TileEntityRendererDispatcher dispatcher){
		super(dispatcher);
	}

	/**
	 * Literally copy-pasted from {@link net.minecraft.client.renderer.tileentity.SkullTileEntityRenderer SkullTileEntityRenderer}
	 */
	@Override public void render(GoldenSkullTile pBlockEntity, float pPartialTicks, MatrixStack pMatrixStack, IRenderTypeBuffer pBuffer, int pCombinedLight, int pCombinedOverlay){
		BlockState blockstate = pBlockEntity.getBlockState();
		boolean isWallSkull = blockstate.getBlock() instanceof WallSkullBlock;
		Direction direction = isWallSkull ? blockstate.getValue(WallSkullBlock.FACING) : null;
		SkullTileEntityRenderer.renderSkull(direction,
				22.5f*(float)(isWallSkull ? (2+direction.get2DDataValue())*4 : blockstate.getValue(SkullBlock.ROTATION)),
				GoldenSkullBlock.TYPE,
				null,
				0,
				pMatrixStack,
				pBuffer,
				pCombinedLight);
	}
}
