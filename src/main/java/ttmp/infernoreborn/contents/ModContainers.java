package ttmp.infernoreborn.contents;

import net.minecraft.inventory.container.ContainerType;
import net.minecraftforge.fml.RegistryObject;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import ttmp.infernoreborn.container.EssenceHolderContainer;
import ttmp.infernoreborn.container.SigilEngravingTableContainer;

import static ttmp.infernoreborn.InfernoReborn.MODID;

public final class ModContainers{
	private ModContainers(){}

	public static final DeferredRegister<ContainerType<?>> REGISTER = DeferredRegister.create(ForgeRegistries.CONTAINERS, MODID);

	public static final RegistryObject<ContainerType<EssenceHolderContainer>> ESSENCE_HOLDER = REGISTER.register("essence_holder", () -> new ContainerType<>(EssenceHolderContainer::new));
	public static final RegistryObject<ContainerType<SigilEngravingTableContainer>> SIGIL_ENGRAVING_TABLE_3X3 = REGISTER.register("sigil_engraving_table_3x3", () -> new ContainerType<>(SigilEngravingTableContainer::create3x3));
	public static final RegistryObject<ContainerType<SigilEngravingTableContainer>> SIGIL_ENGRAVING_TABLE_5X5 = REGISTER.register("sigil_engraving_table_5x5", () -> new ContainerType<>(SigilEngravingTableContainer::create5x5));
	public static final RegistryObject<ContainerType<SigilEngravingTableContainer>> SIGIL_ENGRAVING_TABLE_7X7 = REGISTER.register("sigil_engraving_table_7x7", () -> new ContainerType<>(SigilEngravingTableContainer::create7x7));
}
