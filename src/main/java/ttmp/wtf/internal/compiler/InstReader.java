package ttmp.wtf.internal.compiler;

import com.google.common.primitives.Ints;
import com.google.common.primitives.Longs;
import com.google.common.primitives.Shorts;
import ttmp.wtf.WtfScript;

import java.util.Objects;

public class InstReader{
	public final WtfScript script;

	public int ip = 0;

	public InstReader(WtfScript script){
		this.script = Objects.requireNonNull(script);
	}

	public int getCurrentLine(){
		return script.getLines().getLine(ip-1);
	}

	public byte next(){
		return script.getInst(ip++);
	}
	public int nextU(){
		return Byte.toUnsignedInt(next());
	}
	public short next2(){
		return Shorts.fromBytes(next(), next());
	}
	public int next4(){
		return Ints.fromBytes(next(), next(), next(), next());
	}
	public double nextDouble(){ // haha funny
		return Double.longBitsToDouble(Longs.fromBytes(next(), next(), next(), next(), next(), next(), next(), next()));
	}

	public String identifierAt(){
		return script.getEngine().getConstantPool().getIdentifier(Byte.toUnsignedInt(script.getInst(ip-1)));
	}
	public String identifierAt(int prev){
		return script.getEngine().getConstantPool().getIdentifier(Byte.toUnsignedInt(script.getInst(ip-1-prev)));
	}
	public Object objAt(){
		return script.getEngine().getConstantPool().getObject(Byte.toUnsignedInt(script.getInst(ip-1)));
	}

	public String nextIdentifier(){
		return script.getEngine().getConstantPool().getIdentifier(nextU());
	}
}
