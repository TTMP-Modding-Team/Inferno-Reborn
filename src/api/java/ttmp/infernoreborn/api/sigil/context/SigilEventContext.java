package ttmp.infernoreborn.api.sigil.context;

import net.minecraft.entity.LivingEntity;
import net.minecraft.item.ItemStack;
import ttmp.infernoreborn.api.sigil.SigilHolder;

import javax.annotation.Nullable;
import java.util.function.Predicate;

public interface SigilEventContext{
	static SigilEventContext just(SigilHolder h){
		return new MinimumContext(h);
	}
	static SigilEventContext item(ItemStack stack, SigilHolder h){
		return new ItemContext(stack, h);
	}
	static SigilEventContext living(LivingEntity entity, SigilHolder h){
		return new LivingContext(entity, h);
	}

	SigilHolder holder();

	@Nullable default ItemContext getAsItemContext(){
		return null;
	}
	default boolean isItemContext(){
		return getAsItemContext()!=null;
	}
	default boolean withItemContext(Predicate<ItemContext> consumer){
		ItemContext ctx = getAsItemContext();
		return ctx!=null&&consumer.test(ctx);
	}

	@Nullable default LivingContext getAsLivingContext(){
		return null;
	}
	default boolean isLivingContext(){
		return getAsLivingContext()!=null;
	}
	default boolean withLivingContext(Predicate<LivingContext> consumer){
		LivingContext ctx = getAsLivingContext();
		return ctx!=null&&consumer.test(ctx);
	}
}
