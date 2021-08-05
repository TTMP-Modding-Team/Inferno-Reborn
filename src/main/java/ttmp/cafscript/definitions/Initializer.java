package ttmp.cafscript.definitions;

import javax.annotation.Nullable;

public interface Initializer<T> {
	@Nullable InitDefinition getPropertyDefinition(String property);
	Object getPropertyValue(String property);

	void setPropertyValue(String property, Object o);

	T finish();
}
