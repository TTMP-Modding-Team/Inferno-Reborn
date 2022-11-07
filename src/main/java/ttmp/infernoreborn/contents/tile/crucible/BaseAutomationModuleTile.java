package ttmp.infernoreborn.contents.tile.crucible;

import net.minecraft.block.BlockState;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.nbt.NBTUtil;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.tileentity.TileEntityType;
import net.minecraft.util.Direction;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.common.util.Constants;

import javax.annotation.Nullable;
import java.util.Objects;

public abstract class BaseAutomationModuleTile extends TileEntity implements AutomationModule{
	@Nullable private BlockPos attachedUnitPos;
	@Nullable private Direction attachedUnitDirection;

	public BaseAutomationModuleTile(TileEntityType<?> type){
		super(type);
	}

	@Override public void setUnattached(){
		if(attachedUnitPos!=null){
			signalUnattached();
			attachedUnitPos = null;
			attachedUnitDirection = null;
			setChanged();
		}
	}
	@Override public void setAttached(CrucibleAutomationUnitTile tile, Direction direction){
		if(attachedUnitPos!=null){
			if(attachedUnitPos.equals(tile.getBlockPos())) return;
			signalUnattached();
		}
		attachedUnitPos = tile.getBlockPos();
		attachedUnitDirection = direction;
		tile.setAttached(direction, true);
		setChanged();
	}

	private void signalUnattached(){
		if(level==null) return;
		TileEntity te = level.getBlockEntity(Objects.requireNonNull(attachedUnitPos));
		if(te instanceof CrucibleAutomationUnitTile)
			((CrucibleAutomationUnitTile)te).setAttached(Objects.requireNonNull(attachedUnitDirection), false);
	}

	@Override public void load(BlockState state, CompoundNBT tag){
		if(tag.contains("AttachedUnit", Constants.NBT.TAG_COMPOUND)){
			this.attachedUnitPos = NBTUtil.readBlockPos(tag.getCompound("AttachedUnit"));
			this.attachedUnitDirection = Direction.from3DDataValue(tag.getByte("AttachedDirection"));
		}else{
			this.attachedUnitPos = null;
			this.attachedUnitDirection = null;
		}
		super.load(state, tag);
	}
	@Override public CompoundNBT save(CompoundNBT tag){
		if(attachedUnitPos!=null&&attachedUnitDirection!=null){
			tag.put("AttachedUnit", NBTUtil.writeBlockPos(attachedUnitPos));
			tag.putByte("AttachedDirection", (byte)attachedUnitDirection.get3DDataValue());
		}
		return super.save(tag);
	}
}
