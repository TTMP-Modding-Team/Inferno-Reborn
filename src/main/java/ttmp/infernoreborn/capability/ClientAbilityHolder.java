package ttmp.infernoreborn.capability;

import net.minecraft.client.Minecraft;
import net.minecraft.client.particle.Particle;
import net.minecraft.entity.LivingEntity;
import net.minecraft.particles.ParticleTypes;
import net.minecraft.util.Direction;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.ICapabilityProvider;
import net.minecraftforge.common.util.LazyOptional;
import ttmp.infernoreborn.api.Caps;
import ttmp.infernoreborn.api.ability.Ability;
import ttmp.infernoreborn.api.ability.AbilityHolder;
import ttmp.infernoreborn.api.ability.Cooldown;
import ttmp.infernoreborn.api.ability.EmptyCooldown;
import ttmp.infernoreborn.infernaltype.dsl.effect.ParticleEffect;

import javax.annotation.Nullable;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Random;
import java.util.Set;

public class ClientAbilityHolder implements AbilityHolder, ICapabilityProvider{
	@Nullable
	public static ClientAbilityHolder of(ICapabilityProvider provider){
		AbilityHolder of = AbilityHolder.of(provider);
		return of instanceof ClientAbilityHolder ? (ClientAbilityHolder)of : null;
	}

	private final Set<Ability> abilities = new LinkedHashSet<>();
	@Nullable private List<ParticleEffect> effects;

	@Override public Set<Ability> getAbilities(){
		return abilities;
	}
	@Override public boolean has(Ability ability){
		return abilities.contains(ability);
	}
	@Override public boolean add(Ability ability){
		return abilities.add(ability);
	}
	@Override public boolean remove(Ability ability){
		return abilities.remove(ability);
	}
	@Override public void clear(){
		abilities.clear();
	}

	public void setEffects(List<ParticleEffect> effects){
		this.effects = effects;
	}

	@Override public void update(LivingEntity entity){
		if(effects!=null&&!effects.isEmpty()){
			Random rand = entity.getRandom();
			for(ParticleEffect effect : effects){
				if(effect.size()==0) continue;
				if(rand.nextInt(effects.size()+1)==0){
					int color = effect.color(rand.nextInt(effect.size()));
					float r = (color >> 16&0xFF)/255f;
					float g = (color >> 8&0xFF)/255f;
					float b = (color&0xFF)/255f;

					Particle particle = Minecraft.getInstance().particleEngine.createParticle(ParticleTypes.INSTANT_EFFECT,
							entity.getRandomX(.5),
							entity.getRandomY(),
							entity.getRandomZ(.5),
							rand.nextGaussian(),
							rand.nextGaussian(),
							rand.nextGaussian());
					if(particle!=null) particle.setColor(r, g, b);
				}
			}
		}
	}

	@Override public Cooldown cooldown(){
		return EmptyCooldown.INSTANCE;
	}

	private final LazyOptional<AbilityHolder> self = LazyOptional.of(() -> this);

	@Override public <T> LazyOptional<T> getCapability(Capability<T> cap, @Nullable Direction side){
		return cap==Caps.abilityHolder ? self.cast() : LazyOptional.empty();
	}
}
