package ttmp.infernoreborn.client;

import net.minecraft.client.particle.IAnimatedSprite;
import net.minecraft.client.particle.IParticleFactory;
import net.minecraft.client.particle.IParticleRenderType;
import net.minecraft.client.particle.Particle;
import net.minecraft.client.particle.SpriteTexturedParticle;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.particles.BasicParticleType;
import net.minecraft.util.math.BlockPos;
import ttmp.infernoreborn.client.render.CrucibleTileEntityRenderer;
import ttmp.infernoreborn.contents.tile.crucible.Crucible;
import ttmp.infernoreborn.contents.tile.crucible.CrucibleTile;

public class CrucibleBubbleParticle extends SpriteTexturedParticle{
	protected CrucibleBubbleParticle(ClientWorld level, double x, double y, double z, double xd, double yd, double zd){
		super(level, x, y, z, xd, yd, zd);
		setPower(.1f);
		this.hasPhysics = false;
		this.lifetime *= 2;
	}

	public void tick(){
		this.xo = this.x;
		this.yo = this.y;
		this.zo = this.z;
		if(this.age++<this.lifetime){
			this.yd += 0.01;
			this.move(this.xd, this.yd, this.zd);
			this.xd *= 0.85;
			this.yd *= 0.85;
			this.zd *= 0.85;
			CrucibleTile crucible = Crucible.crucible(level, new BlockPos(this.x, this.y, this.z));
			if(crucible!=null){
				double fillRate = crucible.getMaxFluidFillRate();
				if(fillRate>0){
					boolean onCampfire = crucible.isOnCampfire();
					BlockPos p = crucible.getBlockPos();
					this.y = p.getY()+CrucibleTileEntityRenderer.getFluidLevel(
							onCampfire ? 6/16f : 2/16f, onCampfire ? 1 : 12/16f, fillRate);
					if(x<p.getX()+3/16f) x = p.getX()+4/16f;
					else if(x>p.getX()+13/16f) x = p.getX()+12/16f;
					if(z<p.getZ()+3/16f) z = p.getZ()+4/16f;
					else if(z>p.getZ()+13/16f) z = p.getZ()+12/16f;
					return;
				}
			}
		}
		this.remove();
	}

	@Override public IParticleRenderType getRenderType(){
		return IParticleRenderType.PARTICLE_SHEET_OPAQUE;
	}

	public static class Factory implements IParticleFactory<BasicParticleType>{
		private final IAnimatedSprite sprite;

		public Factory(IAnimatedSprite pSprites){
			this.sprite = pSprites;
		}

		public Particle createParticle(BasicParticleType type, ClientWorld level, double x, double y, double z, double xd, double yd, double zd){
			CrucibleBubbleParticle p = new CrucibleBubbleParticle(level, x, y, z, xd, yd, zd);
			p.pickSprite(this.sprite);
			p.scale(.5f);
			return p;
		}
	}
}
