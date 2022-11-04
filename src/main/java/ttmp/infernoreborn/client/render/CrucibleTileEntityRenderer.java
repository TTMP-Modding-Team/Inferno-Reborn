package ttmp.infernoreborn.client.render;

import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.vertex.IVertexBuilder;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.IRenderTypeBuffer;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.model.IBakedModel;
import net.minecraft.client.renderer.model.ItemCameraTransforms.TransformType;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.client.renderer.tileentity.TileEntityRenderer;
import net.minecraft.client.renderer.tileentity.TileEntityRendererDispatcher;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Direction;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.vector.Matrix4f;
import net.minecraft.util.math.vector.Vector3f;
import net.minecraft.util.math.vector.Vector4f;
import net.minecraftforge.fluids.FluidAttributes;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.IFluidTank;
import ttmp.infernoreborn.contents.tile.crucible.Crucible;
import ttmp.infernoreborn.contents.tile.crucible.CrucibleTile;

import java.util.Random;

import static net.minecraft.inventory.container.PlayerContainer.BLOCK_ATLAS;

public class CrucibleTileEntityRenderer extends TileEntityRenderer<CrucibleTile>{
	public static final float STIR_ROTATION_INCREMENT = 0.15f;
	private static final float INDEX_DIFFERENCE = 0.5f;

	private final Random random = new Random();

	public CrucibleTileEntityRenderer(TileEntityRendererDispatcher dispatcher){
		super(dispatcher);
	}

	@Override public void render(CrucibleTile crucible, float partialTicks, MatrixStack pose, IRenderTypeBuffer buffer, int combinedLight, int combinedOverlay){
		if(crucible.isAutomated()) return;
		Box box = crucible.isOnCampfire() ?
				new Box(3/16f, 13/16f, 6/16f, 1, 3/16f, 13/16f) :
				new Box(3/16f, 13/16f, 2/16f, 12/16f, 3/16f, 13/16f);

		IFluidTank fluid = crucible.getFluidTank();
		float fluidLevel = fluid.getFluid().isEmpty() ? box.yMin : getFluidLevel(box, fluid);

		for(int i = 0; i<crucible.getInputs().getSlots(); i++){
			ItemStack stack = crucible.getInputs().getStackInSlot(i);
			if(stack.isEmpty()) continue;
			pose.pushPose();
			pose.translate(.5, Math.max(box.yMin+1/16f, fluidLevel-2/16f), .5);
			this.random.setSeed(Item.getId(stack.getItem())+stack.getDamageValue());
			IBakedModel bakedModel = Minecraft.getInstance().getItemRenderer()
					.getModel(stack, crucible.getLevel(), null);
			boolean is3d = bakedModel.isGui3d();
			int renderAmount = getRenderAmount(stack);
			pose.mulPose(Vector3f.YP.rotation(crucible.clientStir+
					Crucible.calculateStirRotationIncrement(crucible.getManualStirPower())*partialTicks+
					INDEX_DIFFERENCE*i));
			if(!is3d) pose.translate(0, 0, -0.09375f*(float)(renderAmount-1)*0.5f);

			for(int j = 0; j<renderAmount; ++j){
				pose.pushPose();
				if(is3d) pose.translate((this.random.nextFloat()*2-1)*0.15f,
						(this.random.nextFloat()*2-1)*0.15f,
						(this.random.nextFloat()*2-1)*0.15f);
				else pose.translate((this.random.nextFloat()*2-1)*0.15f*0.5f,
						(this.random.nextFloat()*2-1)*0.15f*0.5f,
						0);

				Minecraft.getInstance().getItemRenderer()
						.render(stack, TransformType.GROUND, false, pose, buffer, combinedLight,
								OverlayTexture.NO_OVERLAY, bakedModel);
				pose.popPose();
				if(!is3d) pose.translate(0, 0, 0.09375f);
			}
			pose.popPose();
		}

		if(!fluid.getFluid().isEmpty())
			drawFluid(crucible, pose, buffer, box, getFluidLevel(box, fluid), combinedLight, combinedOverlay, fluid.getFluid());
	}

	private static int getRenderAmount(ItemStack s){
		int i = 1;
		if(s.getCount()>48) i = 5;
		else if(s.getCount()>32) i = 4;
		else if(s.getCount()>16) i = 3;
		else if(s.getCount()>1) i = 2;
		return i;
	}

	private static float getFluidLevel(Box box, IFluidTank fluid){
		return getFluidLevel(box.yMin, box.yMax, fluid);
	}
	public static float getFluidLevel(float yMin, float yMax, IFluidTank fluid){
		float minLevel = yMin+0.01f;
		float maxLevel = yMax-.5f/16;
		float percentage = (float)fluid.getFluidAmount()/fluid.getCapacity();
		return (maxLevel-minLevel)*percentage+minLevel;
	}

	private static void drawFluid(CrucibleTile crucible, MatrixStack pose, IRenderTypeBuffer buffer, Box box, float y, int packedLight, int packedOverlay, FluidStack fluid){
		FluidAttributes attr = fluid.getFluid().getAttributes();
		ResourceLocation tex;
		int color;
		//noinspection ConstantConditions
		if(crucible!=null&&crucible.getLevel()!=null){
			tex = attr.getStillTexture(crucible.getLevel(), crucible.getBlockPos());
			color = attr.getColor(crucible.getLevel(), crucible.getBlockPos());
		}else{
			tex = attr.getStillTexture(fluid);
			color = attr.getColor(fluid);
		}

		drawFlatQuad(entityTranslucent(buffer, tex), pose, box, y, color, packedLight, packedOverlay);
	}

	private static void drawFlatQuad(IVertexBuilder vc, MatrixStack pose, Box xz, float y, int color, int packedLight, int packedOverlay){
		float a = (color >> 24)/255f;
		float r = (color >> 16)/255f;
		float g = (color >> 8)/255f;
		float b = (color)/255f;
		drawFlatQuadVertex(vc, pose, xz.xMin, y, xz.zMin, r, g, b, a, packedLight, packedOverlay);
		drawFlatQuadVertex(vc, pose, xz.xMin, y, xz.zMax, r, g, b, a, packedLight, packedOverlay);
		drawFlatQuadVertex(vc, pose, xz.xMax, y, xz.zMax, r, g, b, a, packedLight, packedOverlay);
		drawFlatQuadVertex(vc, pose, xz.xMax, y, xz.zMin, r, g, b, a, packedLight, packedOverlay);
	}

	private static void drawFlatQuadVertex(IVertexBuilder vc, MatrixStack pose, float x, float y, float z, float r, float g, float b, float a, int packedLight, int packedOverlay){
		Matrix4f mat = pose.last().pose();
		Vector4f vec = new Vector4f(x, y, z, 1);
		vec.transform(mat);
		Vector3f norm = new Vector3f(0, 1, 0);
		norm.transform(pose.last().normal());
		vc.vertex(vec.x(), vec.y(), vec.z(), r, g, b, a, x, z, packedOverlay, packedLight, norm.x(), norm.y(), norm.z());
	}

	private static IVertexBuilder entitySolid(IRenderTypeBuffer buffer, ResourceLocation texture){
		return applyTexture(buffer.getBuffer(RenderType.entitySolid(BLOCK_ATLAS)), texture);
	}
	private static IVertexBuilder entityTranslucent(IRenderTypeBuffer buffer, ResourceLocation texture){
		return applyTexture(buffer.getBuffer(RenderType.entityTranslucent(BLOCK_ATLAS)), texture);
	}

	private static IVertexBuilder applyTexture(IVertexBuilder vc, ResourceLocation texture){
		return Minecraft.getInstance().getTextureAtlas(BLOCK_ATLAS)
				.apply(texture)
				.wrap(vc);
	}

	public static class Box{
		public float xMin;
		public float xMax;
		public float yMin;
		public float yMax;
		public float zMin;
		public float zMax;

		public Box(float xMin, float xMax, float yMin, float yMax, float zMin, float zMax){
			this.xMin = xMin;
			this.xMax = xMax;
			this.yMin = yMin;
			this.yMax = yMax;
			this.zMin = zMin;
			this.zMax = zMax;
		}

		public void rotateHorizontally(Direction dir){
			switch(dir){
				case NORTH:{
					float xMin2 = -xMin+1;
					this.xMin = -xMax+1;
					this.xMax = xMin2;
					float zMin2 = -zMin+1;
					this.zMin = -zMax+1;
					this.zMax = zMin2;
					return;
				}
				case WEST:{
					float xMin2 = -xMin+1;
					float xMax2 = -xMax+1;
					this.xMin = zMin;
					this.xMax = zMax;
					this.zMin = xMax2;
					this.zMax = xMin2;
					return;
				}
				case EAST:{
					float zMin2 = -zMin+1;
					float zMax2 = -zMax+1;
					this.zMin = xMin;
					this.zMax = xMax;
					this.xMin = zMax2;
					this.xMax = zMin2;
				}
			}
		}

		public double xCenter(){
			return (xMax-xMin)/2;
		}
		public double zCenter(){
			return (zMax-zMin)/2;
		}
	}
}
