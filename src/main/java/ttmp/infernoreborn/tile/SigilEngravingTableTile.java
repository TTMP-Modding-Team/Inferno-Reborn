package ttmp.infernoreborn.tile;

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
import ttmp.infernoreborn.container.SigilEngravingTableContainer;
import ttmp.infernoreborn.contents.ModTileEntities;
import ttmp.infernoreborn.inventory.SigilTableInventory;

public abstract class SigilEngravingTableTile extends TileEntity implements INamedContainerProvider{
	public static SigilEngravingTableTile new3x3(){
		return new SigilEngravingTableTile(ModTileEntities.SIGIL_ENGRAVING_TABLE_3X3.get(), 3){
			@Override protected Container doCreateMenu(int id, PlayerInventory playerInventory, SigilTableInventory inventory, IWorldPosCallable pos){
				return SigilEngravingTableContainer.create3x3(id, playerInventory, inventory, pos);
			}
		};
	}
	public static SigilEngravingTableTile new5x5(){
		return new SigilEngravingTableTile(ModTileEntities.SIGIL_ENGRAVING_TABLE_5X5.get(), 5){
			@Override protected Container doCreateMenu(int id, PlayerInventory playerInventory, SigilTableInventory inventory, IWorldPosCallable pos){
				return SigilEngravingTableContainer.create5x5(id, playerInventory, inventory, pos);
			}
		};
	}
	public static SigilEngravingTableTile new7x7(){
		return new SigilEngravingTableTile(ModTileEntities.SIGIL_ENGRAVING_TABLE_7X7.get(), 7){
			@Override protected Container doCreateMenu(int id, PlayerInventory playerInventory, SigilTableInventory inventory, IWorldPosCallable pos){
				return SigilEngravingTableContainer.create7x7(id, playerInventory, inventory, pos);
			}
		};
	}

	private final SigilTableInventory inventory;

	public SigilEngravingTableTile(TileEntityType<?> type, int size){
		super(type);
		this.inventory = new SigilTableInventory(size, size);
	}

	public SigilTableInventory getInventory(){
		return inventory;
	}

	@Override public ITextComponent getDisplayName(){
		return new TranslationTextComponent("container.infernoreborn.sigil_engraving_table");
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
