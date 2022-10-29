package ttmp.infernoreborn.infernaltype.dsl.effect;

import ttmp.infernoreborn.infernaltype.InfernalGenContext;

import java.util.Arrays;

public final class ParticleEffect implements InfernalEffect{
	private final int[] colors;

	public ParticleEffect(int... colors){
		this.colors = colors.clone();
	}

	@Override public void apply(InfernalGenContext context){
		// TODO
	}

	@Override public String toString(){
		return "ParticleEffect{"+
				"colors="+Arrays.toString(colors)+
				'}';
	}
}
