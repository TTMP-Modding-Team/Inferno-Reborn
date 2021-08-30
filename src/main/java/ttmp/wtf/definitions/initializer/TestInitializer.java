package ttmp.wtf.definitions.initializer;

import it.unimi.dsi.fastutil.ints.IntArrayList;
import it.unimi.dsi.fastutil.ints.IntList;
import ttmp.wtf.exceptions.WtfException;
import ttmp.wtf.internal.WtfExecutor;

import java.util.ArrayList;
import java.util.List;

/**
 * No properties. Apply interaction is matching param to predefined object.
 */
public class TestInitializer implements Initializer<Boolean>{
	private final boolean errorIfFails;
	private final Object[] expectedValues;
	private final List<Object> provided = new ArrayList<>();

	public TestInitializer(Object... expectedValues){
		this(true, expectedValues);
	}
	public TestInitializer(boolean errorIfFails, Object... expectedValues){
		this.errorIfFails = errorIfFails;
		this.expectedValues = expectedValues;
		for(int i = 0; i<expectedValues.length; i++){
			if(expectedValues[i] instanceof Integer){
				expectedValues[i] = ((Integer)expectedValues[i]).doubleValue();
			}
		}
	}

	@Override public void apply(WtfExecutor executor, Object o){
		int size = provided.size();
		if(expectedValues.length>size) provided.add(o);
		else executor.error("Too many values provided: "+size);
	}

	@Override public Boolean finish(WtfExecutor executor){
		if(provided.size()!=expectedValues.length)
			executor.error("Too few of values provided: "+provided.size());
		IntList wrongMatchIndices = new IntArrayList();
		for(int i = 0; i<provided.size(); i++){
			Object p = provided.get(i);
			if(!p.equals(expectedValues[i])){
				wrongMatchIndices.add(i);
			}
		}
		if(wrongMatchIndices.isEmpty()) return true;
		else if(errorIfFails){
			StringBuilder b = new StringBuilder()
					.append(wrongMatchIndices.size()).append(" out of ")
					.append(expectedValues.length).append(" Tests failed:");
			for(int i = 0; i<wrongMatchIndices.size(); i++){
				int index = wrongMatchIndices.getInt(i);
				b.append("\n").append(index+1)
						.append("| Provided: ").append(provided.get(index))
						.append(", Expected: ").append(expectedValues[index]);
			}
			throw new WtfException(b.toString());
		}else return false;
	}
}
