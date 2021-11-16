package ttmp.infernoreborn.contents.block.essencenet;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.entity.LivingEntity;
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

import javax.annotation.Nullable;

import static net.minecraft.state.properties.BlockStateProperties.FACING;
import static ttmp.infernoreborn.contents.block.ModProperties.*;

public class EssenceNetExporterBlock extends Block{
	public EssenceNetExporterBlock(Properties properties){
		super(properties);
		registerDefaultState(defaultBlockState()
				.setValue(NO_NETWORK, false)
				.setValue(ACCELERATED, false)
				.setValue(HAS_FILTER, false));
	}

	@Override protected void createBlockStateDefinition(StateContainer.Builder<Block, BlockState> b){
		b.add(FACING, NO_NETWORK, ACCELERATED, HAS_FILTER);
	}

	@Override public BlockState getStateForPlacement(BlockItemUseContext ctx){
		return this.defaultBlockState().setValue(FACING, ctx.getPlayer()==null||!ctx.getPlayer().isCrouching() ?
				ctx.getClickedFace() : ctx.getClickedFace().getOpposite());
	}

	@Override public void setPlacedBy(World level, BlockPos pos, BlockState state, @Nullable LivingEntity placer, ItemStack stack){
		TileEntity blockEntity = level.getBlockEntity(pos);
		if(blockEntity instanceof EssenceNetExporterTile)
			((EssenceNetExporterTile)blockEntity).updateBlock();
	}

	@SuppressWarnings("deprecation") @Override public ActionResultType use(BlockState state, World level, BlockPos pos, PlayerEntity player, Hand hand, BlockRayTraceResult hit){
		ItemStack s = player.getItemInHand(hand);
		if(!s.isEmpty()){
			EssenceNetExporterTile.Template t = EssenceNetExporterTile.Template.fromItem(s.getItem());
			if(t!=null){
				if(!level.isClientSide){
					TileEntity blockEntity = level.getBlockEntity(pos);
					if(blockEntity instanceof EssenceNetExporterTile){
						EssenceNetExporterTile exporter = (EssenceNetExporterTile)blockEntity;
						exporter.setTemplate(t.equals(exporter.getTemplate()) ? null : t);
					}
				}
				return ActionResultType.sidedSuccess(level.isClientSide);
			}else if(s.getItem()==ModItems.ACCELERATION_RUNE.get()){
				if(!level.isClientSide){
					TileEntity blockEntity = level.getBlockEntity(pos);
					if(blockEntity instanceof EssenceNetExporterTile){
						EssenceNetExporterTile exporter = (EssenceNetExporterTile)blockEntity;
						if(exporter.isAccelerated()){
							exporter.setAccelerated(false);
							InventoryHelper.dropItemStack(level, player.getX(), player.getY(.5), player.getZ(), new ItemStack(ModItems.ACCELERATION_RUNE.get()));
						}else{
							s.shrink(1);
							exporter.setAccelerated(true);
						}
					}
				}
				return ActionResultType.sidedSuccess(level.isClientSide);
			}
		}
		return ActionResultType.PASS;
	}

	@Override public boolean hasTileEntity(BlockState state){
		return true;
	}
	@Override public TileEntity createTileEntity(BlockState state, IBlockReader world){
		return new EssenceNetExporterTile();
	}
}
