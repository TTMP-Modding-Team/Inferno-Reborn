package ttmp.infernoreborn.datagen;

import net.minecraft.data.DataGenerator;
import net.minecraft.loot.conditions.ILootCondition;
import net.minecraft.loot.conditions.KilledByPlayer;
import net.minecraftforge.common.data.GlobalLootModifierProvider;
import ttmp.infernoreborn.contents.ModLootModifiers;
import ttmp.infernoreborn.contents.loot.EssenceLootModifier;

import static ttmp.infernoreborn.InfernoReborn.MODID;

public class LootModifierGen extends GlobalLootModifierProvider{
	public LootModifierGen(DataGenerator gen){
		super(gen, MODID);
	}

	@Override protected void start(){
		add("essence", ModLootModifiers.ESSENCE.get(), new EssenceLootModifier(
				new ILootCondition[]{
						KilledByPlayer.killedByPlayer().build()
				}
		));
	}
}
