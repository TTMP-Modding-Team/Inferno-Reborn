package ttmp.infernoreborn.client.screen;

import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.gui.screen.inventory.ContainerScreen;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.vector.Matrix3f;
import net.minecraft.util.math.vector.Matrix4f;
import net.minecraft.util.text.ITextComponent;
import ttmp.infernoreborn.contents.container.SigilEngravingTableContainer;
import ttmp.infernoreborn.contents.sigil.Sigil;

import java.util.Set;

import static ttmp.infernoreborn.InfernoReborn.MODID;

public abstract class SigilEngravingTableScreen extends ContainerScreen<SigilEngravingTableContainer> implements SigilScreen{
	private SigilWidget sigilWidget;

	public SigilEngravingTableScreen(SigilEngravingTableContainer container, PlayerInventory playerInventory, ITextComponent name){
		super(container, playerInventory, name);
	}

	@Override protected void init(){
		super.init();
		sigilWidget = addButton(new SigilWidget(leftPos+imageWidth+4+2, topPos+2,
				(width-imageWidth)/2-4-20, () -> menu.getMaxPoints(), sigilWidget));
	}

	protected abstract ResourceLocation getImage();
	public SigilWidget getSigilWidget(){
		return sigilWidget;
	}

	@Override public void sync(Set<Sigil> currentSigils, Set<Sigil> newSigils){
		this.sigilWidget.sync(currentSigils, newSigils);
	}

	@Override public void render(MatrixStack stack, int mx, int my, float partialTicks){
		renderBackground(stack);
		super.render(stack, mx, my, partialTicks);
		renderTooltip(stack, mx, my);
	}

	@Override protected void renderBg(MatrixStack stack, float partialTicks, int mx, int my){
		//noinspection deprecation
		RenderSystem.color4f(1, 1, 1, 1);
		//noinspection ConstantConditions
		this.minecraft.getTextureManager().bind(getImage());
		blit(stack, leftPos, topPos, 0, 0, imageWidth, imageHeight);

		ItemStack centerItem = menu.getInventory().getCenterItem();
		if(centerItem.isEmpty())
			blit(stack, leftPos+menu.centerSlotX(), topPos+menu.centerSlotY(), 0, 256-16, 16, 16);
	}

	public static class X3 extends SigilEngravingTableScreen{
		private static final ResourceLocation TEXTURE = new ResourceLocation(MODID, "textures/gui/sigil_engraving_table_3x3.png");

		public X3(SigilEngravingTableContainer container, PlayerInventory playerInventory, ITextComponent name){
			super(container, playerInventory, name);
		}

		@Override protected ResourceLocation getImage(){
			return TEXTURE;
		}

		@Override protected void init(){
			imageWidth = 176;
			imageHeight = 166;
			inventoryLabelY = imageHeight-94;
			super.init();
		}
	}

	public static class X5 extends SigilEngravingTableScreen{
		private static final ResourceLocation TEXTURE = new ResourceLocation(MODID, "textures/gui/sigil_engraving_table_5x5.png");

		public X5(SigilEngravingTableContainer container, PlayerInventory playerInventory, ITextComponent name){
			super(container, playerInventory, name);
		}

		@Override protected ResourceLocation getImage(){
			return TEXTURE;
		}

		@Override protected void init(){
			imageWidth = 176;
			imageHeight = 202;
			inventoryLabelY = imageHeight-94;
			super.init();
		}
	}

	public static class X7 extends SigilEngravingTableScreen{
		private static final ResourceLocation TEXTURE = new ResourceLocation(MODID, "textures/gui/sigil_engraving_table_7x7.png");

		public X7(SigilEngravingTableContainer container, PlayerInventory playerInventory, ITextComponent name){
			super(container, playerInventory, name);
		}

		@Override protected ResourceLocation getImage(){
			return TEXTURE;
		}

		@Override protected void init(){
			imageWidth = 183;
			imageHeight = 220;
			super.init();
		}

		@Override protected void renderLabels(MatrixStack stack, int mx, int my){
			stack.pushPose();
			stack.translate(3, 133, 0);
			rotate90(stack);
			this.font.draw(stack, this.title, 0, 0, 0x404040);
			stack.popPose();
			stack.pushPose();
			stack.translate(3, 213, 0);
			rotate90(stack);
			this.font.draw(stack, this.inventory.getDisplayName(), 0, 0, 0x404040);
			stack.popPose();
		}

		private static void rotate90(MatrixStack stack){
			// this is fucking stupid oh my god
			Matrix3f m = new Matrix3f();
			m.set(0, 1, 1);
			m.set(1, 0, -1);
			m.set(2, 2, 1);
			Matrix4f m2 = new Matrix4f(new float[]{
					0, 1, 0, 0,
					-1, 0, 0, 0,
					0, 0, 1, 0,
					0, 0, 0, 1
			});
			stack.last().pose().multiply(m2);
			stack.last().normal().mul(m);
		}
	}
}
