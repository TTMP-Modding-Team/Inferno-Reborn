package ttmp.infernoreborn.client.render;

import net.minecraft.client.renderer.culling.ClippingHelper;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.EntityRendererManager;
import net.minecraft.inventory.container.PlayerContainer;
import net.minecraft.util.ResourceLocation;
import ttmp.infernoreborn.contents.entity.JudgementEntity;

public class JudgementRenderer extends EntityRenderer<JudgementEntity>{
	public JudgementRenderer(EntityRendererManager m){
		super(m);
	}
	@Override public ResourceLocation getTextureLocation(JudgementEntity entity){
		return PlayerContainer.BLOCK_ATLAS;
	}

	@Override public boolean shouldRender(JudgementEntity entity, ClippingHelper camera, double x, double y, double z){
		return false;
	}
}
