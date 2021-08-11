package ttmp.cafscript.definitions.initializer;

import ttmp.cafscript.internal.CafInterpreter;

public class ColorInitializer implements Initializer<Integer>{
	private int color;

	@Override public void apply(CafInterpreter interpreter, Object o){
		this.color = (Integer)o;
	}

	@Override public Integer finish(){
		return color;
	}
}
