package ttmp.infernoreborn.client.render;

import com.mojang.blaze3d.matrix.MatrixStack;
import net.minecraft.client.renderer.IRenderTypeBuffer;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.AbstractZombieRenderer;
import net.minecraft.client.renderer.entity.EntityRendererManager;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.entity.monster.ZombieEntity;
import net.minecraft.util.ResourceLocation;
import ttmp.infernoreborn.client.render.model.SummonedZombieModel;
import ttmp.infernoreborn.contents.entity.SummonedZombieEntity;

import static ttmp.infernoreborn.InfernoReborn.MODID;

public class SummonedZombieRenderer extends AbstractZombieRenderer<SummonedZombieEntity, SummonedZombieModel<SummonedZombieEntity>>{
	private static final ResourceLocation TEXTURE_LOCATION = new ResourceLocation(MODID, "textures/entity/summoned_zombie.png");
	private static final RenderType RENDER_TYPE = RenderType.entityCutoutNoCull(TEXTURE_LOCATION);

	public SummonedZombieRenderer(EntityRendererManager manager){
		super(manager, new SummonedZombieModel<>(0, false), new SummonedZombieModel<>(0.5f, false), new SummonedZombieModel<>(1, true));
	}

	@Override
	public void render(SummonedZombieEntity entity, float entityYaw, float partialTicks, MatrixStack stack, IRenderTypeBuffer typeBuffer, int packedLight){
		super.render(entity, entityYaw, partialTicks, stack, typeBuffer, packedLight);
		this.model.fire.render(stack, typeBuffer.getBuffer(RENDER_TYPE), 0xF000F0, OverlayTexture.NO_OVERLAY);
	}

	@Override public ResourceLocation getTextureLocation(ZombieEntity pEntity){
		return TEXTURE_LOCATION;
	}
}
