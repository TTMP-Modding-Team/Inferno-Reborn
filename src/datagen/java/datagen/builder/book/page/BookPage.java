package datagen.builder.book.page;

import com.google.gson.JsonObject;
import net.minecraft.util.ResourceLocation;

import javax.annotation.Nullable;

public abstract class BookPage{
	@Nullable private ResourceLocation advancement;
	@Nullable private String anchor;

	public abstract String type();

	public BookPage advancement(ResourceLocation advancement){
		this.advancement = advancement;
		return this;
	}
	public BookPage anchor(String anchor){
		this.anchor = anchor;
		return this;
	}

	public JsonObject serialize(){
		JsonObject o = new JsonObject();
		o.addProperty("type", type());
		if(advancement!=null) o.addProperty("advancement", advancement.toString());
		if(anchor!=null) o.addProperty("anchor", anchor);
		doSerialize(o);
		return o;
	}

	protected abstract void doSerialize(JsonObject object);
}
