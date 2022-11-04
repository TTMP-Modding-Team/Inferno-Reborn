package ttmp.infernoreborn.contents.ability;

import net.minecraft.potion.EffectInstance;
import net.minecraft.potion.Effects;
import net.minecraft.world.World;
import ttmp.infernoreborn.api.ability.Ability;
import ttmp.infernoreborn.api.ability.AbilitySkill;
import ttmp.infernoreborn.api.essence.EssenceType;
import ttmp.infernoreborn.contents.ModEffects;
import ttmp.infernoreborn.contents.entity.WindEntity;

import java.util.function.Function;

public final class WindAbility{
	private WindAbility(){}

	public static Ability wind(int tier){
		return new Ability(new Ability.Properties(0xA1CEE3, 0xA1CEE3, 0xffffff)
				.addTargetedSkill(20, 120+tier*20L, windSkill((world) -> new WindEntity(world, 4+tier*2), 0xFFFFFF))
				.drops(EssenceType.AIR, 3*(tier+1)));
	}

	public static Ability blindingWind(){
		return new Ability(new Ability.Properties(0xA1CEE3, 0x303030, 0xffffff)
				.addTargetedSkill(30, 160, windSkill((world) -> new WindEntity(world, 4, new EffectInstance(Effects.BLINDNESS, 40)), 0xFFFFFF))
				.drops(EssenceType.AIR, 5)
				.drops(EssenceType.DEATH, 5));
	}

	public static Ability fuzzyWind(){
		return new Ability(new Ability.Properties(0xA1CEE3, 0xeaaeee, 0xffffff)
				.addTargetedSkill(30, 160, windSkill((world) -> new WindEntity(world, 4, new EffectInstance(Effects.CONFUSION, 100)), 0xFFFFFF))
				.drops(EssenceType.AIR, 5)
				.drops(EssenceType.MAGIC, 5));
	}

	public static Ability scaldingWind(){
		return new Ability(new Ability.Properties(0xA1CEE3, 0xFF680C, 0xffffff)
				.addTargetedSkill(30, 160, windSkill((world) -> new WindEntity(world, 4, 5), 0xFFFFFF))
				.drops(EssenceType.AIR, 5)
				.drops(EssenceType.FIRE, 5));
	}

	public static Ability freezingWind(){
		return new Ability(new Ability.Properties(0xA1CEE3, 0x92B9FA, 0xffffff)
				.addTargetedSkill(30, 160, windSkill((world) -> new WindEntity(world, 4, new EffectInstance(ModEffects.FROSTBITE.get(), 100)), 0xFFFFFF))
				.drops(EssenceType.AIR, 5)
				.drops(EssenceType.FROST, 5));
	}

	public static Ability poisoningWind(){
		return new Ability(new Ability.Properties(0xA1CEE3, 0x4E9331, 0xffffff)
				.addTargetedSkill(30, 160, windSkill((world) -> new WindEntity(world, 4, new EffectInstance(Effects.POISON, 100, 2)), 0xFFFFFF))
				.drops(EssenceType.AIR, 5)
				.drops(EssenceType.MAGIC, 5));
	}

	private static AbilitySkill.TargetedSkillAction windSkill(Function<World, WindEntity> constructor, int color){
		return (entity, holder, target) -> {
			WindEntity wind = constructor.apply(entity.level);
			wind.setOwner(entity);
			wind.setColor(color);
			wind.shootEntityToTarget(entity, target, 1);
			entity.level.addFreshEntity(wind);
			return true;
		};
	}
}
