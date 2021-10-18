package ttmp.infernoreborn.contents.item.weapon;

import com.mojang.datafixers.util.Pair;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.item.UseAction;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Direction;
import net.minecraft.util.Hand;
import net.minecraft.world.World;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.ICapabilityProvider;
import net.minecraftforge.common.capabilities.ICapabilitySerializable;
import net.minecraftforge.common.util.Constants;
import net.minecraftforge.common.util.LazyOptional;
import ttmp.infernoreborn.InfernoReborn;
import ttmp.infernoreborn.capability.Caps;
import ttmp.infernoreborn.contents.entity.PaperBulletEntity;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.Objects;
import java.util.function.Function;

public class BookGunItem extends Item{
	public static final int RELOAD_TICKS = 20;

	public BookGunItem(Properties properties){
		super(properties);
	}

	@Override public ICapabilityProvider initCapabilities(ItemStack stack, @Nullable CompoundNBT nbt){
		return new Data();
	}

	@Override public UseAction getUseAnimation(ItemStack stack){
		Data data = data(stack);
		return data!=null&&!data.hasAmmo() ? UseAction.CROSSBOW : UseAction.NONE;
	}
	@Override public int getUseDuration(ItemStack stack){
		Data data = data(stack);
		return data!=null&&!data.hasAmmo() ? RELOAD_TICKS : Integer.MAX_VALUE;
	}

	@Override public ActionResult<ItemStack> use(World level, PlayerEntity player, Hand hand){
		ItemStack stack = player.getItemInHand(hand);
		Data data = data(stack);
		if(data!=null&&(data.hasAmmo()||selectAmmo(player, false)!=null)){
			player.startUsingItem(hand);
			return ActionResult.consume(stack);
		}else return ActionResult.fail(stack);
	}
	@Override public void onUseTick(World level, LivingEntity entity, ItemStack stack, int count){
		if(level.isClientSide||!(entity instanceof PlayerEntity)) return;
		Data data = data(stack);
		if(data!=null&&data.consumeAmmo(true)){
			PaperBulletEntity paperBulletEntity = Objects.requireNonNull(data.ammo).entityFactory.apply(level);
			paperBulletEntity.setOwner(entity);
			paperBulletEntity.setPos(entity.getX(), entity.getEyeY()-.1, entity.getZ());
			paperBulletEntity.shootFromRotation(entity, entity.xRot, entity.yRot, 0, .7f, 15f);
			level.addFreshEntity(paperBulletEntity);
			data.consumeAmmo(false);
			if(!data.hasAmmo()){
				entity.stopUsingItem();
				((PlayerEntity)entity).getCooldowns().addCooldown(this, 20);
			}
		}
	}
	@Override public ItemStack finishUsingItem(ItemStack stack, World level, LivingEntity entity){
		if(level.isClientSide||!(entity instanceof PlayerEntity)) return stack;
		Data data = data(stack);
		if(data!=null&&!data.hasAmmo()){
			Pair<Ammo, ItemStack> ammo = selectAmmo((PlayerEntity)entity, true);
			if(ammo!=null){
				data.ammo = ammo.getFirst();
				data.ammoStack = ammo.getSecond();
				data.remainingAmmo = ammo.getFirst().maxAmmo;
			}
		}
		((PlayerEntity)entity).getCooldowns().addCooldown(this, 20);
		return stack;
	}

	@Override public boolean shouldCauseReequipAnimation(ItemStack oldStack, ItemStack newStack, boolean slotChanged){
		if(oldStack.getItem()!=newStack.getItem()) return true;
		Data oldData = data(oldStack);
		Data newData = data(newStack);
		return oldData==null||newData==null||oldData.hasAmmo()!=newData.hasAmmo();
	}

	@Nullable @Override public CompoundNBT getShareTag(ItemStack stack){
		CompoundNBT tag = super.getShareTag(stack);
		Data data = data(stack);
		if(data!=null&&data.hasAmmo())
			return tag!=null ? data.serializeNBT().merge(tag) : data.serializeNBT();
		return tag;
	}
	@Override public void readShareTag(ItemStack stack, @Nullable CompoundNBT nbt){
		super.readShareTag(stack, nbt);
		if(nbt==null) return;
		Data data = data(stack);
		if(data!=null) data.deserializeNBT(nbt);
	}

	@Nullable public static Pair<Ammo, ItemStack> selectAmmo(PlayerEntity player, boolean consume){
		for(int i = 0; i<player.inventory.getContainerSize(); i++){
			ItemStack stack = player.inventory.getItem(i);
			Ammo ammo = getAmmoOfBook(stack);
			if(ammo!=null){
				ItemStack copy = stack.copy();
				copy.setCount(1);
				if(consume&&!player.isCreative()) stack.shrink(1);
				return Pair.of(ammo, copy);
			}
		}
		return player.isCreative() ? Pair.of(BOOK_AMMO, new ItemStack(Items.BOOK)) : null;
	}

	@Nullable public static Ammo getAmmoOfBook(ItemStack stack){
		if(!stack.isEmpty()){
			if(stack.getItem()==Items.BOOK) return BOOK_AMMO;
		}
		return null;
	}

	@SuppressWarnings("ConstantConditions")
	@Nullable
	private static Data data(ItemStack stack){
		return stack.getCapability(Caps.bookGunData).orElse(null);
	}

	public static final Ammo BOOK_AMMO = new Ammo(0xFFFFFF, 40, 2, PaperBulletEntity::new);

	public static final class Ammo{
		public final int color;
		public final int maxAmmo;
		public final int fireDelay;
		public final Function<World, PaperBulletEntity> entityFactory;

		public Ammo(int color, int maxAmmo, int fireDelay, Function<World, PaperBulletEntity> entityFactory){
			this.color = color;
			this.maxAmmo = maxAmmo;
			this.fireDelay = fireDelay;
			this.entityFactory = entityFactory;
		}
	}

	public static final class Data implements ICapabilitySerializable<CompoundNBT>{
		private ItemStack ammoStack = ItemStack.EMPTY;
		@Nullable private Ammo ammo;
		private int remainingAmmo;

		@Nullable private LazyOptional<Data> self;

		public boolean hasAmmo(){
			return remainingAmmo>0&&ammo!=null;
		}
		public boolean consumeAmmo(boolean simulate){
			if(!hasAmmo()) return false;
			if(!simulate&&--remainingAmmo<=0){
				ammoStack = ItemStack.EMPTY;
				ammo = null;
			}
			return true;
		}

		@Nonnull @Override public <T> LazyOptional<T> getCapability(@Nonnull Capability<T> cap, @Nullable Direction side){
			if(cap==Caps.bookGunData){
				if(self==null) self = LazyOptional.of(() -> this);
				return self.cast();
			}else return LazyOptional.empty();
		}
		@Override public CompoundNBT serializeNBT(){
			CompoundNBT tag = new CompoundNBT();
			if(hasAmmo()){
				tag.put("Ammo", ammoStack.serializeNBT());
				tag.putInt("Remaining", remainingAmmo);
			}
			return tag;
		}
		@Override public void deserializeNBT(CompoundNBT tag){
			if(tag.contains("Ammo", Constants.NBT.TAG_COMPOUND)){
				ammoStack = ItemStack.of(tag.getCompound("Ammo"));
				ammo = getAmmoOfBook(ammoStack);
				if(ammo==null){
					InfernoReborn.LOGGER.warn("Failed to parse ammo stack {}", ammoStack);
					ammoStack = ItemStack.EMPTY;
					remainingAmmo = 0;
				}else{
					remainingAmmo = tag.getInt("Remaining");
					if(remainingAmmo<=0){
						ammoStack = ItemStack.EMPTY;
						ammo = null;
					}
				}
			}else{
				ammoStack = ItemStack.EMPTY;
				ammo = null;
				remainingAmmo = 0;
			}
		}
	}
}
