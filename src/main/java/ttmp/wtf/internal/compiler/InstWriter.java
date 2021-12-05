package ttmp.wtf.internal.compiler;

import it.unimi.dsi.fastutil.bytes.ByteArrayList;
import it.unimi.dsi.fastutil.bytes.ByteList;

public class InstWriter{
	public final ByteList inst = new ByteArrayList();

	public byte[] getInstructions(){
		return this.inst.toByteArray();
	}

	public void write(byte inst){
		this.inst.add(inst);
	}
	public void write2(short inst){
		this.inst.add((byte)(inst >> 8));
		this.inst.add((byte)inst);
	}
	public void write4(int inst){
		this.inst.add((byte)(inst >> 24));
		this.inst.add((byte)(inst >> 16));
		this.inst.add((byte)(inst >> 8));
		this.inst.add((byte)inst);
	}
	public void write8(long inst){
		this.inst.add((byte)(inst >> 56));
		this.inst.add((byte)(inst >> 48));
		this.inst.add((byte)(inst >> 40));
		this.inst.add((byte)(inst >> 32));
		this.inst.add((byte)(inst >> 24));
		this.inst.add((byte)(inst >> 16));
		this.inst.add((byte)(inst >> 8));
		this.inst.add((byte)inst);
	}

	public int getNextWritePoint(){
		return this.inst.size();
	}
	public void writeAt(int writePoint, byte inst){
		this.inst.set(writePoint, inst);
	}
	public void write2At(int writePoint, short inst){
		this.inst.set(writePoint, (byte)(inst >> 8));
		this.inst.set(writePoint+1, (byte)inst);
	}
	public void write4At(int writePoint, int inst){
		this.inst.set(writePoint, (byte)(inst >> 24));
		this.inst.set(writePoint+1, (byte)(inst >> 16));
		this.inst.set(writePoint+2, (byte)(inst >> 8));
		this.inst.set(writePoint+3, (byte)inst);
	}

	public void writeAll(byte[] bytes){
		for(byte b : bytes)
			this.inst.add(b);
	}
}
