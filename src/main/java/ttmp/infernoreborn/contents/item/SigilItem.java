package ttmp.infernoreborn.contents.item;

import net.minecraft.item.Item;
import net.minecraft.item.ItemGroup;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.util.NonNullList;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.common.util.Constants;
import ttmp.infernoreborn.contents.ModItems;
import ttmp.infernoreborn.contents.Sigils;
import ttmp.infernoreborn.contents.sigil.Sigil;

import javax.annotation.Nullable;
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
