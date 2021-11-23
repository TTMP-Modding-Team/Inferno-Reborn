package ttmp.infernoreborn.contents.block;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.BlockItemUseContext;
import net.minecraft.item.ItemStack;
import net.minecraft.state.EnumProperty;
import net.minecraft.state.StateContainer;
import net.minecraft.state.properties.BlockStateProperties;
import net.minecraft.state.properties.DoubleBlockHalf;
import net.minecraft.util.ActionResultType;
import net.minecraft.util.Hand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.BlockRayTraceResult;
import net.minecraft.world.IWorld;
import net.minecraft.world.World;

import javax.annotation.Nullable;

public class BlazeCageBlock extends Block{
	public static final EnumProperty<DoubleBlockHalf> HALF = BlockStateProperties.DOUBLE_BLOCK_HALF;

	public BlazeCageBlock(Properties properties){
		super(properties);
		this.registerDefaultState(this.getStateDefinition().any().setValue(HALF,DoubleBlockHalf.LOWER));
	}

	@Nullable
	public BlockState getStateForPlacement(BlockItemUseContext pContext) {
		BlockPos blockpos = pContext.getClickedPos();
		return blockpos.getY() < 255 && pContext.getLevel().getBlockState(blockpos.above()).canBeReplaced(pContext) ? super.getStateForPlacement(pContext) : null;
	}

	@Override protected void createBlockStateDefinition(StateContainer.Builder<Block, BlockState> pBuilder){
		pBuilder.add(HALF);
	}

	@Override public void setPlacedBy(World pLevel, BlockPos pPos, BlockState pState, @Nullable LivingEntity pPlacer, ItemStack pStack){
		pLevel.setBlock(pPos.above(),this.defaultBlockState().setValue(HALF,DoubleBlockHalf.UPPER),3);
	}

	//for world gen
	public void placeAt(IWorld world, BlockPos pos, int n){
		world.setBlock(pos,this.defaultBlockState().setValue(HALF,DoubleBlockHalf.LOWER),n);
		world.setBlock(pos.above(),this.defaultBlockState().setValue(HALF,DoubleBlockHalf.UPPER),n);
	}

	@Override public OffsetType getOffsetType(){
		return OffsetType.XZ;
	}
}
