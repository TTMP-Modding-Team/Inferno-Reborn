package ttmp.infernoreborn.util;

import com.google.common.collect.ListMultimap;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.ai.attributes.Attribute;
import net.minecraft.entity.ai.attributes.AttributeModifier;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.EquipmentSlotType;
import net.minecraft.item.ItemStack;
import net.minecraftforge.common.capabilities.ICapabilityProvider;
import net.minecraftforge.event.entity.living.LivingAttackEvent;
import net.minecraftforge.items.IItemHandlerModifiable;
import top.theillusivec4.curios.api.CuriosApi;
import ttmp.infernoreborn.api.sigil.Sigil;
import ttmp.infernoreborn.api.sigil.SigilHolder;
import ttmp.infernoreborn.api.sigil.SigilSlot;
import ttmp.infernoreborn.api.sigil.context.SigilEventContext;

import java.util.function.BiConsumer;

public final class SigilUtils{
	private SigilUtils(){}

	public static void applyAttributes(SigilHolder sigilHolder, SigilSlot slot, ListMultimap<Attribute, AttributeModifier> modifierMap){
		if(sigilHolder.isEmpty()) return;
		SigilEventContext context = sigilHolder.createContext();
		for(Sigil sigil : sigilHolder.getSigils()) sigil.applyAttributes(context, slot, modifierMap);
	}

	public static void onTick(SigilHolder sigilHolder, SigilSlot slot, PlayerEntity player){
		if(sigilHolder.isEmpty()) return;
		SigilEventContext context = sigilHolder.createContext();
		for(Sigil sigil : sigilHolder.getSigils()) sigil.onTick(context, slot, player);
	}

	public static void onAttack(SigilHolder sigilHolder, SigilSlot slot, LivingAttackEvent event, LivingEntity entity){
		if(sigilHolder.isEmpty()) return;
		SigilEventContext context = sigilHolder.createContext();
		for(Sigil sigil : sigilHolder.getSigils()) sigil.onAttack(context, slot, event, entity);
	}

	public static void onAttacked(SigilHolder sigilHolder, SigilSlot slot, LivingAttackEvent event){
		if(sigilHolder.isEmpty()) return;
		SigilEventContext context = sigilHolder.createContext();
		for(Sigil sigil : sigilHolder.getSigils()) sigil.onAttacked(context, slot, event);
	}

	public static void forEachSigilHolder(LivingEntity entity, BiConsumer<SigilHolder, SigilSlot> consumer){
		tickIfExists(entity, SigilSlot.BODY, consumer);
		for(EquipmentSlotType type : EquipmentSlotType.values()){
			ItemStack itemBySlot = entity.getItemBySlot(type);
			if(!itemBySlot.isEmpty()) tickIfExists(itemBySlot, SigilSlot.of(type), consumer);
		}
		@SuppressWarnings("ConstantConditions") IItemHandlerModifiable equippedCurios = CuriosApi.getCuriosHelper()
				.getEquippedCurios(entity)
				.orElse(null);
		//noinspection ConstantConditions
		if(equippedCurios!=null){
			for(int i = 0; i<equippedCurios.getSlots(); i++){
				ItemStack stackInSlot = equippedCurios.getStackInSlot(i);
				if(!stackInSlot.isEmpty()) tickIfExists(stackInSlot, SigilSlot.CURIO, consumer);
			}
		}
	}

	private static void tickIfExists(ICapabilityProvider provider, SigilSlot slot, BiConsumer<SigilHolder, SigilSlot> consumer){
		SigilHolder h = SigilHolder.of(provider);
		if(h!=null) consumer.accept(h, slot);
	}
}
