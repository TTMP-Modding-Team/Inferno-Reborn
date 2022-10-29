package datagen.builder.book;

import com.google.gson.JsonObject;
import net.minecraft.util.ResourceLocation;

public interface BookFileGenerator{
	void saveBook(ResourceLocation bookId, JsonObject json);
	void saveCategory(ResourceLocation bookId, String categoryId, JsonObject json);
	void saveTemplate(ResourceLocation bookId, String templateId, JsonObject json);
	void saveEntry(ResourceLocation bookId, String categoryId, String entryId, JsonObject json);
}
