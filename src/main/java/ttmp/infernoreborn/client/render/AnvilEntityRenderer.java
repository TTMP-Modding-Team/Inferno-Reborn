package ttmp.infernoreborn.client.render;

import com.mojang.blaze3d.matrix.MatrixStack;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.BlockRendererDispatcher;
import net.minecraft.client.renderer.IRenderTypeBuffer;
import net.minecraft.client.renderer.RenderTypeLookup;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.EntityRendererManager;
import net.minecraft.client.renderer.texture.AtlasTexture;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.inventory.container.PlayerContainer;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraft.client.renderer.RenderType;
import net.minecraftforge.client.ForgeHooksClient;
import net.minecraftforge.client.model.data.EmptyModelData;
import ttmp.infernoreborn.contents.entity.AnvilEntity;

import java.util.Random;

public class AnvilEntityRenderer extends EntityRenderer<AnvilEntity>{
	public AnvilEntityRenderer(EntityRendererManager entityRendererManager){
		super(entityRendererManager);
		this.shadowRadius = 0.5F;
	}

	@Override
	public void render(AnvilEntity entity, float entityYaw, float partialTicks, MatrixStack stack, IRenderTypeBuffer typeBuffer, int packedLightIn){
		BlockState blockState = Blocks.ANVIL.defaultBlockState();
		stack.pushPose();
		BlockPos blockPos = new BlockPos(entity.getX(), entity.getBoundingBox().maxY, entity.getZ());
		stack.translate(-0.5D, 0.0D, -0.5D);
		BlockRendererDispatcher blockRendererDispatcher = Minecraft.getInstance().getBlockRenderer();
		for(RenderType type : RenderType.chunkBufferLayers()){
			if(RenderTypeLookup.canRenderInLayer(blockState, type)){
				ForgeHooksClient.setRenderLayer(type);
				blockRendererDispatcher.getModelRenderer().renderModel(entity.level,
						blockRendererDispatcher.getBlockModel(blockState),
						blockState,
						blockPos,
						stack,
						typeBuffer.getBuffer(type),
						false,
						new Random(),
						blockState.getSeed(BlockPos.ZERO),
						OverlayTexture.NO_OVERLAY,
						EmptyModelData.INSTANCE);
			}
		}
		ForgeHooksClient.setRenderLayer(null);
		stack.popPose();
		super.render(entity, entityYaw, partialTicks, stack, typeBuffer, packedLightIn);
	}

	@Override
	public ResourceLocation getTextureLocation(AnvilEntity entity){
		return PlayerContainer.BLOCK_ATLAS;
	}
}
