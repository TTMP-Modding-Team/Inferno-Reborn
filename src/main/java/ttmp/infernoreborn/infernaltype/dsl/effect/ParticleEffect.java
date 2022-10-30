package ttmp.infernoreborn.infernaltype.dsl.effect;

import net.minecraft.network.PacketBuffer;
import ttmp.infernoreborn.InfernoReborn;
import ttmp.infernoreborn.infernaltype.InfernalGenContext;

import javax.annotation.Nullable;
import java.util.Arrays;
import java.util.stream.Collectors;

public final class ParticleEffect implements InfernalEffect{
	private final int[] colors;

	public ParticleEffect(int... colors){
		this.colors = colors.clone();
	}

	public int size(){
		return colors.length;
	}
	public int color(int i){
		return colors[i];
	}

	@Override public String toString(){
		return "Particle["+Arrays.stream(colors).mapToObj(Integer::toHexString).collect(Collectors.joining(", "))+']';
	}

	@Override public void apply(InfernalGenContext context){
		context.getHolder().addParticleEffect(this);
	}

	public void write(PacketBuffer buf){
		buf.writeVarInt(colors.length);
		for(int i : colors) buf.writeInt(i);
	}

	@Nullable public static ParticleEffect read(PacketBuffer buf){
		int[] colors = new int[buf.readVarInt()];
		for(int i = 0; i<colors.length; i++)
			colors[i] = buf.readInt();
		if(colors.length>0) return new ParticleEffect(colors);
		InfernoReborn.LOGGER.error("Particle with no color");
		return null;
	}
}
