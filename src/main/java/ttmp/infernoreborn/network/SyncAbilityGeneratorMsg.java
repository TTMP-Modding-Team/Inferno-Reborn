package ttmp.infernoreborn.network;

import net.minecraft.network.PacketBuffer;
import net.minecraft.util.ResourceLocation;

import java.util.HashSet;
import java.util.Set;

public class SyncAbilityGeneratorMsg{
	public static SyncAbilityGeneratorMsg read(PacketBuffer buf){
		Set<ResourceLocation> list = new HashSet<>();
		for(int i = buf.readVarInt(); i>0; i--)
			list.add(buf.readResourceLocation());
		return new SyncAbilityGeneratorMsg(list);
	}

	private final Set<ResourceLocation> abilityGenerators;

	public SyncAbilityGeneratorMsg(Set<ResourceLocation> abilityGenerators){
		this.abilityGenerators = abilityGenerators;
	}

	public Set<ResourceLocation> getAbilityGenerators(){
		return abilityGenerators;
	}

	public void write(PacketBuffer buf){
		buf.writeVarInt(abilityGenerators.size());
		for(ResourceLocation g : abilityGenerators)
			buf.writeResourceLocation(g);
	}
}
