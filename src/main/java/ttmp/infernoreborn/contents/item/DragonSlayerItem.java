package ttmp.infernoreborn.contents.item;

import net.minecraft.item.IItemTier;
import net.minecraft.item.SwordItem;
import net.minecraft.item.crafting.Ingredient;
import ttmp.infernoreborn.contents.ModTags;

import javax.annotation.Nullable;

public class DragonSlayerItem extends SwordItem{
	private static final IItemTier MAT = new IItemTier(){
		@Override public int getUses(){
			return 3500;
		}
		@Override public float getSpeed(){
			return 6;
		}
		@Override public float getAttackDamageBonus(){
			return 3;
		}
		@Override public int getLevel(){
			return 3;
		}
		@Override public int getEnchantmentValue(){
			return 9;
		}
		@Nullable private Ingredient repairIngredient;
		@Override public Ingredient getRepairIngredient(){
			if(repairIngredient==null) repairIngredient = Ingredient.of(ModTags.INGOTS_DAMASCUS_STEEL);
			return repairIngredient;
		}
	};

	public DragonSlayerItem(Properties properties){
		super(MAT, 3, -3, properties);
	}
}
