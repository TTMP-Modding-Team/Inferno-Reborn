package ttmp.infernoreborn.contents;

import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.container.ContainerType;
import net.minecraftforge.fml.RegistryObject;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import ttmp.infernoreborn.contents.container.EssenceHolderContainer;
import ttmp.infernoreborn.contents.container.FoundryContainer;
import ttmp.infernoreborn.contents.container.SigilEngravingTableContainer;
import ttmp.infernoreborn.contents.container.SigilScrapperContainer;
import ttmp.infernoreborn.contents.container.StigmaScrapperContainer;
import ttmp.infernoreborn.contents.container.StigmaTableContainer;

import static ttmp.infernoreborn.api.InfernoRebornApi.MODID;

public final class ModContainers{
	private ModContainers(){}

	public static final DeferredRegister<ContainerType<?>> REGISTER = DeferredRegister.create(ForgeRegistries.CONTAINERS, MODID);

	public static final RegistryObject<ContainerType<EssenceHolderContainer>> ESSENCE_HOLDER = REGISTER.register("essence_holder", () -> new ContainerType<>(EssenceHolderContainer::new));

	public static final RegistryObject<ContainerType<SigilEngravingTableContainer>> SIGIL_ENGRAVING_TABLE_3X3 = REGISTER.register("sigil_engraving_table_3x3", () -> new ContainerType<>(SigilEngravingTableContainer::create3x3));
	public static final RegistryObject<ContainerType<SigilEngravingTableContainer>> SIGIL_ENGRAVING_TABLE_5X5 = REGISTER.register("sigil_engraving_table_5x5", () -> new ContainerType<>(SigilEngravingTableContainer::create5x5));
	public static final RegistryObject<ContainerType<SigilEngravingTableContainer>> SIGIL_ENGRAVING_TABLE_7X7 = REGISTER.register("sigil_engraving_table_7x7", () -> new ContainerType<>(SigilEngravingTableContainer::create7x7));

	public static final RegistryObject<ContainerType<StigmaTableContainer>> STIGMA_TABLE_5X5 = REGISTER.register("stigma_table_5x5", () -> new ContainerType<>(StigmaTableContainer::create5x5));
	public static final RegistryObject<ContainerType<StigmaTableContainer>> STIGMA_TABLE_7X7 = REGISTER.register("stigma_table_7x7", () -> new ContainerType<>(StigmaTableContainer::create7x7));

	public static final RegistryObject<ContainerType<SigilScrapperContainer>> SIGIL_SCRAPPER = REGISTER.register("sigil_scrapper", () -> new ContainerType<>(SigilScrapperContainer::new));
	public static final RegistryObject<ContainerType<StigmaScrapperContainer>> STIGMA_SCRAPPER = REGISTER.register("stigma_scrapper", () -> new ContainerType<>((int id, PlayerInventory playerInventory) -> new StigmaScrapperContainer(id, playerInventory)));
	public static final RegistryObject<ContainerType<FoundryContainer>> FOUNDRY = REGISTER.register("foundry", () -> new ContainerType<>(FoundryContainer::new));
}
