package ttmp.wtf;

import ttmp.wtf.internal.Lines;
import ttmp.wtf.internal.WtfScriptFormatter;

import java.util.regex.Pattern;

public final class WtfScript{
	public static final Pattern NAME_PATTERN = Pattern.compile("[A-Za-z_][A-Za-z0-9_]*");

	private final WtfScriptEngine engine;

	private final byte[] inst;
	private final Lines lines;

	public WtfScript(WtfScriptEngine engine,
	                 byte[] inst,
	                 Lines lines){
		this.engine = engine;
		this.inst = inst;
		this.lines = lines;
	}

	public WtfScriptEngine getEngine(){
		return engine;
	}

	public int getInstSize(){
		return inst.length;
	}
	public byte getInst(int at){
		return inst[at];
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
