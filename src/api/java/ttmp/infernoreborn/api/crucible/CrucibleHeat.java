package ttmp.infernoreborn.api.crucible;

public enum CrucibleHeat{
	NONE,
	CAMPFIRE,
	FURNACE,
	FOUNDRY,
	NETHER,
	ESSENCE;

	public byte id(){
		return (byte)ordinal();
	}

	public long maxEssence(){
		switch(this){
			case CAMPFIRE: return 3;
			case FURNACE: return 3*3;
			case FOUNDRY: return 3*3*3;
			case NETHER: return 3*3*3*3;
			case ESSENCE: return Long.MAX_VALUE;
			default: return 1;
		}
	}

	/**
	 * @return Whether water gets instantly boiled in non-automated crucible
	 */
	public boolean boilsWater(){
		return this==NETHER||this==ESSENCE;
	}

	public float damage(){
		switch(this){
			case CAMPFIRE: return 1;
			case FURNACE: return 2;
			case FOUNDRY: return 3;
			case NETHER: return 4;
			case ESSENCE: return 5;
			default: return 0;
		}
	}

	public static CrucibleHeat from(int id){
		CrucibleHeat[] values = CrucibleHeat.values();
		return values[Math.abs(id)%values.length];
	}

	public static CrucibleHeat max(CrucibleHeat h1, CrucibleHeat h2){
		return h1.ordinal()>h2.ordinal() ? h1 : h2;
	}

	public static CrucibleHeat getMinimumHeatRequired(long essences){
		for(CrucibleHeat h : values())
			if(essences<=h.maxEssence())
				return h;
		return ESSENCE;
	}
}
