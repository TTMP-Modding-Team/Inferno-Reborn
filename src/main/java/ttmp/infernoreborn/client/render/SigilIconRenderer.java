package ttmp.infernoreborn.client.render;

import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.datafixers.util.Either;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.IRenderTypeBuffer;
import net.minecraft.client.renderer.ItemRenderer;
import net.minecraft.client.renderer.RenderTypeLookup;
import net.minecraft.client.renderer.model.BlockModel;
import net.minecraft.client.renderer.model.IBakedModel;
import net.minecraft.client.renderer.model.ItemCameraTransforms;
import net.minecraft.client.renderer.model.ItemCameraTransforms.TransformType;
import net.minecraft.client.renderer.model.ModelBakery;
import net.minecraft.client.renderer.model.ModelRotation;
import net.minecraft.client.renderer.model.RenderMaterial;
import net.minecraft.client.renderer.texture.MissingTextureSprite;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.renderer.tileentity.ItemStackTileEntityRenderer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.client.model.ModelLoader;
import ttmp.infernoreborn.api.sigil.Sigil;
import ttmp.infernoreborn.contents.item.SigilItem;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import static net.minecraft.inventory.container.PlayerContainer.BLOCK_ATLAS;
import static ttmp.infernoreborn.api.InfernoRebornApi.MODID;

public class SigilIconRenderer extends ItemStackTileEntityRenderer{
	private static final Map<Sigil, RenderMaterial> overlayRenderMaterial = new HashMap<>();

	private static final ResourceLocation missingno = new ResourceLocation(MODID, "sigil/missingno");
	private static final RenderMaterial missingnoSigil = new RenderMaterial(BLOCK_ATLAS, missingno);

	public static ResourceLocation missingnoTexture(){
		return missingno;
	}

	public static RenderMaterial renderMaterial(Sigil sigil){
		RenderMaterial m = overlayRenderMaterial.computeIfAbsent(sigil,
				s -> new RenderMaterial(BLOCK_ATLAS, s.getSigilTextureLocation()));
		return m.sprite().getName().equals(MissingTextureSprite.getLocation()) ? missingnoSigil : m;
	}
	public static TextureAtlasSprite texture(Sigil sigil){
		return renderMaterial(sigil).sprite();
	}

	private final Map<ResourceLocation, IBakedModel> fuckingModels = new HashMap<>();

	@Override public void renderByItem(ItemStack stack, TransformType transformType, MatrixStack pose, IRenderTypeBuffer buffer, int packedLight, int packedOverlay){
		if(ModelLoader.instance()==null) return;
		Sigil sigil = SigilItem.getSigil(stack);
		RenderMaterial m = sigil!=null ? renderMaterial(sigil) : missingnoSigil;

		IBakedModel fuckingModel = fuckingModels.computeIfAbsent(m.texture(), tex_ -> {
			BlockModel model = new BlockModel(new ResourceLocation("builtin/generated"),
					Collections.emptyList(),
					Collections.singletonMap("layer0", Either.left(m)),
					false,
					BlockModel.GuiLight.FRONT,
					ItemCameraTransforms.NO_TRANSFORMS,
					Collections.emptyList());
			model.parent = ModelBakery.GENERATION_MARKER;
			return model.bake(ModelLoader.instance(), model, r -> r.sprite(), ModelRotation.X0_Y0, m.texture(), false);
		});
		Minecraft.getInstance().getItemRenderer().renderModelLists(
				fuckingModel,
				stack,
				packedLight,
				packedOverlay,
				pose,
				ItemRenderer.getFoilBufferDirect(buffer,
						RenderTypeLookup.getRenderType(stack, true),
						true,
						stack.hasFoil()));
	}
}
