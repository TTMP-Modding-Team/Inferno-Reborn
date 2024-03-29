package datagen;

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
import ttmp.infernoreborn.contents.block.ModProperties;

import java.util.Arrays;

import static net.minecraft.state.properties.BlockStateProperties.HORIZONTAL_FACING;
import static net.minecraft.state.properties.BlockStateProperties.LIT;
import static ttmp.infernoreborn.api.InfernoRebornApi.MODID;
import static ttmp.infernoreborn.contents.block.ModProperties.*;

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

		simpleItemAndBlock(ModBlocks.SIGIL_ENGRAVING_TABLE_3X3.get(), simpleBottomTop(ModBlocks.SIGIL_ENGRAVING_TABLE_3X3.getId().getPath(), "block/sigil_engraving_table_3x3/"));
		simpleItemAndBlock(ModBlocks.SIGIL_ENGRAVING_TABLE_5X5.get(), simpleBottomTop(ModBlocks.SIGIL_ENGRAVING_TABLE_5X5.getId().getPath(), "block/sigil_engraving_table_5x5/"));
		simpleItemAndBlock(ModBlocks.SIGIL_ENGRAVING_TABLE_7X7.get(), simpleBottomTop(ModBlocks.SIGIL_ENGRAVING_TABLE_7X7.getId().getPath(), "block/sigil_engraving_table_7x7/"));
		simpleItemAndBlock(ModBlocks.STIGMA_TABLE_5X5.get(), simpleBottomTop(ModBlocks.STIGMA_TABLE_5X5.getId().getPath(), "block/stigma_table_5x5/"));
		simpleItemAndBlock(ModBlocks.STIGMA_TABLE_7X7.get(), simpleBottomTop(ModBlocks.STIGMA_TABLE_7X7.getId().getPath(), "block/stigma_table_7x7/"));
		simpleItemAndBlock(ModBlocks.SIGIL_SCRAPPER.get(), simpleBottomTop(ModBlocks.SIGIL_SCRAPPER.getId().getPath(), "block/sigil_scrapper/"));

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
				ConfiguredModel.builder().modelFile(existing(res("block/foundry/mold_1")))
						.rotationY(state.getValue(HORIZONTAL_FACING).get2DDataValue()*90-90).build());
		getVariantBuilder(ModBlocks.FOUNDRY_MOLD_2.get()).forAllStates(state ->
				ConfiguredModel.builder().modelFile(existing(res("block/foundry/mold_2")))
						.rotationY(state.getValue(HORIZONTAL_FACING).get2DDataValue()*90+90).build());

		ExistingModelFile crucible = existing(res("block/crucible"));
		ExistingModelFile crucibleAutomated = existing(res("block/crucible_automated"));
		ExistingModelFile crucibleCampfire = existing(res("block/crucible_campfire"));
		ExistingModelFile crucibleCampfireOff = existing(res("block/crucible_campfire_off"));
		ExistingModelFile crucibleCampfireAutomated = existing(res("block/crucible_campfire_automated"));
		ExistingModelFile crucibleCampfireAutomatedOff = existing(res("block/crucible_campfire_automated_off"));

		simpleBlockItem(ModBlocks.CRUCIBLE.get(), crucible);
		getVariantBuilder(ModBlocks.CRUCIBLE.get()).forAllStates(state ->
				ConfiguredModel.builder().modelFile(state.getValue(AUTOMATED) ? crucibleAutomated : crucible).build());
		simpleBlockItem(ModBlocks.CRUCIBLE_CAMPFIRE.get(), crucibleCampfire);
		getVariantBuilder(ModBlocks.CRUCIBLE_CAMPFIRE.get()).forAllStates(state ->
				ConfiguredModel.builder().modelFile(state.getValue(LIT) ?
								state.getValue(AUTOMATED) ? crucibleCampfireAutomated : crucibleCampfire :
								state.getValue(AUTOMATED) ? crucibleCampfireAutomatedOff : crucibleCampfireOff)
						.rotationY(((int)state.getValue(HORIZONTAL_FACING).toYRot())%360).build());

		ExistingModelFile crucibleAU = existing(res("block/automation_unit/base"));
		ExistingModelFile crucibleAUExtension = existing(res("block/automation_unit/top_extension"));
		ExistingModelFile crucibleAUSideExtension = existing(res("block/automation_unit/side_extension"));
		ExistingModelFile crucibleAUOutput = existing(res("block/automation_unit/output"));
		ExistingModelFile crucibleAUJoint = existing(res("block/automation_unit/joint"));

		simpleBlockItem(ModBlocks.CRUCIBLE_AUTOMATION_UNIT.get(), crucibleAU);
		getMultipartBuilder(ModBlocks.CRUCIBLE_AUTOMATION_UNIT.get())
				.part().modelFile(crucibleAU).addModel().end()
				.part().modelFile(crucibleAUJoint).addModel()
				.nestedGroup().condition(MODULE_U, false).end()
				.nestedGroup().useOr()
				.condition(MODULE_N, true)
				.condition(MODULE_S, true)
				.condition(MODULE_E, true)
				.condition(MODULE_W, true)
				.end().end()
				.part().modelFile(crucibleAUExtension).addModel()
				.condition(MODULE_U, true).end()
				.part().modelFile(crucibleAUSideExtension).addModel()
				.condition(MODULE_N, true).end()
				.part().modelFile(crucibleAUSideExtension).rotationY(180).addModel()
				.condition(MODULE_S, true).end()
				.part().modelFile(crucibleAUSideExtension).rotationY(90).addModel()
				.condition(MODULE_E, true).end()
				.part().modelFile(crucibleAUSideExtension).rotationY(270).addModel()
				.condition(MODULE_W, true).end()
				.part().modelFile(crucibleAUOutput).addModel()
				.condition(OUT_N, true).end()
				.part().modelFile(crucibleAUOutput).rotationY(180).addModel()
				.condition(OUT_S, true).end()
				.part().modelFile(crucibleAUOutput).rotationY(90).addModel()
				.condition(OUT_E, true).end()
				.part().modelFile(crucibleAUOutput).rotationY(270).addModel()
				.condition(OUT_W, true).end();

		stove(ModBlocks.FURNACE_STOVE.get(), "stove/furnace");
		stove(ModBlocks.FOUNDRY_STOVE.get(), "stove/foundry");
		stove(ModBlocks.NETHER_STOVE.get(), "stove/nether");
		stove(ModBlocks.ESSENCE_STOVE.get(), "stove/essence");

		simpleItemAndBlock(ModBlocks.ESSENCE_HOLDER_BLOCK.get(), existing(res("block/essence_holder")));
		simpleItemAndBlock(ModBlocks.ESSENCE_NET_CORE.get(), existing(res("block/essence_net_core")));
		getVariantBuilder(ModBlocks.ESSENCE_NET_IMPORTER.get()).forAllStates(state -> ConfiguredModel.builder()
				.modelFile(state.getValue(ModProperties.NO_NETWORK) ?
						models().cubeAll("block/essence_net_importer/essence_net_importer_no_network", res("block/essence_net_importer/essence_net_importer_no_network")) :
						models().cubeAll("block/essence_net_importer/essence_net_importer", res("block/essence_net_importer/essence_net_importer")))
				.build());
		directionalBlock(ModBlocks.ESSENCE_NET_EXPORTER.get(), state -> {
			boolean noNetwork = state.getValue(ModProperties.NO_NETWORK);
			boolean accelerated = state.getValue(ModProperties.ACCELERATED);
			boolean hasFilter = state.getValue(ModProperties.HAS_FILTER);
			String top = noNetwork ?
					accelerated ?
							"block/essence_net_exporter/essence_net_exporter_accelerated_no_network" :
							"block/essence_net_exporter/essence_net_exporter_no_network" :
					accelerated ?
							"block/essence_net_exporter/essence_net_exporter_accelerated" :
							"block/essence_net_exporter/essence_net_exporter";
			String side = hasFilter ?
					"block/essence_net_exporter/essence_net_exporter_side_filter" :
					"block/essence_net_exporter/essence_net_exporter_side";
			String name = "block/essence_net_exporter/essence_net_exporter";
			if(noNetwork) name += "_no_network";
			if(accelerated) name += "_accelerated";
			if(hasFilter) name += "_filtered";
			return models().cubeBottomTop(name, res(side), res("block/essence_net_exporter/essence_net_exporter_side"), res(top));
		});

		simpleBlock(ModBlocks.GOLDEN_SKULL.get(), existing(new ResourceLocation("block/skull")));
		simpleBlock(ModBlocks.GOLDEN_WALL_SKULL.get(), existing(new ResourceLocation("block/skull")));
	}

	private static ResourceLocation res(String path){
		return new ResourceLocation(MODID, path);
	}

	private void simpleItemAndBlock(Block block, ModelFile modelFile){
		simpleBlock(block, modelFile);
		simpleBlockItem(block, modelFile);
	}

	private ModelFile simpleBottomTop(String modelName, String textureBaseName){
		return models().cubeBottomTop(modelName, res(textureBaseName+"side"), res(textureBaseName+"bottom"), res(textureBaseName+"top"));
	}

	private ExistingModelFile existing(ResourceLocation location){
		return new ExistingModelFile(location, existingFileHelper);
	}

	private void stove(Block block, String baseName){
		ResourceLocation bottom = res("block/"+baseName+"/bottom");
		ResourceLocation top = res("block/"+baseName+"/top");
		ModelFile offModel = models().cubeBottomTop(baseName, res("block/"+baseName+"/side"), bottom, top);
		ModelFile onModel = models().cubeBottomTop(baseName+"_on", res("block/"+baseName+"/side_on"), bottom, top);
		getVariantBuilder(block).forAllStates(state -> ConfiguredModel.builder().modelFile(state.getValue(LIT) ? onModel : offModel).build());
		simpleBlockItem(block, offModel);
	}
}
