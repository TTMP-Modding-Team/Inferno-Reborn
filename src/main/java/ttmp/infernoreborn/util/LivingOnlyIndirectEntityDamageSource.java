package ttmp.infernoreborn.util;

import net.minecraft.entity.Entity;
import net.minecraft.util.IndirectEntityDamageSource;

import javax.annotation.Nullable;

public class LivingOnlyIndirectEntityDamageSource extends IndirectEntityDamageSource implements CannotHurtNonLiving{
	public LivingOnlyIndirectEntityDamageSource(String p_i1568_1_, Entity p_i1568_2_, @Nullable Entity p_i1568_3_){
		super(p_i1568_1_, p_i1568_2_, p_i1568_3_);
	}
}
