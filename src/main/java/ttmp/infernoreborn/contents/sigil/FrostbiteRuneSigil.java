package ttmp.infernoreborn.contents.sigil;

import net.minecraft.entity.LivingEntity;
import net.minecraftforge.event.entity.living.LivingAttackEvent;
import ttmp.infernoreborn.contents.ModEffects;
import ttmp.infernoreborn.contents.sigil.context.SigilEventContext;
import ttmp.infernoreborn.util.LivingUtils;
import ttmp.infernoreborn.util.SigilSlot;

public class FrostbiteRuneSigil extends Sigil{
	public FrostbiteRuneSigil(Properties properties){
		super(properties);
	}

	@Override public void onAttack(SigilEventContext ctx, SigilSlot slot, LivingAttackEvent event, LivingEntity entity){
		LivingEntity attacked = event.getEntityLiving();
		LivingUtils.addStackEffect(attacked, ModEffects.FROSTBITE.get(), 100, 0, 1, 0);
	}
}
