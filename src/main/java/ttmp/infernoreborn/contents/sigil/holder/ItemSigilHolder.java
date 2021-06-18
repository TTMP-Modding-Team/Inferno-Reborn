package ttmp.infernoreborn.contents.sigil.holder;

import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraftforge.common.util.Constants;
import ttmp.infernoreborn.config.ModCfg;
import ttmp.infernoreborn.contents.sigil.context.SigilEventContext;

import java.util.Objects;
import java.util.Random;

public class ItemSigilHolder extends AbstractSigilHolder{
	private static final Random SEED_RANDOM = new Random();

	private final ItemStack stack;

	private long gibberishSeed;
	private boolean gibberishSeedGenerated;
	private boolean saveGibberishSeed;

	public ItemSigilHolder(ItemStack stack){
		this.stack = Objects.requireNonNull(stack);
	}

	@Override public int getMaxPoints(){
		return ModCfg.sigilHolderConfig().getMaxPoints(stack.getItem());
	}

	@Override public long getGibberishSeed(){
		if(gibberishSeedGenerated) return gibberishSeed;
		gibberishSeed = (saveGibberishSeed = getMaxPoints()>0) ? SEED_RANDOM.nextLong() : 0;
		gibberishSeedGenerated = true;
		return gibberishSeed;
	}
	public void setGibberishSeed(long gibberishSeed){
		this.gibberishSeed = gibberishSeed;
		this.gibberishSeedGenerated = true;
	}

	@Override protected SigilEventContext createContext(){
		return SigilEventContext.item(stack, this);
	}

	@Override public CompoundNBT serializeNBT(){
		CompoundNBT nbt = super.serializeNBT();
		if(gibberishSeedGenerated&&saveGibberishSeed) nbt.putLong("gibberishSeed", gibberishSeed);
		return nbt;
	}

	@Override public void deserializeNBT(CompoundNBT nbt){
		super.deserializeNBT(nbt);
		if(nbt.contains("gibberishSeed", Constants.NBT.TAG_LONG)){
			gibberishSeed = nbt.getLong("gibberishSeed");
			gibberishSeedGenerated = saveGibberishSeed = true;
		}else{
			gibberishSeed = 0;
			gibberishSeedGenerated = saveGibberishSeed = false;
		}
	}
}
