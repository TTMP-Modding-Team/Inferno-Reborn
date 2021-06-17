package ttmp.infernoreborn.sigil.holder;

import net.minecraft.entity.player.PlayerEntity;
import ttmp.infernoreborn.sigil.context.SigilEventContext;

import java.util.Objects;

public class PlayerSigilHolder extends AbstractSigilHolder{
	private final PlayerEntity player;

	public PlayerSigilHolder(PlayerEntity player){
		this.player = Objects.requireNonNull(player);
	}

	@Override public int getMaxPoints(){
		return 999; // TODO
	}

	@Override public long getGibberishSeed(){
		return player.getUUID().getMostSignificantBits()^player.getUUID().getLeastSignificantBits();
	}

	@Override protected SigilEventContext createContext(){
		return SigilEventContext.living(player, this);
	}
}
