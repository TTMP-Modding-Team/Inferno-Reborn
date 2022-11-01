package ttmp.infernoreborn.contents.ability;

import ttmp.infernoreborn.util.EssenceType;

public final class VeteranAbility{
	private VeteranAbility(){}

	public static Ability meleeVeteran(){
		return new Ability(new Ability.Properties(0xe0c1ad, 0x873211)
				.onHit((entity, holder, event) -> {
					if(entity==event.getSource().getDirectEntity()){
						event.setAmount(event.getAmount()*(1.1f));
					}
				})
				.drops(EssenceType.FIRE, 5)
				.drops(EssenceType.DOMINANCE, 2));
	}

	public static Ability rangedVeteran(){
		return new Ability(new Ability.Properties(0xe0c1ad, 0x3c870a)
				.onHit((entity, holder, event) -> {
					if(entity!=event.getSource().getDirectEntity()&&event.getSource().isProjectile()&&!event.getSource().isMagic()){
						event.setAmount(event.getAmount()*(1.1f));
					}
				})
				.drops(EssenceType.AIR, 5)
				.drops(EssenceType.DOMINANCE, 2));
	}

	public static Ability magicVeteran(){
		return new Ability(new Ability.Properties(0xe0c1ad, 0x950cff)
				.onHit((entity, holder, event) -> {
					if(event.getSource().isMagic()){
						event.setAmount(event.getAmount()*(1.1f));
					}
				})
				.drops(EssenceType.MAGIC, 5)
				.drops(EssenceType.DOMINANCE, 2));
	}
}
