package ttmp.infernoreborn.contents.sigil;

import ttmp.infernoreborn.shield.MutableShield;
import ttmp.infernoreborn.shield.Shield;
import ttmp.infernoreborn.shield.ShieldModifier;
import ttmp.infernoreborn.util.SigilSlot;

import java.util.Objects;

// TODO remove this shit
public class ShieldModifierSigil extends Sigil implements ShieldModifier{
	private final ShieldModifier shieldModifier;

	public ShieldModifierSigil(Properties properties, ShieldModifier shieldModifier){
		super(properties);
		this.shieldModifier = Objects.requireNonNull(shieldModifier);
	}

	@Override public void applyShieldModifier(SigilSlot slot, Shield originalShield, MutableShield shield){
		shieldModifier.applyShieldModifier(slot, originalShield, shield);
	}
}
