package ttmp.infernoreborn.contents.sigil.holder;

import com.google.common.collect.ListMultimap;
import net.minecraft.entity.ai.attributes.Attribute;
import net.minecraft.entity.ai.attributes.AttributeModifier;
import net.minecraft.inventory.EquipmentSlotType;
import net.minecraftforge.common.capabilities.ICapabilityProvider;
import ttmp.infernoreborn.capability.Caps;
import ttmp.infernoreborn.contents.sigil.Sigil;

import javax.annotation.Nullable;
import java.util.Set;

public interface SigilHolder{
	@SuppressWarnings("ConstantConditions") @Nullable static SigilHolder of(ICapabilityProvider provider){
		return provider.getCapability(Caps.sigilHolder).orElse(null);
	}

	int getMaxPoints();

	Set<Sigil> getSigils();
	int getTotalPoint();
	boolean has(Sigil sigil);
	boolean canAdd(Sigil sigil);
	boolean add(Sigil sigil);
	boolean remove(Sigil sigil);

	void forceAdd(Sigil sigil);

	void clear();

	void applyAttributes(@Nullable EquipmentSlotType equipmentSlotType, ListMultimap<Attribute, AttributeModifier> modifierMap);

	long getGibberishSeed();
}
