package ttmp.infernoreborn.capability;

import net.minecraft.entity.LivingEntity;
import net.minecraft.util.Direction;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.CapabilityInject;
import net.minecraftforge.common.capabilities.ICapabilityProvider;
import net.minecraftforge.common.util.LazyOptional;
import ttmp.infernoreborn.ability.Ability;

import javax.annotation.Nullable;
import java.util.Set;

public abstract class AbilityHolder implements ICapabilityProvider{
	@SuppressWarnings("ConstantConditions") @Nullable public static AbilityHolder of(ICapabilityProvider provider){
		return provider.getCapability(capability).orElse(null);
	}

	@CapabilityInject(AbilityHolder.class)
	public static Capability<AbilityHolder> capability;

	public abstract Set<Ability> getAbilities();
	public abstract boolean has(Ability ability);
	public abstract boolean add(Ability ability);
	public abstract boolean remove(Ability ability);

	public abstract void update(LivingEntity entity);

	private final LazyOptional<AbilityHolder> self = LazyOptional.of(() -> this);

	@Override public <T> LazyOptional<T> getCapability(Capability<T> cap, @Nullable Direction side){
		return cap==capability ? self.cast() : LazyOptional.empty();
	}
}
