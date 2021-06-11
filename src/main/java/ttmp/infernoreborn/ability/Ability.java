package ttmp.infernoreborn.ability;

import net.minecraft.entity.ai.attributes.Attribute;
import net.minecraft.entity.ai.attributes.AttributeModifier;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraftforge.event.entity.living.LivingHurtEvent;
import net.minecraftforge.registries.ForgeRegistryEntry;

import javax.annotation.Nullable;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.UUID;

public class Ability extends ForgeRegistryEntry<Ability>{
	private final int primaryColor, secondaryColor, highlightColor;
	private final Map<Attribute, Set<AttributeModifier>> attributes;

	@Nullable private final OnEvent<LivingHurtEvent> onHurt;

	public Ability(Properties properties){
		this.primaryColor = properties.primaryColor;
		this.secondaryColor = properties.secondaryColor;
		this.highlightColor = properties.highlightColor;
		this.attributes = properties.attributes;
		this.onHurt = properties.onHurt;
	}

	public int getPrimaryColor(){
		return primaryColor;
	}
	public int getSecondaryColor(){
		return secondaryColor;
	}
	public int getHighlightColor(){
		return highlightColor;
	}

	public Map<Attribute, Set<AttributeModifier>> getAttributes(){
		return attributes;
	}

	@Nullable public OnEvent<LivingHurtEvent> onHurt(){
		return onHurt;
	}

	public TranslationTextComponent getName(){
		return new TranslationTextComponent(getUnlocalizedName());
	}

	public String getUnlocalizedName(){
		ResourceLocation n = Objects.requireNonNull(getRegistryName());
		return "ability."+n.getNamespace()+"."+n.getPath();
	}

	@Override public int hashCode(){
		ResourceLocation name = getRegistryName();
		return name!=null ? name.hashCode() : 0;
	}
	@Override public boolean equals(Object obj){
		if(obj==this) return true;
		if(!(obj instanceof Ability)) return false;
		return Objects.equals(((Ability)obj).getRegistryName(), this.getRegistryName());
	}

	@Override public String toString(){
		return getUnlocalizedName();
	}

	public static final class Properties{
		private final int primaryColor, secondaryColor, highlightColor;
		private final Map<Attribute, Set<AttributeModifier>> attributes = new HashMap<>();

		@Nullable private OnEvent<LivingHurtEvent> onHurt;

		public Properties(int primaryColor, int secondaryColor){
			this(primaryColor, secondaryColor, primaryColor);
		}
		public Properties(int primaryColor, int secondaryColor, int highlightColor){
			this.primaryColor = primaryColor;
			this.secondaryColor = secondaryColor;
			this.highlightColor = highlightColor;
		}

		public Properties addAttribute(Attribute attribute, UUID uuid, double amount, AttributeModifier.Operation operation){
			Set<AttributeModifier> m = attributes.computeIfAbsent(attribute, a -> new HashSet<>());
			if(!m.add(new AttributeModifier(uuid, "Ability Attributes", amount, operation)))
				throw new IllegalStateException("Registration of attribute with overlapping ID "+uuid);
			return this;
		}

		public Properties onHurt(@Nullable OnEvent<LivingHurtEvent> onHurt){
			this.onHurt = onHurt;
			return this;
		}
	}
}