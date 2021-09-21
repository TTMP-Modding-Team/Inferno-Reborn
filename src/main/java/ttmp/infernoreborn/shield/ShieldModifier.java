package ttmp.infernoreborn.shield;

import ttmp.infernoreborn.util.SigilSlot;

public interface ShieldModifier{
	void applyShieldModifier(SigilSlot slot, Shield originalShield, MutableShield shield);
}
