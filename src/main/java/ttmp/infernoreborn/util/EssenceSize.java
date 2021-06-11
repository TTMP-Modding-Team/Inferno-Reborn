package ttmp.infernoreborn.util;

public enum EssenceSize{
	SHARD,
	CRYSTAL,
	GREATER_CRYSTAL;

	public int getCompressionRate(){
		switch(this){
			case SHARD:
				return 1;
			case CRYSTAL:
				return 9;
			case GREATER_CRYSTAL:
				return 81;
			default:
				throw new IllegalStateException("Unreachable");
		}
	}
}
