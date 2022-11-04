package ttmp.infernoreborn.contents.item.ability;

import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.entity.LivingEntity;
import net.minecraft.item.ItemGroup;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.util.NonNullList;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.IFormattableTextComponent;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.StringTextComponent;
import net.minecraft.util.text.Style;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraft.world.World;
import net.minecraftforge.common.util.Constants;
import ttmp.infernoreborn.api.ability.Ability;
import ttmp.infernoreborn.api.ability.AbilityHolder;
import ttmp.infernoreborn.contents.Abilities;
import ttmp.infernoreborn.util.StupidUtils;

import javax.annotation.Nullable;
import java.util.List;

public class FixedAbilityItem extends AbstractAbilityItem{
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
			IFormattableTextComponent t = a.getName().setStyle(Style.EMPTY.applyFormat(TextFormatting.GRAY));
			if(flags.isAdvanced()){
				ResourceLocation id = a.getRegistryName();
				if(id!=null) t.append("  ")
						.append(new StringTextComponent(id.toString()).withStyle(TextFormatting.DARK_GRAY));
			}
			text.add(t);
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
		return StupidUtils.readToArray(tag.getList("Abilities", Constants.NBT.TAG_STRING), Abilities.getRegistry(), Ability[]::new);
	}

	public static void setAbilities(ItemStack stack, Ability[] abilities){
		stack.getOrCreateTag().put("Abilities", StupidUtils.writeToNbt(abilities, Abilities.getRegistry()));
	}
}
