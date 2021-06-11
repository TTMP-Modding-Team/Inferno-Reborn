package ttmp.infernoreborn.datagen;

import net.minecraft.data.DataGenerator;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.client.model.generators.ItemModelBuilder;
import net.minecraftforge.client.model.generators.ItemModelProvider;
import net.minecraftforge.common.data.ExistingFileHelper;
import ttmp.infernoreborn.contents.ModItems;
import ttmp.infernoreborn.util.EssenceType;

import java.util.Objects;

import static ttmp.infernoreborn.InfernoReborn.MODID;

public class ItemModelGen extends ItemModelProvider{
	public ItemModelGen(DataGenerator generator, ExistingFileHelper existingFileHelper){
		super(generator, MODID, existingFileHelper);
	}

	@Override protected void registerModels(){
		for(EssenceType type : EssenceType.values()){
			item(Objects.requireNonNull(type.getShardItem().getRegistryName()).getPath(),
					new ResourceLocation(MODID, "item/essence/"+type.id+"_shard"));
			item(Objects.requireNonNull(type.getCrystalItem().getRegistryName()).getPath(),
					new ResourceLocation(MODID, "item/essence/"+type.id+"_crystal"));
			item(Objects.requireNonNull(type.getGreaterShardItem().getRegistryName()).getPath(),
					new ResourceLocation(MODID, "item/essence/greater_"+type.id+"_crystal"));
		}
		item(ModItems.ESSENCE_HOLDER.getId().getPath(), new ResourceLocation(MODID, "item/essence_holder"));
		item(ModItems.BOOK_OF_THE_UNSPEAKABLE.getId().getPath(), new ResourceLocation(MODID, "item/book_of_the_unspeakable"));
	}

	protected ItemModelBuilder item(String name, ResourceLocation... textures){
		ItemModelBuilder b = withExistingParent(name, new ResourceLocation("item/generated"));
		for(int i = 0; i<textures.length; i++)
			b.texture("layer"+i, textures[i]);
		return b;
	}
}
