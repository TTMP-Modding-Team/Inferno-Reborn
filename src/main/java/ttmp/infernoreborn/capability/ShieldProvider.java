package ttmp.infernoreborn.capability;

import ttmp.infernoreborn.shield.Shield;

import javax.annotation.Nullable;

public interface ShieldProvider{
	@Nullable Shield getShield();
}
