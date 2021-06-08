package ttmp.infernoreborn.network;

import net.minecraft.network.PacketBuffer;
import ttmp.infernoreborn.ability.Ability;
import ttmp.infernoreborn.contents.Abilities;

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
		return new SyncAbilityHolderMsg(entityId, set);
	}

	private final int entityId;
	private final Set<Ability> abilities;

	public SyncAbilityHolderMsg(int entityId, Set<Ability> abilities){
		this.entityId = entityId;
		this.abilities = abilities;
	}

	public int getEntityId(){
		return entityId;
	}
	public Set<Ability> getAbilities(){
		return abilities;
	}

	public void write(PacketBuffer buf){
		buf.writeInt(entityId);
		buf.writeVarInt(abilities.size());
		for(Ability a : abilities){
			int id = Abilities.getRegistry().getID(a);
			if(id!=-1) buf.writeVarInt(id);
		}
	}
}
