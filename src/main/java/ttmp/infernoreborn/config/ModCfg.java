package ttmp.infernoreborn.config;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;
import net.minecraft.item.ArmorItem;
import net.minecraft.item.Item;
import net.minecraft.item.Items;
import net.minecraft.item.ShootableItem;
import net.minecraft.item.SwordItem;
import net.minecraft.item.ToolItem;
import net.minecraft.server.MinecraftServer;
import net.minecraft.world.storage.FolderName;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.server.FMLServerAboutToStartEvent;
import net.minecraftforge.registries.ForgeRegistries;
import ttmp.infernoreborn.InfernoReborn;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.util.Collections;

import static ttmp.infernoreborn.InfernoReborn.MODID;

@Mod.EventBusSubscriber(modid = MODID)
public final class ModCfg{
	private ModCfg(){}

	private static final Gson GSON = new GsonBuilder()
			.setPrettyPrinting()
			.create();

	private static SigilHolderConfig sigilHolderConfig;

	public static SigilHolderConfig sigilHolderConfig(){
		return sigilHolderConfig;
	}

	@SubscribeEvent
	public static void onServerAboutToStart(FMLServerAboutToStartEvent event){
		MinecraftServer server = event.getServer();
		Path serverConfig = server.getWorldPath(new FolderName("serverconfig"));
		Path config = serverConfig.resolve("infernoreborn/sigil_holders.json");
		try{
			Path parent = config.getParent();
			if(!Files.exists(parent)) Files.createDirectory(parent);
			if(!Files.exists(config)){
				sigilHolderConfig = createDefaultSigilHolderConfig();
				Files.write(config, Collections.singleton(GSON.toJson(sigilHolderConfig.write())), new StandardOpenOption[]{StandardOpenOption.CREATE_NEW});
			}else{
				sigilHolderConfig = new SigilHolderConfig();
				try(InputStream in = Files.newInputStream(config);
				    BufferedReader r = new BufferedReader(new InputStreamReader(in, StandardCharsets.UTF_8))){
					sigilHolderConfig.read(GSON.fromJson(r, JsonObject.class));
				}
			}
		}catch(IOException e){
			InfernoReborn.LOGGER.error("Cannot read or write sigil holder config file", e);
			sigilHolderConfig = createDefaultSigilHolderConfig();
		}
	}

	private static SigilHolderConfig createDefaultSigilHolderConfig(){
		SigilHolderConfig sigilHolderConfig = new SigilHolderConfig();

		// Armors
		sigilHolderConfig.setMaxPoints(Items.LEATHER_HELMET, 3);
		sigilHolderConfig.setMaxPoints(Items.LEATHER_CHESTPLATE, 3);
		sigilHolderConfig.setMaxPoints(Items.LEATHER_LEGGINGS, 3);
		sigilHolderConfig.setMaxPoints(Items.LEATHER_BOOTS, 3);
		sigilHolderConfig.setMaxPoints(Items.CHAINMAIL_HELMET, 4);
		sigilHolderConfig.setMaxPoints(Items.CHAINMAIL_CHESTPLATE, 4);
		sigilHolderConfig.setMaxPoints(Items.CHAINMAIL_LEGGINGS, 4);
		sigilHolderConfig.setMaxPoints(Items.CHAINMAIL_BOOTS, 4);
		sigilHolderConfig.setMaxPoints(Items.IRON_HELMET, 4);
		sigilHolderConfig.setMaxPoints(Items.IRON_CHESTPLATE, 4);
		sigilHolderConfig.setMaxPoints(Items.IRON_LEGGINGS, 4);
		sigilHolderConfig.setMaxPoints(Items.IRON_BOOTS, 4);
		sigilHolderConfig.setMaxPoints(Items.GOLDEN_HELMET, 8);
		sigilHolderConfig.setMaxPoints(Items.GOLDEN_CHESTPLATE, 8);
		sigilHolderConfig.setMaxPoints(Items.GOLDEN_LEGGINGS, 8);
		sigilHolderConfig.setMaxPoints(Items.GOLDEN_BOOTS, 8);
		sigilHolderConfig.setMaxPoints(Items.DIAMOND_HELMET, 7);
		sigilHolderConfig.setMaxPoints(Items.DIAMOND_CHESTPLATE, 7);
		sigilHolderConfig.setMaxPoints(Items.DIAMOND_LEGGINGS, 7);
		sigilHolderConfig.setMaxPoints(Items.DIAMOND_BOOTS, 7);
		sigilHolderConfig.setMaxPoints(Items.NETHERITE_HELMET, 9);
		sigilHolderConfig.setMaxPoints(Items.NETHERITE_CHESTPLATE, 9);
		sigilHolderConfig.setMaxPoints(Items.NETHERITE_LEGGINGS, 9);
		sigilHolderConfig.setMaxPoints(Items.NETHERITE_BOOTS, 9);
		sigilHolderConfig.setMaxPoints(Items.ELYTRA, 4);

		// TOols
		sigilHolderConfig.setMaxPoints(Items.WOODEN_SWORD, 2);
		sigilHolderConfig.setMaxPoints(Items.WOODEN_PICKAXE, 2);
		sigilHolderConfig.setMaxPoints(Items.WOODEN_AXE, 2);
		sigilHolderConfig.setMaxPoints(Items.WOODEN_SHOVEL, 2);
		sigilHolderConfig.setMaxPoints(Items.WOODEN_HOE, 2);
		sigilHolderConfig.setMaxPoints(Items.STONE_SWORD, 3);
		sigilHolderConfig.setMaxPoints(Items.STONE_PICKAXE, 3);
		sigilHolderConfig.setMaxPoints(Items.STONE_AXE, 3);
		sigilHolderConfig.setMaxPoints(Items.STONE_SHOVEL, 3);
		sigilHolderConfig.setMaxPoints(Items.STONE_HOE, 3);
		sigilHolderConfig.setMaxPoints(Items.IRON_SWORD, 4);
		sigilHolderConfig.setMaxPoints(Items.IRON_PICKAXE, 4);
		sigilHolderConfig.setMaxPoints(Items.IRON_AXE, 4);
		sigilHolderConfig.setMaxPoints(Items.IRON_SHOVEL, 4);
		sigilHolderConfig.setMaxPoints(Items.IRON_HOE, 4);
		sigilHolderConfig.setMaxPoints(Items.GOLDEN_SWORD, 8);
		sigilHolderConfig.setMaxPoints(Items.GOLDEN_PICKAXE, 8);
		sigilHolderConfig.setMaxPoints(Items.GOLDEN_AXE, 8);
		sigilHolderConfig.setMaxPoints(Items.GOLDEN_SHOVEL, 8);
		sigilHolderConfig.setMaxPoints(Items.GOLDEN_HOE, 8);
		sigilHolderConfig.setMaxPoints(Items.DIAMOND_SWORD, 7);
		sigilHolderConfig.setMaxPoints(Items.DIAMOND_PICKAXE, 7);
		sigilHolderConfig.setMaxPoints(Items.DIAMOND_AXE, 7);
		sigilHolderConfig.setMaxPoints(Items.DIAMOND_SHOVEL, 7);
		sigilHolderConfig.setMaxPoints(Items.DIAMOND_HOE, 7);
		sigilHolderConfig.setMaxPoints(Items.NETHERITE_SWORD, 9);
		sigilHolderConfig.setMaxPoints(Items.NETHERITE_PICKAXE, 9);
		sigilHolderConfig.setMaxPoints(Items.NETHERITE_AXE, 9);
		sigilHolderConfig.setMaxPoints(Items.NETHERITE_SHOVEL, 9);
		sigilHolderConfig.setMaxPoints(Items.NETHERITE_HOE, 9);

		sigilHolderConfig.setMaxPoints(Items.BOW, 3);
		sigilHolderConfig.setMaxPoints(Items.CROSSBOW, 4);
		sigilHolderConfig.setMaxPoints(Items.TRIDENT, 7);
		sigilHolderConfig.setMaxPoints(Items.TRIDENT, 7);
		sigilHolderConfig.setMaxPoints(Items.SHIELD, 4);

		for(Item item : ForgeRegistries.ITEMS){
			if(sigilHolderConfig.has(item)) continue;
			if(item instanceof ArmorItem||
					item instanceof ToolItem||
					item instanceof SwordItem||
					item instanceof ShootableItem){
				sigilHolderConfig.setMaxPoints(item, 0);
			}
		}
		return sigilHolderConfig;
	}
}
