package ttmp.infernoreborn.contents.item.ability;

import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.entity.LivingEntity;
import net.minecraft.item.ItemGroup;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.util.NonNullList;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.Style;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraft.world.World;
import net.minecraftforge.common.util.Constants;
import ttmp.infernoreborn.contents.ability.holder.ServerAbilityHolder;
import ttmp.infernoreborn.infernaltype.InfernalType;
import ttmp.infernoreborn.infernaltype.InfernalTypes;

import javax.annotation.Nullable;
import java.util.Comparator;
import java.util.List;

public class GeneratorAbilityItem extends AbstractAbilityItem{
	public GeneratorAbilityItem(Properties properties){
		super(properties);
	}

	@Override public void fillItemCategory(ItemGroup group, NonNullList<ItemStack> itemStacks){
		if(!this.allowdedIn(group)) return;
		InfernalTypes.getInfernalTypes().stream()
				.sorted(Comparator.comparing(InfernalType::getId))
				.forEachOrdered(infernalType -> {
					if(infernalType.getItemDisplay()==null) return;
					ItemStack stack = new ItemStack(this);
					setType(stack, infernalType);
					itemStacks.add(stack);
				});
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
		if(type!=null)
			text.add(new TranslationTextComponent("ability_generator."+type.getId().getNamespace()+"."+type.getId().getPath()).setStyle(Style.EMPTY.applyFormat(TextFormatting.GRAY)));
	}

	@Nullable public static InfernalType getType(ItemStack stack){
		CompoundNBT tag = stack.getTag();
		if(tag==null||!tag.contains("Type", Constants.NBT.TAG_STRING)) return null;
		return InfernalTypes.get(new ResourceLocation(tag.getString("Type")));
	}
	public static void setType(ItemStack stack, @Nullable InfernalType type){
		CompoundNBT tag = stack.getOrCreateTag();
		if(type==null) tag.remove("Type");
		else tag.putString("Type", type.getId().toString());
	}
}
