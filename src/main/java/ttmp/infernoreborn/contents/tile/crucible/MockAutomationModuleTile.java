package ttmp.infernoreborn.contents.tile.crucible;

import ttmp.infernoreborn.contents.ModTileEntities;

public class MockAutomationModuleTile extends BaseAutomationModuleTile{
	public MockAutomationModuleTile(){
		super(ModTileEntities.MOCK_AUTOMATION_MODULE.get());
	}

	@Override public void operate(CrucibleTile crucible){}
}
