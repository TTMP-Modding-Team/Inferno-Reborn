package datagen;

import net.minecraft.data.DataGenerator;
import net.minecraft.data.FluidTagsProvider;
import net.minecraft.fluid.Fluids;
import net.minecraftforge.common.ForgeMod;
import net.minecraftforge.common.data.ExistingFileHelper;
import ttmp.infernoreborn.contents.ModTags;

import javax.annotation.Nullable;

import static ttmp.infernoreborn.api.InfernoRebornApi.MODID;

public class FluidTagGen extends FluidTagsProvider{
	public FluidTagGen(DataGenerator gen, @Nullable ExistingFileHelper existingFileHelper){
		super(gen, MODID, existingFileHelper);
	}

	@Override protected void addTags(){
		this.tag(ModTags.Fluids.CAN_VAPORIZE_IN_CRUCIBLE).add(Fluids.WATER, Fluids.FLOWING_WATER, ForgeMod.MILK.get(), ForgeMod.FLOWING_MILK.get());
	}
}
