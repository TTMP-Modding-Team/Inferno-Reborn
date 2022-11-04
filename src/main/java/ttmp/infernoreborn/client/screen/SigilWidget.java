package ttmp.infernoreborn.client.screen;

import com.mojang.blaze3d.matrix.MatrixStack;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.AbstractGui;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.gui.widget.Widget;
import net.minecraft.util.IReorderingProcessor;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.StringTextComponent;
import ttmp.infernoreborn.api.sigil.Sigil;
import ttmp.infernoreborn.client.GibberishFactory;

import javax.annotation.Nullable;
import java.util.Collections;
import java.util.Set;
import java.util.function.IntSupplier;

import static net.minecraft.util.text.TextFormatting.GOLD;

public final class SigilWidget extends Widget{
	private final int maxWordWidth;
	private final IntSupplier maxPoints;
	private final GibberishFactory gibFactory = new GibberishFactory();

	private Set<Sigil> currentSigils = Collections.emptySet();
	private Set<Sigil> newSigils = Collections.emptySet();

	public SigilWidget(int x, int y, int maxWordWidth, IntSupplier maxPoints, @Nullable SigilWidget previousWidget){
		super(x, y, 0, 0, StringTextComponent.EMPTY);
		this.maxWordWidth = maxWordWidth;
		this.maxPoints = maxPoints;
		if(previousWidget!=null){
			this.currentSigils = previousWidget.currentSigils;
			this.newSigils = previousWidget.newSigils;
		}
	}

	public void sync(Set<Sigil> currentSigils, Set<Sigil> newSigils){
		this.currentSigils = currentSigils;
		this.newSigils = newSigils;
	}

	@Override public void renderButton(MatrixStack stack, int mouseX, int mouseY, float partialTicks){
		int maxPoints = this.maxPoints.getAsInt();
		if(maxPoints<=0) return;

		// TODO border?
		FontRenderer font = Minecraft.getInstance().font;
		ITextComponent str = str(maxPoints);
		AbstractGui.drawString(stack, font, str, x, y, 0xFFFFFF);
		ITextComponent gib = gibFactory.toText(currentSigils, newSigils);

		int lines = 1;
		for(IReorderingProcessor p : font.split(gib, maxWordWidth)){
			font.draw(stack, p, x, y+font.lineHeight*lines, 0xFFFFFF);
			lines++;
		}
		this.width = Math.max(font.width(str), font.width(gib));
		this.height = font.lineHeight*(lines+1);
	}

	private ITextComponent str(int maxPoints){
		if(newSigils.isEmpty()) return new StringTextComponent(totalPoint(currentSigils)+" / "+maxPoints);
		int newSigilTotal = totalPoint(newSigils);
		return new StringTextComponent("")
				.append(new StringTextComponent((totalPoint(currentSigils)+newSigilTotal)+"").withStyle(GOLD))
				.append(" / "+maxPoints)
				.append(new StringTextComponent(" (+"+newSigilTotal+')').withStyle(GOLD));
	}

	private static int totalPoint(Set<Sigil> sigils){
		return sigils.stream().mapToInt(s -> s.getPoint()).sum();
	}
}
