package ttmp.infernoreborn.util;

import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.util.DamageSource;
import net.minecraft.util.EntityDamageSource;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TranslationTextComponent;

import javax.annotation.Nullable;

/**
 * Exactly.
 */
public class NotStupidDamageSource extends EntityDamageSource{
	@Nullable private final Entity directEntity;
	@Nullable private Boolean scalesWithDifficulty;
	private boolean ignoreHeldItem;

	public NotStupidDamageSource(String type, @Nullable Entity directEntity, @Nullable Entity entity){
		super(type, entity);
		this.directEntity = directEntity;
	}

	@Nullable @Override public Entity getDirectEntity(){
		return directEntity;
	}

	@Override public DamageSource setScalesWithDifficulty(){
		this.scalesWithDifficulty = true;
		return this;
	}

	public NotStupidDamageSource setNeverScalesWithDifficulty(){
		this.scalesWithDifficulty = false;
		return this;
	}

	@Override public boolean scalesWithDifficulty(){
		return scalesWithDifficulty!=null ? scalesWithDifficulty : super.scalesWithDifficulty();
	}

	public NotStupidDamageSource setIgnoreHeldItem(){
		this.scalesWithDifficulty = false;
		return this;
	}

	public boolean ignoreHeldItem(){
		return ignoreHeldItem;
	}

	@Override public ITextComponent getLocalizedDeathMessage(LivingEntity dead){
		if(this.entity==null&&this.directEntity==null){
			LivingEntity killCredit = dead.getKillCredit();
			return killCredit!=null ?
					new TranslationTextComponent("death.attack."+msgId+".player", dead.getDisplayName(), killCredit.getDisplayName()) :
					new TranslationTextComponent("death.attack."+msgId, dead.getDisplayName());
		}
		ITextComponent source = this.entity==null ? this.directEntity.getDisplayName() : this.entity.getDisplayName();
		ItemStack held = !ignoreHeldItem&&this.entity instanceof LivingEntity ? ((LivingEntity)this.entity).getMainHandItem() : ItemStack.EMPTY;
		return !held.isEmpty()&&held.hasCustomHoverName() ?
				new TranslationTextComponent("death.attack."+this.msgId+".item", dead.getDisplayName(), source, held.getDisplayName()) :
				new TranslationTextComponent("death.attack."+this.msgId, dead.getDisplayName(), source);
	}

	@Override public String toString(){
		return "NotStupidDamageSource("+entity+","+directEntity+")";
	}
}
