package ttmp.infernoreborn.api.essence;

public interface Essences{
	Essences EMPTY = t -> 0;

	int getEssence(EssenceType type);

	default long totalEssences(){
		long sum = 0;
		for(EssenceType t : EssenceType.values()) sum += getEssence(t);
		return sum;
	}

	default boolean isEmpty(){
		for(EssenceType type : EssenceType.values())
			if(getEssence(type)>0) return false;
		return true;
	}
}
