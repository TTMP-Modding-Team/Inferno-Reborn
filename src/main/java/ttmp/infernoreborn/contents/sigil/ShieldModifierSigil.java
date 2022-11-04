package ttmp.infernoreborn.contents.sigil;

import ttmp.infernoreborn.api.shield.MutableShield;
import ttmp.infernoreborn.api.shield.Shield;
import ttmp.infernoreborn.api.shield.ShieldModifier;
import ttmp.infernoreborn.api.sigil.Sigil;
import ttmp.infernoreborn.api.sigil.SigilSlot;

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
