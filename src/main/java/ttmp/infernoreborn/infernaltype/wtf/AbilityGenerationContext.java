package ttmp.infernoreborn.infernaltype.wtf;

import net.minecraft.entity.LivingEntity;
import ttmp.infernoreborn.contents.ability.holder.ServerAbilityHolder;
import ttmp.wtf.EvalContext;

import javax.annotation.Nullable;

public class AbilityGenerationContext implements EvalContext{
	private final LivingEntity entity;
	private final ServerAbilityHolder abilityHolder;

	public AbilityGenerationContext(LivingEntity entity, ServerAbilityHolder abilityHolder){
		this.entity = entity;
		this.abilityHolder = abilityHolder;
	}

	public LivingEntity getEntity(){
		return entity;
	}
	public ServerAbilityHolder getAbilityHolder(){
		return abilityHolder;
	}

	@Nullable @Override public Object getDynamicConstant(String name){
		if("EntityType".equals(name)) return entity.getType().getRegistryName();
		return null;
	}
}
