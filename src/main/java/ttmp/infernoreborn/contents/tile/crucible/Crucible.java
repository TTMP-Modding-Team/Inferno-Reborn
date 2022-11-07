package ttmp.infernoreborn.contents.tile.crucible;

import net.minecraft.entity.item.ItemEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IWorldReader;
import net.minecraft.world.World;
import ttmp.infernoreborn.api.essence.EssenceType;

import javax.annotation.Nullable;
import java.util.Random;

import static ttmp.infernoreborn.api.InfernoRebornApi.MODID;
import static ttmp.infernoreborn.client.render.CrucibleTileEntityRenderer.STIR_ROTATION_INCREMENT;

public final class Crucible{
	private Crucible(){}

	public static final int MANUAL_STIR_TICKS = 20;
	public static final int INPUT_INVENTORY_SIZE = 8;
	public static final int FLUID_TANK_SIZE = 4;
	public static final int FLUID_TANK_CAPACITY = 1000;

	private static final String CRUCIBLE_EXCLUDED = MODID+":crucible_excluded";

	static void partialShuffle(EssenceType[] arr, int end, Random random){
		for(int i = end-1; i>0; i--){
			int j = random.nextInt(i+1);
			EssenceType temp = arr[i];
			arr[i] = arr[j];
			arr[j] = temp;
		}
	}

	public static boolean isExcluded(ItemEntity e){
		return e.getTags().contains(CRUCIBLE_EXCLUDED);
	}

	public static void setExcluded(ItemEntity e, boolean excluded){
		if(excluded) e.addTag(CRUCIBLE_EXCLUDED);
		else e.removeTag(CRUCIBLE_EXCLUDED);
	}

	public static float calculateStirRotationIncrement(float stir){
		return stir>=0 ? STIR_ROTATION_INCREMENT : STIR_ROTATION_INCREMENT*Math.max(0, (MANUAL_STIR_TICKS+stir)/MANUAL_STIR_TICKS);
	}

	public static void spawnItem(@Nullable World level, BlockPos pos, ItemStack stack, boolean launch){
		if(level==null||level.isClientSide||stack.isEmpty()) return;
		ItemEntity e = new ItemEntity(level, pos.getX()+.5, pos.getY()+.5, pos.getZ()+.5, stack);
		if(launch) e.setDeltaMovement(e.getDeltaMovement().add(0, .5, 0));
		setExcluded(e, true);
		level.addFreshEntity(e);
	}

	@Nullable public static CrucibleTile crucible(IWorldReader level, BlockPos pos){
		TileEntity te = level.getBlockEntity(pos);
		return te instanceof CrucibleTile ? (CrucibleTile)te : null;
	}
}
