package ttmp.infernoreborn.network;

import net.minecraft.network.PacketBuffer;
import ttmp.infernoreborn.contents.ability.Ability;
import ttmp.infernoreborn.contents.ability.generator.AbilityGenerators;
import ttmp.infernoreborn.contents.ability.generator.scheme.AbilityGeneratorScheme;
import ttmp.infernoreborn.contents.Abilities;

import javax.annotation.Nullable;
import java.util.HashSet;
import java.util.Set;

public class SyncAbilityHolderMsg{
	public static SyncAbilityHolderMsg read(PacketBuffer buf){
		int entityId = buf.readInt();
		Set<Ability> set = new HashSet<>();
		for(int i = buf.readVarInt(); i>0; i--){
			Ability ability = Abilities.getRegistry().getValue(buf.readVarInt());
			if(ability!=null) set.add(ability);
		}
		return new SyncAbilityHolderMsg(entityId, set, buf.readBoolean() ? AbilityGenerators.findSchemeWithId(buf.readResourceLocation()) : null);
	}

	private final int entityId;
	private final Set<Ability> abilities;
	@Nullable private final AbilityGeneratorScheme appliedGeneratorScheme;

	public SyncAbilityHolderMsg(int entityId, Set<Ability> abilities, @Nullable AbilityGeneratorScheme appliedGeneratorScheme){
		this.entityId = entityId;
		this.abilities = abilities;
		this.appliedGeneratorScheme = appliedGeneratorScheme;
	}

	public int getEntityId(){
		return entityId;
	}
	public Set<Ability> getAbilities(){
		return abilities;
	}
	@Nullable public AbilityGeneratorScheme getAppliedGeneratorScheme(){
		return appliedGeneratorScheme;
	}

	public void write(PacketBuffer buf){
		buf.writeInt(entityId);
		buf.writeVarInt(abilities.size());
		for(Ability a : abilities){
			int id = Abilities.getRegistry().getID(a);
			if(id!=-1) buf.writeVarInt(id);
		}
		buf.writeBoolean(appliedGeneratorScheme!=null);
		if(appliedGeneratorScheme!=null) buf.writeResourceLocation(appliedGeneratorScheme.getId());
	}
}
