package ttmp.infernoreborn.contents.entity.projectile.wind;

import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.potion.EffectInstance;
import net.minecraft.util.math.EntityRayTraceResult;
import net.minecraft.world.World;
import ttmp.infernoreborn.contents.ModEntities;

import java.util.HashSet;
import java.util.Set;

public class EffectWindEntity extends AbstractWindEntity{
	private final Set<EffectInstance> effectInstances = new HashSet<>();
	public EffectWindEntity(World world){
		this(ModEntities.EFFECT_WIND_ENTITY.get(), world);
	}
	public EffectWindEntity(EntityType<? extends AbstractWindEntity> type, World world){
		super(type, world);
	}

	public EffectWindEntity addEffect(EffectInstance effectInstance){
		effectInstances.add(effectInstance);
		return this;
	}

	@Override protected void onHitEntity(EntityRayTraceResult result){
		Entity entity = result.getEntity();
		if(entity instanceof LivingEntity)
			if(effectInstances!=null)
				for(EffectInstance e : this.effectInstances)
					((LivingEntity)entity).addEffect(e);
		super.onHitEntity(result);
	}
}
