package ttmp.infernoreborn.contents.block;

import net.minecraft.state.BooleanProperty;
import net.minecraft.util.Direction;

public final class ModProperties{
	private ModProperties(){}

	public static final BooleanProperty NO_NETWORK = BooleanProperty.create("no_network");
	public static final BooleanProperty ACCELERATED = BooleanProperty.create("accelerated");
	public static final BooleanProperty HAS_FILTER = BooleanProperty.create("has_filter");

	public static final BooleanProperty AUTOMATED = BooleanProperty.create("automated");

	public static final BooleanProperty MODULE_U = BooleanProperty.create("module_u");
	public static final BooleanProperty MODULE_N = BooleanProperty.create("module_n");
	public static final BooleanProperty MODULE_S = BooleanProperty.create("module_s");
	public static final BooleanProperty MODULE_W = BooleanProperty.create("module_w");
	public static final BooleanProperty MODULE_E = BooleanProperty.create("module_e");
	public static final BooleanProperty OUT_N = BooleanProperty.create("out_n");
	public static final BooleanProperty OUT_S = BooleanProperty.create("out_s");
	public static final BooleanProperty OUT_W = BooleanProperty.create("out_w");
	public static final BooleanProperty OUT_E = BooleanProperty.create("out_e");

	public static BooleanProperty moduleProperty(Direction direction){
		switch(direction){
			case UP: return MODULE_U;
			case NORTH: return MODULE_N;
			case SOUTH: return MODULE_S;
			case WEST: return MODULE_W;
			case EAST: return MODULE_E;
			default: throw new IllegalArgumentException("direction");
		}
	}

	public static BooleanProperty outputProperty(Direction direction){
		switch(direction){
			case NORTH: return OUT_N;
			case SOUTH: return OUT_S;
			case WEST: return OUT_W;
			case EAST: return OUT_E;
			default: throw new IllegalArgumentException("direction");
		}
	}
}
