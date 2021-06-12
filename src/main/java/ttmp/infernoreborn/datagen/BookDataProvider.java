package ttmp.infernoreborn.datagen;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;
import net.minecraft.block.Blocks;
import net.minecraft.data.DataGenerator;
import net.minecraft.data.DirectoryCache;
import net.minecraft.data.IDataProvider;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import ttmp.infernoreborn.InfernoReborn;
import ttmp.infernoreborn.ability.Ability;
import ttmp.infernoreborn.contents.Abilities;
import ttmp.infernoreborn.contents.ModItems;
import ttmp.infernoreborn.datagen.book.BookBuilder;
import ttmp.infernoreborn.datagen.book.BookCategory;
import ttmp.infernoreborn.datagen.book.BookEntry;
import ttmp.infernoreborn.datagen.book.BookFileGenerator;
import ttmp.infernoreborn.datagen.book.Stack;
import ttmp.infernoreborn.datagen.book.TextPage;
import ttmp.infernoreborn.item.FixedAbilityItem;

import java.io.IOException;
import java.nio.file.Path;
import java.util.Objects;

import static ttmp.infernoreborn.InfernoReborn.MODID;

public class BookDataProvider implements IDataProvider{
	private static final Gson GSON = new GsonBuilder()
			.setPrettyPrinting()
			.disableHtmlEscaping()
			.create();

	private final DataGenerator generator;

	public BookDataProvider(DataGenerator generator){
		this.generator = generator;
	}

	@Override public void run(DirectoryCache directoryCache){
		new BookBuilder(ModItems.BOOK_OF_THE_UNSPEAKABLE.getId(),
				ModItems.BOOK_OF_THE_UNSPEAKABLE.get().getDescriptionId(),
				i18nText("text", ModItems.BOOK_OF_THE_UNSPEAKABLE.getId(), "landingText"))
				.dontGenerateBook(true)
				.customBookItem(new Stack(ModItems.BOOK_OF_THE_UNSPEAKABLE.get()))
				.i18n(true)
				.category(new BookCategory("quick_start",
						i18nText("text", ModItems.BOOK_OF_THE_UNSPEAKABLE.getId(), "quick_start"),
						new Stack(Blocks.COBBLESTONE))
						.sortnum(1))
				.category(new BookCategory("abilities",
						i18nText("text", ModItems.BOOK_OF_THE_UNSPEAKABLE.getId(), "abilities"),
						new Stack(ModItems.PRIMAL_INFERNO_SPARK.get()))
						.sortnum(2))
				.category(new BookCategory("treasures",
						i18nText("text", ModItems.BOOK_OF_THE_UNSPEAKABLE.getId(), "treasures"),
						new Stack(ModItems.ESSENCE_HOLDER.get()))
						.sortnum(3))
				.makeEntryAndSave(bookEntryConsumer -> {
					for(Ability ability : Abilities.getRegistry()){
						ItemStack stack = new ItemStack(ModItems.INFERNO_SPARK.get());
						FixedAbilityItem.setAbilities(stack, new Ability[]{ability});

						bookEntryConsumer.accept(new BookEntry(
								Objects.requireNonNull(ability.getRegistryName()).getPath(),
								ability.getUnlocalizedName(),
								new ResourceLocation(MODID, "abilities"),
								new Stack(stack))
								.page(new TextPage(i18nText("text", ability.getRegistryName(), "0"))));
					}
				}, new BookFileGenerator(){
					@Override public void saveBook(ResourceLocation bookId, JsonObject json){
						trySave(directoryCache, json, createPath(bookId, "book.json"));
					}
					@Override public void saveCategory(ResourceLocation bookId, String categoryId, JsonObject json){
						trySave(directoryCache, json, createPath(bookId, "en_us/categories/"+categoryId+".json"));
					}
					@Override public void saveEntry(ResourceLocation bookId, String categoryId, String entryId, JsonObject json){
						trySave(directoryCache, json, createPath(bookId, "en_us/entries/"+categoryId+"/"+entryId+".json"));
					}
				});
	}

	private void trySave(DirectoryCache directoryCache, JsonObject json, Path path){
		try{
			IDataProvider.save(GSON, directoryCache, json, path);
		}catch(IOException ex){
			InfernoReborn.LOGGER.error("Couldn't save patchouli book {}", path, ex);
		}
	}

	@Override public String getName(){
		return "The Book";
	}

	private Path createPath(ResourceLocation bookId, String filename){
		return generator.getOutputFolder().resolve("data/"+bookId.getNamespace()+"/patchouli_books/"+bookId.getPath()+"/"+filename);
	}

	private static String i18nText(String type, ResourceLocation id, String s){
		return type+"."+id.getNamespace()+"."+id.getPath()+"."+s;
	}
}
