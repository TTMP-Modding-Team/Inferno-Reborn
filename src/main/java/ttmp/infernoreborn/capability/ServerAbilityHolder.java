package ttmp.infernoreborn.capability;

import net.minecraft.entity.LivingEntity;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.nbt.INBT;
import net.minecraft.nbt.ListNBT;
import net.minecraft.nbt.StringNBT;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.common.capabilities.ICapabilityProvider;
import net.minecraftforge.common.util.Constants;
import net.minecraftforge.common.util.INBTSerializable;
import net.minecraftforge.registries.IForgeRegistry;
import ttmp.infernoreborn.ability.Ability;
import ttmp.infernoreborn.contents.Abilities;

import javax.annotation.Nullable;
import java.util.AbstractList;
import java.util.Collections;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

public class ServerAbilityHolder extends AbilityHolder implements INBTSerializable<CompoundNBT>{
	@Nullable public static ServerAbilityHolder of(ICapabilityProvider provider){
		AbilityHolder of = AbilityHolder.of(provider);
		return of instanceof ServerAbilityHolder ? (ServerAbilityHolder)of : null;
	}

	private final Set<Ability> abilities = new HashSet<>();
	private final Set<Ability> addedAbilities = new HashSet<>();
	private final Set<Ability> removedAbilities = new HashSet<>();
	private final Set<Ability> abilitiesView = Collections.unmodifiableSet(abilities);

	private boolean updateAbility;

	@Override public Set<Ability> getAbilities(){
		return abilitiesView;
	}
	@Override public boolean has(Ability ability){
		return this.abilities.contains(ability);
	}
	@Override public boolean add(Ability ability){
		if(this.abilities.contains(ability)||!addedAbilities.add(ability)) return false;
		updateAbility = true;
		return true;
	}
	@Override public boolean remove(Ability ability){
		if(!this.abilities.contains(ability)||!removedAbilities.add(ability)) return false;
		updateAbility = true;
		return true;
	}

	@Override public void update(LivingEntity entity){
		boolean update = false;
		if(!addedAbilities.isEmpty()){
			for(Ability ability : addedAbilities)
				if(abilities.add(ability))
					onAbilityAdded(ability);
			addedAbilities.clear();
			update = true;
		}
		if(!removedAbilities.isEmpty()){
			for(Ability ability : removedAbilities)
				if(abilities.remove(ability))
					onAbilityRemoved(ability);
			removedAbilities.clear();
			update = true;
		}

		// TODO
	}

	protected void onAbilityAdded(Ability ability){
		// TODO
	}
	protected void onAbilityRemoved(Ability ability){
		// TODO
	}

	@Override public CompoundNBT serializeNBT(){
		CompoundNBT nbt = new CompoundNBT();

		if(!abilities.isEmpty()){
			IForgeRegistry<Ability> registry = Abilities.getRegistry();
			nbt.put("abilities", abilities.stream()
					.map(registry::getKey)
					.filter(Objects::nonNull)
					.map(ResourceLocation::toString)
					.map(StringNBT::valueOf)
					.collect(ListNBT::new, AbstractList::add, (l1, l2) -> {}));
		}
		return nbt;
	}
	@Override public void deserializeNBT(CompoundNBT nbt){
		this.abilities.clear();

		ListNBT abilities = nbt.getList("abilities", Constants.NBT.TAG_STRING);
		if(!abilities.isEmpty()){
			IForgeRegistry<Ability> registry = Abilities.getRegistry();
			abilities.stream()
					.map(INBT::getAsString)
					.map(ResourceLocation::new)
					.map(registry::getValue)
					.filter(Objects::nonNull)
					.forEach(this.abilities::add);
		}
	}
}
