package ttmp.infernoreborn.contents.block;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.container.Container;
import net.minecraft.inventory.container.INamedContainerProvider;
import net.minecraft.util.ActionResultType;
import net.minecraft.util.Hand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.BlockRayTraceResult;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraft.world.World;
import ttmp.infernoreborn.contents.container.StigmaScrapperContainer;

public class StigmaScrapperBlock extends Block{
	public StigmaScrapperBlock(Properties properties){
		super(properties);
	}

	@SuppressWarnings("deprecation")
	@Override
	public ActionResultType use(BlockState state, World world, BlockPos pos, PlayerEntity player, Hand hand, BlockRayTraceResult result){
		if(world.isClientSide) return ActionResultType.SUCCESS;
		player.openMenu(new INamedContainerProvider(){
			@Override public ITextComponent getDisplayName(){
				return new TranslationTextComponent("container.infernoreborn.stigma_scrapper");
			}
			@Override public Container createMenu(int id, PlayerInventory inv, PlayerEntity player){
				return new StigmaScrapperContainer(id, inv);
			}
		});
		return ActionResultType.CONSUME;
	}
}
