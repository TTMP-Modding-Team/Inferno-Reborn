package ttmp.infernoreborn.contents.sigil;

import com.google.common.collect.ListMultimap;
import net.minecraft.entity.MobEntity;
import net.minecraft.entity.ai.attributes.Attribute;
import net.minecraft.entity.ai.attributes.AttributeModifier;
import net.minecraft.inventory.EquipmentSlotType;
import ttmp.infernoreborn.contents.sigil.context.ItemContext;
import ttmp.infernoreborn.contents.sigil.context.SigilEventContext;
import ttmp.infernoreborn.util.LivingUtils;

import javax.annotation.Nullable;
import java.util.EnumMap;
import java.util.UUID;

public abstract class AttributeSigil extends Sigil{
	public AttributeSigil(Properties properties){
		super(properties);
	}

	@Override public boolean canBeAttachedTo(SigilEventContext context){
		ItemContext ictx = context.getAsItemContext();
		return canBeAttachedTo(context, ictx!=null ? MobEntity.getEquipmentSlotForItem(ictx.stack()) : null);
	}

	private Mode modeCache; // random states to save one parameter, because why not

	@Override public void applyAttributes(SigilEventContext ctx, @Nullable EquipmentSlotType equipmentSlotType, ListMultimap<Attribute, AttributeModifier> attributes){
		if(!shouldApplyAttribute(ctx, equipmentSlotType)) return;
		Mode mode;
		if(equipmentSlotType==null) mode = Mode.BODY;
		else switch(equipmentSlotType){
			case MAINHAND:
				mode = Mode.MAINHAND;
				break;
			case OFFHAND:
				mode = Mode.OFFHAND;
				break;
			case FEET:
				mode = Mode.FEET;
				break;
			case LEGS:
				mode = Mode.LEGS;
				break;
			case CHEST:
				mode = Mode.CHEST;
				break;
			case HEAD:
				mode = Mode.HEAD;
				break;
			default:
				throw new IllegalStateException("Fix the fucking bug @Tictim");
		}
		modeCache = mode;
		applyAttributes(mode, attributes);
		modeCache = null;
	}

	protected abstract boolean canBeAttachedTo(SigilEventContext ctx, @Nullable EquipmentSlotType equipmentSlotType);

	protected boolean shouldApplyAttribute(SigilEventContext ctx, @Nullable EquipmentSlotType equipmentSlotType){
		if(equipmentSlotType==null) return true;
		ItemContext ictx = ctx.getAsItemContext();
		if(ictx==null) return true;
		EquipmentSlotType equipmentSlotForItem = MobEntity.getEquipmentSlotForItem(ictx.stack());
		return equipmentSlotForItem==equipmentSlotType;
	}

	protected abstract void applyAttributes(Mode mode, ListMultimap<Attribute, AttributeModifier> attributes);

	private final EnumMap<Mode, UUID> defaultUuids = new EnumMap<>(Mode.class);

	protected UUID getDefaultUuidFor(Mode mode){
		return defaultUuids.computeIfAbsent(mode, m -> UUID.randomUUID());
	}

	protected void addToModifier(ListMultimap<Attribute, AttributeModifier> attributeMap, Attribute attribute, double amount, AttributeModifier.Operation operation){
		LivingUtils.addToModifier(attributeMap, attribute, null, getDefaultUuidFor(modeCache), amount, operation);
	}
	protected void addToModifier(ListMultimap<Attribute, AttributeModifier> attributeMap, Attribute attribute, @Nullable UUID targetUuid, double amount, AttributeModifier.Operation operation){
		LivingUtils.addToModifier(attributeMap, attribute, targetUuid, targetUuid!=null ? targetUuid : getDefaultUuidFor(modeCache), amount, operation);
	}
	protected void addToModifier(ListMultimap<Attribute, AttributeModifier> attributeMap, Attribute attribute, @Nullable UUID targetUuid, @Nullable UUID fallbackUuid, double amount, AttributeModifier.Operation operation){
		LivingUtils.addToModifier(attributeMap, attribute, targetUuid, fallbackUuid!=null ? fallbackUuid : getDefaultUuidFor(modeCache), amount, operation);
	}

	public enum Mode{
		BODY,
		MAINHAND,
		OFFHAND,
		HEAD,
		LEGS,
		CHEST,
		FEET
	}
}
