package ttmp.infernoreborn.client.render.model;

import net.minecraft.client.renderer.entity.model.ZombieModel;
import net.minecraft.client.renderer.model.ModelRenderer;
import ttmp.infernoreborn.contents.entity.SummonedZombieEntity;

public class SummonedZombieModel<T extends SummonedZombieEntity> extends ZombieModel<T>{

	public ModelRenderer fire;
	public SummonedZombieModel(float p_i1168_1_, boolean p_i1168_2_){
		super(p_i1168_1_, p_i1168_2_);
		this.fire = new ModelRenderer(this, 0, 0);
		this.fire.addBox(-2.0F, 28.0F, -2.0F, 4.0F, 4.0F, 4.0F, 0);
		this.fire.setPos(0.0F, 0.0F, 0.0F);
		this.head.visible = false;
	}

	@Override public void setupAnim(T pEntity, float pLimbSwing, float pLimbSwingAmount, float pAgeInTicks, float pNetHeadYaw, float pHeadPitch){
		super.setupAnim(pEntity, pLimbSwing, pLimbSwingAmount, pAgeInTicks, pNetHeadYaw, pHeadPitch);
		this.fire.yRot = this.body.yRot;
		this.fire.xRot = this.body.xRot;
		this.fire.zRot = this.body.zRot;

		if(this.crouching){
			this.fire.y = 4.2F;
		}else
			this.fire.y = 0.0F;
	}


}
