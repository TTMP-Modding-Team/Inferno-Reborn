package ttmp.infernoreborn.client.render;

import net.minecraft.client.renderer.entity.model.BipedModel;
import net.minecraft.client.renderer.model.ModelHelper;
import ttmp.infernoreborn.contents.entity.GhostEntity;

public class GhostModel extends BipedModel<GhostEntity>{
	public GhostModel(float p_i1168_1_, boolean p_i1168_2_){
		this(p_i1168_1_, 0.0F, 64, p_i1168_2_ ? 32 : 64);
	}
	public GhostModel(float p_i51070_1_, float p_i51070_2_, int p_i51070_3_, int p_i51070_4_){
		super(p_i51070_1_, p_i51070_2_, p_i51070_3_, p_i51070_4_);
	}
	public void setupAnim(GhostEntity p_225597_1_, float p_225597_2_, float p_225597_3_, float p_225597_4_, float p_225597_5_, float p_225597_6_){
		super.setupAnim(p_225597_1_, p_225597_2_, p_225597_3_, p_225597_4_, p_225597_5_, p_225597_6_);
		//ModelHelper.animateZombieArms(this.leftArm, this.rightArm, this.isAggressive(p_225597_1_), this.attackTime, p_225597_4_);
	}
	public boolean isAggressive(GhostEntity entity){
		return entity.isAggressive();
	}
}
