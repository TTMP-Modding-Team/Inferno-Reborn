package ttmp.infernoreborn.contents.entity.projectile;

import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.projectile.DamagingProjectileEntity;
import net.minecraft.util.math.BlockRayTraceResult;
import net.minecraft.util.math.EntityRayTraceResult;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraft.world.Explosion;
import net.minecraft.world.World;
import ttmp.infernoreborn.util.LivingOnlyEntityDamageSource;

public class CreeperMissileEntity extends BaseProjectileEntity {
    public CreeperMissileEntity(EntityType<? extends CreeperMissileEntity> type, World world){
        super(type, world);
    }
    @Override
    protected void onHit(RayTraceResult result) {
        Vector3d pos = result.getLocation();
        Entity owner = this.getOwner();
        this.level.explode(owner, new LivingOnlyEntityDamageSource("explosion.player", this, owner).setExplosion(),
                null, pos.x, pos.y, pos.z, 2f, false, Explosion.Mode.NONE);
        super.onHit(result);
    }

    @Override
    protected void defineSynchedData() {}
}
