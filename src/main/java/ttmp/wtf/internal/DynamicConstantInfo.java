package ttmp.wtf.internal;

import java.util.Objects;

public final class DynamicConstantInfo{
	private final byte varId;
	private final Class<?> constantType;

	public DynamicConstantInfo(byte varId, Class<?> constantType){
		this.varId = varId;
		this.constantType = Objects.requireNonNull(constantType);
	}

	public byte getVarId(){
		return varId;
	}
	public Class<?> getConstantType(){
		return constantType;
	}
}
