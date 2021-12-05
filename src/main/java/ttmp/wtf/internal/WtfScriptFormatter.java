package ttmp.wtf.internal;

import ttmp.wtf.WtfScript;
import ttmp.wtf.internal.compiler.InstReader;

import java.util.Locale;

public class WtfScriptFormatter extends InstReader{
	protected final StringBuilder stb = new StringBuilder();

	public WtfScriptFormatter(WtfScript script){
		super(script);
		format();
	}

	protected void format(){
		formatBytecode();
		writeLine();
		formatRawBytecode();
		writeLine();
		formatObjects();
		writeLine();
		formatIdentifiers();
	}

	protected void formatBytecode(){
		writeLine("Instructions: "+script.getInstSize()+" entries");

		int lastLine = 0;
		for(ip = 0; ip<script.getInstSize(); ){
			int line = script.getLines().getLine(ip);
			if(line!=lastLine){
				lastLine = line;
				if(line<=0) writeLine("    | [L:???]");
				else writeLine("    | [L:"+line+"]");
			}
			write(String.format(" %3d| ", ip));

			byte inst = next();
			switch(inst){
				case Inst.DISCARD:
					writeLine("DISCARD");
					break;
				case Inst.DUP:
					writeLine("DUP");
					break;
				case Inst.DUP_AT:
					writeLine("DUP_AT "+nextU());
					break;
				case Inst.JUMP:
					writeLine("JUMP "+next2());
					break;
				case Inst.JUMPIF:
					writeLine("JUMPIF "+next2());
					break;
				case Inst.JUMPELSE:
					writeLine("JUMPELSE "+next2());
					break;
				case Inst.JUMP_IF_LT1:
					writeLine("JUMP_IF_LT1 "+next2());
					break;
				case Inst.JUMP_OR_NEXT:
					writeLine("JUMP_OR_NEXT "+next2());
					break;
				case Inst.RETURN:
					writeLine("RETURN");
					break;
				case Inst.END:
					writeLine("END");
					break;
				case Inst.TRUE:
					writeLine("TRUE");
					break;
				case Inst.FALSE:
					writeLine("FALSE");
					break;
				case Inst.THIS:
					writeLine("THIS");
					break;
				case Inst.NULL:
					writeLine("NULL");
					break;
				case Inst.I:
					writeLine("I "+next4());
					break;
				case Inst.I0:
					writeLine("I0");
					break;
				case Inst.I1:
					writeLine("I1");
					break;
				case Inst.IM1:
					writeLine("IM1");
					break;
				case Inst.D:
					writeLine("D "+nextDouble());
					break;
				case Inst.D0:
					writeLine("D0");
					break;
				case Inst.D1:
					writeLine("D1");
					break;
				case Inst.DM1:
					writeLine("DM1");
					break;
				case Inst.ADD:
					writeLine("ADD");
					break;
				case Inst.SUBTRACT:
					writeLine("SUBTRACT");
					break;
				case Inst.MULTIPLY:
					writeLine("MULTIPLY");
					break;
				case Inst.DIVIDE:
					writeLine("DIVIDE");
					break;
				case Inst.NEGATE:
					writeLine("NEGATE");
					break;
				case Inst.NOT:
					writeLine("NOT");
					break;
				case Inst.EQ:
					writeLine("EQ");
					break;
				case Inst.NEQ:
					writeLine("NEQ");
					break;
				case Inst.LT:
					writeLine("LT");
					break;
				case Inst.GT:
					writeLine("GT");
					break;
				case Inst.LTEQ:
					writeLine("LTEQ");
					break;
				case Inst.GTEQ:
					writeLine("GTEQ");
					break;
				case Inst.ADD1:
					writeLine("ADD1");
					break;
				case Inst.SUB1:
					writeLine("SUB1");
					break;
				case Inst.BUNDLE2:
					writeLine("BUNDLE2");
					break;
				case Inst.BUNDLE3:
					writeLine("BUNDLE3");
					break;
				case Inst.BUNDLE4:
					writeLine("BUNDLE4");
					break;
				case Inst.BUNDLEN:
					writeLine("BUNDLEN "+nextU());
					break;
				case Inst.APPEND2:
					writeLine("APPEND2");
					break;
				case Inst.APPEND3:
					writeLine("APPEND3");
					break;
				case Inst.APPEND4:
					writeLine("APPEND4");
					break;
				case Inst.APPENDN:
					writeLine("APPENDN "+nextU());
					break;
				case Inst.RANGE:
					writeLine("RANGE");
					break;
				case Inst.RAND:
					writeLine("RAND");
					break;
				case Inst.RANDN:
					writeLine("RANDN "+next4()+' '+next4());
					break;
				case Inst.CONST:
					writeLine("CONST "+nextU()+"   #"+objAt());
					break;
				case Inst.ARG:
					writeLine("ARG "+nextU());
					break;
				case Inst.INTERNAL_CONST:
					writeLine("INTERNAL_CONST "+nextU());
					break;
				case Inst.GET_PROPERTY:
					writeLine("GET_PROPERTY #"+identifierAt());
					break;
				case Inst.SET_PROPERTY:
					writeLine("SET_PROPERTY #"+identifierAt());
					break;
				case Inst.DYNAMIC_GET:
					writeLine("DYNAMIC_GET #"+identifierAt());
					break;
				case Inst.APPLY:
					writeLine("APPLY");
					break;
				case Inst.DEBUG:
					writeLine("DEBUG");
					break;
				case Inst.EXECUTE:
					writeLine("EXECUTE");
					break;
				case Inst.CONSTRUCT:
					writeLine("CONSTRUCT");
					break;
				case Inst.MAKE_ITERATOR:
					writeLine("MAKE_ITERATOR");
					break;
				case Inst.IN:
					writeLine("IN");
					break;
				default:
					writeLine("??? ("+Integer.toHexString(inst).toUpperCase(Locale.ROOT)+")");
			}
		}
	}

	protected void formatRawBytecode(){
		writeLine("Raw Bytecode:");
		for(ip = 0; ip<script.getInstSize(); ){
			if(ip!=0) writeLine();
			for(int j = 0; ip<script.getInstSize()&&j<8; j++){
				if(j==0) stb.append(String.format(" %3d| ", ip));
				else stb.append(' ');
				stb.append(String.format("%02X", next()));
			}
		}
		writeLine();
	}

	protected void formatObjects(){
		WtfConstantPool pool = script.getEngine().getConstantPool();
		writeLine("Objects: "+pool.getObjectSize()+" entries");
		for(int i = 0; i<pool.getObjectSize(); i++)
			writeLine(String.format(" %3d| %s", i, pool.getObject(i)));
	}

	protected void formatIdentifiers(){
		WtfConstantPool pool = script.getEngine().getConstantPool();
		writeLine("Identifiers: "+pool.getIdentifierSize()+" entries");
		for(int i = 0; i<pool.getIdentifierSize(); i++)
			writeLine(String.format(" %3d| %s", i, pool.getIdentifier(i)));
	}

	protected void write(Object o){
		stb.append(o);
	}
	protected void writeLine(Object o){
		stb.append(o).append('\n');
	}
	protected void writeLine(){
		stb.append('\n');
	}

	@Override public String toString(){
		return stb.toString();
	}
}
