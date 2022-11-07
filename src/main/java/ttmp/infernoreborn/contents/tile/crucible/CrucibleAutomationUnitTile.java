package ttmp.infernoreborn.contents.tile.crucible;

import net.minecraft.state.BooleanProperty;
import net.minecraft.tileentity.ITickableTileEntity;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.Direction;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import ttmp.infernoreborn.contents.ModTileEntities;

import java.util.Objects;

import static ttmp.infernoreborn.contents.block.ModProperties.moduleProperty;
import static ttmp.infernoreborn.contents.block.ModProperties.outputProperty;

public class CrucibleAutomationUnitTile extends TileEntity implements ITickableTileEntity{
	private int nextOutput;

	private final BlockPos.Mutable mpos = new BlockPos.Mutable();

	public CrucibleAutomationUnitTile(){
		super(ModTileEntities.CRUCIBLE_AUTOMATION_UNIT.get());
	}

	public boolean isAttached(Direction direction){
		return direction!=Direction.DOWN&&getBlockState().getValue(moduleProperty(direction));
	}
	public void setAttached(Direction direction, boolean attached){
		if(direction!=Direction.DOWN){
			boolean prevAttached = isAttached(direction);
			if(attached!=prevAttached){
				Objects.requireNonNull(level).setBlock(getBlockPos(), getBlockState()
						.setValue(moduleProperty(direction), attached), 3);
			}
		}
	}

	public boolean outputsTo(Direction direction){
		return direction!=Direction.UP&&direction!=Direction.DOWN&&
				getBlockState().getValue(outputProperty(direction));
	}
	public void setOutput(Direction direction, boolean output){
		if(direction==Direction.UP||direction==Direction.DOWN) return;
		boolean prevOutput = outputsTo(direction);
		if(output!=prevOutput) Objects.requireNonNull(level)
				.setBlock(getBlockPos(), getBlockState().setValue(outputProperty(direction), output), 3);
	}
	public boolean toggleSetOutput(Direction direction){
		if(direction==Direction.UP||direction==Direction.DOWN) return false;
		BooleanProperty property = outputProperty(direction);
		boolean newValue = !getBlockState().getValue(property);
		Objects.requireNonNull(level).setBlock(getBlockPos(), getBlockState()
				.setValue(property, newValue), 3);
		return newValue;
	}

	@Override public void tick(){
		if(level==null||level.isClientSide) return;
		long gameTime = level.getGameTime();
		CrucibleTile crucible = Crucible.crucible(level, mpos.set(getBlockPos()).move(Direction.DOWN));
		if(crucible==null) return;
		switch((int)(gameTime%10)){
			case 0: tickAttachedModule(crucible, Direction.UP); break;
			case 2: tickAttachedModule(crucible, Direction.NORTH); break;
			case 4: tickAttachedModule(crucible, Direction.SOUTH); break;
			case 6: tickAttachedModule(crucible, Direction.WEST); break;
			case 8: tickAttachedModule(crucible, Direction.EAST); break;
			case 9: output(crucible);
		}
	}

	private void tickAttachedModule(CrucibleTile crucible, Direction dir){
		if(!isAttached(dir)) return;
		mpos.set(this.getBlockPos()).move(Direction.UP);
		if(dir!=Direction.UP) mpos.move(dir);
		World level = Objects.requireNonNull(this.level);
		if(!level.isLoaded(mpos)) return;
		TileEntity te = level.getBlockEntity(mpos);
		if(te instanceof AutomationModule){
			AutomationModule m = (AutomationModule)te;
			m.operate(crucible);
		}
	}

	private void output(CrucibleTile crucible){
		// TODO
	}
}
