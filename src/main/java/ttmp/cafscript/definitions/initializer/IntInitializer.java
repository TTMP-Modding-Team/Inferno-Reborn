package ttmp.cafscript.definitions.initializer;

public class IntInitializer implements Initializer<Integer>{
	private int value;

	public IntInitializer(){}
	public IntInitializer(int defaultValue){
		this.value = defaultValue;
	}

	@Override public void apply(Object o){
		this.value = ((Double)o).intValue();
	}

	@Override public Integer finish(){
		return value;
	}
}
