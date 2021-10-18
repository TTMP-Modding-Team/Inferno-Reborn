package ttmp.infernoreborn.client.render;

import com.mojang.blaze3d.matrix.MatrixStack;
import net.minecraft.client.renderer.IRenderTypeBuffer;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.EntityRendererManager;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import ttmp.infernoreborn.contents.entity.PaperBulletEntity;

// TODO
public class PaperBulletRenderer extends EntityRenderer<PaperBulletEntity>{
	public PaperBulletRenderer(EntityRendererManager manager){
		super(manager);
	}

	@Override protected int getBlockLightLevel(PaperBulletEntity entity, BlockPos pos){
		return 15;
	}

	@Override public void render(PaperBulletEntity entity, float entityYaw, float partialTicks, MatrixStack stack, IRenderTypeBuffer typeBuffer, int packedLight){}

	@Override public ResourceLocation getTextureLocation(PaperBulletEntity entity){
		return new ResourceLocation("");
	}
}
