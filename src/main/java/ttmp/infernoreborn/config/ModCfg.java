package ttmp.infernoreborn.config;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;
import net.minecraft.item.Items;
import net.minecraft.server.MinecraftServer;
import net.minecraft.world.storage.FolderName;
import net.minecraftforge.common.ForgeConfigSpec;
import net.minecraftforge.common.ForgeConfigSpec.IntValue;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.ModLoadingContext;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.config.ModConfig;
import net.minecraftforge.fml.event.server.FMLServerAboutToStartEvent;
import ttmp.infernoreborn.InfernoReborn;
import ttmp.infernoreborn.contents.ModItems;
import ttmp.infernoreborn.infernaltype.InfernalTypes;

import javax.annotation.Nullable;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.util.Collections;

import static ttmp.infernoreborn.api.InfernoRebornApi.MODID;

@Mod.EventBusSubscriber(modid = MODID)
public final class ModCfg{
	private ModCfg(){}

	private static IntValue maxHeartCrystals;

	private static final Gson GSON = new GsonBuilder()
			.setPrettyPrinting()
			.create();

	private static SigilHolderConfig sigilHolderConfig;

	public static int maxHeartCrystals(){
		return maxHeartCrystals.get();
	}

	public static SigilHolderConfig sigilHolderConfig(){
		return sigilHolderConfig;
	}

	public static void init(){
		ForgeConfigSpec.Builder server = new ForgeConfigSpec.Builder();
		maxHeartCrystals = server.comment("Maximum amount of Heart Crystal you can consume.").defineInRange("maxHeartCrystals", 10, 0, Integer.MAX_VALUE);
		ModLoadingContext.get().registerConfig(ModConfig.Type.SERVER, server.build());

		InfernalTypes.load();
	}

	@SubscribeEvent
	public static void onServerAboutToStart(FMLServerAboutToStartEvent event){
		MinecraftServer server = event.getServer();
		Path serverConfig = server.getWorldPath(new FolderName("serverconfig"));
		Path config = serverConfig.resolve("infernoreborn/sigil_holders.json");
		try{
			Path parent = config.getParent();
			if(!Files.exists(parent)) Files.createDirectory(parent);
			boolean write;
			if(!Files.exists(config)){
				sigilHolderConfig = getDefaultSigilHolderConfig();
				write = true;
			}else{
				sigilHolderConfig = new SigilHolderConfig();
				try(InputStream in = Files.newInputStream(config);
				    BufferedReader r = new BufferedReader(new InputStreamReader(in, StandardCharsets.UTF_8))){
					sigilHolderConfig.read(GSON.fromJson(r, JsonObject.class));
				}
				write = sigilHolderConfig.appendMissingEntry(getDefaultSigilHolderConfig());
			}
			if(write)
				Files.write(config, Collections.singleton(GSON.toJson(sigilHolderConfig.write())), new StandardOpenOption[]{
						StandardOpenOption.CREATE, StandardOpenOption.WRITE, StandardOpenOption.TRUNCATE_EXISTING
				});
		}catch(IOException e){
			InfernoReborn.LOGGER.error("Cannot read or write sigil holder config file", e);
			sigilHolderConfig = getDefaultSigilHolderConfig();
		}
	}

	@Nullable private static SigilHolderConfig defaultSigilHolderConfig;

	private static SigilHolderConfig getDefaultSigilHolderConfig(){
		if(defaultSigilHolderConfig==null){
			defaultSigilHolderConfig = new SigilHolderConfig();

			// Armors
			defaultSigilHolderConfig.setMaxPoints(Items.LEATHER_HELMET, 3);
			defaultSigilHolderConfig.setMaxPoints(Items.LEATHER_CHESTPLATE, 3);
			defaultSigilHolderConfig.setMaxPoints(Items.LEATHER_LEGGINGS, 3);
			defaultSigilHolderConfig.setMaxPoints(Items.LEATHER_BOOTS, 3);
			defaultSigilHolderConfig.setMaxPoints(Items.CHAINMAIL_HELMET, 4);
			defaultSigilHolderConfig.setMaxPoints(Items.CHAINMAIL_CHESTPLATE, 4);
			defaultSigilHolderConfig.setMaxPoints(Items.CHAINMAIL_LEGGINGS, 4);
			defaultSigilHolderConfig.setMaxPoints(Items.CHAINMAIL_BOOTS, 4);
			defaultSigilHolderConfig.setMaxPoints(Items.IRON_HELMET, 4);
			defaultSigilHolderConfig.setMaxPoints(Items.IRON_CHESTPLATE, 4);
			defaultSigilHolderConfig.setMaxPoints(Items.IRON_LEGGINGS, 4);
			defaultSigilHolderConfig.setMaxPoints(Items.IRON_BOOTS, 4);
			defaultSigilHolderConfig.setMaxPoints(Items.GOLDEN_HELMET, 8);
			defaultSigilHolderConfig.setMaxPoints(Items.GOLDEN_CHESTPLATE, 8);
			defaultSigilHolderConfig.setMaxPoints(Items.GOLDEN_LEGGINGS, 8);
			defaultSigilHolderConfig.setMaxPoints(Items.GOLDEN_BOOTS, 8);
			defaultSigilHolderConfig.setMaxPoints(Items.DIAMOND_HELMET, 7);
			defaultSigilHolderConfig.setMaxPoints(Items.DIAMOND_CHESTPLATE, 7);
			defaultSigilHolderConfig.setMaxPoints(Items.DIAMOND_LEGGINGS, 7);
			defaultSigilHolderConfig.setMaxPoints(Items.DIAMOND_BOOTS, 7);
			defaultSigilHolderConfig.setMaxPoints(Items.NETHERITE_HELMET, 9);
			defaultSigilHolderConfig.setMaxPoints(Items.NETHERITE_CHESTPLATE, 9);
			defaultSigilHolderConfig.setMaxPoints(Items.NETHERITE_LEGGINGS, 9);
			defaultSigilHolderConfig.setMaxPoints(Items.NETHERITE_BOOTS, 9);
			defaultSigilHolderConfig.setMaxPoints(Items.ELYTRA, 4);
			defaultSigilHolderConfig.setMaxPoints(Items.TURTLE_HELMET, 3);

			// TOols
			defaultSigilHolderConfig.setMaxPoints(Items.WOODEN_SWORD, 3);
			defaultSigilHolderConfig.setMaxPoints(Items.WOODEN_PICKAXE, 3);
			defaultSigilHolderConfig.setMaxPoints(Items.WOODEN_AXE, 3);
			defaultSigilHolderConfig.setMaxPoints(Items.WOODEN_SHOVEL, 3);
			defaultSigilHolderConfig.setMaxPoints(Items.WOODEN_HOE, 3);
			defaultSigilHolderConfig.setMaxPoints(Items.STONE_SWORD, 2);
			defaultSigilHolderConfig.setMaxPoints(Items.STONE_PICKAXE, 2);
			defaultSigilHolderConfig.setMaxPoints(Items.STONE_AXE, 2);
			defaultSigilHolderConfig.setMaxPoints(Items.STONE_SHOVEL, 2);
			defaultSigilHolderConfig.setMaxPoints(Items.STONE_HOE, 2);
			defaultSigilHolderConfig.setMaxPoints(Items.IRON_SWORD, 4);
			defaultSigilHolderConfig.setMaxPoints(Items.IRON_PICKAXE, 4);
			defaultSigilHolderConfig.setMaxPoints(Items.IRON_AXE, 4);
			defaultSigilHolderConfig.setMaxPoints(Items.IRON_SHOVEL, 4);
			defaultSigilHolderConfig.setMaxPoints(Items.IRON_HOE, 4);
			defaultSigilHolderConfig.setMaxPoints(Items.GOLDEN_SWORD, 8);
			defaultSigilHolderConfig.setMaxPoints(Items.GOLDEN_PICKAXE, 8);
			defaultSigilHolderConfig.setMaxPoints(Items.GOLDEN_AXE, 8);
			defaultSigilHolderConfig.setMaxPoints(Items.GOLDEN_SHOVEL, 8);
			defaultSigilHolderConfig.setMaxPoints(Items.GOLDEN_HOE, 8);
			defaultSigilHolderConfig.setMaxPoints(Items.DIAMOND_SWORD, 7);
			defaultSigilHolderConfig.setMaxPoints(Items.DIAMOND_PICKAXE, 7);
			defaultSigilHolderConfig.setMaxPoints(Items.DIAMOND_AXE, 7);
			defaultSigilHolderConfig.setMaxPoints(Items.DIAMOND_SHOVEL, 7);
			defaultSigilHolderConfig.setMaxPoints(Items.DIAMOND_HOE, 7);
			defaultSigilHolderConfig.setMaxPoints(Items.NETHERITE_SWORD, 9);
			defaultSigilHolderConfig.setMaxPoints(Items.NETHERITE_PICKAXE, 9);
			defaultSigilHolderConfig.setMaxPoints(Items.NETHERITE_AXE, 9);
			defaultSigilHolderConfig.setMaxPoints(Items.NETHERITE_SHOVEL, 9);
			defaultSigilHolderConfig.setMaxPoints(Items.NETHERITE_HOE, 9);

			// Shits
			defaultSigilHolderConfig.setMaxPoints(Items.BOW, 3);
			defaultSigilHolderConfig.setMaxPoints(Items.CROSSBOW, 4);
			defaultSigilHolderConfig.setMaxPoints(Items.TRIDENT, 7);
			defaultSigilHolderConfig.setMaxPoints(Items.TRIDENT, 7);
			defaultSigilHolderConfig.setMaxPoints(Items.SHIELD, 4);

			// More Shitz
			defaultSigilHolderConfig.setMaxPoints(ModItems.EXPLOSIVE_SWORD.get(), 13);

			defaultSigilHolderConfig.setMaxPoints(ModItems.CRIMSON_CLAYMORE.get(), 15);
			defaultSigilHolderConfig.setMaxPoints(ModItems.CRIMSON_CHESTPLATE.get(), 15);
			defaultSigilHolderConfig.setMaxPoints(ModItems.CRIMSON_LEGGINGS.get(), 15);
			defaultSigilHolderConfig.setMaxPoints(ModItems.CRIMSON_BOOTS.get(), 15);

			defaultSigilHolderConfig.setMaxPoints(ModItems.DRAGON_SLAYER.get(), 15);
			defaultSigilHolderConfig.setMaxPoints(ModItems.BERSERKER_HELMET.get(), 15);
			defaultSigilHolderConfig.setMaxPoints(ModItems.BERSERKER_CHESTPLATE.get(), 15);
			defaultSigilHolderConfig.setMaxPoints(ModItems.BERSERKER_LEGGINGS.get(), 15);
			defaultSigilHolderConfig.setMaxPoints(ModItems.BERSERKER_BOOTS.get(), 15);

			defaultSigilHolderConfig.setMaxPoints(ModItems.NORMAL_RING.get(), 8);
			defaultSigilHolderConfig.setMaxPoints(ModItems.SHIELD_RING_1.get(), 8);
		}
		return defaultSigilHolderConfig;
	}
}
