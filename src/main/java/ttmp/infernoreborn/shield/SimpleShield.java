package ttmp.infernoreborn.shield;

public class SimpleShield implements Shield{
	private final ShieldSkin skin;
	private final double maxDurability;
	private final double armor;
	private final double toughness;
	private final double resistance;
	private final double regen;
	private final double recovery;

	public SimpleShield(ShieldSkin skin, double maxDurability, double armor, double toughness, double resistance, double regen, double recovery){
		this.skin = skin;
		this.maxDurability = maxDurability;
		this.armor = armor;
		this.toughness = toughness;
		this.resistance = resistance;
		this.regen = regen;
		this.recovery = recovery;
	}

	@Override public ShieldSkin getSkin(){
		return skin;
	}
	@Override public double getMaxDurability(){
		return maxDurability;
	}
	@Override public double getArmor(){
		return armor;
	}
	@Override public double getToughness(){
		return toughness;
	}
	@Override public double getResistance(){
		return resistance;
	}
	@Override public double getRegen(){
		return regen;
	}
	@Override public double getRecovery(){
		return recovery;
	}

	@Override public String toString(){
		return "SimpleShield{"+
				"skin="+skin+
				", maxDurability="+maxDurability+
				", armor="+armor+
				", toughness="+toughness+
				", resistance="+resistance+
				", regen="+regen+
				", recovery="+recovery+
				'}';
	}
}
