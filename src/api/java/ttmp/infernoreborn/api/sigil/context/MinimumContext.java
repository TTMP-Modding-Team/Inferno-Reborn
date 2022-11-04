package ttmp.infernoreborn.api.sigil.context;

import ttmp.infernoreborn.api.sigil.SigilHolder;

public class MinimumContext implements SigilEventContext{
	private final SigilHolder holder;

	public MinimumContext(SigilHolder holder){
		this.holder = holder;
	}

	@Override public SigilHolder holder(){
		return holder;
	}
}
