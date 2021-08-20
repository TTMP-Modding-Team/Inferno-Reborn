package ttmp.wtf;

import com.google.common.primitives.Shorts;
import ttmp.wtf.internal.Inst;
import ttmp.wtf.internal.Lines;

import java.util.Locale;

public final class WtfScript{
	private final byte[] inst;
	private final Object[] objects;
	private final String[] identifiers;

	private final int variables;
	private final int maxStack;

	private final Lines lines;

	public WtfScript(byte[] inst, Object[] objects, String[] identifiers, int variables, int maxStack, Lines lines){
		this.inst = inst;
		this.objects = objects;
		this.identifiers = identifiers;
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
		StringBuilder stb = new StringBuilder("CafScript{");
		stb.append("\nVariables: ").append(variables);
		stb.append("\nMaximum Stack Size: ").append(maxStack);
		stb.append("\nBytecodes: ").append(inst.length).append(" entries");
		int lastLine = 0;
		for(int i = 0; i<inst.length; i++){
			int line = lines.getLine(i);
			if(line!=lastLine){
				lastLine = line;
				if(line<=0) stb.append("\n    | [L:???]");
				else stb.append("\n    | [L:").append(line).append("]");
			}
			stb.append(String.format("\n %3d| ", i));
			switch(inst[i]){
				case Inst.PUSH:
					stb.append("PUSH ").append(inst[++i]).append("   #").append(objString(i));
					break;
				case Inst.DISCARD:
					stb.append("DISCARD");
					break;
				case Inst.DUP:
					stb.append("DUP");
					break;
				case Inst.TRUE:
					stb.append("TRUE");
					break;
				case Inst.FALSE:
					stb.append("FALSE");
					break;
				case Inst.N0:
					stb.append("N0");
					break;
				case Inst.N1:
					stb.append("N1");
					break;
				case Inst.N2:
					stb.append("N2");
					break;
				case Inst.N3:
					stb.append("N3");
					break;
				case Inst.N4:
					stb.append("N4");
					break;
				case Inst.N5:
					stb.append("N5");
					break;
				case Inst.NM1:
					stb.append("NM1");
					break;
				case Inst.ADD:
					stb.append("ADD");
					break;
				case Inst.SUBTRACT:
					stb.append("SUBTRACT");
					break;
				case Inst.MULTIPLY:
					stb.append("MULTIPLY");
					break;
				case Inst.DIVIDE:
					stb.append("DIVIDE");
					break;
				case Inst.NEGATE:
					stb.append("NEGATE");
					break;
				case Inst.NOT:
					stb.append("NOT");
					break;
				case Inst.EQ:
					stb.append("EQ");
					break;
				case Inst.NEQ:
					stb.append("NEQ");
					break;
				case Inst.LT:
					stb.append("LT");
					break;
				case Inst.GT:
					stb.append("GT");
					break;
				case Inst.LTEQ:
					stb.append("LTEQ");
					break;
				case Inst.GTEQ:
					stb.append("GTEQ");
					break;
				case Inst.ADD1:
					stb.append("ADD1");
					break;
				case Inst.SUB1:
					stb.append("SUB1");
					break;
				case Inst.BUNDLE2:
					stb.append("BUNDLE2");
					break;
				case Inst.BUNDLE3:
					stb.append("BUNDLE3");
					break;
				case Inst.BUNDLE4:
					stb.append("BUNDLE4");
					break;
				case Inst.BUNDLEN:
					stb.append("BUNDLEN ").append(Byte.toUnsignedInt(inst[++i]));
					break;
				case Inst.APPEND2:
					stb.append("APPEND2");
					break;
				case Inst.APPEND3:
					stb.append("APPEND3");
					break;
				case Inst.APPEND4:
					stb.append("APPEND4");
					break;
				case Inst.APPENDN:
					stb.append("APPENDN ").append(Byte.toUnsignedInt(inst[++i]));
					break;
				case Inst.GET_PROPERTY:
					stb.append("GET_PROPERTY ").append(inst[++i]).append(" ").append(Byte.toUnsignedInt(inst[++i]))
							.append("   #").append(identifiers[inst[i-1]]);
					break;
				case Inst.SET_PROPERTY:
					stb.append("SET_PROPERTY ").append(inst[++i]).append("   #").append(identifiers[inst[i]]);
					break;
				case Inst.SET_PROPERTY_LAZY:
					stb.append("SET_PROPERTY_LAZY ").append(inst[++i])
							.append(" ").append(Shorts.fromBytes(inst[++i], inst[++i]))
							.append("   #").append(identifiers[inst[i-2]]);
					break;
				case Inst.APPLY:
					stb.append("APPLY");
					break;
				case Inst.GET_VARIABLE:
					stb.append("GET_VARIABLE ").append(Byte.toUnsignedInt(inst[++i]));
					break;
				case Inst.SET_VARIABLE:
					stb.append("SET_VARIABLE ").append(Byte.toUnsignedInt(inst[++i]));
					break;
				case Inst.RANGE:
					stb.append("RANGE");
					break;
				case Inst.NEW:
					stb.append("NEW ").append(inst[++i]).append("   #").append(identifiers[inst[i]]);
					break;
				case Inst.MAKE:
					stb.append("MAKE");
					break;
				case Inst.MAKE_ITERATOR:
					stb.append("MAKE_ITERATOR");
					break;
				case Inst.JUMP:
					stb.append("JUMP ").append(Shorts.fromBytes(inst[++i], inst[++i]));
					break;
				case Inst.JUMPIF:
					stb.append("JUMPIF ").append(Shorts.fromBytes(inst[++i], inst[++i]));
					break;
				case Inst.JUMPELSE:
					stb.append("JUMPELSE ").append(Shorts.fromBytes(inst[++i], inst[++i]));
					break;
				case Inst.JUMP_IF_LT1:
					stb.append("JUMP_IF_LT1 ").append(Shorts.fromBytes(inst[++i], inst[++i]));
					break;
				case Inst.JUMP_OR_NEXT:
					stb.append("JUMP_OR_NEXT ").append(Shorts.fromBytes(inst[++i], inst[++i]));
					break;
				case Inst.DEBUG:
					stb.append("DEBUG");
					break;
				case Inst.FINISH_PROPERTY_INIT:
					stb.append("FINISH_PROPERTY_INIT ").append(inst[++i]).append("   #").append(identifiers[inst[i]]);
					break;
				case Inst.END:
					stb.append("END");
					break;
				default:
					stb.append("??? (").append(Integer.toHexString(inst[i]).toUpperCase(Locale.ROOT)).append(")");
			}
		}
		stb.append("\n\nRaw Bytecodes:\n");
		for(int i = 0; i<inst.length; ){
			if(i!=0) stb.append("\n");
			for(int j = 0; i<inst.length&&j<8; i++, j++){
				byte b = inst[i];
				if(j==0) stb.append(String.format(" %3d| ", i));
				else stb.append(" ");

				stb.append(String.format("%02X", b));
			}
		}
		stb.append("\n\nObjects: ").append(objects.length).append(" entries\n");
		for(int i = 0; i<objects.length; i++){
			String s = (objects[i] instanceof WtfScript) ? ((WtfScript)objects[i]).format() : objects[i].toString();
			stb.append(String.format(" %3d| %s\n", i, s.replace("\n", "\n      ")));
		}
		stb.append("\nIdentifiers: ").append(identifiers.length).append(" entries\n");
		for(int i = 0; i<identifiers.length; i++)
			stb.append(String.format(" %3d| %s\n", i, identifiers[i]));
		return stb.append("}").toString();
	}

	@Override public String toString(){
		return "CafScript";
	}

	private Object objString(int i){
		return objects[inst[i]];
	}
}
