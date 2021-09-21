package ttmp.infernoreborn.network;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.network.PacketBuffer;
import ttmp.infernoreborn.capability.PlayerCapability.ActiveShield;
import ttmp.infernoreborn.shield.ShieldSkin;
import ttmp.infernoreborn.shield.ShieldSkins;

import javax.annotation.Nullable;
import java.util.UUID;

public class SyncShieldMsg{
	public static SyncShieldMsg read(PacketBuffer buffer){
		SyncShieldMsg msg = new SyncShieldMsg(buffer.readUUID(), buffer.readUnsignedByte());
		int entries = buffer.readUnsignedByte();
		for(int i = 0; i<entries; i++){
			int index = buffer.readUnsignedByte();
			msg.add(index, ShieldEntry.read(buffer));
		}
		return msg;
	}

	public final UUID playerId;
	public final int shields;

	private final ShieldEntry[] entries;

	public SyncShieldMsg(PlayerEntity player, int shields){
		this(player.getUUID(), shields);
	}
	public SyncShieldMsg(UUID playerId, int shields){
		if(shields>127) throw new IllegalArgumentException("TOo mAnY ShIeLDs");
		if(shields<0) throw new IllegalArgumentException("Negative amount of shields?");
		this.playerId = playerId;
		this.shields = shields;
		this.entries = new ShieldEntry[shields];
	}

	@Nullable public ShieldEntry get(int index){
		return entries[index];
	}

	public void add(int index, ActiveShield activeShield){
		entries[index] = new ShieldEntry(activeShield);
	}

	private void add(int index, ShieldEntry entry){
		entries[index] = entry;
	}

	public void write(PacketBuffer buffer){
		buffer.writeUUID(playerId);
		buffer.writeByte(shields);
		int wi = buffer.writerIndex();
		buffer.writeByte(0);
		int c = 0;
		for(int i = 0; i<entries.length; i++){
			if(entries[i]!=null){
				buffer.writeByte(i);
				entries[i].write(buffer);
				c++;
			}
		}
		buffer.setByte(wi, c);
	}

	public static final class ShieldEntry{
		public static ShieldEntry read(PacketBuffer buffer){
			ShieldSkin skin = ShieldSkins.getOrError(buffer.readResourceLocation());
			return new ShieldEntry(skin, buffer.readDouble(), buffer.readDouble(), buffer.readBoolean());
		}

		public final ShieldSkin skin;
		public final double durability;
		public final double maxDurability;
		public final boolean down;

		public ShieldEntry(ActiveShield activeShield){
			this(activeShield.getShield().getSkin(),
					activeShield.getDurability(),
					activeShield.getShield().getMaxDurability(),
					activeShield.isDown());
		}
		public ShieldEntry(ShieldSkin skin, double durability, double maxDurability, boolean down){
			this.skin = skin;
			this.durability = durability;
			this.maxDurability = maxDurability;
			this.down = down;
		}

		public void write(PacketBuffer buffer){
			buffer.writeResourceLocation(skin.id);
			buffer.writeDouble(durability);
			buffer.writeDouble(maxDurability);
			buffer.writeBoolean(down);
		}
	}
}
