package ttmp.infernoreborn.contents.tile.crucible;

import net.minecraft.block.BlockState;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.container.Container;
import net.minecraft.inventory.container.INamedContainerProvider;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.item.crafting.IRecipeType;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.tileentity.TileEntityType;
import net.minecraft.util.Direction;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.StringTextComponent;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraftforge.common.ForgeHooks;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.util.Constants;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.items.CapabilityItemHandler;
import net.minecraftforge.items.IItemHandler;
import ttmp.infernoreborn.contents.ModTileEntities;
import ttmp.infernoreborn.contents.container.FuelBasedStoveContainer;
import ttmp.infernoreborn.inventory.BaseInventory;

import javax.annotation.Nullable;

public abstract class FuelBasedStoveTile extends StoveTile implements INamedContainerProvider{
	@Nullable private String name;
	private final BaseInventory inv = new BaseInventory(1)
			.setItemValidator((slot, stack) -> getFuelTicks(stack)>0)
			.setOnContentsChanged(value -> setChanged());

	public FuelBasedStoveTile(TileEntityType<?> type){
		super(type);
	}

	public BaseInventory getInventory(){
		return inv;
	}

	@Override public ITextComponent getDisplayName(){
		return name!=null&&!name.isEmpty() ? new StringTextComponent(name) : getBaseName();
	}
	public void setName(@Nullable String name){
		this.name = name;
	}

	protected abstract ITextComponent getBaseName();

	@Override protected int consumeFuel(){
		ItemStack stack = inv.getStackInSlot(0);
		if(stack.isEmpty()) return 0;
		int fuelTicks = getFuelTicks(stack);
		if(fuelTicks<=0) return 0;
		stack.shrink(1);
		return fuelTicks;
	}

	protected abstract int getFuelTicks(ItemStack stack);

	@Override public void load(BlockState state, CompoundNBT tag){
		this.name = tag.contains("Name", Constants.NBT.TAG_STRING) ? tag.getString("Name") : null;
		this.inv.read(tag);
		super.load(state, tag);
	}
	@Override public CompoundNBT save(CompoundNBT tag){
		if(this.name!=null) tag.putString("Name", this.name);
		this.inv.write(tag);
		return super.save(tag);
	}

	@Nullable private LazyOptional<IItemHandler> itemHandlerLO;

	@Override protected void invalidateCaps(){
		if(itemHandlerLO!=null) itemHandlerLO.invalidate();
		super.invalidateCaps();
	}

	@Override public <T> LazyOptional<T> getCapability(Capability<T> cap, @Nullable Direction side){
		if(cap==CapabilityItemHandler.ITEM_HANDLER_CAPABILITY){
			if(itemHandlerLO==null) itemHandlerLO = LazyOptional.of(() -> this.getInventory());
			return itemHandlerLO.cast();
		}
		return LazyOptional.empty();
	}

	public static final class Furnace extends FuelBasedStoveTile{
		public Furnace(){
			super(ModTileEntities.FURNACE_STOVE.get());
		}
		@Override protected int getFuelTicks(ItemStack stack){
			return ForgeHooks.getBurnTime(stack, IRecipeType.SMELTING)*2;
		}
		@Override protected ITextComponent getBaseName(){
			return new TranslationTextComponent("container.infernoreborn.furnace_stove");
		}
		@Override public Container createMenu(int id, PlayerInventory playerInventory, PlayerEntity player){
			return FuelBasedStoveContainer.furnace(id, playerInventory, this.getInventory());
		}
	}

	public static final class Foundry extends FuelBasedStoveTile{
		public Foundry(){
			super(ModTileEntities.FOUNDRY_STOVE.get());
		}
		@Override protected int getFuelTicks(ItemStack stack){
			int burnTime = ForgeHooks.getBurnTime(stack, IRecipeType.BLASTING);
			return burnTime>=1600 ? burnTime/2 : 0;
		}
		@Override protected ITextComponent getBaseName(){
			return new TranslationTextComponent("container.infernoreborn.foundry_stove");
		}
		@Override public Container createMenu(int id, PlayerInventory playerInventory, PlayerEntity player){
			return FuelBasedStoveContainer.foundry(id, playerInventory, this.getInventory());
		}
	}

	public static final class Nether extends FuelBasedStoveTile{
		public Nether(){
			super(ModTileEntities.NETHER_STOVE.get());
		}
		@Override protected int getFuelTicks(ItemStack stack){
			return stack.getItem()==Items.BLAZE_ROD ? 2400 : 0;
		}
		@Override protected ITextComponent getBaseName(){
			return new TranslationTextComponent("container.infernoreborn.nether_stove");
		}
		@Override public Container createMenu(int id, PlayerInventory playerInventory, PlayerEntity player){
			return FuelBasedStoveContainer.nether(id, playerInventory, this.getInventory());
		}
	}
}
