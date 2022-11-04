package ttmp.infernoreborn.contents.tile;

import net.minecraft.block.BlockState;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.container.Container;
import net.minecraft.inventory.container.INamedContainerProvider;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.Direction;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.util.LazyOptional;
import ttmp.infernoreborn.api.Caps;
import ttmp.infernoreborn.api.essence.EssenceHolder;
import ttmp.infernoreborn.contents.ModTileEntities;
import ttmp.infernoreborn.contents.container.EssenceHolderContainer;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public class EssenceHolderTile extends TileEntity implements INamedContainerProvider{
	private final EssenceHolder essenceHolder = new EssenceHolder();

	public EssenceHolderTile(){
		super(ModTileEntities.ESSENCE_HOLDER.get());
	}

	@Override public ITextComponent getDisplayName(){
		return new TranslationTextComponent("container.infernoreborn.essence_holder");
	}
	@Override public Container createMenu(int id, PlayerInventory inv, PlayerEntity p){
		return new EssenceHolderContainer(id, inv, essenceHolder);
	}

	@Nullable private LazyOptional<EssenceHolder> essenceHolderLO;

	@Nonnull @Override public <T> LazyOptional<T> getCapability(@Nonnull Capability<T> cap, @Nullable Direction side){
		if(cap==Caps.essenceHolder||cap==Caps.essenceHandler){
			if(essenceHolderLO==null) essenceHolderLO = LazyOptional.of(() -> essenceHolder);
			return essenceHolderLO.cast();
		}else return super.getCapability(cap, side);
	}

	@Override protected void invalidateCaps(){
		super.invalidateCaps();
		if(essenceHolderLO!=null){
			essenceHolderLO.invalidate();
			essenceHolderLO = null;
		}
	}

	@Override public void load(BlockState state, CompoundNBT nbt){
		super.load(state, nbt);
		essenceHolder.deserializeNBT(nbt.getCompound("Essence"));
	}
	@Override public CompoundNBT save(CompoundNBT nbt){
		nbt.put("Essence", essenceHolder.serializeNBT());
		return super.save(nbt);
	}
}
