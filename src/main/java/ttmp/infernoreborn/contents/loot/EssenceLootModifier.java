package ttmp.infernoreborn.contents.loot;

import com.google.gson.JsonObject;
import net.minecraft.entity.Entity;
import net.minecraft.item.ItemStack;
import net.minecraft.loot.LootContext;
import net.minecraft.loot.LootParameters;
import net.minecraft.loot.conditions.ILootCondition;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.common.loot.GlobalLootModifierSerializer;
import net.minecraftforge.common.loot.LootModifier;
import ttmp.infernoreborn.contents.ability.Ability;
import ttmp.infernoreborn.contents.ability.holder.AbilityHolder;
import ttmp.infernoreborn.util.EssenceType;

import javax.annotation.Nonnull;
import java.util.List;

public class EssenceLootModifier extends LootModifier{
	public EssenceLootModifier(ILootCondition[] conditions){
		super(conditions);
	}

	@Nonnull @Override protected List<ItemStack> doApply(List<ItemStack> generatedLoot, LootContext context){
		Entity e = context.getParamOrNull(LootParameters.THIS_ENTITY);
		if(e!=null){
			AbilityHolder h = AbilityHolder.of(e);
			if(h!=null){
				for(EssenceType type : EssenceType.values()){
					int amount = 0;
					for(Ability a : h.getAbilities()) amount += a.getDrop(type);

					if(amount>=9*9){
						generatedLoot.add(new ItemStack(type.getExquisiteEssenceItem(), amount/9*9));
						amount %= 9*9;
					}
					if(amount>=9){
						generatedLoot.add(new ItemStack(type.getGreaterEssenceItem(), amount/9));
						amount %= 9;
					}
					if(amount>=1){
						generatedLoot.add(new ItemStack(type.getEssenceItem(), amount));
					}
				}
			}
		}
		return generatedLoot;
	}

	public static final class Serializer extends GlobalLootModifierSerializer<EssenceLootModifier>{
		@Override public EssenceLootModifier read(ResourceLocation location, JsonObject object, ILootCondition[] conditions){
			return new EssenceLootModifier(conditions);
		}
		@Override public JsonObject write(EssenceLootModifier instance){
			return makeConditions(instance.conditions);
		}
	}
}
