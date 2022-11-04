package ttmp.infernoreborn.contents.container;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.container.Container;
import net.minecraft.inventory.container.ContainerType;
import net.minecraft.inventory.container.Slot;
import net.minecraft.item.ItemStack;
import net.minecraft.util.IWorldPosCallable;
import net.minecraft.world.World;
import net.minecraftforge.fml.network.PacketDistributor;
import ttmp.infernoreborn.api.Caps;
import ttmp.infernoreborn.api.sigil.EmptySigilHolder;
import ttmp.infernoreborn.api.sigil.Sigil;
import ttmp.infernoreborn.api.sigil.SigilEngravingRecipe;
import ttmp.infernoreborn.api.sigil.SigilHolder;
import ttmp.infernoreborn.api.sigil.SigilcraftInventory;
import ttmp.infernoreborn.api.sigil.SigilcraftRecipe;
import ttmp.infernoreborn.contents.ModContainers;
import ttmp.infernoreborn.contents.ModRecipes;
import ttmp.infernoreborn.contents.Sigils;
import ttmp.infernoreborn.inventory.DelegateSigilcraftInventory;
import ttmp.infernoreborn.inventory.SigilTableInventory;
import ttmp.infernoreborn.network.ModNet;
import ttmp.infernoreborn.network.SyncSigilScreenMsg;
import ttmp.infernoreborn.util.RealIntReferenceHolder;

import javax.annotation.Nullable;
import java.util.Collections;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

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

	private final PlayerEntity player;
	private final SigilcraftInventory inventory;
	private final IWorldPosCallable access;
	private final SigilHolder sigilHolder;

	private final RealIntReferenceHolder craftingResult = new RealIntReferenceHolder();
	private final RealIntReferenceHolder maxPoints = new RealIntReferenceHolder();

	@Nullable private SigilEngravingRecipe currentRecipe;

	private final Set<Sigil> currentSigils = new HashSet<>();

	public StigmaTableContainer(@Nullable ContainerType<?> type, int id, PlayerInventory playerInventory, int size){
		this(type, id, playerInventory, new SigilTableInventory(size, size), IWorldPosCallable.NULL);
	}
	public StigmaTableContainer(@Nullable ContainerType<?> type, int id, PlayerInventory playerInventory, SigilcraftInventory inventory, IWorldPosCallable access){
		super(type, id);

		this.player = playerInventory.player;
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
		this.craftingResult.register(this::addDataSlot);
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

	public int getCraftingResultSigilId(){
		return craftingResult.get();
	}
	private int craftingResultSigilIdCache;
	@Nullable private Sigil craftingResultSigilCache;
	@Nullable public Sigil getCraftingResultSigil(){
		int id = craftingResult.get();
		if(id!=craftingResultSigilIdCache){
			craftingResultSigilIdCache = id;
			craftingResultSigilCache = Sigils.getRegistry().getValue(id);
		}
		return craftingResultSigilCache;
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
				craftingResult.set(Sigils.getRegistry().getID(sigil));
				return;
			}
		}
		currentRecipe = null;
		craftingResult.set(-1);
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
						!this.moveItemStackTo(stackAtSlot, (playerInvStart)/2, (playerInvStart)/2+1, false))
					return ItemStack.EMPTY;
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

	private int previousResultSigilId = -1;
	@Override public void broadcastChanges(){
		super.broadcastChanges();

		boolean updated = false;
		if(!currentSigils.equals(sigilHolder.getSigils())){
			updated = true;
			currentSigils.clear();
			currentSigils.addAll(sigilHolder.getSigils());
		}
		int sigilId = getCraftingResultSigilId();
		if(previousResultSigilId!=sigilId){
			updated = true;
			previousResultSigilId = sigilId;
		}
		if(updated&&player instanceof ServerPlayerEntity){
			Sigil s = getCraftingResultSigil();
			SyncSigilScreenMsg msg = new SyncSigilScreenMsg(currentSigils, s!=null ? Collections.singleton(s) : Collections.emptySet());
			ModNet.CHANNEL.send(PacketDistributor.PLAYER.with(() -> (ServerPlayerEntity)player), msg);
		}
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
