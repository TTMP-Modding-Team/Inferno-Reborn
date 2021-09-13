package ttmp.wtf.definitions.initializer;

import it.unimi.dsi.fastutil.ints.IntArrayList;
import it.unimi.dsi.fastutil.ints.IntList;
import ttmp.wtf.exceptions.WtfException;
import ttmp.wtf.internal.WtfExecutor;

/**
 * No properties. Apply interaction asserts the
 */
public class AssertInitializer implements Initializer<Boolean>{
	private final boolean errorIfFails;
	private final IntList failedLines = new IntArrayList();

	public AssertInitializer(){
		this(true);
	}
	public AssertInitializer(boolean errorIfFails){
		this.errorIfFails = errorIfFails;
	}

	@Override public void apply(WtfExecutor executor, Object o){
		if(!executor.expectBoolean(o)) failedLines.add(executor.getCurrentLine());
	}

	@Override public Boolean finish(WtfExecutor executor){
		if(failedLines.isEmpty()) return true;
		else if(errorIfFails){
			StringBuilder b = new StringBuilder()
					.append(failedLines.size()).append(" asserts failed: ");
			for(int i = 0; i<failedLines.size(); i++){
				int line = failedLines.getInt(i);
				if(i!=0) b.append(", ");
				b.append("Line ").append(line);
			}
			throw new WtfException(b.toString());
		}else return false;
	}
}
