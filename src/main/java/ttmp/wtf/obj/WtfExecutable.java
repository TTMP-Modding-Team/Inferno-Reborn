package ttmp.wtf.obj;

import ttmp.wtf.internal.WtfExecutor;

import javax.annotation.Nullable;

/**
 * Specifies execute action.<br>
 * Objects implementing this interface will be treated as executables, or 'function's in the script.
 */
@FunctionalInterface
public interface WtfExecutable{
	/**
	 * Executes certain action.
	 *
	 * @param executor The executor, presumably the one called this method.
	 * @param args Arguments. Elements could be null.
	 * @return Result
	 */
	@Nullable Object execute(WtfExecutor executor, Object[] args);
}
