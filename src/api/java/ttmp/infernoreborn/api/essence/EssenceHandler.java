package ttmp.infernoreborn.api.essence;

import ttmp.infernoreborn.api.Simulation;

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

	/**
	 * Consumes essences. The result indicates whether the specified essences can be fully deduced from this handler.
	 * Applying successful result deduces specified essences from this handler, and returns total essences deduced.
	 *
	 * @param ingredient Essences to be deduced
	 * @return Whether the specified essences can be fully deduced from this handler.
	 * Applying successful result deduces specified essences from this handler, and returns total essences deduced.
	 */
	Simulation<Essences> consume(EssenceIngredient ingredient);
}
