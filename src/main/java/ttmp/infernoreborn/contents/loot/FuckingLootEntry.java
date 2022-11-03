package ttmp.infernoreborn.contents.loot;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonObject;
import com.google.gson.JsonSerializationContext;
import net.minecraft.item.ItemStack;
import net.minecraft.loot.ILootGenerator;
import net.minecraft.loot.LootContext;
import net.minecraft.loot.LootEntry;
import net.minecraft.loot.LootPoolEntryType;
import net.minecraft.loot.ValidationTracker;
import net.minecraft.loot.conditions.ILootCondition;
import net.minecraft.util.JSONUtils;
import ttmp.infernoreborn.contents.ModLootModifiers;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.function.Consumer;

/**
 * Mojang you fucking dipshit look what have you made me to write
 */
public class FuckingLootEntry extends LootEntry{
	protected final LootEntry[] children;

	public FuckingLootEntry(LootEntry[] children, ILootCondition[] conditions){
		super(conditions);
		this.children = children;
	}

	public void validate(ValidationTracker validator){
		super.validate(validator);
		if(this.children.length==0) validator.reportProblem("Empty children list");
		for(int i = 0; i<this.children.length; ++i) this.children[i].validate(validator.forChild(".entry["+i+"]"));
	}

	public final boolean expand(LootContext ctx, Consumer<ILootGenerator> consumer){
		if(!this.canRun(ctx)||children.length==0) return false;
		ILootGenerator[] loots = Arrays.stream(children).flatMap(e -> {
			List<ILootGenerator> lootGenerators = new ArrayList<>();
			e.expand(ctx, lootGenerators::add);
			return lootGenerators.stream();
		}).toArray(ILootGenerator[]::new);
		if(loots.length==0) return false;
		consumer.accept(new ILootGenerator(){
			@Override public int getWeight(float luck){
				return loots[0].getWeight(luck);
			}
			@Override public void createItemStack(Consumer<ItemStack> consumer, LootContext ctx){
				for(ILootGenerator loot : loots) loot.createItemStack(consumer, ctx);
			}
		});
		return true;
	}

	@Override public LootPoolEntryType getType(){
		return ModLootModifiers.FUCK_TYPE;
	}

	public static final class Serializer extends LootEntry.Serializer<FuckingLootEntry>{
		@Override public void serializeCustom(JsonObject obj, FuckingLootEntry entry, JsonSerializationContext ctx){
			obj.add("children", ctx.serialize(entry.children));
		}
		@Override public FuckingLootEntry deserializeCustom(JsonObject obj, JsonDeserializationContext ctx, ILootCondition[] conditions){
			LootEntry[] children = JSONUtils.getAsObject(obj, "children", ctx, LootEntry[].class);
			return new FuckingLootEntry(children, conditions);
		}
	}
}
