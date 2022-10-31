package ttmp.infernoreborn.infernaltype.dsl.dynamic;

import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.monster.IMob;
import net.minecraft.util.ResourceLocation;
import ttmp.infernoreborn.infernaltype.InfernalGenContext;

import javax.annotation.Nullable;

public final class DynamicConstants{
	private DynamicConstants(){}

	public static final Dynamic ENTITY_TYPE = new Dynamic(){
		@Override public Object evaluate(@Nullable InfernalGenContext context){
			Dynamic.expectContext(context);
			LivingEntity entity = context.getEntity();
			ResourceLocation id = entity.getType().getRegistryName();
			return id==null ? "" : id.getNamespace().equals("minecraft") ? id.getPath() : id.toString();
		}
		@Override public boolean matches(Class<?> type){
			return type.isAssignableFrom(String.class);
		}
		@Override public String toString(){
			return "<Entity Type>";
		}
	};
	public static final DynamicBool ENTITY_IS_MOB = new DynamicBool(){
		@Override public boolean evaluateBool(@Nullable InfernalGenContext context){
			Dynamic.expectContext(context);
			LivingEntity entity = context.getEntity();
			return entity instanceof IMob;
		}
		@Override public String toString(){
			return "<Entity is Mob>";
		}
	};
}
