package ttmp.infernoreborn.item;

import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.entity.LivingEntity;
import net.minecraft.item.ItemGroup;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.nbt.INBT;
import net.minecraft.nbt.ListNBT;
import net.minecraft.nbt.StringNBT;
import net.minecraft.util.NonNullList;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.StringTextComponent;
import net.minecraft.util.text.Style;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraft.world.World;
import net.minecraftforge.common.util.Constants;
import net.minecraftforge.registries.IForgeRegistry;
import ttmp.infernoreborn.ability.Ability;
import ttmp.infernoreborn.capability.AbilityHolder;
import ttmp.infernoreborn.contents.Abilities;

import javax.annotation.Nullable;
import java.util.AbstractList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;

public class FixedAbilityItem extends BaseAbilityItem{
	public FixedAbilityItem(Properties properties){
		super(properties);
	}

	@Override public ITextComponent getHighlightTip(ItemStack item, ITextComponent displayName){
		Ability[] abilities = getAbilities(item);
		switch(abilities.length){
			case 0:
				return displayName;
			case 1:
				return new StringTextComponent("")
						.append(displayName)
						.append(" (")
						.append(abilities[0].getName().setStyle(Style.EMPTY.applyFormat(TextFormatting.YELLOW)))
						.append(")");
			default:
				return new StringTextComponent("")
						.append(displayName)
						.append(" (")
						.append(new TranslationTextComponent("tooltip.infernoreborn.inferno_spark.n_abilities", abilities.length).setStyle(Style.EMPTY.applyFormat(TextFormatting.YELLOW)))
						.append(")");
		}
	}

	@Override public void fillItemCategory(ItemGroup group, NonNullList<ItemStack> itemStacks){
		if(!this.allowdedIn(group)) return;
		for(Ability ability : Abilities.getRegistry()){
			ItemStack stack = new ItemStack(this);
			setAbilities(stack, new Ability[]{ability});
			itemStacks.add(stack);
		}
	}

	@Override public void appendHoverText(ItemStack stack, @Nullable World world, List<ITextComponent> text, ITooltipFlag flags){
		for(Ability a : getAbilities(stack)){
			text.add(a.getName().setStyle(Style.EMPTY.applyFormat(TextFormatting.GRAY)));
		}
	}

	@Override protected boolean generate(ItemStack stack, LivingEntity entity){
		AbilityHolder h = AbilityHolder.of(entity);
		if(h==null) return false;
		Ability[] abilities = getAbilities(stack);
		boolean succeed = false;
		for(Ability a : abilities) if(h.add(a)) succeed = true;
		return succeed;
	}

	private static final Ability[] EMPTY_ABILITY = new Ability[0];

	public static Ability[] getAbilities(ItemStack stack){
		CompoundNBT tag = stack.getTag();
		if(tag==null||!tag.contains("Abilities", Constants.NBT.TAG_LIST)) return EMPTY_ABILITY;
		IForgeRegistry<Ability> registry = Abilities.getRegistry();
		return tag.getList("Abilities", Constants.NBT.TAG_STRING).stream()
				.map(INBT::getAsString)
				.map(ResourceLocation::new)
				.map(registry::getValue)
				.filter(Objects::nonNull)
				.toArray(Ability[]::new);
	}

	public static void setAbilities(ItemStack stack, Ability[] abilities){
		CompoundNBT tag = stack.getOrCreateTag();
		IForgeRegistry<Ability> registry = Abilities.getRegistry();
		tag.put("Abilities", Arrays.stream(abilities)
				.map(registry::getKey)
				.filter(Objects::nonNull)
				.map(ResourceLocation::toString)
				.map(StringNBT::valueOf)
				.collect(ListNBT::new, AbstractList::add, (l1, l2) -> {}));
	}
}
