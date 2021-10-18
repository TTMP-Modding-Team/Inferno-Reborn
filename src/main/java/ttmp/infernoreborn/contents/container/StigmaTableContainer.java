package ttmp.infernoreborn.contents.container;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.container.Container;
import net.minecraft.inventory.container.ContainerType;
import net.minecraft.inventory.container.Slot;
import net.minecraft.item.ItemStack;
import net.minecraft.util.IWorldPosCallable;
import net.minecraft.world.World;
import ttmp.infernoreborn.capability.Caps;
import ttmp.infernoreborn.contents.ModContainers;
import ttmp.infernoreborn.contents.ModRecipes;
import ttmp.infernoreborn.contents.Sigils;
import ttmp.infernoreborn.contents.recipe.sigilcraft.SigilEngravingRecipe;
import ttmp.infernoreborn.contents.recipe.sigilcraft.SigilcraftRecipe;
import ttmp.infernoreborn.contents.sigil.Sigil;
import ttmp.infernoreborn.contents.sigil.holder.EmptySigilHolder;
import ttmp.infernoreborn.contents.sigil.holder.SigilHolder;
import ttmp.infernoreborn.inventory.DelegateSigilcraftInventory;
import ttmp.infernoreborn.inventory.SigilTableInventory;
import ttmp.infernoreborn.inventory.SigilcraftInventory;
import ttmp.infernoreborn.util.RealIntReferenceHolder;

import javax.annotation.Nullable;
import java.util.Objects;

public abstract class StigmaTableContainer extends Container{
	public static StigmaTableContainer create5x5(int id, PlayerInventory playerInventory){
		return new StigmaTableContainer.X5(ModContainers.STIGMA_TABLE_5X5.get(), id, playerInventory, 5);
	}
	public static StigmaTableContainer create7x7(int id, PlayerInventory playerInventory){
		return new StigmaTableContainer.X7(ModContainers.STIGMA_TABLE_7X7.get(), id, playerInventory, 7);
	}
	public static StigmaTableContainer create5x5(int id, PlayerInventory playerInventory, SigilcraftInventory inventory, IWorldPosCallable access){
		return new StigmaTableContainer.X5(ModContainers.STIGMA_TABLE_5X5.get(), id, playerInventory, inventory, access);
	}
	public static StigmaTableContainer create7x7(int id, PlayerInventory playerInventory, SigilcraftInventory inventory, IWorldPosCallable access){
		return new StigmaTableContainer.X7(ModContainers.STIGMA_TABLE_7X7.get(), id, playerInventory, inventory, access);
	}

	private final SigilcraftInventory inventory;
	private final IWorldPosCallable access;
	private final SigilHolder sigilHolder;

	private final RealIntReferenceHolder currentSigil = new RealIntReferenceHolder();
	private final RealIntReferenceHolder maxPoints = new RealIntReferenceHolder();

	@Nullable private SigilEngravingRecipe currentRecipe;

	public StigmaTableContainer(@Nullable ContainerType<?> type, int id, PlayerInventory playerInventory, int size){
		this(type, id, playerInventory, new SigilTableInventory(size, size), IWorldPosCallable.NULL);
	}
	public StigmaTableContainer(@Nullable ContainerType<?> type, int id, PlayerInventory playerInventory, SigilcraftInventory inventory, IWorldPosCallable access){
		super(type, id);

		this.inventory = new DelegateSigilcraftInventory(inventory, this);
		this.access = access;
		this.sigilHolder = playerInventory.player.getCapability(Caps.sigilHolder).orElse(EmptySigilHolder.INSTANCE);

		for(int i = 0; i<this.inventory.getContainerSize(); i++){
			if(i==this.inventory.getContainerSize()/2) continue;
			addSlot(new Slot(this.inventory, i, gridStartX()+i%this.inventory.getWidth()*17, gridStartY()+i/this.inventory.getWidth()*17));
		}

		for(int y = 0; y<3; ++y)
			for(int x = 0; x<9; ++x)
				this.addSlot(new Slot(playerInventory, x+y*9+9, invStartX()+x*18, invStartY()+y*18));
		for(int i1 = 0; i1<9; ++i1)
			this.addSlot(new Slot(playerInventory, i1, invStartX()+i1*18, invStartY()+18*3+4));

		this.access.execute((w, p) -> slotChangedCraftingGrid(w));
		this.currentSigil.register(this::addDataSlot);
		this.maxPoints.set(sigilHolder.getMaxPoints());
		this.maxPoints.register(this::addDataSlot);
	}

	protected abstract int gridStartX();
	protected abstract int gridStartY();

	protected abstract int invStartX();
	protected abstract int invStartY();

	public int centerSlotX(){
		return gridStartX()+17*(inventory.getWidth()/2);
	}
	public int centerSlotY(){
		return gridStartY()+17*(inventory.getHeight()/2);
	}

	public SigilcraftInventory getInventory(){
		return inventory;
	}
	@Nullable public SigilEngravingRecipe getCurrentRecipe(){
		return currentRecipe;
	}

	public int getCurrentSigil(){
		return currentSigil.get();
	}
	public int getMaxPoints(){
		return maxPoints.get();
	}

	protected void slotChangedCraftingGrid(World world){
		if(world.isClientSide) return;
		for(SigilcraftRecipe recipe : Objects.requireNonNull(world.getServer())
				.getRecipeManager().getAllRecipesFor(ModRecipes.SIGILCRAFT_RECIPE_TYPE)){
			if(!(recipe instanceof SigilEngravingRecipe)) continue;
			Sigil sigil = ((SigilEngravingRecipe)recipe).tryEngrave(sigilHolder, inventory);
			if(sigil!=null){
				currentRecipe = (SigilEngravingRecipe)recipe;
				currentSigil.set(Sigils.getRegistry().getID(sigil));
				return;
			}
		}
		currentSigil.set(-1);
	}

	@Override public void slotsChanged(IInventory inv){
		this.access.execute((w, p) -> slotChangedCraftingGrid(w));
	}

	@Override public boolean stillValid(PlayerEntity player){
		return true;
	}

	@Override public ItemStack quickMoveStack(PlayerEntity player, int slotIndex){
		ItemStack stack = ItemStack.EMPTY;
		Slot slot = this.slots.get(slotIndex);
		if(slot!=null&&slot.hasItem()){
			ItemStack stackAtSlot = slot.getItem();
			stack = stackAtSlot.copy();
			int playerInvStart = inventory.getContainerSize()+1;
			if(slotIndex<playerInvStart){
				if(slotIndex==0){
					this.access.execute((world, pos) -> stackAtSlot.getItem().onCraftedBy(stackAtSlot, world, player));
				}
				if(!this.moveItemStackTo(stackAtSlot, playerInvStart, this.slots.size(), true)) return ItemStack.EMPTY;
				slot.onQuickCraft(stackAtSlot, stack);
			}else{
				SigilHolder h = SigilHolder.of(stackAtSlot);
				if(h!=null&&h.getMaxPoints()>0&&
						!this.moveItemStackTo(stackAtSlot, (playerInvStart)/2, (playerInvStart)/2+1, false)) return ItemStack.EMPTY;
				if(!this.moveItemStackTo(stackAtSlot, slotIndex<playerInvStart+9 ? playerInvStart+9 : playerInvStart,
						slotIndex<playerInvStart+9 ? this.slots.size() : playerInvStart+9, true))
					return ItemStack.EMPTY;
			}

			if(stackAtSlot.isEmpty()) slot.set(ItemStack.EMPTY);
			else slot.setChanged();

			if(stackAtSlot.getCount()==stack.getCount()) return ItemStack.EMPTY;

			ItemStack s = slot.onTake(player, stackAtSlot);
			if(slotIndex==0) player.drop(s, false);
		}
		return stack;
	}

	public static class X5 extends StigmaTableContainer{
		public X5(@Nullable ContainerType<?> type, int id, PlayerInventory playerInventory, int size){
			super(type, id, playerInventory, size);
		}
		public X5(@Nullable ContainerType<?> type, int id, PlayerInventory playerInventory, SigilcraftInventory inventory, IWorldPosCallable access){
			super(type, id, playerInventory, inventory, access);
		}

		@Override protected int gridStartX(){
			return 9;
		}
		@Override protected int gridStartY(){
			return 3+12;
		}
		@Override protected int invStartX(){
			return 8;
		}
		@Override protected int invStartY(){
			return 110+12;
		}
	}

	public static class X7 extends StigmaTableContainer{
		public X7(@Nullable ContainerType<?> type, int id, PlayerInventory playerInventory, int size){
			super(type, id, playerInventory, size);
		}
		public X7(@Nullable ContainerType<?> type, int id, PlayerInventory playerInventory, SigilcraftInventory inventory, IWorldPosCallable access){
			super(type, id, playerInventory, inventory, access);
		}

		@Override protected int gridStartX(){
			return 9;
		}
		@Override protected int gridStartY(){
			return 3+12;
		}
		@Override protected int invStartX(){
			return 8;
		}
		@Override protected int invStartY(){
			return 144+12;
		}
	}
}