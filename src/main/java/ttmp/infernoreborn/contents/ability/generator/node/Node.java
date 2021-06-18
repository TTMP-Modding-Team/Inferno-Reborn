package ttmp.infernoreborn.contents.ability.generator.node;

import com.google.gson.JsonElement;
import net.minecraft.util.ResourceLocation;

public interface Node{
	JsonElement serialize();

	static String toSerializedString(ResourceLocation resourceLocation){
		return resourceLocation.getNamespace().equals("minecraft") ? resourceLocation.getPath() : resourceLocation.toString();
	}
}
