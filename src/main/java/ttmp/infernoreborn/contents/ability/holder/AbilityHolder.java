package ttmp.infernoreborn.contents.ability.holder;

import net.minecraft.entity.LivingEntity;
import net.minecraftforge.common.capabilities.ICapabilityProvider;
import ttmp.infernoreborn.capability.Caps;
import ttmp.infernoreborn.contents.ability.Ability;
import ttmp.infernoreborn.contents.ability.cooldown.Cooldown;

import javax.annotation.Nullable;
import java.util.Set;

public interface AbilityHolder{
	@SuppressWarnings("ConstantConditions") @Nullable static AbilityHolder of(ICapabilityProvider provider){
		return provider.getCapability(Caps.abilityHolder).orElse(null);
	}

	Set<Ability> getAbilities();
	boolean has(Ability ability);
	boolean add(Ability ability);
	boolean remove(Ability ability);

	void clear();

	void update(LivingEntity entity);

	Cooldown cooldown();
}
