package ttmp.infernoreborn.contents.tile;

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
import net.minecraft.tileentity.ITickableTileEntity;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.tileentity.TileEntityType;
import net.minecraft.util.DamageSource;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.SoundEvents;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.IFluidTank;
import net.minecraftforge.fluids.capability.IFluidHandler;
import net.minecraftforge.fluids.capability.templates.FluidTank;
import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.items.IItemHandlerModifiable;
import net.minecraftforge.items.ItemHandlerHelper;
import net.minecraftforge.items.ItemStackHandler;
import net.minecraftforge.items.wrapper.PlayerMainInvWrapper;
import ttmp.infernoreborn.InfernoReborn;
import ttmp.infernoreborn.contents.ModBlocks;
import ttmp.infernoreborn.contents.ModParticles;
import ttmp.infernoreborn.contents.ModTileEntities;
import ttmp.infernoreborn.contents.block.CrucibleHeat;
import ttmp.infernoreborn.contents.block.CrucibleHeatSource;
import ttmp.infernoreborn.util.Essence;
import ttmp.infernoreborn.util.EssenceHandler;
import ttmp.infernoreborn.util.EssenceHolder;
import ttmp.infernoreborn.util.EssenceType;
import ttmp.infernoreborn.util.Essences;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.List;
import java.util.Objects;

import static net.minecraft.state.properties.BlockStateProperties.LIT;
import static ttmp.infernoreborn.client.render.CrucibleTileEntityRenderer.STIR_ROTATION_INCREMENT;
import static ttmp.infernoreborn.contents.block.ModProperties.AUTOMATED;

public class CrucibleTile extends TileEntity implements ITickableTileEntity{
	private static final int MANUAL_STIR = 20;
	private static final int INPUT_INVENTORY_SIZE = 8;

	private final FluidTank fluid = new FluidTank(1000, fluid -> fluid.getFluid().isSame(Fluids.WATER)){
		@Nonnull @Override public FluidStack drain(FluidStack resource, FluidAction action){
			return FluidStack.EMPTY;
		}
		@Nonnull @Override public FluidStack drain(int maxDrain, FluidAction action){
			return FluidStack.EMPTY;
		}
		@Override protected void onContentsChanged(){
			markUpdated();
		}
	};
	private final ItemStackHandler inputs = new ItemStackHandler(INPUT_INVENTORY_SIZE){
		@Override public boolean isItemValid(int slot, @Nonnull ItemStack stack){
			return !Essence.isEssenceItem(stack);
		}
		@Override protected void onContentsChanged(int slot){
			markUpdated();
		}
	};
	private final EssenceHolder essences = new EssenceHolder(){
		@Override protected void onChanged(EssenceType type){
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
	};

	private int stir = 0;

	private CrucibleHeat heat = CrucibleHeat.NONE;
	private boolean updateHeat = true;

	@Nullable private EssenceType lastInsertedEssence;

	public float clientStir;

	protected CrucibleTile(TileEntityType<?> type){
		super(type);
	}
	public CrucibleTile(){
		this(ModTileEntities.CRUCIBLE.get());
	}

	private World expectLevel(){
		return Objects.requireNonNull(level);
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
	public boolean stirManually(){
		if(!fluid.isEmpty()&&stir<=MANUAL_STIR/2){
			stir = MANUAL_STIR;
			return true;
		}else return false;
	}
	public void increaseStir(){
		stir++;
	}

	public void empty(PlayerEntity player, boolean emptyWater){
		if(!dropContents(true, false, player, true)&&emptyWater&&!fluid.isEmpty()){
			fluid.setFluid(FluidStack.EMPTY);
			expectLevel().playSound(null, getBlockPos(),
					SoundEvents.BOAT_PADDLE_WATER, SoundCategory.PLAYERS, 1,
					0.8f+0.4f*expectLevel().random.nextFloat());
			dropContents(false, true, player, true);
		}
	}

	public void markUpdateHeat(){
		this.updateHeat = true;
	}

	public void markUpdated(){
		setChanged();
		if(hasLevel())
			Objects.requireNonNull(getLevel()).sendBlockUpdated(getBlockPos(), getBlockState(), getBlockState(), 3);
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
				if(!stack.isEmpty()) spawnItem(stack, launch);
				this.inputs.setStackInSlot(i, ItemStack.EMPTY);
				succeed = true;
			}
		}
		if(essences){
			for(ItemStack stack : Essence.items(this.essences)){
				if(player!=null) stack = tryGive(stack, player);
				if(!stack.isEmpty()) spawnItem(stack, launch);
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
			aabbCache = new AxisAlignedBB(
					getBlockPos().getX()+2/16.0, getBlockPos().getY()+2/16.0, getBlockPos().getZ()+2/16.0,
					getBlockPos().getX()+14/16.0, getBlockPos().getY()+1, getBlockPos().getZ()+14/16.0);
		}
		return aabbCache;
	}

	@Override public void tick(){
		World level = expectLevel();
		if(level.isClientSide){
			makeParticles();
			clientStir += calculateStirRotationIncrement(stir);
			if(clientStir>Math.PI) clientStir -= Math.PI*2;
			if(stir>-MANUAL_STIR) stir--;
			return;
		}
		if(stir>0) stir--;
		if(updateHeat){
			updateHeat = false;
			CrucibleHeat baseHeat = isOnLitCampfire() ? CrucibleHeat.CAMPFIRE : CrucibleHeat.NONE;
			BlockPos belowPos = getBlockPos().below();
			BlockState below = this.expectLevel().getBlockState(belowPos);
			CrucibleHeat newHeat = below.getBlock() instanceof CrucibleHeatSource ?
					CrucibleHeat.max(((CrucibleHeatSource)below.getBlock()).getHeat(this.expectLevel(), belowPos), baseHeat) :
					baseHeat;
			if(newHeat!=this.heat){
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
			if(e instanceof ItemEntity){
				ItemEntity ie = (ItemEntity)e;
				if(isExcluded(ie)) continue;
				Essence essence = Essence.from(ie.getItem());
				if(essence!=null){
					int leftover = this.essenceInputHandler.insertEssence(essence.getType(), essence.getAmount(), false);
					if(leftover!=essence.getAmount()){
						if(leftover>0){
							List<ItemStack> items = Essence.items(essence.getType(), leftover);
							ie.setItem(items.get(0));
							for(int i = 1; i<items.size(); i++){
								ItemEntity ie2 = new ItemEntity(expectLevel(), ie.getX(), ie.getY(), ie.getZ(), items.get(i));
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
			if(automated) return; // just don't process recipes 4head
			else{
				EssenceType[] values = EssenceType.values();
				// some bullshittery to guarantee last inserted essence to stay
				if(this.lastInsertedEssence!=null){
					// permute array to position last inserted essence type to last, then shuffle rest
					values[this.lastInsertedEssence.ordinal()] = values[values.length-1];
					values[values.length-1] = this.lastInsertedEssence;
					partialShuffle(values, values.length-1);
				}else partialShuffle(values, values.length);

				for(EssenceType t : values){
					int essence = this.essences.getEssence(t);
					if(essence<=0) continue;
					if(essence<essenceOverflow){
						essenceOverflow -= essence;
						for(ItemStack stack : Essence.items(t, essence))
							spawnItem(stack, true);
					}else{
						for(ItemStack stack : Essence.items(t, (int)essenceOverflow))
							spawnItem(stack, true);
						break;
					}
				}
			}
		}

		// TODO crafting stuff
	}

	@Nullable @Override public SUpdateTileEntityPacket getUpdatePacket(){
		return isAutomated() ? null : new SUpdateTileEntityPacket(getBlockPos(), 1, getSyncTag());
	}
	@Override public CompoundNBT getUpdateTag(){
		if(isAutomated()) return super.getUpdateTag();
		CompoundNBT tag = new CompoundNBT();
		tag.putByte("Heat", heat.id());
		return save(tag);
	}
	@Override public void onDataPacket(NetworkManager net, SUpdateTileEntityPacket pkt){
		loadSyncTag(pkt.getTag());
	}
	@Override public void handleUpdateTag(BlockState state, CompoundNBT tag){
		super.handleUpdateTag(state, tag);
		this.heat = CrucibleHeat.from(tag.getByte("Heat"));
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
		this.fluid.readFromNBT(tag.getCompound("Fluid"));
		this.inputs.deserializeNBT(tag.getCompound("Inputs"));
		this.essences.deserializeNBT(tag.getCompound("Essences"));
		this.stir = tag.getByte("Stir");
		this.updateHeat = true;
		super.load(state, tag);
	}
	@Override public CompoundNBT save(CompoundNBT tag){
		tag.put("Fluid", fluid.writeToNBT(new CompoundNBT()));
		tag.put("Inputs", inputs.serializeNBT());
		tag.put("Essences", essences.serializeNBT());
		tag.putByte("Stir", (byte)stir);
		return super.save(tag);
	}

	private void makeParticles(){
		World level = this.getLevel();
		if(level!=null){
			if(!fluid.isEmpty()&&heat!=CrucibleHeat.NONE&&
					level.random.nextFloat()<0.3f*heat.ordinal()){
				double y = (isOnCampfire() ? 6 : 2)/16.0;
				level.addParticle(ModParticles.CRUCIBLE_BUBBLE.get(),
						getBlockPos().getX()+0.25+level.random.nextDouble()/2,
						getBlockPos().getY()+y+0.02,
						getBlockPos().getZ()+0.25+level.random.nextDouble()/2,
						0, 0, 0);
			}
		}
	}

	private void spawnItem(ItemStack stack, boolean launch){
		if(expectLevel().isClientSide||stack.isEmpty()) return;
		ItemEntity e = new ItemEntity(expectLevel(), getBlockPos().getX()+.5,
				getBlockPos().getY()+.5, getBlockPos().getZ()+.5, stack);
		if(launch) e.setDeltaMovement(e.getDeltaMovement().add(0, 1, 0));
		setExcluded(e, true);
		expectLevel().addFreshEntity(e);
	}

	private void partialShuffle(EssenceType[] arr, int end){
		for(int i = end-1; i>0; i--){
			int j = expectLevel().random.nextInt(i+1);
			EssenceType temp = arr[i];
			arr[i] = arr[j];
			arr[j] = temp;
		}
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

	private static final String CRUCIBLE_EXCLUDED = InfernoReborn.MODID+":crucible_excluded";

	public static boolean isExcluded(ItemEntity e){
		return e.getTags().contains(CRUCIBLE_EXCLUDED);
	}
	public static void setExcluded(ItemEntity e, boolean excluded){
		if(excluded) e.addTag(CRUCIBLE_EXCLUDED);
		else e.removeTag(CRUCIBLE_EXCLUDED);
	}

	public static float calculateStirRotationIncrement(float stir){
		return stir>=0 ? STIR_ROTATION_INCREMENT : STIR_ROTATION_INCREMENT*Math.max(0, (MANUAL_STIR+stir)/MANUAL_STIR);
	}
}