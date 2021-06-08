package ttmp.infernoreborn.capability;

import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.ai.attributes.Attribute;
import net.minecraft.entity.ai.attributes.AttributeModifier;
import net.minecraft.entity.ai.attributes.ModifiableAttributeInstance;
import net.minecraft.entity.monster.IMob;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.nbt.INBT;
import net.minecraft.nbt.ListNBT;
import net.minecraft.nbt.StringNBT;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.common.capabilities.ICapabilityProvider;
import net.minecraftforge.common.util.Constants;
import net.minecraftforge.common.util.INBTSerializable;
import net.minecraftforge.fml.network.PacketDistributor;
import ttmp.infernoreborn.ability.Ability;
import ttmp.infernoreborn.ability.generator.AbilityGenerator;
import ttmp.infernoreborn.ability.generator.AbilityGenerators;
import ttmp.infernoreborn.contents.Abilities;
import ttmp.infernoreborn.network.ModNet;
import ttmp.infernoreborn.network.SyncAbilityHolderMsg;

import javax.annotation.Nullable;
import java.util.AbstractList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.Map.Entry;
import java.util.Objects;
import java.util.Set;
import java.util.function.Consumer;

public class ServerAbilityHolder extends AbilityHolder implements INBTSerializable<CompoundNBT>{
	@Nullable
	public static ServerAbilityHolder of(ICapabilityProvider provider){
		AbilityHolder of = AbilityHolder.of(provider);
		return of instanceof ServerAbilityHolder ? (ServerAbilityHolder)of : null;
	}

	private final Set<Ability> abilities = new HashSet<>();
	private final Set<Ability> addedAbilities = new HashSet<>();
	private final Set<Ability> removedAbilities = new HashSet<>();
	private final Set<Ability> abilitiesView = Collections.unmodifiableSet(abilities);

	private boolean generateAbility = true;

	@Override public Set<Ability> getAbilities(){
		return abilitiesView;
	}
	@Override public boolean has(Ability ability){
		return this.abilities.contains(ability);
	}
	@Override public boolean add(Ability ability){
		return !this.abilities.contains(ability)&&addedAbilities.add(ability);
	}
	@Override public boolean remove(Ability ability){
		return this.abilities.contains(ability)&&removedAbilities.add(ability);
	}
	@Override public void clear(){
		if(this.abilities.isEmpty()) return;
		removedAbilities.addAll(this.abilities);
	}

	@Override
	public void update(LivingEntity entity){
		if(generateAbility){
			if(entity instanceof IMob){
				AbilityGenerator generator = AbilityGenerators.getWeightedPool().nextItem(entity.getRandom());
				if(generator!=null) generator.generate(entity);
			}
			generateAbility = false;
		}

		boolean update = false;
		if(!addedAbilities.isEmpty()){
			for(Ability ability : addedAbilities){
				if(abilities.add(ability))
					onAbilityAdded(ability, entity);
			}
			addedAbilities.clear();
			update = true;
		}
		if(!removedAbilities.isEmpty()){
			for(Ability ability : removedAbilities){
				if(abilities.remove(ability))
					onAbilityRemoved(ability, entity);
			}
			removedAbilities.clear();
			update = true;
		}

		if(update){
			ModNet.CHANNEL.send(PacketDistributor.TRACKING_ENTITY.with(() -> entity), new SyncAbilityHolderMsg(entity.getId(), abilities));
		}
	}

	protected void onAbilityAdded(Ability ability, LivingEntity entity){
		for(Entry<Attribute, Set<AttributeModifier>> entry : ability.getAttributes().entrySet()){
			ModifiableAttributeInstance instance = entity.getAttributes().getInstance(entry.getKey());
			if(instance==null) continue;
			for(AttributeModifier m : entry.getValue())
				instance.addTransientModifier(m);
		}
	}

	protected void onAbilityRemoved(Ability ability, LivingEntity entity){
		for(Entry<Attribute, Set<AttributeModifier>> entry : ability.getAttributes().entrySet()){
			ModifiableAttributeInstance instance = entity.getAttributes().getInstance(entry.getKey());
			if(instance==null) continue;
			for(AttributeModifier m : entry.getValue())
				instance.removeModifier(m.getId());
		}
	}

	@Override
	public CompoundNBT serializeNBT(){
		CompoundNBT nbt = new CompoundNBT();
		if(!abilities.isEmpty()) nbt.put("abilities", serializeAbilities(abilities));
		if(this.generateAbility) nbt.putBoolean("generateAbility", true);
		return nbt;
	}

	@Override
	public void deserializeNBT(CompoundNBT nbt){
		this.clear();

		ListNBT abilities = nbt.getList("abilities", Constants.NBT.TAG_STRING);
		deserializeAbilities(abilities, this::add);
		this.generateAbility = nbt.getBoolean("generateAbility");
	}

	private static ListNBT serializeAbilities(Collection<Ability> abilities){
		return abilities.stream()
				.map(Abilities.getRegistry()::getKey)
				.filter(Objects::nonNull)
				.map(ResourceLocation::toString)
				.map(StringNBT::valueOf)
				.collect(ListNBT::new, AbstractList::add, (l1, l2) -> {});
	}

	private static void deserializeAbilities(ListNBT nbt, Consumer<Ability> forEach){
		if(!nbt.isEmpty()) nbt.stream()
				.map(INBT::getAsString)
				.map(ResourceLocation::new)
				.map(Abilities.getRegistry()::getValue)
				.filter(Objects::nonNull)
				.forEach(forEach);
	}
}
