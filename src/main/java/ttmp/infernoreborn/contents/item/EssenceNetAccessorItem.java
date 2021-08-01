package ttmp.infernoreborn.contents.item;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.nbt.IntNBT;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Direction;
import net.minecraft.util.Hand;
import net.minecraft.world.World;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.ICapabilityProvider;
import net.minecraftforge.common.capabilities.ICapabilitySerializable;
import net.minecraftforge.common.util.LazyOptional;
import ttmp.infernoreborn.capability.Caps;
import ttmp.infernoreborn.capability.EssenceNetProvider;
import ttmp.infernoreborn.contents.block.essencenet.EssenceNetCoreBlock;
import ttmp.infernoreborn.contents.container.EssenceHolderContainerProvider;
import ttmp.infernoreborn.util.EssenceHolder;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public class EssenceNetAccessorItem extends Item implements EssenceNetCoreBlock.EssenceNetAcceptable{
	public EssenceNetAccessorItem(Properties properties){
		super(properties);
	}

	@Nullable @Override public ICapabilityProvider initCapabilities(ItemStack stack, @Nullable CompoundNBT nbt){
		return new Data();
	}

	@Override public void setNetwork(ItemStack stack, int network){
		stack.getCapability(Caps.essenceNetAccessorData).ifPresent(data -> data.setNetworkId(network));
	}

	@Override public ActionResult<ItemStack> use(World world, PlayerEntity player, Hand hand){
		ItemStack stack = player.getItemInHand(hand);
		if(!world.isClientSide&&stack.getCapability(Caps.essenceHolder).isPresent())
			player.openMenu(new EssenceHolderContainerProvider(stack.getHoverName(), player, hand));
		return ActionResult.sidedSuccess(stack, world.isClientSide);
	}

	public static final class Data implements ICapabilitySerializable<IntNBT>{
		private int networkId;

		public int getNetworkId(){
			return networkId;
		}

		public void setNetworkId(int networkId){
			if(this.networkId==networkId) return;
			this.networkId = networkId;
			if(essenceHolderCache!=null){
				LazyOptional<EssenceHolder> c = essenceHolderCache;
				essenceHolderCache = null;
				c.invalidate();
			}
		}

		@Nullable private LazyOptional<EssenceHolder> essenceHolderCache;
		@Nullable private LazyOptional<Data> self;

		@Nonnull @Override public <T> LazyOptional<T> getCapability(Capability<T> cap, @Nullable Direction side){
			if(cap==Caps.essenceHolder){
				if(essenceHolderCache==null){
					EssenceHolder h = EssenceNetProvider.getInstance().get(networkId);
					essenceHolderCache = h!=null ? LazyOptional.of(() -> h) : LazyOptional.empty();
				}
				return essenceHolderCache.cast();
			}else if(cap==Caps.essenceNetAccessorData){
				if(self==null) self = LazyOptional.of(() -> this);
				return self.cast();
			}else return LazyOptional.empty();
		}

		@Override public IntNBT serializeNBT(){
			return IntNBT.valueOf(networkId);
		}
		@Override public void deserializeNBT(IntNBT nbt){
			this.networkId = nbt.getAsInt();
		}
	}
}
