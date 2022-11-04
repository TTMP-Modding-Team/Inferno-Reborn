package ttmp.infernoreborn.api.sigil.page;

import com.google.common.collect.ImmutableList;

import java.util.List;

public class SimpleSigilBookEntry implements SigilBookEntry{
	private final int descriptionPages;
	private final List<EffectPage> effectPages;

	public SimpleSigilBookEntry(int descriptionPages, List<EffectPage> effectPages){
		this.descriptionPages = descriptionPages;
		this.effectPages = ImmutableList.copyOf(effectPages);
	}

	@Override public int getDescriptionPages(){
		return descriptionPages;
	}
	@Override public List<EffectPage> getEffectPages(){
		return effectPages;
	}
}
