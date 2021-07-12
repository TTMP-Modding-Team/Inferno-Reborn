package ttmp.infernoreborn.contents.tile;

import net.minecraft.block.BlockState;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.tileentity.TileEntityType;
import net.minecraft.util.Direction;
import net.minecraft.util.math.BlockPos;
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

public class FoundryProxyTile extends TileEntity{
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
			World level = getLevel();
			if(level==null) return setPlaceholder();
			BlockPos estimatedHeadPos = getEstimatedHeadPos();
			if(!level.isLoaded(estimatedHeadPos)) return setPlaceholder();
			TileEntity t = level.getBlockEntity(estimatedHeadPos);
			if(!(t instanceof FoundryTile)) return setPlaceholder();

			FoundryTile foundry = (FoundryTile)t;
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

	private BlockPos getEstimatedHeadPos(){
		BlockPos pos = getBlockPos();
		BlockState state = getBlockState();
		Direction dir = state.getValue(HORIZONTAL_FACING);
		FoundryBlock.Part part = state.getValue(FoundryBlock.PART);
		// TODO
		return pos;
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
}
