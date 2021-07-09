package ttmp.infernoreborn.contents.render;

import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.vertex.IVertexBuilder;
import net.minecraft.client.renderer.IRenderTypeBuffer;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.EntityRendererManager;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.vector.Matrix3f;
import net.minecraft.util.math.vector.Matrix4f;
import net.minecraft.util.math.vector.Vector3f;
import ttmp.infernoreborn.contents.entity.WindProjectileEntity;

import static ttmp.infernoreborn.InfernoReborn.MODID;

public class WindProjectileEntityRenderer extends EntityRenderer<WindProjectileEntity>{
	private static final ResourceLocation TEXTURE_LOCATION = new ResourceLocation(MODID, "textures/entity/wind_entity.png");
	private static final RenderType RENDER_TYPE = RenderType.entityCutoutNoCull(TEXTURE_LOCATION);

	public WindProjectileEntityRenderer(EntityRendererManager manager){
		super(manager);
	}

	protected int getBlockLightLevel(WindProjectileEntity e, BlockPos p){
		return 15;
	}

	public void render(WindProjectileEntity entity, float p_225623_2_, float p_225623_3_, MatrixStack stack, IRenderTypeBuffer typeBuffer, int p_225623_6_){
		stack.pushPose();
		stack.scale(2.0F, 2.0F, 2.0F);
		stack.mulPose(this.entityRenderDispatcher.cameraOrientation());
		stack.mulPose(Vector3f.YP.rotationDegrees(180.0F));
		MatrixStack.Entry matrixstack$entry = stack.last();
		Matrix4f matrix4f = matrixstack$entry.pose();
		Matrix3f matrix3f = matrixstack$entry.normal();
		IVertexBuilder ivertexbuilder = typeBuffer.getBuffer(RENDER_TYPE);
		vertex(ivertexbuilder, matrix4f, matrix3f, p_225623_6_, 0.0F, 0, 0, 1);
		vertex(ivertexbuilder, matrix4f, matrix3f, p_225623_6_, 1.0F, 0, 1, 1);
		vertex(ivertexbuilder, matrix4f, matrix3f, p_225623_6_, 1.0F, 1, 1, 0);
		vertex(ivertexbuilder, matrix4f, matrix3f, p_225623_6_, 0.0F, 1, 0, 0);
		stack.popPose();
		super.render(entity, p_225623_2_, p_225623_3_, stack, typeBuffer, p_225623_6_);
	}

	private static void vertex(IVertexBuilder p_229045_0_, Matrix4f p_229045_1_, Matrix3f p_229045_2_, int p_229045_3_, float p_229045_4_, int p_229045_5_, int p_229045_6_, int p_229045_7_){
		p_229045_0_.vertex(p_229045_1_, p_229045_4_-0.5F, (float)p_229045_5_-0.25F, 0.0F).color(255, 255, 255, 255).uv((float)p_229045_6_, (float)p_229045_7_).overlayCoords(OverlayTexture.NO_OVERLAY).uv2(p_229045_3_).normal(p_229045_2_, 0.0F, 1.0F, 0.0F).endVertex();
	}

	public ResourceLocation getTextureLocation(WindProjectileEntity e){
		return TEXTURE_LOCATION;
	}
}