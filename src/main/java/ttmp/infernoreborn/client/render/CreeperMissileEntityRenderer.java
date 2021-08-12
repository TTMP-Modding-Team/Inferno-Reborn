package ttmp.infernoreborn.client.render;

import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.vertex.IVertexBuilder;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.SkullBlock;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.BlockRendererDispatcher;
import net.minecraft.client.renderer.IRenderTypeBuffer;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.RenderTypeLookup;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.EntityRendererManager;
import net.minecraft.client.renderer.entity.MobRenderer;
import net.minecraft.client.renderer.entity.layers.CreeperChargeLayer;
import net.minecraft.client.renderer.entity.model.CreeperModel;
import net.minecraft.client.renderer.entity.model.GenericHeadModel;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.client.renderer.tileentity.SkullTileEntityRenderer;
import net.minecraft.entity.monster.CreeperEntity;
import net.minecraft.entity.projectile.WitherSkullEntity;
import net.minecraft.inventory.container.PlayerContainer;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.client.ForgeHooksClient;
import net.minecraftforge.client.model.data.EmptyModelData;
import ttmp.infernoreborn.contents.block.GoldenSkullBlock;
import ttmp.infernoreborn.contents.entity.projectile.CreeperMissileEntity;

import java.util.Random;

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