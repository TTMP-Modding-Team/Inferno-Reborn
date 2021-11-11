package ttmp.infernoreborn.client.render;

import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.vertex.IVertexBuilder;
import net.minecraft.client.renderer.IRenderTypeBuffer;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.EntityRendererManager;
import net.minecraft.client.renderer.entity.SkeletonRenderer;
import net.minecraft.client.renderer.model.ModelRenderer;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.entity.monster.AbstractSkeletonEntity;
import net.minecraft.util.ResourceLocation;

import static ttmp.infernoreborn.InfernoReborn.MODID;

public class SummonedSkeletonRenderer extends SkeletonRenderer{
	private static final ResourceLocation TEXTURE_LOCATION = new ResourceLocation(MODID, "textures/entity/summoned_fire.png");
	private static final RenderType RENDER_TYPE = RenderType.entityCutoutNoCull(TEXTURE_LOCATION);

	private final ModelRenderer fire;

	public SummonedSkeletonRenderer(EntityRendererManager manager){
		super(manager);
		this.model.head.visible = false;
		this.fire = new ModelRenderer(16, 16, 0, 0);
		this.fire.addBox(-4.0F, -8.0F, -4.0F, 8.0F, 8.0F, 8.0F, 0);
		this.fire.setPos(0.0F, 0.0F+0, 0.0F);
	}


	@Override
	public void render(AbstractSkeletonEntity entity, float entityYaw, float partialTicks, MatrixStack stack, IRenderTypeBuffer typeBuffer, int packedLight){
		super.render(entity, entityYaw, partialTicks, stack, typeBuffer, packedLight);
		stack.pushPose();
		stack.scale(0.5f, 0.5f, 0.5f);
		stack.translate(0, 3.5D, 0);
		this.fire.render(stack, typeBuffer.getBuffer(RENDER_TYPE), packedLight, OverlayTexture.NO_OVERLAY);
		stack.popPose();
	}
}
