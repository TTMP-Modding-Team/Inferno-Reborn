package ttmp.infernoreborn.contents.sigil;

import net.minecraft.entity.LivingEntity;
import net.minecraftforge.event.entity.living.LivingAttackEvent;
import ttmp.infernoreborn.contents.sigil.context.SigilEventContext;
import ttmp.infernoreborn.util.SigilSlot;

public class ScaldRuneSigil extends Sigil{
	public ScaldRuneSigil(Properties properties){
		super(properties);
	}

	@Override public void onAttack(SigilEventContext ctx, SigilSlot slot, LivingAttackEvent event, LivingEntity entity){
		LivingEntity attacked = event.getEntityLiving();
		attacked.setSecondsOnFire(5);
	}
}
