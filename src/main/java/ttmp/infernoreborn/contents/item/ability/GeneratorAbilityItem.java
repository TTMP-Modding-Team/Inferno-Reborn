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
import ttmp.infernoreborn.contents.ability.holder.ServerAbilityHolder;
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
		if(tag==null||!tag.contains("Type", Constants.NBT.TAG_STRING)) return null;
		return InfernalTypes.getInfernalType(tag.getString("Type"));
	}
	public static void setType(ItemStack stack, @Nullable String type){
		CompoundNBT tag = stack.getOrCreateTag();
		if(type==null) tag.remove("Type");
		else tag.putString("Type", type);
	}
}
