package ttmp.cafscript.definitions.initializer;

public class NumberInitializer implements Initializer<Double>{
	private double value;

	public NumberInitializer(){}
	public NumberInitializer(double defaultValue){
		this.value = defaultValue;
	}

	@Override public void apply(Object o){
		this.value = (Double)o;
	}

	@Override public Double finish(){
		return value;
	}
}
