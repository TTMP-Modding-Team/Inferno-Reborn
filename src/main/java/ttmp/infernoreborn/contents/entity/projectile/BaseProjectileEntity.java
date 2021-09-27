package ttmp.infernoreborn.contents.entity.projectile;

import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.projectile.ProjectileEntity;
import net.minecraft.entity.projectile.ProjectileHelper;
import net.minecraft.network.IPacket;
import net.minecraft.util.DamageSource;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraft.world.World;
import net.minecraftforge.event.ForgeEventFactory;
import net.minecraftforge.fml.network.NetworkHooks;

public abstract class BaseProjectileEntity extends ProjectileEntity {
    public BaseProjectileEntity(EntityType<? extends BaseProjectileEntity> type, World world){
        super(type, world);
    }

    @Override
    public void tick(){
        Entity entity = this.getOwner();
        //noinspection deprecation
        if(this.level.isClientSide||(entity==null||entity.isAlive())&&this.level.hasChunkAt(this.blockPosition())){
            super.tick();

            RayTraceResult ray = ProjectileHelper.getHitResult(this, this::canHitEntity);
            if(ray.getType()!=RayTraceResult.Type.MISS&&!ForgeEventFactory.onProjectileImpact(this, ray)){
                this.onHit(ray);
            }

            this.checkInsideBlocks();
            Vector3d delta = this.getDeltaMovement();
            double x = this.getX()+delta.x;
            double y = this.getY()+delta.y;
            double z = this.getZ()+delta.z;
            ProjectileHelper.rotateTowardsMovement(this, 0.2F);
            this.setPos(x, y, z);
        }else this.remove();
    }

    @Override protected void onHit(RayTraceResult result){
        super.onHit(result);
        if(!this.level.isClientSide) this.remove();
    }
    @Override public boolean hurt(DamageSource source, float amount){
        return false;
    }
    @Override public IPacket<?> getAddEntityPacket(){
        return NetworkHooks.getEntitySpawningPacket(this);
    }

    public void shootEntityToTarget(Entity shooter, Entity target, float velocity){
        this.setPos(shooter.getX(), shooter.getEyeY(), shooter.getZ());
        double xDiff = target.getX()-shooter.getX();
        double yDiff = target.getY(1/3.0)-shooter.getY();
        double zDiff = target.getZ()-shooter.getZ();
        this.shoot(xDiff, yDiff, zDiff, velocity, 0);
    }
}
