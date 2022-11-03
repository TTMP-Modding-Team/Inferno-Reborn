package ttmp.infernoreborn.util;

public interface EssenceHandler{
	/**
	 * Inserts essence of chosen type.
	 *
	 * @param type     Type of essence
	 * @param essence  Maximum amount of essence to insert
	 * @param simulate Whether this action should modify state or not
	 * @return Amount of essence inserted, {@code 0} if none was inserted
	 */
	int insertEssence(EssenceType type, int essence, boolean simulate);
	/**
	 * Extracts essence of chosen type.
	 *
	 * @param type     Type of essence
	 * @param essence  Maximum amount of essence to extract
	 * @param simulate Whether this action should modify state or not
	 * @return Amount of essence extracted, {@code 0} if none was extracted
	 */
	int extractEssence(EssenceType type, int essence, boolean simulate);

	default boolean insertEssences(Essences holder, boolean simulate){
		for(EssenceType type : EssenceType.values()){
			int essence = holder.getEssence(type);
			if(essence>0&&insertEssence(type, essence, true)!=essence) return false;
		}
		if(!simulate){
			for(EssenceType type : EssenceType.values()){
				int essence = holder.getEssence(type);
				if(essence>0) insertEssence(type, essence, false);
			}
		}
		return true;
	}
	default boolean extractEssences(Essences holder, boolean simulate){
		for(EssenceType type : EssenceType.values()){
			int essence = holder.getEssence(type);
			if(essence>0&&extractEssence(type, essence, true)!=essence) return false;
		}
		if(!simulate){
			for(EssenceType type : EssenceType.values()){
				int essence = holder.getEssence(type);
				if(essence>0) extractEssence(type, essence, false);
			}
		}
		return true;
	}
}
