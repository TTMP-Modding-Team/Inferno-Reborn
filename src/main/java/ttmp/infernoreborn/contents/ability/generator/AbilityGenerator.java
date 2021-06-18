package ttmp.infernoreborn.contents.ability.generator;

import com.google.common.collect.ImmutableSet;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.util.JSONUtils;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.registries.ForgeRegistries;
import ttmp.infernoreborn.contents.ability.generator.node.Node;
import ttmp.infernoreborn.contents.ability.generator.node.action.Action;
import ttmp.infernoreborn.contents.ability.generator.parser.Parsers;
import ttmp.infernoreborn.contents.ability.generator.scheme.AbilityGeneratorScheme;
import ttmp.infernoreborn.contents.ability.holder.AbilityHolder;

import javax.annotation.Nullable;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

public final class AbilityGenerator{
	private final AbilityGeneratorScheme scheme;
	private final int weight;
	@Nullable private final Set<EntityType<?>> target;
	@Nullable private final Action action;

	public AbilityGenerator(AbilityGeneratorScheme scheme,
	                        int weight,
	                        @Nullable Set<EntityType<?>> target,
	                        @Nullable Action action){
		this.scheme = Objects.requireNonNull(scheme);
		this.weight = Math.max(0, weight);
		this.target = target;
		this.action = action;
	}
	public AbilityGenerator(ResourceLocation id, JsonObject object){
		this.scheme = new AbilityGeneratorScheme(id, object);
		this.weight = JSONUtils.getAsInt(object, "weight");
		if(object.has("target")){
			JsonArray target = JSONUtils.getAsJsonArray(object, "target");
			ImmutableSet.Builder<EntityType<?>> b = ImmutableSet.builder();
			for(JsonElement e : target){
				EntityType<?> t = ForgeRegistries.ENTITIES.getValue(new ResourceLocation(e.getAsString()));
				if(t!=null) b.add(t);
			}
			this.target = b.build();
		}else this.target = null;
		this.action = Parsers.ACTION_PARSER.parseOrNull(object);
	}

	public AbilityGeneratorScheme getScheme(){
		return scheme;
	}
	public int getWeight(){
		return weight;
	}
	@Nullable public Set<EntityType<?>> getTarget(){
		return target;
	}
	@Nullable public Action getAction(){
		return action;
	}

	public void generate(LivingEntity entity){
		AbilityHolder h = AbilityHolder.of(entity);
		if(h==null) return;
		if(target!=null&&!target.contains(entity.getType())) return;
		if(action!=null) action.act(entity, h);
	}

	public JsonObject serialize(){
		JsonObject o = action!=null ? action.serialize() : new JsonObject();
		scheme.serialize(o);
		o.addProperty("weight", weight);
		if(target!=null){
			JsonArray array = new JsonArray();
			for(EntityType<?> t : target)
				array.add(Node.toSerializedString(Objects.requireNonNull(t.getRegistryName())));
			o.add("target", array);
		}
		return o;
	}

	@Override public boolean equals(Object o){
		if(this==o) return true;
		if(o==null||getClass()!=o.getClass()) return false;
		AbilityGenerator that = (AbilityGenerator)o;
		return getScheme().equals(that.getScheme());
	}

	@Override public int hashCode(){
		return Objects.hash(getScheme());
	}

	@Override public String toString(){
		return "AbilityGenerator{"+
				"scheme="+scheme+
				", weight="+weight+
				", target="+(target!=null ? target.stream().map(EntityType::toString).collect(Collectors.joining(", ", "[", "]")) : "null")+
				", action="+action+
				'}';
	}
}
