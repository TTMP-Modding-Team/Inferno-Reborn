package ttmp.wtf.obj;

import javax.annotation.Nullable;

public interface WtfPropertyHolder{
	@Nullable Object getProperty(String name);
	/**
	 *
	 * @param name
	 * @param value
	 * @return Whether the property was set - in other words, {@code true} implies success and {@code false} implies failure
	 */
	boolean setProperty(String name, @Nullable Object value);
}
