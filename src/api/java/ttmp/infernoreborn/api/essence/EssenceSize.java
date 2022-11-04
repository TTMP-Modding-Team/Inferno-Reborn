package ttmp.infernoreborn.api.essence;

public enum EssenceSize{
	ESSENCE,
	GREATER_ESSENCE,
	EXQUISITE_ESSENCE;

	public int getCompressionRate(){
		switch(this){
			case ESSENCE:
				return 1;
			case GREATER_ESSENCE:
				return 9;
			case EXQUISITE_ESSENCE:
				return 81;
			default:
				throw new IllegalStateException("Unreachable");
		}
	}

	public static EssenceSize of(int ordinal){
		EssenceSize[] values = values();
		return values[ordinal%values.length];
	}
}
