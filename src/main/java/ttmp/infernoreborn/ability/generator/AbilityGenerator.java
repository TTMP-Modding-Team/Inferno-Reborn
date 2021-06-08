package ttmp.infernoreborn.ability.generator;

import com.google.common.collect.ImmutableSet;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.util.JSONUtils;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.registries.ForgeRegistries;
import ttmp.infernoreborn.ability.generator.node.Node;
import ttmp.infernoreborn.ability.generator.node.action.Action;
import ttmp.infernoreborn.ability.generator.parser.Parsers;
import ttmp.infernoreborn.capability.AbilityHolder;

import javax.annotation.Nullable;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

public final class AbilityGenerator{
	private final ResourceLocation id;
	private final int weight;
	@Nullable private final Set<EntityType<?>> target;
	@Nullable private final Action action;

	private final boolean displayItem;

	public AbilityGenerator(ResourceLocation id, int weight, @Nullable Set<EntityType<?>> target, @Nullable Action action, boolean displayItem){
		this.id = Objects.requireNonNull(id);
		this.weight = Math.max(0, weight);
		this.target = target;
		this.action = action;
		this.displayItem = displayItem;
	}
	public AbilityGenerator(ResourceLocation id, JsonObject object){
		this.id = Objects.requireNonNull(id);
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
		this.displayItem = JSONUtils.getAsBoolean(object, "displayItem", true);
	}

	public ResourceLocation getId(){
		return id;
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

	public boolean displayItem(){
		return displayItem;
	}

	public void generate(LivingEntity entity){
		AbilityHolder h = AbilityHolder.of(entity);
		if(h==null) return;
		if(target!=null&&!target.contains(entity.getType())) return;
		if(action!=null) action.act(entity, h);
	}

	public JsonObject serialize(){
		JsonObject o = action!=null ? action.serialize() : new JsonObject();
		o.addProperty("weight", weight);
		if(target!=null){
			JsonArray array = new JsonArray();
			for(EntityType<?> t : target)
				array.add(Node.toSerializedString(Objects.requireNonNull(t.getRegistryName())));
			o.add("target", array);
		}
		if(!displayItem) o.addProperty("displayItem", false);
		return o;
	}

	@Override public boolean equals(Object o){
		if(this==o) return true;
		if(o==null||getClass()!=o.getClass()) return false;
		AbilityGenerator that = (AbilityGenerator)o;
		return Objects.equals(getId(), that.getId());
	}
	@Override public int hashCode(){
		return getId().hashCode();
	}

	@Override public String toString(){
		return "AbilityGenerator{"+
				"id="+id+
				", weight="+weight+
				", target="+(target==null ? "null" : target.stream().map(EntityType::toString).collect(Collectors.joining(", ", "[", "]")))+
				", action="+action+
				", displayItem="+displayItem+
				'}';
	}
}
