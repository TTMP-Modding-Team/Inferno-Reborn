package ttmp.cafscript.definitions.initializer;

import it.unimi.dsi.fastutil.ints.IntArrayList;
import it.unimi.dsi.fastutil.ints.IntList;
import ttmp.cafscript.exceptions.CafException;
import ttmp.cafscript.internal.CafInterpreter;

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

	@Override public void apply(CafInterpreter interpreter, Object o){
		if(!interpreter.expectBoolean(o)) failedLines.add(interpreter.getCurrentLine());
	}

	@Override public Boolean finish(CafInterpreter interpreter){
		if(failedLines.isEmpty()) return true;
		else if(errorIfFails){
			StringBuilder b = new StringBuilder()
					.append(failedLines.size()).append(" asserts failed: ");
			for(int i = 0; i<failedLines.size(); i++){
				int line = failedLines.getInt(i);
				if(i!=0) b.append(", ");
				b.append("Line ").append(line);
			}
			throw new CafException(b.toString());
		}else return false;
	}
}
