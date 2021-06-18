package ttmp.infernoreborn.contents.sigil.context;

import ttmp.infernoreborn.contents.sigil.holder.SigilHolder;

public class MinimumContext implements SigilEventContext{
	private final SigilHolder holder;

	public MinimumContext(SigilHolder holder){
		this.holder = holder;
	}

	@Override public SigilHolder holder(){
		return holder;
	}
}
