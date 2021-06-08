package ttmp.infernoreborn.item;

import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.entity.LivingEntity;
import net.minecraft.item.ItemGroup;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.util.NonNullList;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.StringTextComponent;
import net.minecraft.util.text.Style;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.world.World;
import net.minecraftforge.common.util.Constants;
import ttmp.infernoreborn.ability.generator.AbilityGenerator;
import ttmp.infernoreborn.ability.generator.AbilityGenerators;
import ttmp.infernoreborn.capability.AbilityHolder;

import javax.annotation.Nullable;
import java.util.List;

public class GeneratorAbilityItem extends BaseAbilityItem{
	public GeneratorAbilityItem(Properties properties){
		super(properties);
	}

	@Override public void fillItemCategory(ItemGroup group, NonNullList<ItemStack> itemStacks){
		if(!this.allowdedIn(group)) return;
		for(AbilityGenerator generator : AbilityGenerators.getWeightedPool().getItems().keySet()){
			if(!generator.displayItem()) continue;
			ItemStack stack = new ItemStack(this);
			setGenerator(stack, generator);
			itemStacks.add(stack);
		}
	}

	@Override protected boolean generate(ItemStack stack, LivingEntity entity){
		AbilityHolder h = AbilityHolder.of(entity);
		if(h==null) return false;
		AbilityGenerator generator = getGenerator(stack);
		if(generator==null) return false;
		h.clear();
		generator.generate(entity);
		return true;
	}

	@Override public void appendHoverText(ItemStack stack, @Nullable World world, List<ITextComponent> text, ITooltipFlag flags){
		AbilityGenerator generator = getGenerator(stack);
		if(generator!=null)
			text.add(new StringTextComponent(generator.getId().toString()).setStyle(Style.EMPTY.applyFormat(TextFormatting.GRAY)));
	}

	@Nullable public static AbilityGenerator getGenerator(ItemStack stack){
		CompoundNBT tag = stack.getTag();
		if(tag==null||!tag.contains("Generator", Constants.NBT.TAG_STRING)) return null;
		ResourceLocation generator = new ResourceLocation(tag.getString("Generator"));
		for(AbilityGenerator g : AbilityGenerators.getWeightedPool().getItems().keySet()){
			if(g.getId().equals(generator)) return g;
		}
		return null;
	}
	public static void setGenerator(ItemStack stack, @Nullable AbilityGenerator generator){
		CompoundNBT tag = stack.getOrCreateTag();
		if(generator==null) tag.remove("Generator");
		else tag.putString("Generator", generator.getId().toString());
	}
}
