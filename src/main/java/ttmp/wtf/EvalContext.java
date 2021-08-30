package ttmp.wtf;

import javax.annotation.Nullable;

@FunctionalInterface
public interface EvalContext{
	EvalContext DEFAULT = s -> null;

	@Nullable Object getDynamicConstant(String name);
}
