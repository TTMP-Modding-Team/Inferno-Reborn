package ttmp.infernoreborn.contents.tile.crucible;

import net.minecraft.block.BlockState;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.item.ItemEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.fluid.Fluids;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.network.NetworkManager;
import net.minecraft.network.play.server.SUpdateTileEntityPacket;
import net.minecraft.particles.ParticleTypes;
import net.minecraft.server.MinecraftServer;
import net.minecraft.tileentity.ITickableTileEntity;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.tileentity.TileEntityType;
import net.minecraft.util.DamageSource;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.SoundEvents;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.common.util.Constants;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.IFluidTank;
import net.minecraftforge.fluids.capability.IFluidHandler;
import net.minecraftforge.fluids.capability.IFluidHandler.FluidAction;
import net.minecraftforge.fluids.capability.templates.FluidTank;
import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.items.IItemHandlerModifiable;
import net.minecraftforge.items.ItemHandlerHelper;
import net.minecraftforge.items.ItemStackHandler;
import net.minecraftforge.items.wrapper.PlayerMainInvWrapper;
import net.minecraftforge.items.wrapper.RecipeWrapper;
import ttmp.infernoreborn.InfernoReborn;
import ttmp.infernoreborn.api.RecipeTypes;
import ttmp.infernoreborn.api.Simulation;
import ttmp.infernoreborn.api.crucible.CrucibleHeat;
import ttmp.infernoreborn.api.crucible.CrucibleHeatSource;
import ttmp.infernoreborn.api.crucible.CrucibleInventory;
import ttmp.infernoreborn.api.crucible.CrucibleRecipe;
import ttmp.infernoreborn.api.essence.Essence;
import ttmp.infernoreborn.api.essence.EssenceHandler;
import ttmp.infernoreborn.api.essence.EssenceHolder;
import ttmp.infernoreborn.api.essence.EssenceIngredient;
import ttmp.infernoreborn.api.essence.EssenceType;
import ttmp.infernoreborn.api.essence.Essences;
import ttmp.infernoreborn.contents.ModBlocks;
import ttmp.infernoreborn.contents.ModParticles;
import ttmp.infernoreborn.contents.ModTileEntities;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.List;
import java.util.Objects;

import static net.minecraft.state.properties.BlockStateProperties.LIT;
import static ttmp.infernoreborn.contents.block.ModProperties.AUTOMATED;

public class CrucibleTile extends TileEntity implements ITickableTileEntity{
	private final FluidTank fluid = new FluidTank(Crucible.FLUID_TANK_SIZE, fluid -> fluid.getFluid().isSame(Fluids.WATER)){
		@Nonnull @Override public FluidStack drain(FluidStack resource, FluidAction action){
			return FluidStack.EMPTY;
		}
		@Nonnull @Override public FluidStack drain(int maxDrain, FluidAction action){
			return FluidStack.EMPTY;
		}
		@Override protected void onContentsChanged(){
			updateRecipe = saveAndSync = true;
		}
	};
	private final ItemStackHandler inputs = new ItemStackHandler(Crucible.INPUT_INVENTORY_SIZE){
		@Override public boolean isItemValid(int slot, @Nonnull ItemStack stack){
			return !Essence.isEssenceItem(stack);
		}
		@Override protected void onContentsChanged(int slot){
			updateRecipe = saveAndSync = true;
		}
	};
	private final EssenceHolder essences = new EssenceHolder(){
		@Override protected void onChanged(EssenceType type){
			updateRecipe = saveAndSync = true;
			markUpdated();
		}
	};
	private final EssenceHandler essenceInputHandler = new EssenceHandler(){
		@Override public int insertEssence(EssenceType type, int essence, boolean simulate){
			int inserted = essences.insertEssence(type, Math.min(essence,
					(int)Math.min(Integer.MAX_VALUE, getMaxEssences()-essences.totalEssences())), simulate);
			if(inserted>0&&!simulate) lastInsertedEssence = type;
			return inserted;
		}
		@Override public int extractEssence(EssenceType type, int essence, boolean simulate){
			return 0;
		}
		@Override public Simulation<Essences> consume(EssenceIngredient ingredient){
			return Simulation.fail();
		}
	};
	private final CrucibleInventory crucibleInventory = new CrucibleInventoryImpl();

	private int stir = 0;

	private CrucibleHeat heat = CrucibleHeat.NONE;
	private boolean updateHeat = true;

	@Nullable private EssenceType lastInsertedEssence;

	@Nullable private CrucibleRecipeProcess process;
	private boolean updateRecipe = true;

	private boolean saveAndSync;

	public float clientStir;

	protected CrucibleTile(TileEntityType<?> type){
		super(type);
	}
	public CrucibleTile(){
		this(ModTileEntities.CRUCIBLE.get());
	}

	public IFluidHandler getFluidHandler(){
		return fluid;
	}
	public IFluidTank getFluidTank(){
		return fluid;
	}
	public IItemHandlerModifiable getInputs(){
		return inputs;
	}
	public Essences getEssences(){
		return essences;
	}
	public EssenceHandler getEssenceInputHandler(){
		return essenceInputHandler;
	}

	public CrucibleHeat getHeat(){
		return heat;
	}
	public long getMaxEssences(){
		return fluid.isEmpty() ? 0 : heat.maxEssence();
	}

	public int getManualStirPower(){
		return stir;
	}

	@Nullable public CrucibleRecipeProcess getProcess(){
		return process;
	}

	public boolean stirManually(){
		if(!fluid.isEmpty()&&stir<=Crucible.MANUAL_STIR_TICKS/2){
			stir = Crucible.MANUAL_STIR_TICKS;
			return true;
		}else return false;
	}
	public void increaseStir(){
		stir++;
	}

	public void empty(PlayerEntity player, boolean emptyWater){
		if(!dropContents(true, false, player, true)&&emptyWater&&!fluid.isEmpty()){
			fluid.setFluid(FluidStack.EMPTY);
			player.level.playSound(null, getBlockPos(),
					SoundEvents.BOAT_PADDLE_WATER, SoundCategory.PLAYERS, 1,
					0.8f+0.4f*player.level.random.nextFloat());
			dropContents(false, true, player, true);
		}
	}

	public void markUpdateHeat(){
		this.updateHeat = true;
	}

	public void markUpdated(){
		setChanged();
		if(level!=null) level.sendBlockUpdated(getBlockPos(), getBlockState(), getBlockState(), 3);
	}

	public void dropContents(){
		dropContents(true, true, null, false);
	}
	public boolean dropContents(boolean inputs, boolean essences, @Nullable PlayerEntity player, boolean launch){
		boolean succeed = false;
		if(inputs){
			for(int i = 0; i<this.inputs.getSlots(); i++){
				ItemStack stack = this.inputs.getStackInSlot(i);
				if(stack.isEmpty()) continue;
				if(player!=null) stack = tryGive(stack, player);
				if(!stack.isEmpty()) Crucible.spawnItem(level, getBlockPos(), stack, launch);
				this.inputs.setStackInSlot(i, ItemStack.EMPTY);
				succeed = true;
			}
		}
		if(essences){
			for(ItemStack stack : Essence.items(this.essences)){
				if(player!=null) stack = tryGive(stack, player);
				if(!stack.isEmpty()) Crucible.spawnItem(level, getBlockPos(), stack, launch);
				succeed = true;
			}
			this.essences.clear();
		}
		return succeed;
	}

	private ItemStack tryGive(ItemStack stack, PlayerEntity player){
		if(player.level.isClientSide) return stack;
		// shameless ripoff from ItemHandlerHelper#giveItemToPlayer, but with 'preferred item slot' and dropping removed
		IItemHandler inventory = new PlayerMainInvWrapper(player.inventory);
		// try adding it into the inventory
		if(!stack.isEmpty()) stack = ItemHandlerHelper.insertItemStacked(inventory, stack, false);
		// play sound if something got picked up
		if(stack.isEmpty()||stack.getCount()!=stack.getCount()){
			player.level.playSound(null, player.getX(), player.getY()+0.5, player.getZ(),
					SoundEvents.ITEM_PICKUP, SoundCategory.PLAYERS, 0.2f,
					((player.level.random.nextFloat()-player.level.random.nextFloat())*0.7f+1)*2);
		}
		return stack;
	}

	@Nullable private BlockPos boxPosCache;
	@Nullable private AxisAlignedBB aabbCache;
	private AxisAlignedBB box(){
		if(aabbCache==null||Objects.equals(boxPosCache, getBlockPos())){
			boxPosCache = getBlockPos();
			boolean onCampfire = isOnCampfire();
			aabbCache = new AxisAlignedBB(
					getBlockPos().getX()+2/16.0, getBlockPos().getY()+(onCampfire ? 6 : 2)/16.0, getBlockPos().getZ()+2/16.0,
					getBlockPos().getX()+14/16.0, getBlockPos().getY()+(onCampfire ? 20/16.0 : 1), getBlockPos().getZ()+14/16.0);
		}
		return aabbCache;
	}

	@Override public void tick(){
		World level = this.level;
		if(level==null) return;
		if(level.isClientSide){
			if(!fluid.isEmpty()&&heat!=CrucibleHeat.NONE&&
					level.random.nextFloat()<0.3f*heat.ordinal()){
				double y = (isOnCampfire() ? 6 : 2)/16.0;
				level.addParticle(ModParticles.CRUCIBLE_BUBBLE.get(),
						getBlockPos().getX()+0.25+level.random.nextDouble()/2,
						getBlockPos().getY()+y+0.02,
						getBlockPos().getZ()+0.25+level.random.nextDouble()/2,
						0, 0, 0);
			}
			clientStir += Crucible.calculateStirRotationIncrement(stir);
			if(clientStir>Math.PI) clientStir -= Math.PI*2;
			if(stir>-Crucible.MANUAL_STIR_TICKS) stir--;
			return;
		}
		boolean stir = this.stir>0;
		if(stir) this.stir--;
		if(updateHeat){
			updateHeat = false;
			CrucibleHeat baseHeat = isOnLitCampfire() ? CrucibleHeat.CAMPFIRE : CrucibleHeat.NONE;
			BlockPos belowPos = getBlockPos().below();
			BlockState below = level.getBlockState(belowPos);
			CrucibleHeat newHeat = below.getBlock() instanceof CrucibleHeatSource ?
					CrucibleHeat.max(((CrucibleHeatSource)below.getBlock()).getHeat(level, belowPos), baseHeat) :
					baseHeat;
			if(newHeat!=this.heat){
				updateRecipe = true;
				this.heat = newHeat;
				level.sendBlockUpdated(getBlockPos(), getBlockState(), getBlockState(), 3);
			}
		}
		boolean automated = isAutomated();
		if(!automated&&this.heat.boilsWater()&&!this.fluid.isEmpty()){
			this.fluid.setFluid(FluidStack.EMPTY);
			level.playSound(null, getBlockPos(), SoundEvents.FIRE_EXTINGUISH, SoundCategory.BLOCKS,
					0.5f, 2.6f+(level.random.nextFloat()-level.random.nextFloat())*0.8f);
			for(int l = 0; l<8; ++l)
				level.addParticle(ParticleTypes.LARGE_SMOKE,
						getBlockPos().getX()+.5+Math.random(),
						getBlockPos().getY()+.5+Math.random(),
						getBlockPos().getZ()+.5+Math.random(),
						0, 0, 0);
			float damage = this.heat.damage();
			if(damage>0){
				for(Entity e : level.getEntitiesOfClass(LivingEntity.class, new AxisAlignedBB(getBlockPos())
						.inflate(0.5f))){
					if(!e.isAlive()) continue;
					e.hurt(DamageSource.ON_FIRE, damage);
				}
			}
		}

		if(!automated&&level.getGameTime()%2==0) for(Entity e : level.getEntities(null, box())){
			if(!e.isAlive()) continue;
			if(e instanceof ItemEntity){ // TODO splash sound
				ItemEntity ie = (ItemEntity)e;
				if(Crucible.isExcluded(ie)) continue;
				Essence essence = Essence.from(ie.getItem());
				if(essence!=null){
					int inserted = this.essences.insertEssence(essence.getType(), essence.getAmount(), false);
					if(inserted>0){
						this.lastInsertedEssence = essence.getType();
						if(inserted==essence.getAmount()) ie.remove();
						else{
							List<ItemStack> items = Essence.items(essence.getType(), essence.getAmount()-inserted);
							ie.setItem(items.get(0));
							for(int i = 1; i<items.size(); i++){
								ItemEntity ie2 = new ItemEntity(level, ie.getX(), ie.getY(), ie.getZ(), items.get(i));
								ie2.setDeltaMovement(ie.getDeltaMovement());
								level.addFreshEntity(ie2);
							}
						}
					}
				}else{
					ItemStack stack2 = ie.getItem();
					for(int i = 0; i<inputs.getSlots(); i++){
						stack2 = this.inputs.insertItem(i, stack2, false);
						if(stack2.isEmpty()) break;
					}
					if(stack2.isEmpty()) ie.remove();
					else if(ie.getItem().getCount()!=stack2.getCount()) ie.setItem(stack2);
				}
			}else if(e instanceof LivingEntity){
				float damage = heat.damage();
				if(damage<=0) continue;
				e.hurt(DamageSource.HOT_FLOOR, damage);
			}
		}

		long essenceOverflow = this.essences.totalEssences()-getMaxEssences();
		if(essenceOverflow>0){
			updateRecipe = true;
			if(!automated){
				EssenceType[] values = EssenceType.values();
				// some bullshittery to guarantee last inserted essence to stay
				if(this.lastInsertedEssence!=null){
					// permute array to position last inserted essence type to last, then shuffle rest
					values[this.lastInsertedEssence.ordinal()] = values[values.length-1];
					values[values.length-1] = this.lastInsertedEssence;
					Crucible.partialShuffle(values, values.length-1, level.random);
				}else Crucible.partialShuffle(values, values.length, level.random);

				for(EssenceType t : values){
					int essence = this.essences.getEssence(t);
					if(essence<=0) continue;
					if(essence<essenceOverflow){
						essenceOverflow -= essence;
						for(ItemStack stack : Essence.items(t, essence))
							Crucible.spawnItem(level, getBlockPos(), stack, true);
						this.essences.setEssence(t, 0);
					}else{
						for(ItemStack stack : Essence.items(t, (int)essenceOverflow))
							Crucible.spawnItem(level, getBlockPos(), stack, true);
						this.essences.setEssence(t, (int)(this.essences.getEssence(t)-essenceOverflow));
						break;
					}
				}
			}
		}

		if(updateRecipe){
			updateRecipe = false;
			CrucibleRecipeProcess proc = searchRecipe(null);
			if(proc!=null){
				if(this.process!=null&&Objects.equals(this.process.getRecipeId(), proc.getRecipeId()))
					proc.setCurrentStir(this.process.getCurrentStir());
				this.process = proc;
			}
		}

		if(process==null) this.process = searchRecipe(null);
		else{
			if(stir) process.incrementStir();
			if(process.isWorkComplete()){
				if(process.getSimulation()!=null){
					if(!automated){ // If automated, the recipe should be handled by automation module
						if(fluid.getFluidAmount()>=process.getWaterRequirement()){
							fluid.drain(process.getWaterRequirement(), FluidAction.EXECUTE);
							CrucibleRecipe.Result result = process.getSimulation().apply();
							for(ItemStack output : result.outputs())
								Crucible.spawnItem(level, getBlockPos(), output, true);
							// TODO spawn particle and play sound if fluid output is discarded
							this.process = searchRecipe(process.getRecipe());
							this.updateRecipe = false;
						}
					}
				}else updateRecipe = true;
			}
		}

		if(saveAndSync){
			saveAndSync = false;
			markUpdated();
		}
	}

	@Nullable private CrucibleRecipeProcess searchRecipe(@Nullable CrucibleRecipe prevRecipe){
		if(crucibleInventory.isEmpty()) return null;
		MinecraftServer server = Objects.requireNonNull(level).getServer();
		if(server!=null){
			if(prevRecipe!=null){
				Simulation<CrucibleRecipe.Result> sim = prevRecipe.consume(crucibleInventory);
				if(sim.isSuccess()) return new CrucibleRecipeProcess(prevRecipe, sim, crucibleInventory);
			}
			for(CrucibleRecipe r : server.getRecipeManager().getAllRecipesFor(RecipeTypes.crucible())){
				if(r==prevRecipe) continue;
				Simulation<CrucibleRecipe.Result> sim = r.consume(crucibleInventory);
				if(sim.isSuccess()) return new CrucibleRecipeProcess(r, sim, crucibleInventory);
			}
		}else InfernoReborn.LOGGER.warn("Crucible is trying to search for recipes in client side!");
		return null;
	}

	@Nullable @Override public SUpdateTileEntityPacket getUpdatePacket(){
		return isAutomated() ? null : new SUpdateTileEntityPacket(getBlockPos(), 1, getSyncTag());
	}
	@Override public CompoundNBT getUpdateTag(){
		if(isAutomated()) return super.getUpdateTag();
		return super.save(getSyncTag());
	}
	@Override public void onDataPacket(NetworkManager net, SUpdateTileEntityPacket pkt){
		loadSyncTag(pkt.getTag());
	}
	@Override public void handleUpdateTag(BlockState state, CompoundNBT tag){
		super.load(state, tag);
		if(!state.hasProperty(AUTOMATED)||!state.getValue(AUTOMATED))
			loadSyncTag(tag);
	}

	private CompoundNBT getSyncTag(){
		CompoundNBT tag = new CompoundNBT();
		tag.put("Fluid", fluid.writeToNBT(new CompoundNBT()));
		tag.put("Inputs", inputs.serializeNBT());
		tag.put("Essences", essences.serializeNBT());
		tag.putByte("Heat", heat.id());
		return tag;
	}

	private void loadSyncTag(CompoundNBT tag){
		this.fluid.readFromNBT(tag.getCompound("Fluid"));
		this.inputs.deserializeNBT(tag.getCompound("Inputs"));
		this.essences.deserializeNBT(tag.getCompound("Essences"));
		this.heat = CrucibleHeat.from(tag.getByte("Heat"));
	}

	@Override public void load(BlockState state, CompoundNBT tag){
		this.process = tag.contains("Process", Constants.NBT.TAG_COMPOUND) ?
				new CrucibleRecipeProcess(tag.getCompound("Process")) : null;
		this.fluid.readFromNBT(tag.getCompound("Fluid"));
		this.inputs.deserializeNBT(tag.getCompound("Inputs"));
		this.essences.deserializeNBT(tag.getCompound("Essences"));
		this.stir = tag.getByte("Stir");
		this.updateHeat = true;
		this.updateRecipe = true;
		super.load(state, tag);
	}
	@Override public CompoundNBT save(CompoundNBT tag){
		if(process!=null) tag.put("Process", process.write());
		tag.put("Fluid", fluid.writeToNBT(new CompoundNBT()));
		tag.put("Inputs", inputs.serializeNBT());
		tag.put("Essences", essences.serializeNBT());
		tag.putByte("Stir", (byte)stir);
		return super.save(tag);
	}

	public boolean isAutomated(){
		if(level==null) return false;
		BlockState state = getBlockState();
		return state.hasProperty(AUTOMATED)&&state.getValue(AUTOMATED);
	}

	public boolean isOnCampfire(){
		BlockState state = getBlockState();
		return state.is(ModBlocks.CRUCIBLE_CAMPFIRE.get());
	}

	public boolean isOnLitCampfire(){
		BlockState state = getBlockState();
		return state.is(ModBlocks.CRUCIBLE_CAMPFIRE.get())&&state.getValue(LIT);
	}

	public final class CrucibleInventoryImpl extends RecipeWrapper implements CrucibleInventory{
		CrucibleInventoryImpl(){
			super(inputs);
		}

		@Override public EssenceHandler essences(){
			return essences;
		}
		@Override public CrucibleHeat heat(){
			return heat;
		}
		@Override public int waterLevel(){
			return fluid.getFluidAmount();
		}
	}
}