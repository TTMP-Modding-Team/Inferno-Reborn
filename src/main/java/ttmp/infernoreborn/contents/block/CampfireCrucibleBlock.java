package ttmp.infernoreborn.contents.block;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.CampfireBlock;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.BlockItemUseContext;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.particles.ParticleTypes;
import net.minecraft.state.StateContainer;
import net.minecraft.util.ActionResultType;
import net.minecraft.util.Direction;
import net.minecraft.util.Hand;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.SoundEvents;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.BlockRayTraceResult;
import net.minecraft.util.math.shapes.ISelectionContext;
import net.minecraft.util.math.shapes.VoxelShape;
import net.minecraft.util.math.shapes.VoxelShapes;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.World;
import net.minecraftforge.common.ToolType;

import java.util.Random;

import static net.minecraft.state.properties.BlockStateProperties.HORIZONTAL_FACING;
import static net.minecraft.state.properties.BlockStateProperties.LIT;
import static ttmp.infernoreborn.contents.block.ModProperties.AUTOMATED;

public class CampfireCrucibleBlock extends CrucibleBlock{
	private static final VoxelShape SHAPE = VoxelShapes.or(
			box(1, 4, 1, 15, 6, 15),
			box(1, 6, 1, 3, 16, 15),
			box(13, 6, 1, 15, 16, 15),
			box(3, 6, 1, 13, 16, 3),
			box(3, 6, 13, 13, 16, 15),
			box(0, 0, 0, 16, 4, 16)
	).optimize();

	public CampfireCrucibleBlock(Properties p){
		super(p);
	}

	@Override protected void createBlockStateDefinition(StateContainer.Builder<Block, BlockState> b){
		b.add(AUTOMATED, LIT, HORIZONTAL_FACING);
	}

	@Override public VoxelShape getShape(BlockState state, IBlockReader level, BlockPos pos, ISelectionContext ctx){
		return SHAPE;
	}

	@Override public BlockState getStateForPlacement(BlockItemUseContext ctx){
		return this.defaultBlockState().setValue(HORIZONTAL_FACING, ctx.getHorizontalDirection().getOpposite());
	}

	@Override public ActionResultType use(BlockState state, World level, BlockPos pos, PlayerEntity player, Hand hand, BlockRayTraceResult hit){
		if(hit.getDirection()==Direction.DOWN) return super.use(state, level, pos, player, hand, hit);

		boolean lit = state.getValue(LIT);

		ItemStack stack = player.getItemInHand(hand);
		if(lit){
			if(stack.getToolTypes().contains(ToolType.SHOVEL)){
				if(level.isClientSide){
					for(int i = 0; i<20; ++i) CampfireBlock.makeParticles(level, pos, false, true);
				}else{
					level.levelEvent(null, 1009, pos, 0);
					level.setBlock(pos, state.setValue(LIT, false), 11);
					stack.hurtAndBreak(1, player, c -> c.broadcastBreakEvent(hand));
				}
				return ActionResultType.sidedSuccess(level.isClientSide);
			}
		}else if(stack.getItem()==Items.FLINT_AND_STEEL){
			level.playSound(null, pos, SoundEvents.FLINTANDSTEEL_USE, SoundCategory.BLOCKS, 1, player.getRandom().nextFloat()*0.4f+0.8f);
			level.setBlock(pos, state.setValue(LIT, true), 11);
			stack.hurtAndBreak(1, player, c -> c.broadcastBreakEvent(hand));
			return ActionResultType.sidedSuccess(level.isClientSide);
		}else if(stack.getItem()==Items.FIRE_CHARGE){
			level.playSound(null, pos, SoundEvents.FIRECHARGE_USE, SoundCategory.BLOCKS, 1, (player.getRandom().nextFloat()-player.getRandom().nextFloat())*0.2f+1);
			level.setBlock(pos, state.setValue(LIT, true), 11);
			stack.shrink(1);
			return ActionResultType.sidedSuccess(level.isClientSide);
		}
		return super.use(state, level, pos, player, hand, hit);
	}

	@Override public void animateTick(BlockState state, World level, BlockPos pos, Random rand){
		if(state.getValue(LIT)){
			if(rand.nextInt(20)==0){
				level.playLocalSound(pos.getX()+.5, pos.getY()+.5, pos.getZ()+.5,
						SoundEvents.CAMPFIRE_CRACKLE, SoundCategory.BLOCKS, .5f+rand.nextFloat(),
						rand.nextFloat()*0.7f+0.6f, false);
			}

			if(rand.nextInt(10)==0){
				for(int i = 0; i<rand.nextInt(1)+1; ++i){
					level.addParticle(ParticleTypes.LAVA,
							pos.getX()+.5, pos.getY()+.125, pos.getZ()+.5,
							rand.nextFloat()/2, 0.00005, rand.nextFloat()/2);
				}
			}
		}
		super.animateTick(state, level, pos, rand);
	}
}
