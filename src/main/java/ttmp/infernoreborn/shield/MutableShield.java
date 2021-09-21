package ttmp.infernoreborn.shield;

public class MutableShield implements Shield{
	private final ShieldSkin skin;
	public double maxDurability;
	public double armor;
	public double toughness;
	public double resistance;
	public double regen;
	public double recovery;

	public MutableShield(Shield shield){
		this(shield.getSkin(), shield.getMaxDurability(), shield.getArmor(), shield.getToughness(), shield.getResistance(), shield.getRegen(), shield.getRecovery());
	}
	public MutableShield(ShieldSkin skin, double maxDurability, double armor, double toughness, double resistance, double regen, double recovery){
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
	public void setMaxDurability(double maxDurability){
		this.maxDurability = maxDurability;
	}
	@Override public double getArmor(){
		return armor;
	}
	public void setArmor(double armor){
		this.armor = armor;
	}
	@Override public double getToughness(){
		return toughness;
	}
	public void setToughness(double toughness){
		this.toughness = toughness;
	}
	@Override public double getResistance(){
		return resistance;
	}
	public void setResistance(double resistance){
		this.resistance = resistance;
	}
	@Override public double getRegen(){
		return regen;
	}
	public void setRegen(double regen){
		this.regen = regen;
	}
	@Override public double getRecovery(){
		return recovery;
	}
	public void setRecoveryTime(double recovery){
		this.recovery = recovery;
	}

	public Shield toImmutable(){
		return new SimpleShield(getSkin(), getMaxDurability(), getArmor(), getToughness(), getResistance(), getRegen(), getRecovery());
	}

	@Override public String toString(){
		return "MutableShield{"+
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
