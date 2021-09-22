package ttmp.infernoreborn.contents.sigil.holder;

import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.nbt.ListNBT;
import net.minecraft.nbt.StringNBT;
import net.minecraft.util.Direction;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.ICapabilityProvider;
import net.minecraftforge.common.util.Constants;
import net.minecraftforge.common.util.LazyOptional;
import ttmp.infernoreborn.InfernoReborn;
import ttmp.infernoreborn.capability.Caps;
import ttmp.infernoreborn.config.ModCfg;
import ttmp.infernoreborn.contents.Sigils;
import ttmp.infernoreborn.contents.sigil.Sigil;
import ttmp.infernoreborn.contents.sigil.context.SigilEventContext;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.Collections;
import java.util.LinkedHashSet;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

public class ItemSigilHolder implements SigilHolder, ICapabilityProvider{
	private static final String SIGIL_NBT = "InfernoRebornSigil";

	private final ItemStack stack;

	private boolean read = true;

	@Nullable private Set<Sigil> sigils;

	private int totalPoint = 0;
	private boolean totalPointDirty;

	public ItemSigilHolder(ItemStack stack){
		this.stack = Objects.requireNonNull(stack);
	}

	private void readFromItem(){
		if(!read) return;
		read = false;
		CompoundNBT tag = this.stack.getTag();
		sigils = null;
		if(tag!=null){
			ListNBT list = tag.getList(SIGIL_NBT, Constants.NBT.TAG_STRING);
			if(!list.isEmpty()){
				for(int i = 0; i<list.size(); i++){
					Sigil s = Sigils.getRegistry().getValue(new ResourceLocation(list.getString(i)));
					if(s!=null){
						if(sigils==null) sigils = new LinkedHashSet<>();
						sigils.add(s);
					}
				}
			}
		}
		totalPointDirty = true;
	}

	private void writeSigils(){
		if(sigils==null||sigils.isEmpty()){
			CompoundNBT tag = this.stack.getTag();
			if(tag!=null){
				tag.remove(SIGIL_NBT);
				if(tag.isEmpty()) this.stack.setTag(null);
			}
		}else{
			CompoundNBT tag = this.stack.getOrCreateTag();
			String tagBefore = tag.toString();
			ListNBT list = tag.getList(SIGIL_NBT, Constants.NBT.TAG_STRING);

			if(list.isEmpty()){
				for(Sigil s : sigils)
					list.add(StringNBT.valueOf(Objects.requireNonNull(s.getRegistryName()).toString()));
				tag.put(SIGIL_NBT, list);
			}else{
				Sigil[] sigils = this.sigils.toArray(new Sigil[0]);
				int si = 0;
				for(int i = 0; i<list.size(); i++){
					if(si>=sigils.length) list.remove(i--);
					else if(list.getString(i).equals(Objects.requireNonNull(sigils[si].getRegistryName()).toString())) si++;
					else list.remove(i--); // Assume it's deleted
				}
				for(; si<sigils.length; si++)
					list.add(StringNBT.valueOf(Objects.requireNonNull(sigils[si].getRegistryName()).toString()));
			}
			InfernoReborn.LOGGER.debug("Shit {}\n{}\n{}", tagBefore, tag, sigils.stream()
					.map(s -> Objects.requireNonNull(s.getRegistryName()).toString())
					.collect(Collectors.joining(", ")));
		}
	}

	@Override public Set<Sigil> getSigils(){
		readFromItem();
		return sigils!=null ? Collections.unmodifiableSet(sigils) : Collections.emptySet();
	}

	@Override public int getMaxPoints(){
		readFromItem();
		return ModCfg.sigilHolderConfig().getMaxPoints(stack.getItem());
	}
	@Override public int getTotalPoint(){
		readFromItem();
		if(totalPointDirty){
			totalPoint = 0;
			if(this.sigils!=null){
				for(Sigil sigil : this.sigils)
					totalPoint += sigil.getPoint();
			}
			totalPointDirty = false;
		}
		return totalPoint;
	}
	@Override public boolean has(Sigil sigil){
		readFromItem();
		return sigils!=null&&sigils.contains(sigil);
	}
	@Override public boolean add(Sigil sigil, boolean force){
		readFromItem();
		if(!force&&!canAdd(sigil)) return false;
		if(sigils==null) this.sigils = new LinkedHashSet<>();
		if(!sigils.add(sigil)) return false;
		writeSigils();
		totalPointDirty = true;
		return true;
	}
	@Override public boolean remove(Sigil sigil){
		readFromItem();
		if(sigils==null||!sigils.remove(sigil)) return false;
		if(sigils.isEmpty()) sigils = null;
		writeSigils();
		totalPointDirty = true;
		return false;
	}
	@Override public boolean isEmpty(){
		readFromItem();
		return sigils==null||sigils.isEmpty();
	}
	@Override public void clear(){
		readFromItem();
		if(sigils==null) return;
		sigils = null;
		totalPointDirty = true;
	}

	@Override public SigilEventContext createContext(){
		return SigilEventContext.item(stack, this);
	}

	private LazyOptional<SigilHolder> self;

	@Nonnull @Override public <T> LazyOptional<T> getCapability(Capability<T> cap, @Nullable Direction side){
		return cap==Caps.sigilHolder ? (self!=null ? self : (self = LazyOptional.of(() -> this))).cast() : LazyOptional.empty();
	}
}
