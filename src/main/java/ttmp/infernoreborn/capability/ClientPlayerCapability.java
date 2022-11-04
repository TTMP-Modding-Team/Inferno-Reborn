package ttmp.infernoreborn.capability;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.Direction;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.CapabilityInject;
import net.minecraftforge.common.capabilities.ICapabilityProvider;
import net.minecraftforge.common.util.LazyOptional;
import ttmp.infernoreborn.api.Caps;
import ttmp.infernoreborn.api.shield.ShieldSkin;
import ttmp.infernoreborn.api.sigil.SigilHolder;
import ttmp.infernoreborn.network.SyncShieldMsg;
import ttmp.infernoreborn.shield.ShieldSkins;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.List;

public class ClientPlayerCapability implements ICapabilityProvider{
	@CapabilityInject(ClientPlayerCapability.class)
	public static Capability<ClientPlayerCapability> clientPlayerShield;

	private final PlayerSigilHolder sigils;
	public final List<ActiveShield> shields = new ArrayList<>();

	public ClientPlayerCapability(PlayerEntity player){
		sigils = new PlayerSigilHolder(player);
	}

	public void update(SyncShieldMsg msg){
		if(msg.shields==shields.size()){
			for(int i = 0; i<msg.shields; i++)
				shields.get(i).update(msg.get(i));
		}else{
			int i;
			for(i = 0; i<msg.shields; i++){
				if(shields.size()>i) shields.get(i).update(msg.get(i));
				else shields.add(new ActiveShield(msg.get(i)));
			}
			while(i<shields.size()) shields.remove(i);
		}
	}

	@Nullable private LazyOptional<ClientPlayerCapability> self = null;
	@Nullable private LazyOptional<SigilHolder> sigilHolderLO = null;

	@Nonnull @Override public <T> LazyOptional<T> getCapability(@Nonnull Capability<T> cap, @Nullable Direction side){
		if(cap==clientPlayerShield){
			if(self==null) self = LazyOptional.of(() -> this);
			return self.cast();
		}else if(cap==Caps.sigilHolder){
			if(sigilHolderLO==null) sigilHolderLO = LazyOptional.of(() -> sigils);
			return sigilHolderLO.cast();
		}else return LazyOptional.empty();
	}

	public static final class ActiveShield{
		private ShieldSkin skin = ShieldSkins.ERROR;
		private double durability;
		private double maxDurability;
		private boolean down;

		private boolean increase;
		private boolean hasStartTime;
		private long startTime;

		public ShieldSkin getSkin(){
			return skin;
		}
		public double getDurability(){
			return durability;
		}
		public double getMaxDurability(){
			return maxDurability;
		}
		public boolean isDown(){
			return down;
		}

		public double getDurabilityPortion(){
			return maxDurability==0 ? 1 : durability/maxDurability;
		}

		public boolean isIncrease(){
			return increase;
		}
		public long getAnimationTime(){
			if(hasStartTime) return System.currentTimeMillis()-startTime;
			this.startTime = System.currentTimeMillis();
			this.hasStartTime = true;
			return 0;
		}

		public ActiveShield(@Nullable SyncShieldMsg.ShieldEntry entry){
			if(entry==null) return;
			this.skin = entry.skin;
			this.durability = entry.durability;
			this.maxDurability = entry.maxDurability;
			this.down = entry.down;

			this.increase = true;
			this.hasStartTime = false;
		}

		public void update(@Nullable SyncShieldMsg.ShieldEntry entry){
			if(entry==null) return;
			this.increase = durability<entry.durability;
			this.hasStartTime = false;

			this.skin = entry.skin;
			this.durability = entry.durability;
			this.maxDurability = entry.maxDurability;
			this.down = entry.down;
		}
	}
}
