package ttmp.infernoreborn.contents.sigil.holder;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.nbt.ListNBT;
import net.minecraftforge.common.util.Constants;
import ttmp.infernoreborn.contents.Sigils;
import ttmp.infernoreborn.contents.sigil.Sigil;
import ttmp.infernoreborn.contents.sigil.context.SigilEventContext;
import ttmp.infernoreborn.util.StupidUtils;

import java.util.Collections;
import java.util.LinkedHashSet;
import java.util.Set;

import static net.minecraftforge.common.util.Constants.NBT.TAG_LIST;

public class PlayerSigilHolder implements SigilHolder{
	private final PlayerEntity player;
	private final Set<Sigil> sigils = new LinkedHashSet<>();

	private int totalPoint = 0;
	private boolean totalPointDirty;

	public PlayerSigilHolder(PlayerEntity player){
		this.player = player;
	}

	@Override public Set<Sigil> getSigils(){
		return Collections.unmodifiableSet(sigils);
	}

	@Override public int getMaxPoints(){
		return 999; // TODO
	}
	@Override public int getTotalPoint(){
		if(totalPointDirty){
			totalPoint = 0;
			for(Sigil sigil : this.sigils)
				totalPoint += sigil.getPoint();
			totalPointDirty = false;
		}
		return totalPoint;
	}

	@Override public boolean has(Sigil sigil){
		return sigils.contains(sigil);
	}
	@Override public boolean add(Sigil sigil, boolean force){
		if(!force&&!canAdd(sigil)) return false;
		if(!sigils.add(sigil)) return false;
		totalPointDirty = true;
		return true;
	}
	@Override public boolean remove(Sigil sigil){
		if(!sigils.remove(sigil)) return false;
		totalPointDirty = true;
		return true;
	}
	@Override public boolean isEmpty(){
		return sigils.isEmpty();
	}
	@Override public void clear(){
		if(!sigils.isEmpty()){
			sigils.clear();
			totalPointDirty = true;
		}
	}

	@Override public SigilEventContext createContext(){
		return SigilEventContext.living(player, this);
	}

	public void write(CompoundNBT nbt){
		nbt.put("sigils", StupidUtils.writeToNbt(sigils, Sigils.getRegistry()));
	}

	public void read(CompoundNBT nbt){
		sigils.clear();
		if(nbt.contains("sigils", TAG_LIST)){
			ListNBT list = nbt.getList("sigils", Constants.NBT.TAG_STRING);
			StupidUtils.read(list, Sigils.getRegistry(), this::add);
		}
		totalPointDirty = true;
	}
}
