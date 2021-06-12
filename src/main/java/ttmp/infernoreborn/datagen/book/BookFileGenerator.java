package ttmp.infernoreborn.datagen.book;

import com.google.gson.JsonObject;
import net.minecraft.util.ResourceLocation;

public interface BookFileGenerator{
	void saveBook(ResourceLocation bookId, JsonObject json);
	void saveCategory(ResourceLocation bookId, String categoryId, JsonObject json);
	void saveEntry(ResourceLocation bookId, String categoryId, String entryId, JsonObject json);
}
