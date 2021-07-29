package ttmp.infernoreborn.capability;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.util.Direction;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.ICapabilityProvider;
import net.minecraftforge.common.capabilities.ICapabilitySerializable;
import net.minecraftforge.common.util.LazyOptional;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public class PlayerCapability implements ICapabilitySerializable<CompoundNBT>{
	@SuppressWarnings("ConstantConditions") @Nullable public static PlayerCapability of(ICapabilityProvider provider){
		return provider.getCapability(Caps.playerCapability).orElse(null);
	}

	private final SimpleShieldHolder shieldHolder;

	private int judgementCooldown;

	public PlayerCapability(PlayerEntity player){
		this.shieldHolder = new SimpleShieldHolder(player);
	}

	public boolean hasJudgementCooldown(){
		return judgementCooldown>0;
	}
	public int getJudgementCooldown(){
		return judgementCooldown;
	}
	public void setJudgementCooldown(int judgementCooldown){
		this.judgementCooldown = judgementCooldown;
	}

	@Nullable private LazyOptional<ShieldHolder> shieldHolderLO;
	@Nullable private LazyOptional<PlayerCapability> playerCapabilityLO;

	@Nonnull @Override public <T> LazyOptional<T> getCapability(@Nonnull Capability<T> cap, @Nullable Direction side){
		if(cap==Caps.shieldHolder){
			if(shieldHolderLO==null) shieldHolderLO = LazyOptional.of(() -> shieldHolder);
			return shieldHolderLO.cast();
		}else if(cap==Caps.playerCapability){
			if(playerCapabilityLO==null) playerCapabilityLO = LazyOptional.of(() -> this);
			return playerCapabilityLO.cast();
		}
		return LazyOptional.empty();
	}

	@Override public CompoundNBT serializeNBT(){
		CompoundNBT nbt = new CompoundNBT();
		if(shieldHolder.getShield()>0) nbt.putFloat("shield", shieldHolder.getShield());
		if(judgementCooldown>0) nbt.putInt("judgement", judgementCooldown);
		return nbt;
	}

	@Override public void deserializeNBT(CompoundNBT nbt){
		shieldHolder.setShield(nbt.getFloat("shield"));
		judgementCooldown = nbt.getInt("judgement");
	}
}
