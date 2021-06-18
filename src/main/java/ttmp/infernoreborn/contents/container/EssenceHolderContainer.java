package ttmp.infernoreborn.contents.container;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.Inventory;
import net.minecraft.inventory.container.Container;
import net.minecraft.inventory.container.ContainerType;
import net.minecraft.inventory.container.Slot;
import net.minecraft.item.ItemStack;
import net.minecraft.util.IntReferenceHolder;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.fml.network.PacketDistributor;
import ttmp.infernoreborn.capability.Caps;
import ttmp.infernoreborn.capability.EssenceHolder;
import ttmp.infernoreborn.contents.ModContainers;
import ttmp.infernoreborn.network.EssenceHolderScreenEssenceSyncMsg;
import ttmp.infernoreborn.network.ModNet;
import ttmp.infernoreborn.util.EssenceType;

import javax.annotation.Nullable;

public class EssenceHolderContainer extends Container{
	private final PlayerInventory playerInventory;
	private final EssenceHolderItemHandler essenceHolder;

	private final IntReferenceHolder holderSlot = IntReferenceHolder.standalone();

	private final int[] essenceCache = new int[EssenceType.values().length];

	public EssenceHolderContainer(int id, PlayerInventory playerInventory){
		this(ModContainers.ESSENCE_HOLDER.get(), id, playerInventory);
	}
	public EssenceHolderContainer(@Nullable ContainerType<?> type, int id, PlayerInventory playerInventory){
		super(type, id);
		this.playerInventory = playerInventory;
		this.essenceHolder = EssenceHolderItemHandler.withLazyOptional(() -> {
			int holderSlot = this.holderSlot.get();
			return holderSlot>=0&&holderSlot<this.playerInventory.getContainerSize() ?
					this.playerInventory.getItem(holderSlot).getCapability(Caps.essenceHolder) : LazyOptional.empty();
		});

		this.addSlot(new Slot(new Inventory(0), 0, 0, 0){
			@Override public ItemStack getItem(){
				return ItemStack.EMPTY;
			}
			@Override public boolean mayPlace(ItemStack stack){
				for(int i = 0; i<getEssenceHolder().getSlots(); i++)
					if(getEssenceHolder().isItemValid(i, stack)) return true;
				return false;
			}
			@Override public void set(ItemStack stack){
				for(int i = 0; i<getEssenceHolder().getSlots()&&!stack.isEmpty(); i++)
					stack = getEssenceHolder().insertItem(i, stack, false);
			}
			@Override public void setChanged(){}
			@Override public int getMaxStackSize(){
				return this.container.getMaxStackSize();
			}
			@Override public ItemStack remove(int amount){
				return ItemStack.EMPTY;
			}
			@Override public boolean isActive(){
				return false;
			}
		});

		for(int y = 0; y<3; ++y)
			for(int x = 0; x<9; ++x)
				this.addSlot(createSlot(playerInventory, x+y*9+9, 8+x*18, 84+y*18));
		for(int i1 = 0; i1<9; ++i1)
			this.addSlot(createSlot(playerInventory, i1, 8+i1*18, 142));

		holderSlot.set(-1);
		holderSlot.checkAndClearUpdateFlag();
		addDataSlot(holderSlot);
	}

	private Slot createSlot(IInventory inv, int index, int x, int y){
		return new Slot(inv, index, x, y){
			@Override public boolean mayPlace(ItemStack stack){
				return super.mayPlace(stack)&&getSlotIndex()!=holderSlot.get();
			}
			@Override public boolean mayPickup(PlayerEntity player){
				return super.mayPickup(player)&&getSlotIndex()!=holderSlot.get();
			}
		};
	}

	public EssenceHolderItemHandler getEssenceHolder(){
		return essenceHolder;
	}
	public int getHolderSlot(){
		return holderSlot.get();
	}
	public void setHolderSlot(int holderSlot){
		this.holderSlot.set(holderSlot);
	}

	public void handleEssenceHolderSlotClick(int slot, int type, boolean shift){
		if(shift){
			ItemStack stack = essenceHolder.extractItem(slot, 64, true);
			moveItemStackTo(stack, 1, this.slots.size(), true);
			essenceHolder.extractItem(slot, 64-stack.getCount(), false);
		}else{
			ItemStack carried = playerInventory.getCarried();
			if(type==1){
				if(carried.isEmpty()){
					ItemStack fullExtract = essenceHolder.extractItem(slot, 64, true);
					playerInventory.setCarried(essenceHolder.extractItem(slot, fullExtract.getCount()/2+fullExtract.getCount()%2, false));
				}else{
					ItemStack copy = carried.copy();
					copy.setCount(1);
					if(essenceHolder.insertItem(slot, copy, false).isEmpty())
						carried.shrink(1);
				}
			}else{
				playerInventory.setCarried(carried.isEmpty() ?
						essenceHolder.extractItem(slot, 64, false) :
						essenceHolder.insertItem(slot, carried, false));
			}
		}
	}

	@Override public void broadcastChanges(){
		super.broadcastChanges();
		if(!(playerInventory.player instanceof ServerPlayerEntity)) return;
		boolean essenceNeedsSync = false;
		EssenceHolder h = this.essenceHolder.getEssenceHolder();
		for(EssenceType type : EssenceType.values()){
			int essence = h.getEssence(type);
			if(essence!=essenceCache[type.ordinal()]){
				essenceCache[type.ordinal()] = essence;
				essenceNeedsSync = true;
			}
		}
		if(essenceNeedsSync){
			ModNet.CHANNEL.send(PacketDistributor.PLAYER.with(() -> (ServerPlayerEntity)playerInventory.player), new EssenceHolderScreenEssenceSyncMsg(h));
		}
	}

	@Override public boolean stillValid(PlayerEntity player){
		return true;
	}

	@Override public ItemStack quickMoveStack(PlayerEntity player, int slotIndex){
		ItemStack returns = ItemStack.EMPTY;
		Slot slot = this.slots.get(slotIndex);
		if(slot!=null&&slot.hasItem()){
			ItemStack stackAtSlot = slot.getItem();
			returns = stackAtSlot.copy();
			if(slotIndex<1){
				if(!this.moveItemStackTo(stackAtSlot, 1, this.slots.size(), true)) return ItemStack.EMPTY;
			}else if(!this.moveItemStackTo(stackAtSlot, 0, 1, false)) return ItemStack.EMPTY;

			if(stackAtSlot.isEmpty()) slot.set(ItemStack.EMPTY);
			else slot.setChanged();
		}

		return returns;
	}
}
