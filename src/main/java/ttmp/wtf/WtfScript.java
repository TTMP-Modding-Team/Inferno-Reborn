package ttmp.wtf;

import com.google.common.collect.ImmutableMap;
import ttmp.wtf.internal.DynamicConstantInfo;
import ttmp.wtf.internal.Lines;
import ttmp.wtf.internal.WtfScriptFormatter;
import ttmp.wtf.obj.Address;

import java.util.Map;
import java.util.regex.Pattern;

public final class WtfScript{
	public static final Pattern NAME_PATTERN = Pattern.compile("[A-Za-z_][A-Za-z0-9_]*(?:\\.[A-Za-z_][A-Za-z0-9_]*)*");

	private final byte[] inst;
	private final Object[] objects;
	private final String[] identifiers;
	private final Map<Address, DynamicConstantInfo> dynamicConstants;

	private final int variables;
	private final int maxStack;

	private final Lines lines;

	public WtfScript(byte[] inst,
	                 Object[] objects,
	                 String[] identifiers,
	                 Map<Address, DynamicConstantInfo> dynamicConstants,
	                 int variables,
	                 int maxStack,
	                 Lines lines){
		this.inst = inst;
		this.objects = objects;
		this.identifiers = identifiers;
		this.dynamicConstants = ImmutableMap.copyOf(dynamicConstants);
		this.variables = variables;
		this.maxStack = maxStack;
		this.lines = lines;
	}

	public int getInstSize(){
		return inst.length;
	}
	public byte getInst(int at){
		return inst[at];
	}

	public int getObjectSize(){
		return objects.length;
	}
	public Object getObject(int at){
		return objects[at];
	}
	public int getIdentifierSize(){
		return identifiers.length;
	}
	public String getIdentifier(int at){
		return identifiers[at];
	}

	public Map<Address, DynamicConstantInfo> getDynamicConstants(){
		return dynamicConstants;
	}

	public int getVariables(){
		return variables;
	}
	public int getMaxStack(){
		return maxStack;
	}

	public Lines getLines(){
		return lines;
	}

	public String format(){
		return new WtfScriptFormatter(this).toString();
	}

	@Override public String toString(){
		return "WtfScript";
	}
}
