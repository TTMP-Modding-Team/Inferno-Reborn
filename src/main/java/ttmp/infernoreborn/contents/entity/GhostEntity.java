package ttmp.infernoreborn.contents.entity;

import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.MobEntity;
import net.minecraft.entity.ai.attributes.AttributeModifierMap;
import net.minecraft.entity.monster.SkeletonEntity;
import net.minecraft.entity.monster.ZombieEntity;
import net.minecraft.inventory.EquipmentSlotType;
import net.minecraft.item.ItemStack;
import net.minecraft.util.HandSide;
import net.minecraft.util.NonNullList;
import net.minecraft.world.World;
import ttmp.infernoreborn.contents.ModEntities;

public class GhostEntity extends MobEntity{

	private final NonNullList<ItemStack> armorItems = NonNullList.withSize(4, ItemStack.EMPTY);
	private final NonNullList<ItemStack> handItems = NonNullList.withSize(2, ItemStack.EMPTY);

	public GhostEntity(World world){
		this(ModEntities.GHOST.get(), world);
	}
	public GhostEntity(EntityType<? extends MobEntity> p_i48577_1_, World p_i48577_2_){
		super(p_i48577_1_, p_i48577_2_);
	}
	@Override public Iterable<ItemStack> getArmorSlots(){
		return this.armorItems;
	}
	@Override public ItemStack getItemBySlot(EquipmentSlotType pSlot){
		switch(pSlot.getType()){
			case HAND:
				return this.handItems.get(pSlot.getIndex());
			case ARMOR:
				return this.armorItems.get(pSlot.getIndex());
			default:
				return ItemStack.EMPTY;
		}
	}
	@Override public void setItemSlot(EquipmentSlotType pSlot, ItemStack pStack){
		switch(pSlot.getType()){
			case HAND:
				this.handItems.set(pSlot.getIndex(), pStack);
				break;
			case ARMOR:
				if(pSlot.getIndex()==0) return;
				this.armorItems.set(pSlot.getIndex(), pStack);
		}
	}
	@Override public HandSide getMainArm(){
		return HandSide.RIGHT;
	}

	public static AttributeModifierMap.MutableAttribute registerAttributes(){
		return ZombieEntity.createAttributes();
	}

}
