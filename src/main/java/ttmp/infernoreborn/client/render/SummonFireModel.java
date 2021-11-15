package ttmp.infernoreborn.client.render;

import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.vertex.IVertexBuilder;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.model.Model;
import net.minecraft.client.renderer.model.ModelRenderer;
import net.minecraft.util.ResourceLocation;

import java.util.function.Function;

public class SummonFireModel extends Model{
	public final ModelRenderer fire;

	public SummonFireModel(){
		this(RenderType::entityCutoutNoCull);
	}
	public SummonFireModel(Function<ResourceLocation, RenderType> function){
		super(function);
		ModelRenderer fire1 = new ModelRenderer(this, 0, 0).setTexSize(16, 8);
		fire1.addBox(-2, (float)-4, -2, 4, 4, 4, 0);
		fire1.setPos(0, 0, 0);
		this.fire = fire1;
	}

	@Override public void renderToBuffer(MatrixStack pose, IVertexBuilder buffer, int packedLight, int packedOverlay, float r, float g, float b, float a){
		fire.render(pose, buffer, packedLight, packedOverlay, r, g, b, a);
	}
}
