package ttmp.infernoreborn.compat.jei;

import mezz.jei.api.IModPlugin;
import mezz.jei.api.JeiPlugin;
import mezz.jei.api.registration.IRecipeRegistration;
import mezz.jei.api.registration.ISubtypeRegistration;
import net.minecraft.util.ResourceLocation;
import ttmp.infernoreborn.contents.ModItems;
import ttmp.infernoreborn.contents.item.FixedAbilityItem;
import ttmp.infernoreborn.contents.item.GeneratorAbilityItem;

import static ttmp.infernoreborn.InfernoReborn.MODID;

@JeiPlugin
public class JEIHelper implements IModPlugin{
	@Override public ResourceLocation getPluginUid(){
		return new ResourceLocation(MODID);
	}
	@Override public void registerItemSubtypes(ISubtypeRegistration registration){
		registration.registerSubtypeInterpreter(ModItems.INFERNO_SPARK.get(), stack -> String.valueOf(FixedAbilityItem.getAbilities(stack)));
		registration.registerSubtypeInterpreter(ModItems.GENERATOR_INFERNO_SPARK.get(), stack -> String.valueOf(GeneratorAbilityItem.getGenerator(stack)));
	}
	@Override public void registerRecipes(IRecipeRegistration registration){
	}
}
