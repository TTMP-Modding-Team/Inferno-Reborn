package ttmp.infernoreborn.contents.container;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.inventory.container.Container;
import net.minecraftforge.fml.network.PacketDistributor;
import ttmp.infernoreborn.api.Caps;
import ttmp.infernoreborn.api.sigil.EmptySigilHolder;
import ttmp.infernoreborn.api.sigil.Sigil;
import ttmp.infernoreborn.api.sigil.SigilHolder;
import ttmp.infernoreborn.contents.ModContainers;
import ttmp.infernoreborn.network.ModNet;
import ttmp.infernoreborn.network.SyncScrapperScreenMsg;

import java.util.HashSet;
import java.util.Set;

public class StigmaScrapperContainer extends Container{
	private final PlayerEntity player;
	private final SigilHolder sigilHolder;

	public StigmaScrapperContainer(int id, PlayerInventory playerInventory){
		super(ModContainers.STIGMA_SCRAPPER.get(), id);
		this.player = playerInventory.player;
		this.sigilHolder = playerInventory.player.getCapability(Caps.sigilHolder).orElse(EmptySigilHolder.INSTANCE);
	}

	public SigilHolder getSigilHolder(){
		return sigilHolder;
	}

	@Override public boolean stillValid(PlayerEntity pPlayer){
		return true;
	}

	private int maxSigilsCache;
	private final Set<Sigil> sigilsCache = new HashSet<>();

	@Override public void broadcastChanges(){
		super.broadcastChanges();
		int maxSigils = sigilHolder.getMaxPoints();
		Set<Sigil> sigils = sigilHolder.getSigils();

		boolean updated = false;
		if(maxSigilsCache!=maxSigils){
			updated = true;
			maxSigilsCache = maxSigils;
		}
		if(!sigilsCache.equals(sigils)){
			updated = true;
			sigilsCache.clear();
			sigilsCache.addAll(sigils);
		}
		if(updated&&player instanceof ServerPlayerEntity){
			ModNet.CHANNEL.send(PacketDistributor.PLAYER.with(() -> (ServerPlayerEntity)player),
					new SyncScrapperScreenMsg(maxSigils, sigilsCache));
		}
	}
}
