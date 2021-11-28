package ttmp.wtf.operation;

import javax.annotation.Nullable;

@FunctionalInterface
public interface WtfPropertySet{
	void set(Object o, @Nullable Object v);
}
