package ttmp.infernoreborn.contents.sigil;

import com.google.common.collect.ListMultimap;
import net.minecraft.entity.ai.attributes.Attribute;
import net.minecraft.entity.ai.attributes.AttributeModifier;
import ttmp.infernoreborn.api.LivingUtils;
import ttmp.infernoreborn.api.sigil.Sigil;
import ttmp.infernoreborn.api.sigil.SigilSlot;
import ttmp.infernoreborn.api.sigil.context.ItemContext;
import ttmp.infernoreborn.api.sigil.context.SigilEventContext;

import javax.annotation.Nullable;
import java.util.EnumMap;
import java.util.UUID;

public abstract class AttributeSigil extends Sigil{
	public AttributeSigil(Properties properties){
		super(properties);
	}

	private SigilSlot slotCache; // random states to save one parameter, because why not

	@Override public void applyAttributes(SigilEventContext ctx, SigilSlot slot, ListMultimap<Attribute, AttributeModifier> attributes){
		if(!shouldApplyAttribute(ctx, slot)) return;
		slotCache = slot;
		applyAttributes(slot, attributes);
		slotCache = null;
	}

	protected boolean shouldApplyAttribute(SigilEventContext ctx, SigilSlot slot){
		if(slot.isAvailableWithoutItem()) return true;
		ItemContext ictx = ctx.getAsItemContext();
		if(ictx==null) return true;
		return slot.isAvailableForItem(ictx.stack());
	}

	protected abstract void applyAttributes(SigilSlot slot, ListMultimap<Attribute, AttributeModifier> attributes);

	private final EnumMap<SigilSlot, UUID> defaultUuids = new EnumMap<>(SigilSlot.class);

	protected UUID getDefaultUuidFor(SigilSlot slot){
		return defaultUuids.computeIfAbsent(slot, m -> UUID.randomUUID());
	}

	protected void addToModifier(ListMultimap<Attribute, AttributeModifier> attributeMap, Attribute attribute, double amount, AttributeModifier.Operation operation){
		LivingUtils.addToModifier(attributeMap, attribute, null, getDefaultUuidFor(slotCache), amount, operation);
	}
	protected void addToModifier(ListMultimap<Attribute, AttributeModifier> attributeMap, Attribute attribute, @Nullable UUID targetUuid, double amount, AttributeModifier.Operation operation){
		LivingUtils.addToModifier(attributeMap, attribute, targetUuid, targetUuid!=null ? targetUuid : getDefaultUuidFor(slotCache), amount, operation);
	}
	protected void addToModifier(ListMultimap<Attribute, AttributeModifier> attributeMap, Attribute attribute, @Nullable UUID targetUuid, @Nullable UUID fallbackUuid, double amount, AttributeModifier.Operation operation){
		LivingUtils.addToModifier(attributeMap, attribute, targetUuid, fallbackUuid!=null ? fallbackUuid : getDefaultUuidFor(slotCache), amount, operation);
	}
}
