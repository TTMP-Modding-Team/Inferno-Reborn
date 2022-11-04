package ttmp.infernoreborn.contents.item;

import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.item.Item;
import net.minecraft.item.ItemGroup;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Rarity;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.util.NonNullList;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.StringTextComponent;
import net.minecraft.util.text.Style;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraft.world.World;
import net.minecraftforge.common.util.Constants;
import ttmp.infernoreborn.api.sigil.Sigil;
import ttmp.infernoreborn.api.sigil.SigilSlot;
import ttmp.infernoreborn.client.color.ColorUtils;
import ttmp.infernoreborn.contents.ModItems;
import ttmp.infernoreborn.contents.Sigils;

import javax.annotation.Nullable;
import java.util.List;
import java.util.Objects;

public class SigilItem extends Item{
	public SigilItem(Properties properties){
		super(properties);
	}

	@Override public void fillItemCategory(ItemGroup group, NonNullList<ItemStack> itemStacks){
		if(!this.allowdedIn(group)) return;
		for(Sigil sigil : Sigils.getRegistry()){
			ItemStack stack = new ItemStack(this);
			setSigil(stack, sigil);
			itemStacks.add(stack);
		}
	}

	@Override public String getDescriptionId(ItemStack stack){
		Sigil sigil = getSigil(stack);
		return sigil!=null ? sigil.getUnlocalizedName() : super.getDescriptionId();
	}

	@Override public Rarity getRarity(ItemStack stack){
		Sigil sigil = getSigil(stack);
		return sigil!=null ? sigil.getRarity() : super.getRarity(stack);
	}

	@Override public void appendHoverText(ItemStack stack, @Nullable World level, List<ITextComponent> text, ITooltipFlag flags){
		Sigil sigil = getSigil(stack);
		if(sigil!=null) appendTooltip(text, sigil, flags.isAdvanced());
	}

	public static void appendTooltip(List<ITextComponent> text, Sigil sigil, boolean advanced){
		text.add(new TranslationTextComponent("tooltip.infernoreborn.sigil.points",
				new StringTextComponent(String.valueOf(sigil.getPoint()))
						.withStyle(TextFormatting.GOLD))
				.withStyle(Style.EMPTY.withColor(ColorUtils.SIGIL_TEXT_COLOR)));
		StringTextComponent slots = new StringTextComponent("");
		slots.withStyle(TextFormatting.GOLD);
		boolean first = true;
		for(SigilSlot slot : sigil.getApplicableSlots()){
			if(first) first = false;
			else slots.append(", ");
			slots.append(slot.getName());
		}
		text.add(new TranslationTextComponent("tooltip.infernoreborn.sigil.slots", slots)
				.withStyle(Style.EMPTY.withColor(ColorUtils.SIGIL_TEXT_COLOR)));
		if(advanced){
			ResourceLocation id = sigil.getRegistryName();
			if(id!=null)
				text.add(new TranslationTextComponent("tooltip.infernoreborn.sigil.id", id)
						.withStyle(TextFormatting.DARK_GRAY));
		}
	}

	@Nullable public static Sigil getSigil(ItemStack stack){
		CompoundNBT tag = stack.getTag();
		if(tag==null||!tag.contains("Sigils", Constants.NBT.TAG_STRING)) return null;
		return Sigils.getRegistry().getValue(new ResourceLocation(tag.getString("Sigils")));
	}

	public static void setSigil(ItemStack stack, Sigil sigil){
		stack.getOrCreateTag().putString("Sigils", Objects.requireNonNull(sigil.getRegistryName()).toString());
	}

	public static ItemStack createSigilItem(Sigil sigil){
		ItemStack stack = new ItemStack(ModItems.SIGIL.get());
		setSigil(stack, sigil);
		return stack;
	}
}
