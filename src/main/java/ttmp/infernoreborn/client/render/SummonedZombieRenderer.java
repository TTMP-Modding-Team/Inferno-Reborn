package ttmp.infernoreborn.client.render;

import com.mojang.blaze3d.matrix.MatrixStack;
import net.minecraft.client.renderer.IRenderTypeBuffer;
import net.minecraft.client.renderer.entity.EntityRendererManager;
import net.minecraft.client.renderer.entity.ZombieRenderer;
import net.minecraft.entity.monster.ZombieEntity;

public class SummonedZombieRenderer extends ZombieRenderer{
	public SummonedZombieRenderer(EntityRendererManager manager){
		super(manager);
		addLayer(new SummonFireBipedLayer<>(this));
	}

	@Override public void render(ZombieEntity entity, float yaw, float partialTicks, MatrixStack pose, IRenderTypeBuffer buffer, int packedLight){
		this.model.head.visible = false;
		this.model.hat.visible = false;
		super.render(entity, yaw, partialTicks, pose, buffer, packedLight);
	}
}
