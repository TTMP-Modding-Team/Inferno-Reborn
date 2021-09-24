package ttmp.infernoreborn.util.damage;

import net.minecraft.entity.Entity;

import javax.annotation.Nullable;

public class LivingOnlyEntityDamageSource extends NotStupidDamageSource implements LivingOnly{
	public LivingOnlyEntityDamageSource(String type, @Nullable Entity directEntity, @Nullable Entity entity){
		super(type, directEntity, entity);
	}
}
