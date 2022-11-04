package ttmp.infernoreborn.event;

import com.google.common.collect.ArrayListMultimap;
import com.google.common.collect.ListMultimap;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.ai.attributes.Attribute;
import net.minecraft.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.Explosion;
import net.minecraftforge.event.ItemAttributeModifierEvent;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.event.world.ExplosionEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import ttmp.infernoreborn.api.TickingTaskHandler;
import ttmp.infernoreborn.api.sigil.SigilHolder;
import ttmp.infernoreborn.api.sigil.SigilSlot;
import ttmp.infernoreborn.capability.SimpleTickingTaskHandler;
import ttmp.infernoreborn.util.SigilUtils;
import ttmp.infernoreborn.util.damage.LivingOnly;

import static ttmp.infernoreborn.api.InfernoRebornApi.MODID;

@Mod.EventBusSubscriber(modid = MODID)
public final class CommonEventHandlers{
	private CommonEventHandlers(){}

	@SubscribeEvent
	public static void onItemAttributeModifier(ItemAttributeModifierEvent event){
		SigilHolder h = SigilHolder.of(event.getItemStack());
		if(h==null) return;
		ListMultimap<Attribute, AttributeModifier> m = ArrayListMultimap.create(event.getModifiers());
		SigilUtils.applyAttributes(h, SigilSlot.of(event.getSlotType()), m);
		event.clearModifiers();
		m.forEach(event::addModifier);
	}

	@SubscribeEvent
	public static void onWorldTick(TickEvent.WorldTickEvent event){
		if(event.phase!=TickEvent.Phase.START) return;
		TickingTaskHandler h = TickingTaskHandler.of(event.world);
		if(h instanceof SimpleTickingTaskHandler) ((SimpleTickingTaskHandler)h).update();
	}

	@SubscribeEvent
	public static void onExplosionDetonate(ExplosionEvent.Detonate event){
		Explosion explosion = event.getExplosion();
		if(explosion.getDamageSource() instanceof LivingOnly){
			event.getAffectedEntities().removeIf(entity -> !(entity instanceof LivingEntity));
		}
	}
}
