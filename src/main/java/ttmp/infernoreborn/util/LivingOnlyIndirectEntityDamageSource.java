package ttmp.infernoreborn.util;

import net.minecraft.entity.Entity;
import net.minecraft.util.IndirectEntityDamageSource;

import javax.annotation.Nullable;

public class LivingOnlyIndirectEntityDamageSource extends IndirectEntityDamageSource implements CannotHurtNonLiving{
	public LivingOnlyIndirectEntityDamageSource(String damageType, Entity directEntity, @Nullable Entity entity){
		super(damageType, directEntity, entity);
	}
}
