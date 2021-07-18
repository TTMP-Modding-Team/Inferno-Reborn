package ttmp.infernoreborn.compat.jei;

import mezz.jei.api.IModPlugin;
import mezz.jei.api.JeiPlugin;
import mezz.jei.api.registration.IRecipeCatalystRegistration;
import mezz.jei.api.registration.IRecipeCategoryRegistration;
import mezz.jei.api.registration.IRecipeRegistration;
import mezz.jei.api.registration.ISubtypeRegistration;
import net.minecraft.util.ResourceLocation;
import ttmp.infernoreborn.contents.ModItems;
import ttmp.infernoreborn.contents.item.FixedAbilityItem;
import ttmp.infernoreborn.contents.item.GeneratorAbilityItem;

import static ttmp.infernoreborn.InfernoReborn.MODID;

@JeiPlugin
public class InfernoRebornJeiPlugin implements IModPlugin{
	@Override public ResourceLocation getPluginUid(){
		return new ResourceLocation(MODID);
	}
	@Override public void registerItemSubtypes(ISubtypeRegistration registration){
		registration.registerSubtypeInterpreter(ModItems.INFERNO_SPARK.get(), stack -> String.valueOf(FixedAbilityItem.getAbilities(stack)));
		registration.registerSubtypeInterpreter(ModItems.GENERATOR_INFERNO_SPARK.get(), stack -> String.valueOf(GeneratorAbilityItem.getGenerator(stack)));
	}
	@Override public void registerCategories(IRecipeCategoryRegistration registration){
		//IGuiHelper guiHelper = registration.getJeiHelpers().getGuiHelper();
		//registration.addRecipeCategories(
		//		new FoundryRecipeCategory(guiHelper)
		//);
	}
	@Override public void registerRecipes(IRecipeRegistration registration){
		//ClientWorld world = Minecraft.getInstance().level;
		//registration.addRecipes(world.getRecipeManager().getAllRecipesFor(ModRecipes.FOUNDRY_RECIPE_TYPE), FoundryRecipeCategory.UID);
	}

	@Override public void registerRecipeCatalysts(IRecipeCatalystRegistration registration){
		//registration.addRecipeCatalyst(new ItemStack(ModItems.FOUNDRY.get()), FoundryRecipeCategory.UID);
	}
}
