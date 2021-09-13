package ttmp.wtf.definitions.initializer;

import ttmp.wtf.internal.WtfExecutor;
import ttmp.wtf.obj.RGB;

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

	@Override public Object getPropertyValue(WtfExecutor executor, String property){
		switch(property){
			case "Red":
				return red;
			case "Green":
				return green;
			case "Blue":
				return blue;
			default:
				return executor.noPropertyError(property);
		}
	}
	@Override public void setPropertyValue(WtfExecutor executor, String property, Object o){
		switch(property){
			case "Red":
				red = executor.expectNumber(o);
				break;
			case "Green":
				green = executor.expectNumber(o);
				break;
			case "Blue":
				blue = executor.expectNumber(o);
				break;
			default:
				executor.noPropertyError(property);
				break;
		}
	}
	@Nullable @Override public Initializer<?> setPropertyValueLazy(WtfExecutor executor, String property, int codepoint){
		switch(property){
			case "Red":
				return new NumberInitializer(red);
			case "Green":
				return new NumberInitializer(green);
			case "Blue":
				return new NumberInitializer(blue);
			default:
				return executor.noPropertyError(property);
		}
	}

	@Override public void apply(WtfExecutor executor, Object o){
		set(executor.expectType(RGB.class, o));
	}

	@Override public RGB finish(WtfExecutor executor){
		return new RGB((int)(red*255), (int)(green*255), (int)(blue*255));
	}
}
