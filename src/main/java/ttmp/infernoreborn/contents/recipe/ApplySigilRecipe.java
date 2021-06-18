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
import ttmp.infernoreborn.contents.sigil.context.ItemContext;
import ttmp.infernoreborn.contents.sigil.holder.SigilHolder;

import java.util.HashSet;
import java.util.Set;

public class ApplySigilRecipe extends SpecialRecipe{
	public ApplySigilRecipe(ResourceLocation id){
		super(id);
	}

	@Override public boolean matches(CraftingInventory inv, World world){
		ItemStack sigilHolderStack = null;
		SigilHolder sigilHolder = null;
		Set<Sigil> sigils = null;
		for(int i = 0; i<inv.getContainerSize(); i++){
			ItemStack stack = inv.getItem(i);
			if(stack.isEmpty()) continue;
			if(stack.getItem() instanceof SigilItem){
				Sigil sigil = ((SigilItem)stack.getItem()).getSigil();
				if(sigils==null) sigils = new HashSet<>();
				if(!sigils.add(sigil)) return false;
			}else{
				SigilHolder h = SigilHolder.of(stack);
				if(h!=null){
					if(sigilHolder!=null) return false;
					sigilHolder = h;
					sigilHolderStack = stack;
				}else return false;
			}
		}
		if(sigilHolder==null||sigils==null) return false;

		ItemContext context = new ItemContext(sigilHolderStack, sigilHolder);
		for(Sigil sigil : sigils)
			if(!sigil.canBeAttachedTo(context)) return false;
		return true;
	}

	@Override public ItemStack assemble(CraftingInventory inv){
		ItemStack sigilHolderStack = ItemStack.EMPTY;
		Set<Sigil> sigils = new HashSet<>();
		for(int i = 0; i<inv.getContainerSize(); i++){
			ItemStack stack = inv.getItem(i);
			if(stack.isEmpty()) continue;
			if(stack.getItem() instanceof SigilItem) sigils.add(((SigilItem)stack.getItem()).getSigil());
			else if(stack.getCapability(Caps.sigilHolder).isPresent()) sigilHolderStack = stack.copy();
		}

		SigilHolder h = SigilHolder.of(sigilHolderStack);
		if(h!=null){
			ItemContext context = new ItemContext(sigilHolderStack, h);
			for(Sigil sigil : sigils) h.add(sigil);
		}

		return sigilHolderStack;
	}

	@Override public boolean canCraftInDimensions(int x, int y){
		return x*y>1;
	}

	@Override public IRecipeSerializer<?> getSerializer(){
		return ModRecipes.APPLY_SIGIL.get();
	}
}
