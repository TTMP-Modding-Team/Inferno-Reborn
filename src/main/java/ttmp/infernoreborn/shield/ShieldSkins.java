package ttmp.infernoreborn.shield;

import net.minecraft.util.ResourceLocation;
import ttmp.infernoreborn.api.shield.ShieldSkin;

import javax.annotation.Nullable;
import java.util.HashMap;
import java.util.Map;

import static ttmp.infernoreborn.api.InfernoRebornApi.MODID;

public final class ShieldSkins{
	private ShieldSkins(){}

	private static final Map<ResourceLocation, ShieldSkin> shieldSkins = new HashMap<>();

	public static final ShieldSkin ERROR = registerNew("error", 14, 14);
	public static final ShieldSkin DEFAULT_ARMOR = registerNew("default_armor", 14, 12);
	public static final ShieldSkin DEFAULT_CURIO = registerNew("default_curio", 12, 12);

	public static final ShieldSkin SHIELD_RING = registerNew("shield_ring", "generic", 14, 14);

	public static final ShieldSkin CRIMSON = registerNew("crimson", 12, 14);
	public static final ShieldSkin THANATOS = registerNew("thanatos", "error", 14, 14);

	@Nullable public static ShieldSkin get(ResourceLocation id){
		return shieldSkins.get(id);
	}

	public static ShieldSkin getOrError(ResourceLocation id){
		return shieldSkins.getOrDefault(id, ERROR);
	}

	public static void register(ShieldSkin skin){
		if(shieldSkins.putIfAbsent(skin.id, skin)!=null)
			throw new IllegalStateException("ShieldSkin with duplicated ID '"+skin.id+"'");
	}

	private static ShieldSkin registerNew(String id, int textureWidth, int textureHeight){
		ShieldSkin skin = new ShieldSkin(new ResourceLocation(MODID, id), textureWidth, textureHeight);
		register(skin);
		return skin;
	}
	private static ShieldSkin registerNew(String id, String texture, int textureWidth, int textureHeight){
		ShieldSkin skin = new ShieldSkin(new ResourceLocation(MODID, id), new ResourceLocation(MODID, "textures/shield/"+texture+".png"), textureWidth, textureHeight);
		register(skin);
		return skin;
	}
}
