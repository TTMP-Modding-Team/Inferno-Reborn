package ttmp.infernoreborn.infernaltype;

import net.minecraft.entity.LivingEntity;
import ttmp.infernoreborn.contents.ability.holder.ServerAbilityHolder;

import java.util.Random;

public final class InfernalGenContext{
	private final LivingEntity entity;
	private final ServerAbilityHolder holder;
	private final Random random;

	public InfernalGenContext(LivingEntity entity, ServerAbilityHolder holder, Random random){
		this.entity = entity;
		this.holder = holder;
		this.random = random;
	}

	public LivingEntity getEntity(){
		return entity;
	}
	public ServerAbilityHolder getHolder(){
		return holder;
	}
	public Random getRandom(){
		return random;
	}
}
