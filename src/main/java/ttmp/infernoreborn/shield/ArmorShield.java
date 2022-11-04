package ttmp.infernoreborn.shield;

import ttmp.infernoreborn.api.shield.Shield;
import ttmp.infernoreborn.api.shield.SimpleShield;

import javax.annotation.Nullable;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public final class ArmorShield{
	private static final Map<String, ArmorShield> shields = new HashMap<>();

	public static final Shield DEFAULT_ARMOR_SHIELD = new SimpleShield(ShieldSkins.DEFAULT_ARMOR, 3, 0, 0, 0, .5, .25);
	public static final Shield DEFAULT_CURIO_SHIELD = new SimpleShield(ShieldSkins.DEFAULT_CURIO, 1, 0, 0, 0, 1/3.0, .25);
	public static final Shield BODY_SHIELD = new SimpleShield(ShieldSkins.DEFAULT_CURIO, 1, 0, 0, 0, 1, .5);

	static{
		add("crimson", ArmorSets.CRIMSON, new SimpleShield(ShieldSkins.CRIMSON, 15, 5, 0, 0, 3/2.0, .5));
		add("thanatos_light", ArmorSets.THANATOS_LIGHT, new SimpleShield(ShieldSkins.THANATOS, 30, 10, 0, 0, 2/3.0, 1/3.0));
		add("thanatos_heavy", ArmorSets.THANATOS_HEAVY, new SimpleShield(ShieldSkins.THANATOS, 60, 20, 4, .5, 1, .5));
	}

	public static void add(String id, ArmorSet armorSet, Shield shield){
		if(shields.putIfAbsent(id, new ArmorShield(id, armorSet, shield))!=null)
			throw new IllegalStateException("Duplicated armor shield with ID '"+id+'\'');
	}

	public static Collection<ArmorShield> getArmorShields(){
		return shields.values();
	}

	@Nullable public static ArmorShield get(String id){
		return shields.get(id);
	}

	public final String id;
	public final ArmorSet armorSet;
	public final Shield shield;

	private ArmorShield(String id, ArmorSet armorSet, Shield shield){
		this.id = Objects.requireNonNull(id);
		this.armorSet = Objects.requireNonNull(armorSet);
		this.shield = Objects.requireNonNull(shield);
	}
}
