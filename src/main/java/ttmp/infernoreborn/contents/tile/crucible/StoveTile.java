package ttmp.infernoreborn.contents.tile.crucible;

import net.minecraft.block.BlockState;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.tileentity.ITickableTileEntity;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.tileentity.TileEntityType;

import static net.minecraft.state.properties.BlockStateProperties.LIT;

public abstract class StoveTile extends TileEntity implements ITickableTileEntity{
	private int burningTicks;

	public StoveTile(TileEntityType<?> type){
		super(type);
	}

	@Override public void tick(){
		if(this.level==null||this.level.isClientSide) return;
		boolean lit = burningTicks>0&&--burningTicks>0;
		if(!lit){
			burningTicks = consumeFuel();
			if(burningTicks>0) lit = true;
		}
		boolean blockLit = getBlockState().getValue(LIT);
		if(lit!=blockLit){
			level.setBlockAndUpdate(getBlockPos(), getBlockState().setValue(LIT, lit));
			setChanged();
		}
	}

	protected abstract int consumeFuel();

	@Override public void load(BlockState state, CompoundNBT tag){
		this.burningTicks = tag.getInt("BurningTicks");
		super.load(state, tag);
	}

	@Override public CompoundNBT save(CompoundNBT tag){
		if(burningTicks>0)
			tag.putInt("BurningTicks", burningTicks);
		return super.save(tag);
	}
}
