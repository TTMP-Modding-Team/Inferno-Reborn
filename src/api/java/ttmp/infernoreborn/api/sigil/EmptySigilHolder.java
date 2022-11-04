package ttmp.infernoreborn.api.sigil;

import ttmp.infernoreborn.api.sigil.context.SigilEventContext;

import java.util.Collections;
import java.util.Set;

public final class EmptySigilHolder implements SigilHolder{
	public static final EmptySigilHolder INSTANCE = new EmptySigilHolder();

	private EmptySigilHolder(){}

	@Override public Set<Sigil> getSigils(){
		return Collections.emptySet();
	}
	@Override public int getMaxPoints(){
		return 0;
	}
	@Override public int getTotalPoint(){
		return 0;
	}
	@Override public boolean has(Sigil sigil){
		return false;
	}
	@Override public boolean add(Sigil sigil, boolean force){
		return false;
	}
	@Override public boolean remove(Sigil sigil){
		return false;
	}
	@Override public boolean isEmpty(){
		return true;
	}
	@Override public void clear(){}
	@Override public SigilEventContext createContext(){
		return SigilEventContext.just(this);
	}
}
