package ttmp.infernoreborn.client.render;

import com.mojang.blaze3d.matrix.MatrixStack;
import net.minecraft.block.SkullBlock;
import net.minecraft.client.renderer.IRenderTypeBuffer;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.EntityRendererManager;
import net.minecraft.client.renderer.tileentity.SkullTileEntityRenderer;
import net.minecraft.inventory.container.PlayerContainer;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import ttmp.infernoreborn.contents.entity.projectile.CreeperMissileEntity;

@OnlyIn(Dist.CLIENT)
public class CreeperMissileEntityRenderer extends EntityRenderer<CreeperMissileEntity> {
    public CreeperMissileEntityRenderer(EntityRendererManager manager) {
        super(manager);
    }

    public void render(CreeperMissileEntity entity, float entityYaw, float partialTicks, MatrixStack stack, IRenderTypeBuffer typeBuffer, int packedLight) {
        SkullTileEntityRenderer.renderSkull(null, 180, SkullBlock.Types.CREEPER, null, 0, stack, typeBuffer, packedLight);
        super.render(entity, entityYaw, partialTicks, stack, typeBuffer, packedLight);
    }

    /**
     * Returns the location of an entity's texture.
     */
    public ResourceLocation getTextureLocation(CreeperMissileEntity pEntity) {
        return PlayerContainer.BLOCK_ATLAS;
    }
}