package ttmp.infernoreborn.api.shield;

public interface Shield{
	ShieldSkin getSkin();

	double getMaxDurability();

	double getArmor();
	double getToughness();
	double getResistance();

	/**
	 * Amount of shield generated each 20 ticks
	 */
	double getRegen();
	/**
	 * Amount of shield replenished each 20 ticks when shield is down
	 */
	double getRecovery();
}
