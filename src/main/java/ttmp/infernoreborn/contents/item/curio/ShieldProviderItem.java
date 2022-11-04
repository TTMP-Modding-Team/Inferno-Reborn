package ttmp.infernoreborn.contents.item.curio;

import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.util.Direction;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.ICapabilityProvider;
import net.minecraftforge.common.util.LazyOptional;
import ttmp.infernoreborn.api.Caps;
import ttmp.infernoreborn.api.shield.Shield;
import ttmp.infernoreborn.api.shield.ShieldProvider;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.Objects;

public class ShieldProviderItem extends BaseCurioItem{
	private final Shield shield;

	public ShieldProviderItem(Properties properties, Shield shield){
		super(properties);
		this.shield = Objects.requireNonNull(shield);
	}

	@Override public ICapabilityProvider initCapabilities(ItemStack stack, @Nullable CompoundNBT nbt){
		return new ICapabilityProvider(){
			private LazyOptional<ShieldProvider> shieldProvider;

			@Nonnull @Override public <T> LazyOptional<T> getCapability(@Nonnull Capability<T> cap, @Nullable Direction side){
				if(cap==Caps.shieldProvider){
					if(shieldProvider==null) shieldProvider = LazyOptional.of(() -> () -> shield);
					return shieldProvider.cast();
				}
				return LazyOptional.empty();
			}
		};
	}
}
