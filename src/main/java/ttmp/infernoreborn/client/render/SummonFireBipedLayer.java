package ttmp.infernoreborn.client.render;

import com.mojang.blaze3d.matrix.MatrixStack;
import net.minecraft.client.renderer.IRenderTypeBuffer;
import net.minecraft.client.renderer.LightTexture;
import net.minecraft.client.renderer.entity.IEntityRenderer;
import net.minecraft.client.renderer.entity.layers.LayerRenderer;
import net.minecraft.client.renderer.entity.model.BipedModel;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.entity.LivingEntity;
import net.minecraft.util.ResourceLocation;

import static ttmp.infernoreborn.api.InfernoRebornApi.MODID;

public class SummonFireBipedLayer<E extends LivingEntity, M extends BipedModel<E>> extends LayerRenderer<E, M>{
	public static final ResourceLocation TEXTURE = new ResourceLocation(MODID, "textures/entity/summon_fire.png");

	private final SummonFireModel model;

	public SummonFireBipedLayer(IEntityRenderer<E, M> renderer){
		super(renderer);
		this.model = new SummonFireModel();
	}

	@Override public void render(MatrixStack pose, IRenderTypeBuffer buffer, int packedLight, E entity, float limbSwing, float limbSwingAmount, float partialTicks, float ageInTicks, float netHeadYaw, float headPitch){
		pose.pushPose();
		M model = this.getParentModel();
		this.model.fire.copyFrom(model.body);
		if(model.young){
			float scale = 1/model.babyBodyScale;
			pose.scale(scale, scale, scale);
			pose.translate(0, model.bodyYOffset/16.0, 0);
		}
		this.model.renderToBuffer(pose, buffer.getBuffer(this.model.renderType(TEXTURE)), LightTexture.pack(15, 15), OverlayTexture.NO_OVERLAY, 1, 1, 1, 1);
		pose.popPose();
	}
}
