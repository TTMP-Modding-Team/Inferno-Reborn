package ttmp.infernoreborn.contents.item.ability;

import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.entity.LivingEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.StringTextComponent;
import net.minecraft.util.text.Style;
import net.minecraft.world.World;
import net.minecraftforge.common.util.Constants;
import ttmp.infernoreborn.capability.ServerAbilityHolder;
import ttmp.infernoreborn.contents.ModItems;
import ttmp.infernoreborn.infernaltype.InfernalType;
import ttmp.infernoreborn.infernaltype.InfernalTypes;

import javax.annotation.Nullable;
import java.util.List;

import static net.minecraft.util.text.TextFormatting.*;

public class GeneratorAbilityItem extends AbstractAbilityItem{
	public GeneratorAbilityItem(Properties properties){
		super(properties);
	}

	@Override protected boolean generate(ItemStack stack, LivingEntity entity){
		ServerAbilityHolder h = ServerAbilityHolder.of(entity);
		if(h==null) return false;
		InfernalType type = getType(stack);
		if(type==null) return false;
		h.clear();
		InfernalTypes.generate(entity, h, type);
		return true;
	}

	@Override public void appendHoverText(ItemStack stack, @Nullable World world, List<ITextComponent> text, ITooltipFlag flags){
		InfernalType type = getType(stack);
		text.add(type!=null ?
				new StringTextComponent(type.getName()!=null ? type.getName() : "???").setStyle(Style.EMPTY.applyFormats(GRAY, ITALIC)) :
				new StringTextComponent("Invalid").setStyle(Style.EMPTY.applyFormat(RED)));
	}

	@Nullable public static InfernalType getType(ItemStack stack){
		CompoundNBT tag = stack.getTag();
		return tag!=null&&tag.contains("Type", Constants.NBT.TAG_STRING) ?
				InfernalTypes.getInfernalType(tag.getString("Type")) : null;
	}

	public static int getPrimaryColor(ItemStack stack, int fallback){
		CompoundNBT tag = stack.getTag();
		return tag!=null&&tag.contains("PrimaryColor", Constants.NBT.TAG_INT) ?
				tag.getInt("PrimaryColor") : fallback;
	}

	public static int getSecondaryColor(ItemStack stack, int fallback){
		CompoundNBT tag = stack.getTag();
		return tag!=null&&tag.contains("SecondaryColor", Constants.NBT.TAG_INT) ?
				tag.getInt("SecondaryColor") : fallback;
	}

	public static int getHighlightColor(ItemStack stack, int fallback){
		CompoundNBT tag = stack.getTag();
		return tag!=null&&tag.contains("HighlightColor", Constants.NBT.TAG_INT) ?
				tag.getInt("HighlightColor") : fallback;
	}

	public static ItemStack createItemStack(InfernalType type){
		ItemStack stack = new ItemStack(ModItems.GENERATOR_INFERNO_SPARK.get());
		if(type.getName()!=null){
			CompoundNBT tag = stack.getOrCreateTag();
			tag.putString("Type", type.getName());
			InfernalType.ItemColor color = type.getItemColor();
			if(color!=null){
				if(color.getPrimary()!=null) tag.putInt("PrimaryColor", color.getPrimary());
				if(color.getSecondary()!=null) tag.putInt("SecondaryColor", color.getSecondary());
				if(color.getHighlight()!=null) tag.putInt("HighlightColor", color.getHighlight());
			}
		}
		return stack;
	}
}
