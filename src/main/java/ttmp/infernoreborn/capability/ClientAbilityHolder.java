package ttmp.infernoreborn.capability;

import net.minecraft.entity.LivingEntity;
import ttmp.infernoreborn.ability.Ability;

import java.util.HashSet;
import java.util.Set;

public class ClientAbilityHolder extends AbilityHolder{
	private final Set<Ability> abilities = new HashSet<>();

	@Override public Set<Ability> getAbilities(){
		return abilities;
	}
	@Override public boolean has(Ability ability){
		return abilities.contains(ability);
	}
	@Override public boolean add(Ability ability){
		return abilities.add(ability);
	}
	@Override public boolean remove(Ability ability){
		return abilities.remove(ability);
	}
	@Override public void update(LivingEntity entity){}
}
