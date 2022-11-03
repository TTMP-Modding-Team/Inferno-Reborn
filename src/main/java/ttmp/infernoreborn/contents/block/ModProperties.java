package ttmp.infernoreborn.contents.block;

import net.minecraft.state.BooleanProperty;

public final class ModProperties{
	private ModProperties(){}

	public static final BooleanProperty NO_NETWORK = BooleanProperty.create("no_network");
	public static final BooleanProperty ACCELERATED = BooleanProperty.create("accelerated");
	public static final BooleanProperty HAS_FILTER = BooleanProperty.create("has_filter");

	public static final BooleanProperty AUTOMATED = BooleanProperty.create("automated");
}
