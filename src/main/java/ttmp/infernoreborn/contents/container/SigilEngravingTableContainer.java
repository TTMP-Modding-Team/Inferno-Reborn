package ttmp.infernoreborn.contents.container;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.inventory.CraftResultInventory;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.container.Container;
import net.minecraft.inventory.container.ContainerType;
import net.minecraft.inventory.container.Slot;
import net.minecraft.item.ItemStack;
import net.minecraft.network.play.server.SSetSlotPacket;
import net.minecraft.util.IWorldPosCallable;
import net.minecraft.util.NonNullList;
import net.minecraft.world.World;
import net.minecraftforge.common.ForgeHooks;
import ttmp.infernoreborn.contents.ModContainers;
import ttmp.infernoreborn.contents.ModRecipes;
import ttmp.infernoreborn.contents.recipe.sigilcraft.SigilcraftRecipe;
import ttmp.infernoreborn.contents.sigil.holder.SigilHolder;
import ttmp.infernoreborn.inventory.DelegateSigilcraftInventory;
import ttmp.infernoreborn.inventory.SigilTableInventory;
import ttmp.infernoreborn.inventory.SigilcraftInventory;
import ttmp.infernoreborn.util.RealIntReferenceHolder;

import javax.annotation.Nullable;
import java.util.Objects;

public abstract class SigilEngravingTableContainer extends Container{
	public static SigilEngravingTableContainer create3x3(int id, PlayerInventory playerInventory){
		return new X3(ModContainers.SIGIL_ENGRAVING_TABLE_3X3.get(), id, playerInventory, 3);
	}
	public static SigilEngravingTableContainer create5x5(int id, PlayerInventory playerInventory){
		return new X5(ModContainers.SIGIL_ENGRAVING_TABLE_5X5.get(), id, playerInventory, 5);
	}
	public static SigilEngravingTableContainer create7x7(int id, PlayerInventory playerInventory){
		return new X7(ModContainers.SIGIL_ENGRAVING_TABLE_7X7.get(), id, playerInventory, 7);
	}
	public static SigilEngravingTableContainer create3x3(int id, PlayerInventory playerInventory, SigilcraftInventory inventory, IWorldPosCallable access){
		return new X3(ModContainers.SIGIL_ENGRAVING_TABLE_3X3.get(), id, playerInventory, inventory, access);
	}
	public static SigilEngravingTableContainer create5x5(int id, PlayerInventory playerInventory, SigilcraftInventory inventory, IWorldPosCallable access){
		return new X5(ModContainers.SIGIL_ENGRAVING_TABLE_5X5.get(), id, playerInventory, inventory, access);
	}
	public static SigilEngravingTableContainer create7x7(int id, PlayerInventory playerInventory, SigilcraftInventory inventory, IWorldPosCallable access){
		return new X7(ModContainers.SIGIL_ENGRAVING_TABLE_7X7.get(), id, playerInventory, inventory, access);
	}

	private final SigilcraftInventory inventory;
	private final CraftResultInventory resultSlots = new CraftResultInventory();
	private final IWorldPosCallable access;
	private final PlayerEntity player;

	private final RealIntReferenceHolder maxPoints = new RealIntReferenceHolder();

	public SigilEngravingTableContainer(@Nullable ContainerType<?> type, int id, PlayerInventory playerInventory, int size){
		this(type, id, playerInventory, new SigilTableInventory(size, size), IWorldPosCallable.NULL);
	}
	public SigilEngravingTableContainer(@Nullable ContainerType<?> type, int id, PlayerInventory playerInventory, SigilcraftInventory inventory, IWorldPosCallable access){
		super(type, id);

		this.inventory = new DelegateSigilcraftInventory(inventory, this);
		this.access = access;
		this.player = playerInventory.player;

		addSlot(new SigilEngravingResultSlot(resultSlots, 0, resultX(), resultY(), this.inventory));

		for(int i = 0; i<this.inventory.getContainerSize(); i++)
			addSlot(new Slot(this.inventory, i, gridStartX()+i%this.inventory.getWidth()*18, gridStartY()+i/this.inventory.getWidth()*18));

		for(int y = 0; y<3; ++y)
			for(int x = 0; x<9; ++x)
				this.addSlot(new Slot(playerInventory, x+y*9+9, invStartX()+x*18, invStartY()+y*18));
		for(int i1 = 0; i1<9; ++i1)
			this.addSlot(new Slot(playerInventory, i1, invStartX()+i1*18, invStartY()+18*3+4));

		this.access.execute((w, p) -> slotChangedCraftingGrid(w));
		updateMaxPoint();

		this.maxPoints.register(this::addDataSlot);
	}

	protected abstract int resultX();
	protected abstract int resultY();

	protected abstract int gridStartX();
	protected abstract int gridStartY();

	protected abstract int invStartX();
	protected abstract int invStartY();

	public int centerSlotX(){
		return gridStartX()+18*(inventory.getWidth()/2);
	}
	public int centerSlotY(){
		return gridStartY()+18*(inventory.getHeight()/2);
	}

	public int getMaxPoints(){
		return this.maxPoints.get();
	}

	public SigilcraftInventory getInventory(){
		return inventory;
	}
	public ItemStack getResultCache(){
		return resultSlots.getItem(0);
	}

	protected void slotChangedCraftingGrid(World world){
		if(world.isClientSide) return;
		ServerPlayerEntity player = (ServerPlayerEntity)this.player;
		ItemStack resultStack = ItemStack.EMPTY;
		SigilcraftRecipe recipe = Objects.requireNonNull(world.getServer())
				.getRecipeManager()
				.getRecipeFor(ModRecipes.SIGILCRAFT_RECIPE_TYPE, inventory, world)
				.orElse(null);
		if(recipe!=null){
			if(resultSlots.setRecipeUsed(world, player, recipe))
				resultStack = recipe.assemble(inventory);
		}

		resultSlots.setItem(0, resultStack);
		player.connection.send(new SSetSlotPacket(containerId, 0, resultStack));
	}

	protected void updateMaxPoint(){
		if(player.level.isClientSide()) return;
		ItemStack stack = inventory.getCenterItem();
		SigilHolder h = SigilHolder.of(stack);
		if(h==null) return;
		ItemStack stack2 = getResultCache();
		SigilHolder h2 = SigilHolder.of(stack2);
		maxPoints.set((h2!=null ? h2 : h).getMaxPoints());
	}

	@Override public void slotsChanged(IInventory inv){
		this.access.execute((w, p) -> slotChangedCraftingGrid(w));
		updateMaxPoint();
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

	@Override public boolean canTakeItemForPickAll(ItemStack stack, Slot slot){
		return slot.container!=this.resultSlots&&super.canTakeItemForPickAll(stack, slot);
	}

	public static class SigilEngravingResultSlot extends Slot{
		private final SigilcraftInventory sigilcraftInventory;

		public SigilEngravingResultSlot(CraftResultInventory craftResultInventory, int i, int x, int y, SigilcraftInventory sigilcraftInventory){
			super(craftResultInventory, i, x, y);
			this.sigilcraftInventory = sigilcraftInventory;
		}

		@Override public boolean mayPlace(ItemStack stack){
			return false;
		}

		protected void onQuickCraft(ItemStack stack, int amount){
			this.checkTakeAchievements(stack);
		}

		@Override public ItemStack onTake(PlayerEntity player, ItemStack stack){
			this.checkTakeAchievements(stack);
			ForgeHooks.setCraftingPlayer(player);
			NonNullList<ItemStack> remainingItems = player.level.getRecipeManager().getRemainingItemsFor(ModRecipes.SIGILCRAFT_RECIPE_TYPE, this.sigilcraftInventory, player.level);
			ForgeHooks.setCraftingPlayer(null);
			for(int i = 0; i<remainingItems.size(); ++i){
				ItemStack stackIn = this.sigilcraftInventory.getItem(i);
				ItemStack remaining = remainingItems.get(i);
				if(!stackIn.isEmpty()){
					this.sigilcraftInventory.removeItem(i, 1);
					stackIn = this.sigilcraftInventory.getItem(i);
				}

				if(!remaining.isEmpty()){
					if(stackIn.isEmpty()){
						this.sigilcraftInventory.setItem(i, remaining);
					}else if(ItemStack.isSame(stackIn, remaining)&&ItemStack.tagMatches(stackIn, remaining)){
						remaining.grow(stackIn.getCount());
						this.sigilcraftInventory.setItem(i, remaining);
					}else if(!player.inventory.add(remaining)){
						player.drop(remaining, false);
					}
				}
			}

			return stack;
		}
	}

	public static class X3 extends SigilEngravingTableContainer{
		public X3(@Nullable ContainerType<?> type, int id, PlayerInventory playerInventory, int size){
			super(type, id, playerInventory, size);
		}
		public X3(@Nullable ContainerType<?> type, int id, PlayerInventory playerInventory, SigilcraftInventory inventory, IWorldPosCallable access){
			super(type, id, playerInventory, inventory, access);
		}

		@Override protected int resultX(){
			return 149;
		}
		@Override protected int resultY(){
			return 51;
		}
		@Override protected int gridStartX(){
			return 62;
		}
		@Override protected int gridStartY(){
			return 18;
		}
		@Override protected int invStartX(){
			return 8;
		}
		@Override protected int invStartY(){
			return 84;
		}
	}

	public static class X5 extends SigilEngravingTableContainer{
		public X5(@Nullable ContainerType<?> type, int id, PlayerInventory playerInventory, int size){
			super(type, id, playerInventory, size);
		}
		public X5(@Nullable ContainerType<?> type, int id, PlayerInventory playerInventory, SigilcraftInventory inventory, IWorldPosCallable access){
			super(type, id, playerInventory, inventory, access);
		}

		@Override protected int resultX(){
			return 149;
		}
		@Override protected int resultY(){
			return 87;
		}
		@Override protected int gridStartX(){
			return 44;
		}
		@Override protected int gridStartY(){
			return 18;
		}
		@Override protected int invStartX(){
			return 8;
		}
		@Override protected int invStartY(){
			return 120;
		}
	}

	public static class X7 extends SigilEngravingTableContainer{
		public X7(@Nullable ContainerType<?> type, int id, PlayerInventory playerInventory, int size){
			super(type, id, playerInventory, size);
		}
		public X7(@Nullable ContainerType<?> type, int id, PlayerInventory playerInventory, SigilcraftInventory inventory, IWorldPosCallable access){
			super(type, id, playerInventory, inventory, access);
		}

		@Override protected int resultX(){
			return 156;
		}
		@Override protected int resultY(){
			return 113;
		}
		@Override protected int gridStartX(){
			return 15;
		}
		@Override protected int gridStartY(){
			return 8;
		}
		@Override protected int invStartX(){
			return 15;
		}
		@Override protected int invStartY(){
			return 138;
		}
	}
}
