package ttmp.infernoreborn.inventory;

import net.minecraft.nbt.CompoundNBT;
import net.minecraft.nbt.ListNBT;
import net.minecraftforge.common.util.Constants;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.capability.IFluidHandler;
import net.minecraftforge.fluids.capability.templates.FluidTank;
import ttmp.infernoreborn.api.recipe.RecipeHelper;

import javax.annotation.Nullable;
import java.util.function.IntConsumer;

public final class MultiFluidTank implements IFluidHandler, RecipeHelper.FluidTankAccessor{
	private final FluidTank[] tanks;
	private final int tankCapacity;

	@Nullable private FluidFilter fluidFilter;
	@Nullable private IntConsumer onContentsChanged;

	public MultiFluidTank(int tanks, int tankCapacity){
		this.tankCapacity = tankCapacity;
		if(tanks<0) throw new IllegalArgumentException("tanks");
		if(tankCapacity<0) throw new IllegalArgumentException("tankCapacity");
		this.tanks = new FluidTank[tanks];
		for(int i = 0; i<tanks; i++){
			int idx = i;
			this.tanks[idx] = new FluidTank(tankCapacity, f -> isFluidValid(idx, f)){
				@Override protected void onContentsChanged(){
					if(onContentsChanged!=null)
						onContentsChanged.accept(idx);
				}
			};
		}
	}

	public MultiFluidTank filter(@Nullable FluidFilter filter){
		this.fluidFilter = filter;
		return this;
	}

	public MultiFluidTank onContentsChanged(@Nullable IntConsumer onContentsChanged){
		this.onContentsChanged = onContentsChanged;
		return this;
	}

	@Override public int getTanks(){
		return tanks.length;
	}
	@Override public FluidStack getFluidInTank(int tank){
		return tanks[tank].getFluid();
	}

	public void setFluid(int tank, FluidStack fluid){
		tanks[tank].setFluid(fluid);
	}

	public boolean isEmpty(){
		for(FluidTank tank : tanks)
			if(!tank.isEmpty()) return false;
		return true;
	}
	public void clear(){
		for(FluidTank tank : tanks)
			tank.setFluid(FluidStack.EMPTY);
	}

	@Override public int getTankCapacity(int tank){
		return tankCapacity;
	}
	@Override public boolean isFluidValid(int tank, FluidStack stack){
		return fluidFilter==null||fluidFilter.isFluidValid(tank, stack);
	}
	@Override public int fill(FluidStack fluid, FluidAction action){
		// Do not allow same fluid to occupy multiple slots
		for(FluidTank tank : tanks)
			if(!tank.isEmpty()&&tank.getFluid().isFluidEqual(fluid))
				return tank.fill(fluid, action);
		for(FluidTank tank : tanks)
			if(tank.isEmpty())
				return tank.fill(fluid, action);
		return 0;
	}
	@Override public FluidStack drain(int maxDrain, FluidAction action){
		for(FluidTank tank : tanks){
			FluidStack drained = tank.drain(maxDrain, action);
			if(!drained.isEmpty()) return drained;
		}
		return FluidStack.EMPTY;
	}
	@Override public FluidStack drain(FluidStack fluid, FluidAction action){
		for(FluidTank tank : tanks){
			FluidStack drained = tank.drain(fluid, action);
			if(!drained.isEmpty()) return drained;
		}
		return FluidStack.EMPTY;
	}

	public void write(CompoundNBT tag){
		ListNBT list = new ListNBT();
		for(int i = 0; i<tanks.length; i++){
			FluidTank tank = tanks[i];
			if(!tank.isEmpty()){
				CompoundNBT tag2 = new CompoundNBT();
				tag2.putInt("Slot", i);
				list.add(tank.writeToNBT(tag2));
			}
		}
		if(!list.isEmpty()) tag.put("Fluids", list);
	}

	public void read(CompoundNBT tag){
		clear();
		if(!tag.contains("Fluids", Constants.NBT.TAG_LIST)) return;
		ListNBT list = tag.getList("Fluids", Constants.NBT.TAG_COMPOUND);
		for(int i = 0; i<list.size(); i++){
			CompoundNBT tag2 = list.getCompound(i);
			if(tag2.contains("Slot", Constants.NBT.TAG_INT)){
				int slot = tag2.getInt("Slot");
				if(slot>=0&&slot<getTanks())
					tanks[slot].readFromNBT(tag2);
			}
		}
	}

	@Override public int tanks(){
		return tanks.length;
	}
	@Override public FluidStack fluidAt(int tank){
		return tanks[tank].getFluid();
	}
	@Override public void drain(int tank, int amount){
		tanks[tank].drain(amount, FluidAction.EXECUTE);
	}

	@FunctionalInterface
	public interface FluidFilter{
		boolean isFluidValid(int tank, FluidStack stack);
	}
}
