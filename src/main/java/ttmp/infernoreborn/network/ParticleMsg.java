package ttmp.infernoreborn.network;

import net.minecraft.network.PacketBuffer;
import net.minecraft.particles.ParticleType;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraftforge.registries.ForgeRegistries;

import javax.annotation.Nullable;
import java.util.Objects;

public class ParticleMsg{
	public static ParticleMsg read(PacketBuffer buf){
		return new ParticleMsg(buf.readVarInt(),
				buf.readVarInt(),
				buf.readVarInt(),
				ForgeRegistries.PARTICLE_TYPES.getValue(buf.readResourceLocation()),
				readVector(buf),
				readOptionalVector(buf),
				readVector(buf),
				readOptionalVector(buf),
				buf.readVarIntArray());
	}

	private final int ticks;
	private final int interval;
	private final int amount;
	@Nullable private final ParticleType<?> particleType;
	private final Vector3d pos;
	private final Vector3d spread;
	private final Vector3d velocity;
	private final Vector3d velocitySpread;
	private final int[] colors;

	public ParticleMsg(int ticks, int interval, int amount, @Nullable ParticleType<?> particleType, Vector3d pos, Vector3d spread, Vector3d velocity, Vector3d velocitySpread, int[] colors){
		this.ticks = ticks;
		this.interval = interval;
		this.amount = amount;
		this.particleType = particleType;
		this.pos = pos;
		this.spread = spread;
		this.velocity = velocity;
		this.velocitySpread = velocitySpread;
		this.colors = colors;
	}

	public int getTicks(){
		return ticks;
	}
	public int getInterval(){
		return interval;
	}
	public int getAmount(){
		return amount;
	}
	@Nullable public ParticleType<?> getParticleType(){
		return particleType;
	}
	public Vector3d getPos(){
		return pos;
	}
	public Vector3d getSpread(){
		return spread;
	}
	public Vector3d getVelocity(){
		return velocity;
	}
	public Vector3d getVelocitySpread(){
		return velocitySpread;
	}
	public int[] getColors(){
		return colors;
	}

	public void write(PacketBuffer buf){
		buf.writeVarInt(ticks);
		buf.writeVarInt(interval);
		buf.writeVarInt(amount);
		buf.writeResourceLocation(Objects.requireNonNull(Objects.requireNonNull(particleType).getRegistryName()));
		writeVector(buf, pos);
		writeOptionalVector(buf, spread);
		writeVector(buf, velocity);
		writeOptionalVector(buf, velocitySpread);
		buf.writeVarIntArray(colors);
	}

	public static void writeVector(PacketBuffer buf, Vector3d pos){
		buf.writeDouble(pos.x).writeDouble(pos.y).writeDouble(pos.z);
	}

	public static Vector3d readVector(PacketBuffer buf){
		return new Vector3d(buf.readDouble(), buf.readDouble(), buf.readDouble());
	}

	public static void writeOptionalVector(PacketBuffer buf, Vector3d pos){
		if(pos.equals(Vector3d.ZERO)) buf.writeBoolean(false);
		else buf.writeBoolean(true).writeDouble(pos.x).writeDouble(pos.y).writeDouble(pos.z);
	}

	public static Vector3d readOptionalVector(PacketBuffer buf){
		return buf.readBoolean() ? new Vector3d(buf.readDouble(), buf.readDouble(), buf.readDouble()) : Vector3d.ZERO;
	}
}
