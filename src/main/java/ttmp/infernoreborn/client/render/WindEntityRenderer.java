package ttmp.infernoreborn.client.render;

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
import ttmp.infernoreborn.contents.entity.WindEntity;

import static ttmp.infernoreborn.InfernoReborn.MODID;

public class WindEntityRenderer extends EntityRenderer<WindEntity>{
	private static final ResourceLocation TEXTURE_LOCATION = new ResourceLocation(MODID, "textures/entity/wind_entity.png");
	private static final RenderType RENDER_TYPE = RenderType.entityCutoutNoCull(TEXTURE_LOCATION);

	public WindEntityRenderer(EntityRendererManager manager){
		super(manager);
	}

	@Override
	protected int getBlockLightLevel(WindEntity e, BlockPos p){
		return 15;
	}

	@Override
	public void render(WindEntity entity, float entityYaw, float partialTicks, MatrixStack stack, IRenderTypeBuffer typeBuffer, int packedLight){
		stack.pushPose();
		stack.scale(2, 2, 2);
		stack.mulPose(this.entityRenderDispatcher.cameraOrientation());
		stack.mulPose(Vector3f.YP.rotationDegrees(180));
		MatrixStack.Entry e = stack.last();
		Matrix4f pose = e.pose();
		Matrix3f normal = e.normal();
		IVertexBuilder buffer = typeBuffer.getBuffer(RENDER_TYPE);
		vertex(buffer, pose, normal, packedLight, 0, 0, 0, 1, entity.getColor());
		vertex(buffer, pose, normal, packedLight, 1, 0, 1, 1, entity.getColor());
		vertex(buffer, pose, normal, packedLight, 1, 1, 1, 0, entity.getColor());
		vertex(buffer, pose, normal, packedLight, 0, 1, 0, 0, entity.getColor());
		stack.popPose();
		super.render(entity, entityYaw, partialTicks, stack, typeBuffer, packedLight);
	}

	private static void vertex(IVertexBuilder b, Matrix4f pose, Matrix3f normal, int uv2, float x, int y, int u, int v, int color){
		b.vertex(pose, x-0.5F, (float)y-0.25F, 0.0F)
				.color(color<<16&0xFF, color<<8&0xFF, color&0xFF, 255)
				.uv((float)u, (float)v)
				.overlayCoords(OverlayTexture.NO_OVERLAY)
				.uv2(uv2)
				.normal(normal, 0, 1, 0)
				.endVertex();
	}

	@Override public ResourceLocation getTextureLocation(WindEntity e){
		return TEXTURE_LOCATION;
	}
}