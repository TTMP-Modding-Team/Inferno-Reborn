package datagen;

import net.minecraft.client.renderer.model.BlockModel;
import net.minecraft.data.DataGenerator;
import net.minecraft.item.Item;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.client.model.generators.ItemModelBuilder;
import net.minecraftforge.client.model.generators.ItemModelProvider;
import net.minecraftforge.client.model.generators.ModelBuilder;
import net.minecraftforge.client.model.generators.ModelFile.UncheckedModelFile;
import net.minecraftforge.common.data.ExistingFileHelper;
import ttmp.infernoreborn.api.essence.EssenceType;
import ttmp.infernoreborn.contents.ModItems;

import java.util.Objects;

import static ttmp.infernoreborn.api.InfernoRebornApi.MODID;

public class ItemModelGen extends ItemModelProvider{
	public ItemModelGen(DataGenerator generator, ExistingFileHelper existingFileHelper){
		super(generator, MODID, existingFileHelper);
	}

	@Override protected void registerModels(){
		item(ModItems.BOOK_OF_THE_UNSPEAKABLE.getId().getPath(), new ResourceLocation(MODID, "item/book_of_the_unspeakable"));
		item(ModItems.BOOK_OF_THE_UNSPEAKABLE_COMBINED.getId().getPath(),
				new ResourceLocation(MODID, "item/book_of_the_unspeakable_combined"),
				new ResourceLocation(MODID, "item/book_of_the_unspeakable_combined_2"));
		item(ModItems.ESSENCE_HOLDER.getId().getPath(),
				new ResourceLocation(MODID, "item/essence_holder_0"),
				new ResourceLocation(MODID, "item/essence_holder_1"));
		simpleItem(ModItems.HEART_CRYSTAL.get());
		held(ModItems.EXPLOSIVE_SWORD.getId().getPath(), new ResourceLocation(MODID, "item/explosive_sword"))
				.override()
				.predicate(new ResourceLocation("using"), 1)
				.model(new UncheckedModelFile(new ResourceLocation(MODID, "item/explosive_sword_using")))
				.end();
		held("explosive_sword_using", new ResourceLocation(MODID, "item/explosive_sword"))
				.transforms()
				.transform(ModelBuilder.Perspective.FIRSTPERSON_RIGHT)
				.rotation(0, -90, 14)
				.translation(-9, 13, 2.25f)
				.scale(.68f)
				.end()
				.transform(ModelBuilder.Perspective.FIRSTPERSON_LEFT)
				.rotation(0, -90, 14)
				.translation(-9, 13, 2.25f)
				.scale(.68f)
				.end();

		simpleItem(ModItems.TERRASTONE_HEADGEAR.get());
		simpleItem(ModItems.TERRASTONE_CHESTPLATE.get());
		simpleItem(ModItems.TERRASTONE_LEGGINGS.get());
		simpleItem(ModItems.TERRASTONE_BOOTS.get());

		simpleHeld(ModItems.CRIMSON_CLAYMORE.get());
		simpleItem(ModItems.CRIMSON_CHESTPLATE.get());
		simpleItem(ModItems.CRIMSON_LEGGINGS.get());
		simpleItem(ModItems.CRIMSON_BOOTS.get());

		simpleHeld(ModItems.DRAGON_SLAYER.get());
		simpleItem(ModItems.BERSERKER_HELMET.get());
		simpleItem(ModItems.BERSERKER_CHESTPLATE.get());
		simpleItem(ModItems.BERSERKER_LEGGINGS.get());
		simpleItem(ModItems.BERSERKER_BOOTS.get());

		simpleItem(ModItems.THANATOS_LIGHT_HELMET.get());
		simpleItem(ModItems.THANATOS_LIGHT_CHESTPLATE.get());
		simpleItem(ModItems.THANATOS_LIGHT_LEGGINGS.get());
		simpleItem(ModItems.THANATOS_LIGHT_BOOTS.get());

		simpleItem(ModItems.THANATOS_HEAVY_HELMET.get());
		simpleItem(ModItems.THANATOS_HEAVY_CHESTPLATE.get());
		simpleItem(ModItems.THANATOS_HEAVY_LEGGINGS.get());
		simpleItem(ModItems.THANATOS_HEAVY_BOOTS.get());

		simpleItem(ModItems.THANATOS_BELT.get());
		simpleItem(ModItems.CLOUD_SCARF.get());

		withExistingParent(ModItems.GOLDEN_SKULL.getId().getPath(), "item/template_skull");
		item(ModItems.JUDGEMENT.getId().getPath(), new ResourceLocation(MODID, "item/judgement"))
				.override()
				.predicate(new ResourceLocation("off"), 1)
				.model(item("item/judgement_off", new ResourceLocation(MODID, "item/judgement_off")));

		simpleItem(ModItems.NORMAL_RING.get());
		simpleItem(ModItems.SHIELD_RING_1.get());
		simpleItem(ModItems.BATTLE_MITTS.get());

		for(EssenceType type : EssenceType.values()){
			item(Objects.requireNonNull(type.getEssenceItem().getRegistryName()).getPath(),
					new ResourceLocation(MODID, "item/essence/"+type.id));
			item(Objects.requireNonNull(type.getGreaterEssenceItem().getRegistryName()).getPath(),
					new ResourceLocation(MODID, "item/greater_essence/"+type.id));
			item(Objects.requireNonNull(type.getExquisiteEssenceItem().getRegistryName()).getPath(),
					new ResourceLocation(MODID, "item/exquisite_essence/"+type.id));
		}

		simpleItem(ModItems.PYRITE_INGOT.get());
		simpleItem(ModItems.PYRITE_NUGGET.get());

		simpleItem(ModItems.CRIMSON_METAL_SCRAP.get());

		simpleItem(ModItems.DAMASCUS_STEEL_INGOT.get());

		simpleItem(ModItems.TERRASTONE.get());

		simpleItem(ModItems.ESSENCE_NET_ACCESSOR.get())
				.override().predicate(new ResourceLocation("no_network"), 1)
				.model(item("item/essence_net_accessor_no_network", new ResourceLocation(MODID, "item/essence_net_accessor_no_network")));
		getBuilder(ModItems.ESSENCE_NET_IMPORTER.getId().getPath()).parent(new UncheckedModelFile(new ResourceLocation(MODID, "block/essence_net_importer/essence_net_importer")))
				.override().predicate(new ResourceLocation("no_network"), 1)
				.model(getBuilder("essence_net_importer_no_network").parent(new UncheckedModelFile(new ResourceLocation(MODID, "block/essence_net_importer/essence_net_importer_no_network"))));
		getBuilder(ModItems.ESSENCE_NET_EXPORTER.getId().getPath()).parent(new UncheckedModelFile(new ResourceLocation(MODID, "block/essence_net_exporter/essence_net_exporter")))
				.override().predicate(new ResourceLocation("no_network"), 1)
				.model(getBuilder("essence_net_exporter_no_network").parent(new UncheckedModelFile(new ResourceLocation(MODID, "block/essence_net_exporter/essence_net_exporter_no_network"))));

		simpleItem(ModItems.SIGIL.get());
		simpleItem(ModItems.BODY_SIGIL.get());
		getBuilder(ModItems.SIGIL_ICON.getId().getPath())
				.parent(new UncheckedModelFile("builtin/entity"))
				.guiLight(BlockModel.GuiLight.FRONT)
				.transforms()
				.transform(ModelBuilder.Perspective.GROUND)
				.rotation(0, 0, 0)
				.translation(0, 2, 0)
				.scale(.5f, .5f, .5f).end()
				.transform(ModelBuilder.Perspective.HEAD)
				.rotation(0, 180, 0)
				.translation(0, 13, 7)
				.scale(1, 1, 1).end()
				.transform(ModelBuilder.Perspective.THIRDPERSON_RIGHT)
				.rotation(0, 0, 0)
				.translation(0, 3, 1)
				.scale(.55f, .55f, .55f).end()
				.transform(ModelBuilder.Perspective.FIRSTPERSON_RIGHT)
				.rotation(0, -90, 25)
				.translation(1.13f, 3.2f, 1.13f)
				.scale(.68f, .68f, .68f).end()
				.transform(ModelBuilder.Perspective.FIXED)
				.rotation(0, 180, 0)
				.scale(1, 1, 1).end();
	}

	protected ItemModelBuilder simpleItem(Item item){
		ResourceLocation id = Objects.requireNonNull(item.getRegistryName());
		return item(id.getPath(), new ResourceLocation(id.getNamespace(), "item/"+id.getPath()));
	}
	protected ItemModelBuilder simpleHeld(Item item){
		ResourceLocation id = Objects.requireNonNull(item.getRegistryName());
		return held(id.getPath(), new ResourceLocation(id.getNamespace(), "item/"+id.getPath()));
	}
	protected ItemModelBuilder item(String name, ResourceLocation... textures){
		ItemModelBuilder b = withExistingParent(name, new ResourceLocation("item/generated"));
		for(int i = 0; i<textures.length; i++)
			b.texture("layer"+i, textures[i]);
		return b;
	}
	protected ItemModelBuilder held(String name, ResourceLocation... textures){
		ItemModelBuilder b = withExistingParent(name, new ResourceLocation("item/handheld"));
		for(int i = 0; i<textures.length; i++)
			b.texture("layer"+i, textures[i]);
		return b;
	}
}
