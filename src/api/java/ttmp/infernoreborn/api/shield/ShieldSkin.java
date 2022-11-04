package ttmp.infernoreborn.api.shield;

import net.minecraft.util.ResourceLocation;

import java.util.Objects;

public final class ShieldSkin{
	public final ResourceLocation id;
	public final ResourceLocation texture;
	public final int textureWidth;
	public final int textureHeight;

	public ShieldSkin(ResourceLocation id, int textureWidth, int textureHeight){
		this(id, new ResourceLocation(id.getNamespace(), "textures/shield/"+id.getPath()+".png"), textureWidth, textureHeight);
	}
	public ShieldSkin(ResourceLocation id, ResourceLocation texture, int textureWidth, int textureHeight){
		if(textureWidth<=0||textureWidth>24) throw new IllegalArgumentException("textureWidth");
		if(textureHeight<=0||textureHeight>24) throw new IllegalArgumentException("textureHeight");
		this.id = Objects.requireNonNull(id);
		this.texture = Objects.requireNonNull(texture);
		this.textureWidth = textureWidth;
		this.textureHeight = textureHeight;
	}

	@Override public boolean equals(Object o){
		if(this==o) return true;
		if(o==null||getClass()!=o.getClass()) return false;
		ShieldSkin that = (ShieldSkin)o;
		return id.equals(that.id);
	}
	@Override public int hashCode(){
		return Objects.hash(id);
	}

	@Override public String toString(){
		return "ShieldSkin{"+
				"id="+id+
				", texture="+texture+
				", textureWidth="+textureWidth+
				", textureHeight="+textureHeight+
				'}';
	}
}
