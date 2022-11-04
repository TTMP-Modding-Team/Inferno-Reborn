package ttmp.infernoreborn.contents.sigil;

import net.minecraft.entity.LivingEntity;
import net.minecraftforge.event.entity.living.LivingAttackEvent;
import ttmp.infernoreborn.api.sigil.Sigil;
import ttmp.infernoreborn.api.sigil.SigilSlot;
import ttmp.infernoreborn.api.sigil.context.SigilEventContext;

public class ScaldRuneSigil extends Sigil{
	public ScaldRuneSigil(Properties properties){
		super(properties);
	}

	@Override public void onAttack(SigilEventContext ctx, SigilSlot slot, LivingAttackEvent event, LivingEntity entity){
		LivingEntity attacked = event.getEntityLiving();
		attacked.setSecondsOnFire(5);
	}
}
