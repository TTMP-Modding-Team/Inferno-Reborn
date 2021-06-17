package ttmp.infernoreborn.sigil;

import com.google.common.collect.ListMultimap;
import net.minecraft.entity.ai.attributes.Attribute;
import net.minecraft.entity.ai.attributes.AttributeModifier;
import net.minecraft.inventory.EquipmentSlotType;
import net.minecraft.item.Item;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraftforge.registries.ForgeRegistryEntry;
import ttmp.infernoreborn.sigil.context.SigilEventContext;

import javax.annotation.Nullable;
import java.util.Objects;
import java.util.function.Predicate;

public class Sigil extends ForgeRegistryEntry<Sigil>{
	private final int brighterColor, darkerColor;
	private final int point;

	@Nullable private final Item item;
	@Nullable private final Predicate<SigilEventContext> restriction;

	public Sigil(Properties properties){
		this.brighterColor = properties.brighterColor;
		this.darkerColor = properties.darkerColor;
		this.point = properties.point;
		this.item = properties.item;
		this.restriction = properties.restriction;
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

	@Nullable public Item getItem(){
		return item;
	}

	public boolean canBeAttachedTo(SigilEventContext context){
		return restriction==null||restriction.test(context);
	}

	public void applyAttributes(SigilEventContext ctx, @Nullable EquipmentSlotType equipmentSlotType, ListMultimap<Attribute, AttributeModifier> modifierMap){}

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

	public static final class Properties{
		private final int brighterColor, darkerColor;
		private final int point;

		@Nullable private Item item;
		@Nullable private Predicate<SigilEventContext> restriction;

		public Properties(int brighterColor, int darkerColor, int point){
			if(point<0) throw new IllegalArgumentException("point");
			this.brighterColor = brighterColor;
			this.darkerColor = darkerColor;
			this.point = point;
		}

		public Properties item(Item item){
			this.item = item;
			return this;
		}

		public Properties restrict(Predicate<SigilEventContext> restriction){
			this.restriction = restriction;
			return this;
		}
	}
}
