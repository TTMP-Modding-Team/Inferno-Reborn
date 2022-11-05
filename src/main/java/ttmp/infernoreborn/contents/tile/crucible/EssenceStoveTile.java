package ttmp.infernoreborn.contents.tile.crucible;

import ttmp.infernoreborn.contents.ModTileEntities;

public class EssenceStoveTile extends StoveTile{
	public EssenceStoveTile(){
		super(ModTileEntities.ESSENCE_STOVE.get());
	}

	@Override protected int consumeFuel(){
		return 0;
	}
}
