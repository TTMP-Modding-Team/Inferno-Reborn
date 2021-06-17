package ttmp.infernoreborn.sigil.holder;

import com.google.common.collect.ListMultimap;
import net.minecraft.entity.ai.attributes.Attribute;
import net.minecraft.entity.ai.attributes.AttributeModifier;
import net.minecraft.inventory.EquipmentSlotType;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.nbt.ListNBT;
import net.minecraft.util.Direction;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.ICapabilitySerializable;
import net.minecraftforge.common.util.Constants;
import net.minecraftforge.common.util.LazyOptional;
import ttmp.infernoreborn.capability.Caps;
import ttmp.infernoreborn.contents.Sigils;
import ttmp.infernoreborn.sigil.Sigil;
import ttmp.infernoreborn.sigil.context.SigilEventContext;
import ttmp.infernoreborn.util.StupidUtils;

import javax.annotation.Nullable;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

public abstract class AbstractSigilHolder implements SigilHolder, ICapabilitySerializable<CompoundNBT>{
	@Nullable private Set<Sigil> sigils;
	private int totalPoint = 0;
	private boolean totalPointDirty;

	@Override public Set<Sigil> getSigils(){
		return sigils!=null ? Collections.unmodifiableSet(sigils) : Collections.emptySet();
	}
	@Override public int getTotalPoint(){
		if(totalPointDirty){
			totalPoint = 0;
			if(this.sigils!=null)
				for(Sigil sigil : this.sigils)
					totalPoint += sigil.getPoint();
			totalPointDirty = false;
		}
		return totalPoint;
	}
	@Override public boolean has(Sigil sigil){
		return sigils!=null&&sigils.contains(sigil);
	}
	@Override public boolean canAdd(Sigil sigil){
		return !has(sigil)&&
				sigil.canBeAttachedTo(createContext())&&
				getMaxPoints()>=sigil.getPoint()+getTotalPoint();
	}
	@Override public boolean add(Sigil sigil){
		if(!canAdd(sigil)) return false;
		if(sigils==null) sigils = new HashSet<>();
		if(!sigils.add(sigil)) return false;
		totalPointDirty = true;
		return true;
	}
	@Override public boolean remove(Sigil sigil){
		if(sigils==null||!sigils.remove(sigil)) return false;
		if(sigils.isEmpty()) sigils = null;
		totalPointDirty = true;
		return true;
	}
	@Override public void clear(){
		if(sigils!=null&&!sigils.isEmpty()){
			sigils.clear();
			totalPointDirty = true;
		}
	}

	@Override public void forceAdd(Sigil sigil){
		if(sigils==null) sigils = new HashSet<>();
		sigils.add(sigil);
	}

	@Override public void applyAttributes(@Nullable EquipmentSlotType equipmentSlotType, ListMultimap<Attribute, AttributeModifier> modifierMap){
		if(sigils==null) return;
		SigilEventContext context = createContext();
		for(Sigil sigil : sigils) sigil.applyAttributes(context, equipmentSlotType, modifierMap);
	}

	protected abstract SigilEventContext createContext();

	private final LazyOptional<SigilHolder> self = LazyOptional.of(() -> this);

	@Override public <T> LazyOptional<T> getCapability(Capability<T> cap, @Nullable Direction side){
		return cap==Caps.sigilHolder ? self.cast() : LazyOptional.empty();
	}

	@Override public CompoundNBT serializeNBT(){
		CompoundNBT nbt = new CompoundNBT();
		if(sigils!=null)
			nbt.put("sigils", StupidUtils.writeToNbt(sigils, Sigils.getRegistry()));
		return nbt;
	}
	@Override public void deserializeNBT(CompoundNBT nbt){
		if(nbt.contains("sigils", Constants.NBT.TAG_LIST)){
			ListNBT list = nbt.getList("sigils", Constants.NBT.TAG_STRING);
			Set<Sigil> sigils = new HashSet<>();
			StupidUtils.read(list, Sigils.getRegistry(), sigils::add);
			this.sigils = sigils.isEmpty() ? null : sigils;
		}else sigils = null;
		totalPointDirty = true;
	}
}
