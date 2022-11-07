package ttmp.infernoreborn.contents.item;

import net.minecraft.block.Block;
import net.minecraft.item.BlockItem;
import net.minecraft.item.BlockItemUseContext;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ActionResultType;
import net.minecraft.util.Direction;
import ttmp.infernoreborn.contents.tile.crucible.AutomationModule;
import ttmp.infernoreborn.contents.tile.crucible.CrucibleAutomationUnitTile;
import ttmp.infernoreborn.util.AutomationModulePlaceContext;

import static ttmp.infernoreborn.util.ReplaceBlockContext.realClickedPos;

public class AutomationModuleBlockItem extends BlockItem{
	private final boolean connectToTop;
	private final boolean connectToSide;

	public AutomationModuleBlockItem(Block block, Properties p, boolean connectToTop, boolean connectToSide){
		super(block, p);
		this.connectToTop = connectToTop;
		this.connectToSide = connectToSide;
	}

	@Override public ActionResultType place(BlockItemUseContext ctx){
		if(canConnect(ctx.getClickedFace())){
			TileEntity te = ctx.getLevel().getBlockEntity(realClickedPos(ctx));
			if(te instanceof CrucibleAutomationUnitTile){
				AutomationModulePlaceContext newCtx = new AutomationModulePlaceContext(ctx);
				ActionResultType result = super.place(newCtx);
				if(result!=ActionResultType.FAIL&&!ctx.getLevel().isClientSide){
					TileEntity te2 = ctx.getLevel().getBlockEntity(newCtx.getClickedPos());
					if(te2 instanceof AutomationModule)
						((AutomationModule)te2).setAttached((CrucibleAutomationUnitTile)te, ctx.getClickedFace());
				}
				return result;
			}
		}
		return super.place(ctx);
	}

	protected boolean canConnect(Direction clickedFace){
		switch(clickedFace){
			case DOWN: return false;
			case UP: return connectToTop;
			default: return connectToSide;
		}
	}
}
