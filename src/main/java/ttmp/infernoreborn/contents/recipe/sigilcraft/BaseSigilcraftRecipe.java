package ttmp.infernoreborn.contents.recipe.sigilcraft;

import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.IRecipeSerializer;
import net.minecraft.item.crafting.IRecipeType;
import net.minecraft.item.crafting.Ingredient;
import net.minecraft.util.NonNullList;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.World;
import net.minecraftforge.common.crafting.IShapedRecipe;
import ttmp.infernoreborn.api.RecipeTypes;
import ttmp.infernoreborn.api.sigil.SigilcraftInventory;
import ttmp.infernoreborn.api.sigil.SigilcraftRecipe;

public abstract class BaseSigilcraftRecipe implements SigilcraftRecipe, IShapedRecipe<SigilcraftInventory>{
	protected final ResourceLocation id;
	protected final String group;
	protected final int width;
	protected final int height;
	protected final NonNullList<Ingredient> ingredients;
	protected final int centerIngredient;
	protected final boolean mirror;

	public BaseSigilcraftRecipe(ResourceLocation id,
	                            String group,
	                            int width,
	                            int height,
	                            NonNullList<Ingredient> ingredients,
	                            int centerIngredient,
	                            boolean mirror){
		this.id = id;
		this.group = group;
		this.width = width;
		this.height = height;
		this.ingredients = ingredients;
		this.centerIngredient = centerIngredient;
		this.mirror = mirror;
	}

	public int getCenterIngredient(){
		return centerIngredient;
	}

	public int getCenterX(){
		return indexToX(getCenterIngredient());
	}
	public int getCenterY(){
		return indexToY(getCenterIngredient());
	}

	public boolean isMirror(){
		return mirror;
	}

	@Override public boolean matches(SigilcraftInventory inv, World world){
		return matchCore(inv)&&(matches(inv, false)||isMirror()&&matches(inv, true));
	}
	@Override public abstract ItemStack assemble(SigilcraftInventory inv);

	protected boolean matches(SigilcraftInventory inv, boolean mirror){
		int recipeOriginX = inv.getWidth()/2-getCenterX();
		int recipeOriginY = inv.getHeight()/2-getCenterY();
		if(recipeOriginX<0||recipeOriginY<0||recipeOriginX+getRecipeWidth()>inv.getWidth()||recipeOriginY+getRecipeHeight()>inv.getHeight()) return false;

		for(int x = 0; x<inv.getWidth(); ++x){
			for(int y = 0; y<inv.getHeight(); ++y){
				if(x==inv.getWidth()/2&&y==inv.getHeight()/2) continue;

				int ingX = x-recipeOriginX;
				int ingY = y-recipeOriginY;
				Ingredient ingredient = ingX>=0&&ingY>=0&&ingX<getRecipeWidth()&&ingY<getRecipeHeight() ?
						getIngredients().get(toIndex(mirror ? getRecipeWidth()-ingX-1 : ingX, ingY)) :
						Ingredient.EMPTY;

				if(!ingredient.test(inv.getItem(x, y))) return false;
			}
		}
		return true;
	}

	protected boolean matchCore(SigilcraftInventory inv){
		return getIngredients().get(centerIngredient).test(inv.getCenterItem());
	}

	@Override public boolean canCraftInDimensions(int width, int height){
		int cx = indexToX(centerIngredient);
		int cy = indexToY(centerIngredient);

		int minIngredientX = width/2-getRecipeWidth()+cx+1;
		int maxIngredientX = width/2+getRecipeWidth()-cx-1;
		int minIngredientY = height/2-getRecipeHeight()+cy+1;
		int maxIngredientY = height/2+getRecipeHeight()-cy-1;

		return minIngredientX>=0&&maxIngredientX<width&&minIngredientY>=0&&maxIngredientY<height;
	}

	@Override public abstract ItemStack getResultItem();
	@Override public ResourceLocation getId(){
		return id;
	}
	@Override public String getGroup(){
		return group;
	}
	@Override public NonNullList<Ingredient> getIngredients(){
		return ingredients;
	}

	@Override public abstract IRecipeSerializer<?> getSerializer();
	@Override public IRecipeType<?> getType(){
		return RecipeTypes.sigilcraft();
	}

	@Override public int getRecipeWidth(){
		return width;
	}
	@Override public int getRecipeHeight(){
		return height;
	}

	public final int indexToX(int pos){
		return indexToX(pos, getRecipeWidth());
	}
	public final int indexToY(int pos){
		return indexToY(pos, getRecipeWidth());
	}
	public final int toIndex(int x, int y){
		return toIndex(x, y, getRecipeWidth());
	}

	public static int indexToX(int pos, int width){
		return pos%width;
	}
	public static int indexToY(int pos, int width){
		return pos/width;
	}
	public static int toIndex(int x, int y, int width){
		return x+y*width;
	}
}
