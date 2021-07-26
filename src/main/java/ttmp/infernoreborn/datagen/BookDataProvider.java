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
import ttmp.infernoreborn.compat.patchouli.AbilityAttributeComponent;
import ttmp.infernoreborn.compat.patchouli.SigilEffectComponent;
import ttmp.infernoreborn.compat.patchouli.sigil.SigilBookEntry;
import ttmp.infernoreborn.contents.Abilities;
import ttmp.infernoreborn.contents.ModItems;
import ttmp.infernoreborn.contents.Sigils;
import ttmp.infernoreborn.contents.ability.Ability;
import ttmp.infernoreborn.contents.item.FixedAbilityItem;
import ttmp.infernoreborn.contents.item.SigilItem;
import ttmp.infernoreborn.contents.sigil.Sigil;
import ttmp.infernoreborn.datagen.builder.book.BookBuilder;
import ttmp.infernoreborn.datagen.builder.book.BookCategory;
import ttmp.infernoreborn.datagen.builder.book.BookEntry;
import ttmp.infernoreborn.datagen.builder.book.BookFileGenerator;
import ttmp.infernoreborn.datagen.builder.book.Stack;
import ttmp.infernoreborn.datagen.builder.book.page.TemplatePage;
import ttmp.infernoreborn.datagen.builder.book.page.TextPage;

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
		final ResourceLocation bookId = ModItems.BOOK_OF_THE_UNSPEAKABLE.getId();
		new BookBuilder(bookId,
				ModItems.BOOK_OF_THE_UNSPEAKABLE.get().getDescriptionId(),
				i18nText("text", bookId, "landingText"))
				.dontGenerateBook(true)
				.customBookItem(new Stack(ModItems.BOOK_OF_THE_UNSPEAKABLE.get()))
				.i18n(true)
				.category(new BookCategory("journals",
						i18nText("text", bookId, "journals"),
						new Stack(Blocks.COBBLESTONE))
						.sortnum(1))
				.category(new BookCategory("abilities",
						i18nText("text", bookId, "abilities"),
						new Stack(ModItems.PRIMAL_INFERNO_SPARK.get()))
						.sortnum(2))
				.category(new BookCategory("artifacts",
						i18nText("text", bookId, "artifacts"),
						new Stack(ModItems.ESSENCE_HOLDER.get()))
						.sortnum(3))
				.category(new BookCategory("sigils",
						i18nText("text", bookId, "sigils"),
						new Stack(ModItems.SIGIL.get()))
						.sortnum(4))
				.template("ability_attribute", new AbilityAttributeComponent())
				.template("sigil_effect", new SigilEffectComponent())
				.makeEntryAndSave(bookEntryConsumer -> {
					final ResourceLocation abilities = new ResourceLocation(MODID, "abilities");
					final ResourceLocation sigils = new ResourceLocation(MODID, "sigils");

					final ResourceLocation abilityAttribute = new ResourceLocation(MODID, "ability_attribute");
					final ResourceLocation sigilEffect = new ResourceLocation(MODID, "sigil_effect");

					for(Ability ability : Abilities.getRegistry()){
						ItemStack stack = new ItemStack(ModItems.INFERNO_SPARK.get());
						FixedAbilityItem.setAbilities(stack, new Ability[]{ability});
						BookEntry book = new BookEntry(
								Objects.requireNonNull(ability.getRegistryName()).getPath(),
								ability.getUnlocalizedName(),
								abilities,
								new Stack(stack));
						book.page(new TextPage(i18nText("text", "ability", ability.getRegistryName(), "0")));
						if(!ability.getAttributes().isEmpty())
							book.page(new TemplatePage(abilityAttribute).param("ability", ability.getRegistryName()));
						bookEntryConsumer.accept(book);
					}

					for(Sigil sigil : Sigils.getRegistry()){
						SigilBookEntry bookPageContent = sigil.getSigilBookEntryContent();
						ItemStack stack = SigilItem.createSigilItem(sigil);
						BookEntry bookEntry = new BookEntry(
								Objects.requireNonNull(sigil.getRegistryName()).getPath(),
								sigil.getUnlocalizedName(),
								sigils,
								new Stack(stack));
						for(int i = 0; i<bookPageContent.getDescriptionPages(); i++){
							bookEntry.page(new TextPage(i18nText("text", "sigil", sigil.getRegistryName(), String.valueOf(i))));
						}
						for(int i = 0; i<bookPageContent.getEffectPages().size(); i+=2){
							bookEntry.page(new TemplatePage(sigilEffect)
									.param("sigil", sigil.getRegistryName())
									.param("page", i));
						}
						bookEntryConsumer.accept(bookEntry);
					}
				}, new BookFileGenerator(){
					@Override public void saveBook(ResourceLocation bookId, JsonObject json){
						trySave(directoryCache, json, createPath(bookId, "book.json"));
					}
					@Override public void saveCategory(ResourceLocation bookId, String categoryId, JsonObject json){
						trySave(directoryCache, json, createPath(bookId, "en_us/categories/"+categoryId+".json"));
					}
					@Override public void saveTemplate(ResourceLocation bookId, String templateId, JsonObject json){
						trySave(directoryCache, json, createPath(bookId, "en_us/templates/"+templateId+".json"));
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
	private static String i18nText(String type, String subtype, ResourceLocation id, String s){
		return type+"."+id.getNamespace()+"."+subtype+"."+id.getPath()+"."+s;
	}
}
