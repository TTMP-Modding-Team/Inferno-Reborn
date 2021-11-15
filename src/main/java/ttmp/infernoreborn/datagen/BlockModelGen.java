package ttmp.infernoreborn.datagen;

import net.minecraft.block.Block;
import net.minecraft.data.DataGenerator;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.client.model.generators.BlockModelBuilder;
import net.minecraftforge.client.model.generators.BlockStateProvider;
import net.minecraftforge.client.model.generators.ConfiguredModel;
import net.minecraftforge.client.model.generators.ModelFile;
import net.minecraftforge.client.model.generators.ModelFile.ExistingModelFile;
import net.minecraftforge.common.data.ExistingFileHelper;
import ttmp.infernoreborn.contents.ModBlocks;
import ttmp.infernoreborn.contents.ModItems;

import java.util.Arrays;

import static net.minecraft.state.properties.BlockStateProperties.HORIZONTAL_FACING;
import static net.minecraft.state.properties.BlockStateProperties.LIT;
import static ttmp.infernoreborn.InfernoReborn.MODID;

public class BlockModelGen extends BlockStateProvider{
	private final ExistingFileHelper existingFileHelper;

	public BlockModelGen(DataGenerator gen, ExistingFileHelper existingFileHelper){
		super(gen, MODID, existingFileHelper);
		this.existingFileHelper = existingFileHelper;
	}

	@Override protected void registerStatesAndModels(){
		simpleItemAndBlock(ModBlocks.HEART_CRYSTAL_ORE.get(), models().cubeAll("block/heart_crystal_ore", res("block/heart_crystal_ore")));
		ModelFile[] pyriteOres = new ModelFile[]{
				models().cubeAll("block/pyrite_ore/1", res("block/pyrite_ore/1")),
				models().cubeAll("block/pyrite_ore/2", res("block/pyrite_ore/2")),
				models().cubeAll("block/pyrite_ore/3", res("block/pyrite_ore/3")),
				models().cubeAll("block/pyrite_ore/4", res("block/pyrite_ore/4"))
		};
		simpleBlock(ModBlocks.PYRITE_ORE.get(), Arrays.stream(pyriteOres).map(ConfiguredModel::new).toArray(ConfiguredModel[]::new));
		simpleBlockItem(ModBlocks.PYRITE_ORE.get(), pyriteOres[0]);

		simpleItemAndBlock(ModBlocks.RUNESTONE.get(), models().cubeAll("block/runestone", res("block/runestone")));
		simpleItemAndBlock(ModBlocks.FOUNDRY_TILE.get(), models().cubeAll("block/foundry/tile", res("block/foundry/tile")));

		models().withExistingParent("block/foundry/foundry_tile_base", "cube")
				.texture("down", "#tile")
				.texture("up", "#tile")
				.texture("north", "#face")
				.texture("south", "#face")
				.texture("east", "#tile")
				.texture("west", "#face")
				.texture("particle", "#tile")
				.texture("tile", res("block/foundry/tile"));

		BlockModelBuilder foundryFirebox = models().withExistingParent("block/foundry/firebox", res("block/foundry/foundry_tile_base"))
				.texture("face", res("block/foundry/firebox"));
		BlockModelBuilder foundryFireboxOn = models().withExistingParent("block/foundry/firebox_on", res("block/foundry/foundry_tile_base"))
				.texture("face", res("block/foundry/firebox_on"));
		BlockModelBuilder foundryGrate = models().withExistingParent("block/foundry/grate", res("block/foundry/foundry_tile_base"))
				.texture("face", res("block/foundry/grate"));

		getVariantBuilder(ModBlocks.FOUNDRY.get()).forAllStates(state -> ConfiguredModel.builder()
				.modelFile(state.getValue(LIT) ? foundryFireboxOn : foundryFirebox)
				.uvLock(true)
				.rotationY(state.getValue(HORIZONTAL_FACING).get2DDataValue()*90)
				.build());
		getVariantBuilder(ModBlocks.FOUNDRY_FIREBOX.get()).forAllStates(state -> ConfiguredModel.builder()
				.modelFile(state.getValue(LIT) ? foundryFireboxOn : foundryFirebox)
				.uvLock(true)
				.rotationY(state.getValue(HORIZONTAL_FACING).get2DDataValue()*90)
				.build());
		getVariantBuilder(ModBlocks.FOUNDRY_GRATE_1.get()).forAllStates(state -> ConfiguredModel.builder()
				.modelFile(foundryGrate)
				.uvLock(true)
				.rotationY(state.getValue(HORIZONTAL_FACING).get2DDataValue()*90)
				.build());
		getVariantBuilder(ModBlocks.FOUNDRY_GRATE_2.get()).forAllStates(state -> ConfiguredModel.builder()
				.modelFile(foundryGrate)
				.uvLock(true)
				.rotationY(state.getValue(HORIZONTAL_FACING).get2DDataValue()*90)
				.build());

		getVariantBuilder(ModBlocks.FOUNDRY_MOLD_1.get()).forAllStates(state ->
				ConfiguredModel.builder().modelFile(new ExistingModelFile(res("block/foundry/mold_1"), existingFileHelper))
						.rotationY(state.getValue(HORIZONTAL_FACING).get2DDataValue()*90-90).build());
		getVariantBuilder(ModBlocks.FOUNDRY_MOLD_2.get()).forAllStates(state ->
				ConfiguredModel.builder().modelFile(new ExistingModelFile(res("block/foundry/mold_2"), existingFileHelper))
						.rotationY(state.getValue(HORIZONTAL_FACING).get2DDataValue()*90+90).build());

		ExistingModelFile essenceHolderModel = new ExistingModelFile(res("block/essence_holder"), existingFileHelper);
		simpleBlock(ModBlocks.ESSENCE_HOLDER.get(), essenceHolderModel);
		itemModels().getBuilder(ModItems.ESSENCE_HOLDER_BLOCK.getId().getPath()).parent(essenceHolderModel);

		simpleBlock(ModBlocks.GOLDEN_SKULL.get(), new ExistingModelFile(new ResourceLocation("block/skull"), existingFileHelper));
		simpleBlock(ModBlocks.GOLDEN_WALL_SKULL.get(), new ExistingModelFile(new ResourceLocation("block/skull"), existingFileHelper));
	}

	private static ResourceLocation res(String path){
		return new ResourceLocation(MODID, path);
	}

	private void simpleItemAndBlock(Block block, ModelFile modelFile){
		simpleBlock(block, modelFile);
		simpleBlockItem(block, modelFile);
	}
}
