package ttmp.infernoreborn.contents.sigil;

import com.google.common.collect.ListMultimap;
import net.minecraft.entity.ai.attributes.Attribute;
import net.minecraft.entity.ai.attributes.AttributeModifier;
import net.minecraft.inventory.EquipmentSlotType;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraftforge.registries.ForgeRegistryEntry;
import ttmp.infernoreborn.contents.sigil.context.SigilEventContext;

import javax.annotation.Nullable;
import java.util.Objects;

public class Sigil extends ForgeRegistryEntry<Sigil>{
	private final int brighterColor, darkerColor;
	private final int point;

	public Sigil(Properties properties){
		this.brighterColor = properties.brighterColor;
		this.darkerColor = properties.darkerColor;
		this.point = properties.point;
	}

	public int getBrighterColor(){
		return brighterColor;
	}
	public int getDarkerColor(){
		return darkerColor;
	}

	public int getPoint(){
		return point;
	}

	public String getUnlocalizedName(){
		ResourceLocation n = Objects.requireNonNull(getRegistryName());
		return "sigil."+n.getNamespace()+"."+n.getPath();
	}

	public ITextComponent getName(){
		return new TranslationTextComponent(getUnlocalizedName());
	}

	public boolean canBeAttachedTo(SigilEventContext context){
		return true;
	}

	public void applyAttributes(SigilEventContext ctx, @Nullable EquipmentSlotType equipmentSlotType, ListMultimap<Attribute, AttributeModifier> attributes){}

	@Override public boolean equals(Object obj){
		if(obj==this) return true;
		if(!(obj instanceof Sigil)) return false;
		return Objects.equals(((Sigil)obj).getRegistryName(), this.getRegistryName());
	}

	@Override public int hashCode(){
		ResourceLocation n = getRegistryName();
		return n==null ? 0 : n.hashCode();
	}

	@Override public String toString(){
		return getUnlocalizedName();
	}

	@Nullable private ResourceLocation sigilTextureLocation;

	public ResourceLocation getSigilTextureLocation(){
		if(sigilTextureLocation==null){
			synchronized(this){
				if(sigilTextureLocation==null)
					sigilTextureLocation = createSigilTextureLocation();
			}
		}
		return sigilTextureLocation;
	}

	protected ResourceLocation createSigilTextureLocation(){
		ResourceLocation id = Objects.requireNonNull(this.getRegistryName());
		return new ResourceLocation(id.getNamespace(), "textures/sigil/"+id.getPath()+".png");
	}

	public static final class Properties{
		private final int brighterColor, darkerColor;
		private final int point;

		public Properties(int brighterColor, int darkerColor, int point){
			if(point<0) throw new IllegalArgumentException("point");
			this.brighterColor = brighterColor;
			this.darkerColor = darkerColor;
			this.point = point;
		}
	}
}
