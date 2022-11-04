package ttmp.infernoreborn.api.shield;

import ttmp.infernoreborn.api.sigil.SigilSlot;

public interface ShieldModifier{
	void applyShieldModifier(SigilSlot slot, Shield originalShield, MutableShield shield);
}
