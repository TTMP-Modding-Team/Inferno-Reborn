package ttmp.cafscript.definitions.initializer;

import ttmp.cafscript.internal.CafInterpreter;
import ttmp.cafscript.obj.RGB;

import javax.annotation.Nullable;

public class ColorInitializer implements Initializer<RGB>{
	private double red;
	private double green;
	private double blue;

	public ColorInitializer(){
		this(RGB.BLACK);
	}
	public ColorInitializer(RGB defaultColor){
		set(defaultColor);
	}

	private void set(RGB rgb){
		this.red = rgb.getRed()/255.0;
		this.green = rgb.getGreen()/255.0;
		this.blue = rgb.getBlue()/255.0;
	}

	@Override public Object getPropertyValue(CafInterpreter interpreter, String property){
		switch(property){
			case "Red":
				return red;
			case "Green":
				return green;
			case "Blue":
				return blue;
			default:
				return interpreter.noPropertyError(property);
		}
	}
	@Override public void setPropertyValue(CafInterpreter interpreter, String property, Object o){
		switch(property){
			case "Red":
				red = interpreter.expectNumber(o);
				break;
			case "Green":
				green = interpreter.expectNumber(o);
				break;
			case "Blue":
				blue = interpreter.expectNumber(o);
				break;
			default:
				interpreter.noPropertyError(property);
				break;
		}
	}
	@Nullable @Override public Initializer<?> setPropertyValueLazy(CafInterpreter interpreter, String property, int codepoint){
		switch(property){
			case "Red":
				return new NumberInitializer(red);
			case "Green":
				return new NumberInitializer(green);
			case "Blue":
				return new NumberInitializer(blue);
			default:
				return interpreter.noPropertyError(property);
		}
	}

	@Override public void apply(CafInterpreter interpreter, Object o){
		set(interpreter.expectType(RGB.class, o));
	}

	@Override public RGB finish(CafInterpreter interpreter){
		return new RGB((int)(red*255), (int)(green*255), (int)(blue*255));
	}
}
