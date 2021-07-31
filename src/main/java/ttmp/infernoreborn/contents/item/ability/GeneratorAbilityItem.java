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
import ttmp.infernoreborn.contents.ability.generator.AbilityGenerator;
import ttmp.infernoreborn.contents.ability.generator.AbilityGenerators;
import ttmp.infernoreborn.contents.ability.generator.scheme.AbilityGeneratorScheme;
import ttmp.infernoreborn.contents.ability.holder.ServerAbilityHolder;

import javax.annotation.Nullable;
import java.util.List;

public class GeneratorAbilityItem extends AbstractAbilityItem{
	public GeneratorAbilityItem(Properties properties){
		super(properties);
	}

	@Override public void fillItemCategory(ItemGroup group, NonNullList<ItemStack> itemStacks){
		if(!this.allowdedIn(group)) return;
		for(AbilityGeneratorScheme scheme : AbilityGenerators.getSchemes()){
			if(scheme.getItemDisplay()==null) continue;
			ItemStack stack = new ItemStack(this);
			setGenerator(stack, scheme);
			itemStacks.add(stack);
		}
	}

	@Override protected boolean generate(ItemStack stack, LivingEntity entity){
		ServerAbilityHolder h = ServerAbilityHolder.of(entity);
		if(h==null) return false;
		AbilityGenerator generator = getGenerator(stack);
		if(generator==null) return false;
		h.generate(entity, generator);
		return true;
	}

	@Override public void appendHoverText(ItemStack stack, @Nullable World world, List<ITextComponent> text, ITooltipFlag flags){
		AbilityGeneratorScheme scheme = getScheme(stack);
		if(scheme!=null)
			text.add(new TranslationTextComponent("ability_generator."+scheme.getId().getNamespace()+"."+scheme.getId().getPath()).setStyle(Style.EMPTY.applyFormat(TextFormatting.GRAY)));
	}

	@Nullable public static AbilityGenerator getGenerator(ItemStack stack){
		CompoundNBT tag = stack.getTag();
		if(tag==null||!tag.contains("Generator", Constants.NBT.TAG_STRING)) return null;
		return AbilityGenerators.findGeneratorWithId(new ResourceLocation(tag.getString("Generator")));
	}
	@Nullable public static AbilityGeneratorScheme getScheme(ItemStack stack){
		CompoundNBT tag = stack.getTag();
		if(tag==null||!tag.contains("Generator", Constants.NBT.TAG_STRING)) return null;
		return AbilityGenerators.findSchemeWithId(new ResourceLocation(tag.getString("Generator")));
	}
	public static void setGenerator(ItemStack stack, @Nullable AbilityGenerator generator){
		setGenerator(stack, generator==null ? null : generator.getScheme());
	}
	public static void setGenerator(ItemStack stack, @Nullable AbilityGeneratorScheme scheme){
		CompoundNBT tag = stack.getOrCreateTag();
		if(scheme==null) tag.remove("Generator");
		else tag.putString("Generator", scheme.getId().toString());
	}
}
