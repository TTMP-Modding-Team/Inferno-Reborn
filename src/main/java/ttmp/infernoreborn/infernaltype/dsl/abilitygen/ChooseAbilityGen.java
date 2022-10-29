package ttmp.infernoreborn.infernaltype.dsl.abilitygen;

import ttmp.infernoreborn.contents.ability.Ability;
import ttmp.infernoreborn.infernaltype.InfernalGenContext;
import ttmp.infernoreborn.infernaltype.dsl.dynamic.Dynamic;
import ttmp.infernoreborn.infernaltype.dsl.dynamic.DynamicInt;
import ttmp.infernoreborn.util.Weighted;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

public final class ChooseAbilityGen implements AbilityGen{
	private final List<Entry> entries;
	@Nullable private final DynamicInt quantity;

	public ChooseAbilityGen(List<Entry> entries){
		this(entries, null);
	}
	public ChooseAbilityGen(List<Entry> entries, @Nullable DynamicInt quantity){
		this.entries = entries;
		this.quantity = quantity;
	}

	@Override public List<Ability> generate(InfernalGenContext context){
		List<RollEntry> rollEntries = new ArrayList<>();
		for(Entry e : entries){
			int wgt = e.weight.evaluateInt(context);
			if(wgt<=0) continue;
			for(Ability a : e.abilityGen.generate(context))
				rollEntries.add(new RollEntry(wgt, a));
		}
		if(quantity==null){
			RollEntry picked = Weighted.pick(context.getRandom(), rollEntries);
			return picked!=null ? Collections.singletonList(picked.ability) : Collections.emptyList();
		}
		return Weighted.pickMultiple(context.getRandom(), rollEntries, quantity.evaluateInt(context))
				.stream().map(RollEntry::ability).collect(Collectors.toList());
	}

	@Override public void validate(){
		for(Entry e : this.entries) e.abilityGen.validate();
	}

	@Override public String toString(){
		return "choose("+entries.stream().map(Object::toString).collect(Collectors.joining(", "))+")";
	}

	private static final DynamicInt ONE = Dynamic.constantInt(1);

	public static final class Entry{
		private final AbilityGen abilityGen;
		private final DynamicInt weight;

		public Entry(AbilityGen abilityGen){
			this(abilityGen, ONE);
		}
		public Entry(AbilityGen abilityGen, DynamicInt weight){
			this.abilityGen = abilityGen;
			this.weight = weight;
		}

		public AbilityGen getAbilityGen(){
			return abilityGen;
		}
		public DynamicInt getWeight(){
			return weight;
		}

		@Override public String toString(){
			return weight==ONE ? abilityGen+"" : abilityGen+" weight "+weight;
		}
	}

	private static final class RollEntry implements Weighted{
		private final int weight;
		private final Ability ability;
		private RollEntry(int weight, Ability ability){
			this.weight = weight;
			this.ability = ability;
		}

		@Override public int weight(){
			return weight;
		}
		public Ability ability(){
			return ability;
		}
	}
}
