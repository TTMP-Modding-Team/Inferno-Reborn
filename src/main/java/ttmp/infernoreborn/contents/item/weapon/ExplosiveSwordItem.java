package ttmp.infernoreborn.contents.item.weapon;

import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.IItemTier;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.item.SwordItem;
import net.minecraft.item.UseAction;
import net.minecraft.item.crafting.Ingredient;
import net.minecraft.particles.ParticleTypes;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.SoundEvents;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraft.world.Explosion;
import net.minecraft.world.World;
import ttmp.infernoreborn.api.TickingTaskHandler;
import ttmp.infernoreborn.util.damage.LivingOnlyEntityDamageSource;

import javax.annotation.Nullable;

public class ExplosiveSwordItem extends SwordItem{
	private static final IItemTier MAT = new IItemTier(){
		@Override public int getUses(){
			return 1500;
		}
		@Override public float getSpeed(){
			return 9;
		}
		@Override public float getAttackDamageBonus(){
			return 5;
		}
		@Override public int getLevel(){
			return 4;
		}
		@Override public int getEnchantmentValue(){
			return 18;
		}
		@Nullable private Ingredient repairIngredient;
		@Override public Ingredient getRepairIngredient(){
			if(repairIngredient==null) repairIngredient = Ingredient.of(Items.GUNPOWDER);
			return repairIngredient;
		}
	};
	private static final int E1 = 40;
	private static final int E2 = 80;
	private static final int E3 = 120;
	private static final int MAX_USE = 140;

	public ExplosiveSwordItem(Properties properties){
		super(MAT, 3, -2.4f, properties);
	}

	@Override public ActionResult<ItemStack> use(World world, PlayerEntity player, Hand hand){
		player.startUsingItem(hand);
		return ActionResult.consume(player.getItemInHand(hand));
	}

	@Override public void onUseTick(World world, LivingEntity entity, ItemStack stack, int ticks){
		int ticksLeft = MAX_USE-ticks;
		if(ticksLeft==E1||ticksLeft==E2||ticksLeft==E3){
			if(world.isClientSide){
				for(int i = 16; i>0; i--)
					world.addParticle(ParticleTypes.FLAME, entity.getRandomX(1), entity.getRandomY(), entity.getRandomZ(1), 0, 0, 0);
			}else world.playSound(null, entity.getX(), entity.getY(), entity.getZ(), SoundEvents.PISTON_EXTEND, SoundCategory.PLAYERS, 1, 1);
		}else if(world.isClientSide) world.addParticle(ParticleTypes.SMOKE, entity.getRandomX(1), entity.getRandomY(), entity.getRandomZ(1), 0, 0, 0);
	}

	@Override public ItemStack finishUsingItem(ItemStack stack, World world, LivingEntity entity){
		detonate(stack, world, entity, MAX_USE);
		return stack;
	}
	@Override public void releaseUsing(ItemStack stack, World world, LivingEntity entity, int ticks){
		detonate(stack, world, entity, MAX_USE-ticks);
	}

	protected void detonate(ItemStack stack, World world, LivingEntity entity, int ticks){
		if(world.isClientSide||ticks<E1) return;
		TickingTaskHandler h = TickingTaskHandler.of(world);
		if(h==null) return;
		Vector3d lookAngle = entity.getLookAngle();

		int dur;
		int cooldown;
		if(ticks<E2){
			createExplosion(world, entity, lookAngle, 3, 2);
			dur = 2;
			cooldown = 60;
		}else if(ticks<E3){
			createExplosion(world, entity, lookAngle, 3, 2);
			h.add(3, () -> createExplosion(world, entity, lookAngle, 6, 3));
			dur = 4;
			cooldown = 80;
		}else{
			createExplosion(world, entity, lookAngle, 3, 2);
			h.add(2, () -> createExplosion(world, entity, lookAngle, 6, 3));
			h.add(4, () -> createExplosion(world, entity, lookAngle, 10, 4));
			dur = 6;
			cooldown = 100;
		}
		stack.hurtAndBreak(dur, entity, e -> e.broadcastBreakEvent(entity.getUsedItemHand()));
		if(entity instanceof PlayerEntity) ((PlayerEntity)entity).getCooldowns().addCooldown(this, cooldown);
	}

	protected static void createExplosion(World world, LivingEntity entity, Vector3d angle, double distance, float power){
		world.explode(entity,
				new LivingOnlyEntityDamageSource("explosion.player", null, entity).setExplosion(),
				null,
				entity.getX()+angle.x*distance,
				entity.getEyeY()+angle.y*distance,
				entity.getZ()+angle.z*distance,
				power,
				false,
				Explosion.Mode.NONE);
	}

	@Override public UseAction getUseAnimation(ItemStack stack){
		return UseAction.NONE;
	}
	@Override public int getUseDuration(ItemStack stack){
		return MAX_USE;
	}
}
