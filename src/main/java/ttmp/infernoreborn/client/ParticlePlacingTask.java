package ttmp.infernoreborn.client;

import net.minecraft.client.Minecraft;
import net.minecraft.client.particle.Particle;
import net.minecraft.particles.IParticleData;
import net.minecraft.util.math.vector.Vector3d;
import ttmp.infernoreborn.network.ParticleMsg;
import ttmp.infernoreborn.network.TickingTask;

import javax.annotation.Nullable;
import java.util.Random;

public class ParticlePlacingTask extends TickingTask{
	private static final Random RANDOM = new Random();

	private final int interval;
	private final int amount;
	private final IParticleData particle;
	private final Vector3d pos;
	private final Vector3d spread;
	private final Vector3d velocity;
	private final Vector3d velocitySpread;
	private final int[] colors;

	public ParticlePlacingTask(int ticks, int interval, int amount, IParticleData particle, Vector3d pos, Vector3d spread, Vector3d velocity, Vector3d velocitySpread, int[] colors){
		super(ticks);
		this.interval = interval;
		this.amount = amount;
		this.particle = particle;
		this.pos = pos;
		this.spread = spread;
		this.velocity = velocity;
		this.velocitySpread = velocitySpread;
		this.colors = colors;
	}

	@Nullable public static ParticlePlacingTask from(ParticleMsg msg){
		if(msg.getTicks()>0&&msg.getInterval()>0&&msg.getAmount()>0&&msg.getParticleType() instanceof IParticleData){
			return new ParticlePlacingTask(msg.getTicks(),
					msg.getInterval(),
					msg.getAmount(),
					(IParticleData)msg.getParticleType(),
					msg.getPos(),
					msg.getSpread(),
					msg.getVelocity(),
					msg.getVelocitySpread(),
					msg.getColors());
		}
		return null;
	}

	@Override public void onTick(){
		super.onTick();
		if(this.ticks%interval==0){
			for(int i = amount; i>0; i--){
				Particle particle = Minecraft.getInstance().particleEngine.createParticle(this.particle,
						x(pos, spread),
						y(pos, spread),
						z(pos, spread),
						x(velocity, velocitySpread),
						y(velocity, velocitySpread),
						z(velocity, velocitySpread));
				if(particle!=null&colors.length>0){
					int color = colors[RANDOM.nextInt(colors.length)];
					particle.setColor(
							(color<<16&0xFF)/255f,
							(color<<8&0xFF)/255f,
							(color&0xFF)/255f
					);
				}
			}
		}
	}

	private static double x(Vector3d p, Vector3d s){
		return s.x==0 ? p.x : p.x+(RANDOM.nextDouble()*s.x-RANDOM.nextDouble()*s.x);
	}
	private static double y(Vector3d p, Vector3d s){
		return s.y==0 ? p.y : p.y+(RANDOM.nextDouble()*s.y-RANDOM.nextDouble()*s.y);
	}
	private static double z(Vector3d p, Vector3d s){
		return s.z==0 ? p.z : p.z+(RANDOM.nextDouble()*s.z-RANDOM.nextDouble()*s.z);
	}
}
