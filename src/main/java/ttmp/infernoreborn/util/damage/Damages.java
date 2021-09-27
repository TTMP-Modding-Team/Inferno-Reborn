package ttmp.infernoreborn.util.damage;

import net.minecraft.entity.Entity;
import net.minecraft.util.DamageSource;
import ttmp.infernoreborn.contents.entity.WindEntity;

import static ttmp.infernoreborn.InfernoReborn.MODID;

public final class Damages{
	private Damages(){}

	private static final DamageSource FROSTBITE = new DamageSource(MODID+".frostbite").bypassArmor();
	private static final DamageSource ENGRAVING = new DamageSource(MODID+".engraving").bypassArmor().bypassMagic().bypassInvul();

	public static DamageSource frostbite(){
		return FROSTBITE;
	}
	public static DamageSource killerQueen(Entity source){
		return new NotStupidDamageSource(MODID+".killerQueen", null, source)
				.setIgnoreHeldItem().setNeverScalesWithDifficulty()
				.bypassArmor().bypassMagic().bypassInvul();
	}
	public static DamageSource midas(Entity source){
		return new NotStupidDamageSource(MODID+".midas", null, source)
				.setIgnoreHeldItem().setNeverScalesWithDifficulty()
				.bypassArmor().bypassMagic().bypassInvul();
	}
	public static DamageSource engraving(){
		return ENGRAVING;
	}
	public static DamageSource wind(WindEntity wind){
		return new NotStupidDamageSource(MODID+".wind", wind, wind.getOwner()).setProjectile();
	}
}
