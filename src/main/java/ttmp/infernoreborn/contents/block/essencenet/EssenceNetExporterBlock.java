package ttmp.infernoreborn.contents.block.essencenet;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.InventoryHelper;
import net.minecraft.item.BlockItemUseContext;
import net.minecraft.item.ItemStack;
import net.minecraft.state.StateContainer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ActionResultType;
import net.minecraft.util.Hand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.BlockRayTraceResult;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.World;
import ttmp.infernoreborn.contents.ModItems;
import ttmp.infernoreborn.contents.tile.EssenceNetExporterTile;

import static net.minecraft.state.properties.BlockStateProperties.FACING;

public class EssenceNetExporterBlock extends Block{
	public EssenceNetExporterBlock(Properties properties){
		super(properties);
	}

	@Override protected void createBlockStateDefinition(StateContainer.Builder<Block, BlockState> b){
		b.add(FACING);
	}

	@Override public BlockState getStateForPlacement(BlockItemUseContext ctx){
		return this.defaultBlockState().setValue(FACING, ctx.getPlayer()==null||!ctx.getPlayer().isCrouching() ?
				ctx.getClickedFace() : ctx.getClickedFace().getOpposite());
	}

	@SuppressWarnings("deprecation") @Override public ActionResultType use(BlockState pState, World pLevel, BlockPos pPos, PlayerEntity pPlayer, Hand pHand, BlockRayTraceResult pHit){
		if(pLevel.isClientSide) return ActionResultType.SUCCESS;
		TileEntity be = pLevel.getBlockEntity(pPos);
		if(be instanceof EssenceNetExporterTile){
			EssenceNetExporterTile exporter = (EssenceNetExporterTile)be;
			ItemStack s = pPlayer.getItemInHand(pHand);
			if(!s.isEmpty()){
				EssenceNetExporterTile.Template t = EssenceNetExporterTile.Template.fromItem(s.getItem());
				if(t!=null){
					exporter.setTemplate(t.equals(exporter.getTemplate()) ? null : t);
					return ActionResultType.CONSUME;
				}
				if(s.getItem()==ModItems.ACCELERATION_RUNE.get()){
					if(exporter.isAccelerated()){
						exporter.setAccelerated(false);
						InventoryHelper.dropItemStack(pLevel, pPlayer.getX(), pPlayer.getY(.5), pPlayer.getZ(), new ItemStack(ModItems.ACCELERATION_RUNE.get()));
					}else{
						s.shrink(1);
						exporter.setAccelerated(true);
					}
				}
			}
		}
		return ActionResultType.CONSUME;
	}

	@Override public boolean hasTileEntity(BlockState state){
		return true;
	}
	@Override public TileEntity createTileEntity(BlockState state, IBlockReader world){
		return new EssenceNetExporterTile();
	}
}
