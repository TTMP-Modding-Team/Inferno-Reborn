package ttmp.infernoreborn.util;

public interface Essences{
	Essences EMPTY = t -> 0;

	int getEssence(EssenceType type);

	default boolean isEmpty(){
		for(EssenceType type : EssenceType.values())
			if(getEssence(type)>0) return false;
		return true;
	}
}
