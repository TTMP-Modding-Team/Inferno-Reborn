package ttmp.cafscript.definitions.initializer;

public class ColorInitializer implements Initializer<Integer>{
	private int color;

	@Override public void apply(Object o){
		this.color = (Integer)o;
	}

	@Override public Integer finish(){
		return color;
	}
}
