package ttmp.infernoreborn.ability;

import net.minecraft.entity.ai.attributes.Attribute;
import net.minecraft.entity.ai.attributes.AttributeModifier;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraftforge.registries.ForgeRegistryEntry;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.UUID;

public class Ability extends ForgeRegistryEntry<Ability>{
	private final int color;
	private final Map<Attribute, Set<AttributeModifier>> attributes;

	public Ability(Properties properties){
		this.color = properties.color;
		this.attributes = properties.attributes;
	}

	public int getColor(){
		return color;
	}

	public Map<Attribute, Set<AttributeModifier>> getAttributes(){
		return attributes;
	}

	public TranslationTextComponent getName(){
		return new TranslationTextComponent(getUnlocalizedName());
	}

	protected String getUnlocalizedName(){
		ResourceLocation n = Objects.requireNonNull(getRegistryName());
		return "ability."+n.getNamespace()+"."+n.getPath();
	}

	@Override public String toString(){
		return getUnlocalizedName();
	}

	public static final class Properties{
		private final int color;
		private final Map<Attribute, Set<AttributeModifier>> attributes = new HashMap<>();

		public Properties(int color){
			this.color = color;
		}

		public Properties addAttribute(Attribute attribute, UUID uuid, double amount, AttributeModifier.Operation operation){
			Set<AttributeModifier> m = attributes.computeIfAbsent(attribute, a -> new HashSet<>());
			if(!m.add(new AttributeModifier(uuid, "Ability Attributes", amount, operation)))
				throw new IllegalStateException("Registration of attribute with overlapping ID "+uuid);
			return this;
		}
	}
}