package ttmp.infernoreborn.mixin;

import net.minecraft.entity.EntityType;
import net.minecraft.entity.MobEntity;
import net.minecraft.entity.monster.SlimeEntity;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import ttmp.infernoreborn.util.SlimeEntityAccessor;

@Mixin(SlimeEntity.class)
public class MixinSlimeEntity extends MobEntity implements SlimeEntityAccessor{
	public MixinSlimeEntity(EntityType<? extends MobEntity> type, World world){
		super(type, world);
	}

	@Shadow private void setSize(int size, boolean resetHealth){}

	@Override public void setSlimeSize(int size, boolean resetHealth){
		this.setSize(size, resetHealth);
	}
}
