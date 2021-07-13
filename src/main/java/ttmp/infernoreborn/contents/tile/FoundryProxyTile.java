package ttmp.infernoreborn.contents.tile;

import net.minecraft.block.BlockState;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.container.Container;
import net.minecraft.inventory.container.INamedContainerProvider;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.tileentity.TileEntityType;
import net.minecraft.util.Direction;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraft.world.World;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.items.CapabilityItemHandler;
import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.items.wrapper.EmptyHandler;
import ttmp.infernoreborn.contents.ModTileEntities;
import ttmp.infernoreborn.contents.block.FoundryBlock;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.Objects;

import static net.minecraft.state.properties.BlockStateProperties.HORIZONTAL_FACING;

public class FoundryProxyTile extends TileEntity implements INamedContainerProvider{
	public FoundryProxyTile(){
		this(ModTileEntities.FOUNDRY_PROXY.get());
	}
	public FoundryProxyTile(TileEntityType<?> tile){
		super(tile);
	}

	@Nullable private LazyOptional<IItemHandler> itemLO;
	private boolean placeholder;

	@Override public <T> LazyOptional<T> getCapability(@Nonnull Capability<T> cap, @Nullable Direction side){
		if(cap!=CapabilityItemHandler.ITEM_HANDLER_CAPABILITY) return super.getCapability(cap, side);
		if(itemLO==null||!itemLO.isPresent()||placeholder){
			FoundryTile foundry = findFoundry();
			if(foundry==null) return setPlaceholder();
			switch(getBlockState().getValue(FoundryBlock.PART)){
				case B000_FIREBOX:
				case B001_FIREBOX:
					itemLO = foundry.getEssenceInputLO();
					break;
				case B010_GRATE:
				case B011_GRATE:
					itemLO = foundry.getInputLO();
					break;
				case B100_MOLD:
				case B101_MOLD:
					itemLO = foundry.getOutputLO();
					break;
			}
			placeholder = false;
		}
		return itemLO.cast();
	}

	@Nullable private FoundryTile findFoundry(){
		World level = getLevel();
		if(level==null) return null;
		BlockPos estimatedHeadPos = getEstimatedHeadPos();
		if(!level.isLoaded(estimatedHeadPos)) return null;
		TileEntity t = level.getBlockEntity(estimatedHeadPos);
		return t instanceof FoundryTile ? (FoundryTile)t : null;
	}

	private BlockPos getEstimatedHeadPos(){
		BlockPos.Mutable mpos = new BlockPos.Mutable();
		mpos.set(getBlockPos());
		BlockState state = getBlockState();
		Direction dir = state.getValue(HORIZONTAL_FACING);
		FoundryBlock.Part part = state.getValue(FoundryBlock.PART);
		mpos.move(dir.getClockWise(), part.x);
		mpos.move(Direction.DOWN, part.y);
		mpos.move(dir, part.z);
		return mpos;
	}

	private <T> LazyOptional<T> setPlaceholder(){
		if(!placeholder){
			placeholder = true;
			return (itemLO = createPlaceholder()).cast();
		}
		return Objects.requireNonNull(itemLO).cast();
	}

	private static LazyOptional<IItemHandler> createPlaceholder(){
		return LazyOptional.of(() -> EmptyHandler.INSTANCE);
	}

	@Override public ITextComponent getDisplayName(){
		return new TranslationTextComponent("container.infernoreborn.foundry");
	}
	@Nullable @Override public Container createMenu(int id, PlayerInventory playerInventory, PlayerEntity player){
		FoundryTile foundry = findFoundry();
		return foundry!=null ? foundry.createMenu(id, playerInventory, player) : null;
	}
}
