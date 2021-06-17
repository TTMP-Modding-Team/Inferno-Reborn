package ttmp.infernoreborn.recipe.sigilcraft;

import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import com.google.gson.JsonSyntaxException;
import net.minecraft.item.crafting.IRecipeSerializer;
import net.minecraft.item.crafting.Ingredient;
import net.minecraft.util.JSONUtils;
import net.minecraft.util.NonNullList;
import net.minecraftforge.registries.ForgeRegistryEntry;

import java.util.Map;
import java.util.Set;

public abstract class BaseSigilcraftRecipeSerializer<R extends SigilcraftRecipe> extends ForgeRegistryEntry<IRecipeSerializer<?>> implements IRecipeSerializer<R>{
	protected int readCenter(JsonObject object, int recipeWidth, int recipeHeight){
		String[] pattern = patternFromJson(JSONUtils.getAsJsonArray(object, "pattern"));
		String center = JSONUtils.getAsString(object, "center");
		if(center.length()!=1) throw new JsonParseException("Invalid center");
		int centerIndex = -1;
		for(int y = 0; y<pattern.length; y++){
			String p = pattern[y];
			for(int x = 0; x<p.length(); x++){
				if(p.charAt(x)==center.charAt(0)){
					if(centerIndex>=0) throw new JsonParseException("Duplicated occurrence of center key");
					centerIndex = x+y*recipeWidth;
				}
			}
		}
		if(centerIndex<0) throw new JsonParseException("No occurrence of center key");
		return centerIndex;
	}

	protected String[] patternFromJson(JsonArray arr){
		String[] pattern = new String[arr.size()];
		if(pattern.length==0) throw new JsonSyntaxException("Invalid pattern: empty pattern not allowed");
		for(int i = 0; i<pattern.length; ++i){
			String s = JSONUtils.convertToString(arr.get(i), "pattern["+i+"]");
			if(i>0&&pattern[0].length()!=s.length()) throw new JsonSyntaxException("Invalid pattern: each row must be the same width");
			pattern[i] = s;
		}

		return pattern;
	}

	protected NonNullList<Ingredient> dissolvePattern(String[] pattern, Map<String, Ingredient> keys, int width, int height, int center, boolean allowEmptyCenter){
		NonNullList<Ingredient> list = NonNullList.withSize(width*height, Ingredient.EMPTY);
		Set<String> set = Sets.newHashSet(keys.keySet());
		set.remove(" ");

		for(int y = 0; y<pattern.length; ++y){
			String s = pattern[y];
			for(int x = 0; x<s.length(); ++x){
				String key = s.substring(x, x+1);

				if(center==x+y*width&&allowEmptyCenter){
					set.remove(key);
					list.set(x+width*y, Ingredient.EMPTY);
					continue;
				}
				Ingredient ingredient = keys.get(key);
				if(ingredient==null) throw new JsonSyntaxException("Pattern references symbol '"+key+"' but it's not defined in the key");

				set.remove(key);
				list.set(x+width*y, ingredient);
			}
		}
		if(!set.isEmpty()) throw new JsonSyntaxException("Key defines symbols that aren't used in pattern: "+set);
		else return list;
	}

	protected String[] shrink(String... wtf){
		int i = Integer.MAX_VALUE;
		int j = 0;
		int k = 0;
		int l = 0;

		for(int i1 = 0; i1<wtf.length; ++i1){
			String s = wtf[i1];
			i = Math.min(i, firstNonSpace(s));
			int j1 = lastNonSpace(s);
			j = Math.max(j, j1);
			if(j1<0){
				if(k==i1) ++k;
				++l;
			}else l = 0;
		}

		if(wtf.length==l) return new String[0];
		String[] arr = new String[wtf.length-l-k];
		for(int k1 = 0; k1<arr.length; ++k1){
			arr[k1] = wtf[k1+k].substring(i, j+1);
		}
		return arr;
	}

	protected int firstNonSpace(String s){
		int i = 0;
		while(i<s.length()&&s.charAt(i)==' ') ++i;
		return i;
	}

	protected int lastNonSpace(String s){
		int i = s.length()-1;
		while(i>=0&&s.charAt(i)==' ') --i;
		return i;
	}

	protected Map<String, Ingredient> keyFromJson(JsonObject o){
		Map<String, Ingredient> map = Maps.newHashMap();
		for(Map.Entry<String, JsonElement> entry : o.entrySet()){
			if(entry.getKey().length()!=1) throw new JsonSyntaxException("Invalid key entry: '"+entry.getKey()+"' is an invalid symbol (must be 1 character only).");
			if(" ".equals(entry.getKey())) throw new JsonSyntaxException("Invalid key entry: ' ' is a reserved symbol.");
			map.put(entry.getKey(), Ingredient.fromJson(entry.getValue()));
		}
		map.put(" ", Ingredient.EMPTY);
		return map;
	}
}
