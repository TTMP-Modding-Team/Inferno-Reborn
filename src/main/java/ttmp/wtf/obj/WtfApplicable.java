package ttmp.wtf.obj;

import javax.annotation.Nullable;

/**
 * Specifies apply action.<br>
 * Objects implementing this interface will be able to use apply action, either with ':' operator or 'apply' function form.<br>
 * Please note that apply action defined by this interface has priority over the apply action defined in {@link ttmp.wtf.EvalContext EvalContext}.
 */
@FunctionalInterface
public interface WtfApplicable{
	void apply(@Nullable Object v);
}
