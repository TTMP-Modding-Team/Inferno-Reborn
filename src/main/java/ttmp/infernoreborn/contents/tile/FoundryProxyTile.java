package ttmp.infernoreborn.contents.tile;

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

public abstract class FoundryProxyTile extends TileEntity implements INamedContainerProvider{
	public static FoundryProxyTile fireboxProxy(){
		return new FoundryProxyTile(ModTileEntities.FOUNDRY_FIREBOX_PROXY.get()){
			@Override protected LazyOptional<IItemHandler> getProxyItemHandler(FoundryTile tile){
				return tile.getEssenceInputLO();
			}
		};
	}
	public static FoundryProxyTile grateProxy(){
		return new FoundryProxyTile(ModTileEntities.FOUNDRY_GRATE_PROXY.get()){
			@Override protected LazyOptional<IItemHandler> getProxyItemHandler(FoundryTile tile){
				return tile.getInputLO();
			}
		};
	}
	public static FoundryProxyTile moldProxy(){
		return new FoundryProxyTile(ModTileEntities.FOUNDRY_MOLD_PROXY.get()){
			@Override protected LazyOptional<IItemHandler> getProxyItemHandler(FoundryTile tile){
				return tile.getOutputLO();
			}
		};
	}

	protected FoundryProxyTile(TileEntityType<?> tile){
		super(tile);
	}

	@Nullable private LazyOptional<IItemHandler> itemLO;
	private boolean placeholder;

	@Override public <T> LazyOptional<T> getCapability(@Nonnull Capability<T> cap, @Nullable Direction side){
		if(cap!=CapabilityItemHandler.ITEM_HANDLER_CAPABILITY) return super.getCapability(cap, side);
		if(itemLO==null||!itemLO.isPresent()||placeholder){
			FoundryTile foundry = findFoundry();
			if(foundry==null) return setPlaceholder();
			itemLO = getProxyItemHandler(foundry);
			placeholder = false;
		}
		return itemLO.cast();
	}

	protected abstract LazyOptional<IItemHandler> getProxyItemHandler(FoundryTile tile);

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
		return FoundryBlock.moveToOrigin(mpos, getBlockState());
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
