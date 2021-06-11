package ttmp.infernoreborn.client;

import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.gui.screen.inventory.ContainerScreen;
import net.minecraft.client.util.InputMappings;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.container.Slot;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TextFormatting;
import ttmp.infernoreborn.container.EssenceHolderContainer;
import ttmp.infernoreborn.container.EssenceHolderItemHandler;
import ttmp.infernoreborn.network.EssenceHolderSlotClickMsg;
import ttmp.infernoreborn.network.ModNet;

import javax.annotation.Nullable;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;

import static ttmp.infernoreborn.InfernoReborn.MODID;

public class EssenceHolderScreen extends ContainerScreen<EssenceHolderContainer>{
	private static final ResourceLocation GUI = new ResourceLocation(MODID, "textures/gui/essence_holder.png");

	private final List<EssenceSlot> essenceSlots = new ArrayList<>();

	public EssenceHolderScreen(EssenceHolderContainer container, PlayerInventory playerInventory, ITextComponent name){
		super(container, playerInventory, name);
		for(int i = 0, until = container.getEssenceHolder().getSlots(); i<until; i++){
			essenceSlots.add(new EssenceSlot(container.getEssenceHolder(), i, 8+(i/3)*18, 18+(i%3)*18));
		}
	}

	@Override public void render(MatrixStack matrixStack, int mouseX, int mouseY, float partialTicks){
		this.renderBackground(matrixStack);
		super.render(matrixStack, mouseX, mouseY, partialTicks);
		this.renderTooltip(matrixStack, mouseX, mouseY);
	}

	@Override protected void renderBg(MatrixStack matrixStack, float partialTicks, int mouseX, int mouseY){
		//noinspection deprecation
		RenderSystem.color4f(1, 1, 1, 1);
		//noinspection ConstantConditions
		this.minecraft.getTextureManager().bind(GUI);
		blit(matrixStack, leftPos, topPos, 0, 0, imageWidth, imageHeight);

		Slot lockedSlot = getLockedSlot();
		if(lockedSlot!=null){
			blit(matrixStack, leftPos+lockedSlot.x-1, topPos+lockedSlot.y-1, 256, 256-18, 18, 18);
		}
	}

	@Override protected void renderLabels(MatrixStack matrixStack, int mouseX, int mouseY){
		for(EssenceSlot slot : essenceSlots){
			//noinspection ConstantConditions
			this.itemRenderer.renderAndDecorateItem(this.minecraft.player, slot.getItemStack(), slot.x, slot.y);
			this.itemRenderer.renderGuiItemDecorations(this.font, slot.getItemStack(), slot.x, slot.y, slot.formatCount());

			if(this.isHovering(slot.x, slot.y, 16, 16, mouseX, mouseY)){
				RenderSystem.disableDepthTest();
				RenderSystem.colorMask(true, true, true, false);
				int slotColor = this.getSlotColor(this.slotColor);
				this.fillGradient(matrixStack, slot.x, slot.y, slot.x+16, slot.y+16, slotColor, slotColor);
				RenderSystem.colorMask(true, true, true, true);
				RenderSystem.enableDepthTest();
			}
		}

		super.renderLabels(matrixStack, mouseX, mouseY);
	}

	@Override protected void renderTooltip(MatrixStack matrixStack, int mouseX, int mouseY){
		super.renderTooltip(matrixStack, mouseX, mouseY);
		for(EssenceSlot slot : essenceSlots){
			//noinspection ConstantConditions
			if(this.minecraft.player.inventory.getCarried().isEmpty()&&isHovering(slot.x, slot.y, 16, 16, mouseX, mouseY)){
				this.renderTooltip(matrixStack, slot.getItemStack(), mouseX, mouseY);
			}
		}
	}

	@Override public boolean mouseClicked(double x, double y, int type){
		for(EssenceSlot slot : essenceSlots){
			if(isHovering(slot.x, slot.y, 16, 16, x, y)){
				boolean shift = hasShiftDown();
				menu.handleEssenceHolderSlotClick(slot.slot, type, shift);
				ModNet.CHANNEL.sendToServer(new EssenceHolderSlotClickMsg(slot.slot, type, shift));
				return true;
			}
		}
		return super.mouseClicked(x, y, type);
	}

	@Override protected boolean checkHotbarKeyPressed(int key, int scancode){
		//noinspection ConstantConditions
		if(minecraft.player.inventory.getCarried().isEmpty()&&this.hoveredSlot!=null){
			if(minecraft.options.keySwapOffhand.isActiveAndMatches(InputMappings.getKey(key, scancode)))
				return menu.getHolderSlot()!=40&&super.checkHotbarKeyPressed(key, scancode);

			for(int i = 0; i<9; ++i){
				if(minecraft.options.keyHotbarSlots[i].isActiveAndMatches(InputMappings.getKey(key, scancode)))
					return menu.getHolderSlot()!=i&&super.checkHotbarKeyPressed(key, scancode);
			}
		}
		return super.checkHotbarKeyPressed(key, scancode);
	}

	@Nullable private Slot getLockedSlot(){
		int lockedSlot = menu.getHolderSlot();
		if(lockedSlot<0) return null;
		for(Slot slot : menu.slots)
			if(slot.isActive()&&slot.getSlotIndex()==lockedSlot) return slot;
		return null;
	}

	private static final class EssenceSlot{
		private static final DecimalFormat F = new DecimalFormat("0.#");

		private final EssenceHolderItemHandler essenceHolder;
		private final int slot;
		private final int x;
		private final int y;

		private EssenceSlot(EssenceHolderItemHandler essenceHolder, int slot, int x, int y){
			this.essenceHolder = essenceHolder;
			this.slot = slot;
			this.x = x;
			this.y = y;
		}

		private ItemStack stack;

		public ItemStack getItemStack(){
			if(stack==null) stack = new ItemStack(essenceHolder.item(slot));
			return stack;
		}
		public int getCount(){
			return essenceHolder.getEssenceHolder().getEssence(essenceHolder.type(slot))/essenceHolder.size(slot).getCompressionRate();
		}
		public String formatCount(){
			int count = getCount();
			if(count<=0) return TextFormatting.RED+"0";
			if(count<1_000) return String.valueOf(count);
			if(count<1_000_000) return F.format(count/1_000.0)+"K";
			if(count<1_000_000_000) return F.format(count/1_000_000.0)+"M";
			return F.format(count/1_000_000_000.0)+"G";
		}
	}
}
