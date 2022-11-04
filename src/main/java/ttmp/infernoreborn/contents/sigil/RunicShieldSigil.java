package ttmp.infernoreborn.contents.sigil;

import ttmp.infernoreborn.api.shield.MutableShield;
import ttmp.infernoreborn.api.shield.Shield;
import ttmp.infernoreborn.api.shield.ShieldModifier;
import ttmp.infernoreborn.api.sigil.Sigil;
import ttmp.infernoreborn.api.sigil.SigilSlot;

public class RunicShieldSigil extends Sigil implements ShieldModifier{
	public RunicShieldSigil(Properties properties){
		super(properties);
	}

	@Override public void applyShieldModifier(SigilSlot slot, Shield originalShield, MutableShield shield){
		shield.maxDurability += slot==SigilSlot.BODY ? 10 : 2;
	}
}
