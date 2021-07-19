package ttmp.infernoreborn.contents.recipe;

import net.minecraft.inventory.CraftingInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.IRecipeSerializer;
import net.minecraft.item.crafting.SpecialRecipe;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.World;
import ttmp.infernoreborn.capability.Caps;
import ttmp.infernoreborn.contents.ModRecipes;
import ttmp.infernoreborn.contents.item.SigilItem;
import ttmp.infernoreborn.contents.sigil.Sigil;
import ttmp.infernoreborn.contents.sigil.holder.SigilHolder;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class ApplySigilRecipe extends SpecialRecipe{
	public ApplySigilRecipe(ResourceLocation id){
		super(id);
	}

	@Override public boolean matches(CraftingInventory inv, World world){
		SigilHolder sigilHolder = null;
		Set<Sigil> sigils = null;
		for(int i = 0; i<inv.getContainerSize(); i++){
			ItemStack stack = inv.getItem(i);
			if(stack.isEmpty()) continue;
			if(stack.getItem() instanceof SigilItem){
				Sigil sigil = SigilItem.getSigil(stack);
				if(sigil!=null){
					if(sigils==null) sigils = new HashSet<>();
					if(!sigils.add(sigil)) return false;
				}
			}else{
				SigilHolder h = SigilHolder.of(stack);
				if(h==null) return false;
				if(sigilHolder!=null) return false;
				sigilHolder = h;
			}
		}
		if(sigilHolder==null||sigils==null) return false;

		for(Sigil sigil : sigils)
			if(!sigilHolder.canAdd(sigil)) return false;
		return true;
	}

	@Override public ItemStack assemble(CraftingInventory inv){
		ItemStack sigilHolderStack = ItemStack.EMPTY;
		List<Sigil> sigils = new ArrayList<>();
		for(int i = 0; i<inv.getContainerSize(); i++){
			ItemStack stack = inv.getItem(i);
			if(stack.isEmpty()) continue;
			if(stack.getItem() instanceof SigilItem){
				Sigil sigil = SigilItem.getSigil(stack);
				if(sigil!=null) sigils.add(sigil);
			}else if(stack.getCapability(Caps.sigilHolder).isPresent()) sigilHolderStack = stack.copy();
		}

		SigilHolder h = SigilHolder.of(sigilHolderStack);
		if(h!=null)
			for(Sigil sigil : sigils)
				h.add(sigil);

		return sigilHolderStack;
	}

	@Override public boolean canCraftInDimensions(int x, int y){
		return x*y>1;
	}

	@Override public IRecipeSerializer<?> getSerializer(){
		return ModRecipes.APPLY_SIGIL.get();
	}
}
