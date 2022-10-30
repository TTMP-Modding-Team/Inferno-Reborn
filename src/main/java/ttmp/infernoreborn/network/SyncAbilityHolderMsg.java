package ttmp.infernoreborn.network;

import net.minecraft.network.PacketBuffer;
import ttmp.infernoreborn.contents.Abilities;
import ttmp.infernoreborn.contents.ability.Ability;
import ttmp.infernoreborn.infernaltype.dsl.effect.ParticleEffect;

import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

public class SyncAbilityHolderMsg{
	public static SyncAbilityHolderMsg read(PacketBuffer buf){
		int entityId = buf.readInt();
		Set<Ability> set = new LinkedHashSet<>();
		for(int i = buf.readVarInt(); i>0; i--){
			Ability ability = Abilities.getRegistry().getValue(buf.readVarInt());
			if(ability!=null) set.add(ability);
		}
		List<ParticleEffect> particleEffects = new ArrayList<>();
		for(int i = buf.readVarInt(); i>0; i--){
			ParticleEffect e = ParticleEffect.read(buf);
			if(e!=null) particleEffects.add(e);
		}
		return new SyncAbilityHolderMsg(entityId, set, particleEffects);
	}

	private final int entityId;
	private final Set<Ability> abilities;
	private final List<ParticleEffect> effects;

	public SyncAbilityHolderMsg(int entityId, Set<Ability> abilities, List<ParticleEffect> effects){
		this.entityId = entityId;
		this.abilities = abilities;
		this.effects = effects;
	}

	public int getEntityId(){
		return entityId;
	}
	public Set<Ability> getAbilities(){
		return abilities;
	}
	public List<ParticleEffect> getEffects(){
		return effects;
	}

	public void write(PacketBuffer buf){
		buf.writeInt(entityId);
		buf.writeVarInt(abilities.size());
		for(Ability a : abilities)
			buf.writeVarInt(Abilities.getRegistry().getID(a));
		buf.writeVarInt(effects.size());
		for(ParticleEffect e : effects)
			e.write(buf);
	}
}
