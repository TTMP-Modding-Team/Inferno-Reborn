package ttmp.infernoreborn.network;

import net.minecraft.network.PacketBuffer;
import ttmp.infernoreborn.contents.ability.generator.scheme.AbilityGeneratorScheme;

import java.util.HashSet;
import java.util.Set;

public class SyncAbilitySchemeMsg{
	public static SyncAbilitySchemeMsg read(PacketBuffer buf){
		Set<AbilityGeneratorScheme> list = new HashSet<>();
		for(int i = buf.readVarInt(); i>0; i--)
			list.add(AbilityGeneratorScheme.read(buf));
		return new SyncAbilitySchemeMsg(list);
	}

	private final Set<AbilityGeneratorScheme> schemes;

	public SyncAbilitySchemeMsg(Set<AbilityGeneratorScheme> schemes){
		this.schemes = schemes;
	}

	public Set<AbilityGeneratorScheme> getSchemes(){
		return schemes;
	}

	public void write(PacketBuffer buf){
		buf.writeVarInt(schemes.size());
		for(AbilityGeneratorScheme s : schemes)
			s.write(buf);
	}
}
