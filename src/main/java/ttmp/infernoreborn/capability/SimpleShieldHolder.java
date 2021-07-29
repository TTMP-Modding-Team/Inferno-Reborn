package ttmp.infernoreborn.capability;

import net.minecraft.entity.LivingEntity;
import ttmp.infernoreborn.contents.ModAttributes;
import ttmp.infernoreborn.util.LivingUtils;

public class SimpleShieldHolder implements ShieldHolder{
	private final LivingEntity entity;
	private float shield;

	public SimpleShieldHolder(LivingEntity entity){
		this.entity = entity;
	}

	public float getShield(){
		return shield;
	}
	public void setShield(float shield){
		this.shield = shield<=0 ? 0 : Math.min((float)LivingUtils.getAttrib(entity, ModAttributes.SHIELD.get()), shield);
	}
}
