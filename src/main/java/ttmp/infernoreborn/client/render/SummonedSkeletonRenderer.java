package ttmp.infernoreborn.client.render;

import com.mojang.blaze3d.matrix.MatrixStack;
import net.minecraft.client.renderer.IRenderTypeBuffer;
import net.minecraft.client.renderer.entity.EntityRendererManager;
import net.minecraft.client.renderer.entity.SkeletonRenderer;
import net.minecraft.entity.monster.AbstractSkeletonEntity;

public class SummonedSkeletonRenderer extends SkeletonRenderer{
	public SummonedSkeletonRenderer(EntityRendererManager manager){
		super(manager);
		addLayer(new SummonFireBipedLayer<>(this));
	}

	@Override public void render(AbstractSkeletonEntity entity, float yaw, float partialTicks, MatrixStack pose, IRenderTypeBuffer buffer, int packedLight){
		this.model.head.visible = false;
		this.model.hat.visible = false;
		super.render(entity, yaw, partialTicks, pose, buffer, packedLight);
	}
}
