package ttmp.infernoreborn.contents.sigil;

import ttmp.infernoreborn.shield.MutableShield;
import ttmp.infernoreborn.shield.Shield;
import ttmp.infernoreborn.shield.ShieldModifier;
import ttmp.infernoreborn.util.SigilSlot;

public class RunicShieldSigil extends Sigil implements ShieldModifier{
	public RunicShieldSigil(Properties properties){
		super(properties);
	}

	@Override public void applyShieldModifier(SigilSlot slot, Shield originalShield, MutableShield shield){
		shield.maxDurability += slot==SigilSlot.BODY ? 10 : 2;
	}
}
