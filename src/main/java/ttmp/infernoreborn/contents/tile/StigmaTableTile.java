package ttmp.infernoreborn.contents.tile;

import net.minecraft.block.BlockState;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.container.Container;
import net.minecraft.inventory.container.INamedContainerProvider;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.tileentity.TileEntityType;
import net.minecraft.util.IWorldPosCallable;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TranslationTextComponent;
import ttmp.infernoreborn.contents.ModTileEntities;
import ttmp.infernoreborn.contents.container.StigmaTableContainer;
import ttmp.infernoreborn.inventory.SigilTableInventory;

public abstract class StigmaTableTile extends TileEntity implements INamedContainerProvider{
	public static StigmaTableTile new5x5(){
		return new StigmaTableTile(ModTileEntities.STIGMA_TABLE_5X5.get(), 5){
			@Override protected Container doCreateMenu(int id, PlayerInventory playerInventory, SigilTableInventory inventory, IWorldPosCallable pos){
				return StigmaTableContainer.create5x5(id, playerInventory, inventory, pos);
			}
		};
	}
	public static StigmaTableTile new7x7(){
		return new StigmaTableTile(ModTileEntities.STIGMA_TABLE_7X7.get(), 7){
			@Override protected Container doCreateMenu(int id, PlayerInventory playerInventory, SigilTableInventory inventory, IWorldPosCallable pos){
				return StigmaTableContainer.create7x7(id, playerInventory, inventory, pos);
			}
		};
	}

	private final SigilTableInventory inventory;

	public StigmaTableTile(TileEntityType<?> type, int size){
		super(type);
		this.inventory = new SigilTableInventory(size, size);
	}

	public SigilTableInventory getInventory(){
		return inventory;
	}

	@Override public ITextComponent getDisplayName(){
		return new TranslationTextComponent("container.infernoreborn.stigma_table");
	}

	@Override public Container createMenu(int id, PlayerInventory playerInventory, PlayerEntity player){
		return doCreateMenu(id,
				playerInventory,
				inventory,
				getLevel()!=null ? IWorldPosCallable.create(getLevel(), getBlockPos()) : IWorldPosCallable.NULL);
	}

	protected abstract Container doCreateMenu(int id, PlayerInventory playerInventory, SigilTableInventory inventory, IWorldPosCallable pos);

	@Override public void load(BlockState state, CompoundNBT nbt){
		super.load(state, nbt);
		inventory.loadFrom(nbt);
	}
	@Override public CompoundNBT save(CompoundNBT nbt){
		super.save(nbt);
		inventory.saveTo(nbt);
		return nbt;
	}
}
