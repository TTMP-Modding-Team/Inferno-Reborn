package ttmp.infernoreborn.contents;

import net.minecraft.inventory.container.ContainerType;
import net.minecraftforge.fml.RegistryObject;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import ttmp.infernoreborn.container.EssenceHolderContainer;

import static ttmp.infernoreborn.InfernoReborn.MODID;

public final class ModContainers{
	private ModContainers(){}

	public static final DeferredRegister<ContainerType<?>> REGISTER = DeferredRegister.create(ForgeRegistries.CONTAINERS, MODID);

	public static final RegistryObject<ContainerType<EssenceHolderContainer>> ESSENCE_HOLDER = REGISTER.register("essence_holder", () -> new ContainerType<>(EssenceHolderContainer::new));
}
