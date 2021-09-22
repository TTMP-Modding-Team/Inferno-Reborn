package ttmp.infernoreborn.client;

import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.Color;
import net.minecraft.util.text.StringTextComponent;
import net.minecraft.util.text.Style;
import ttmp.infernoreborn.client.color.ColorUtils;
import ttmp.infernoreborn.contents.sigil.Sigil;
import ttmp.infernoreborn.contents.sigil.holder.SigilHolder;

import javax.annotation.Nullable;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;

public class GibberishFactory{
	private static final String ABCDEFGHIJKLMNOPQRSTUVWXYZ = "abcdefghijklmnopqrstuvwxyz";
	private static final ResourceLocation ALT_FONT = new ResourceLocation("minecraft", "alt");
	private static final Style ROOT_STYLE = Style.EMPTY.withFont(ALT_FONT);
	private static final long PHASE = 1000;
	private static final long PHASE2 = 500;

	private final Map<Sigil, String> gibberish = new HashMap<>();

	public StringTextComponent toText(SigilHolder h, @Nullable SigilHolder h2){
		StringTextComponent text = new StringTextComponent("");
		text.withStyle(ROOT_STYLE);
		boolean first = true;
		long t = System.currentTimeMillis();
		double blend = getBlend(t, PHASE)*.5;

		for(Sigil sigil : h.getSigils()){
			if(sigil.getPoint()<=0) continue;
			if(first) first = false;
			else text.append(" ");
			StringTextComponent subtext = new StringTextComponent(getOrCreateGibberish(sigil));
			subtext.setStyle(subtext.getStyle().withColor(Color.fromRgb(ColorUtils.blend(sigil.getBrighterColor(), sigil.getDarkerColor(), blend))));
			text.append(subtext);
		}
		if(h2!=null){
			double blend2 = getBlend(t, PHASE2);
			for(Sigil sigil : h2.getSigils()){
				if(sigil.getPoint()<=0||h.has(sigil)) continue;
				if(first) first = false;
				else text.append(" ");
				StringTextComponent subtext = new StringTextComponent(getOrCreateGibberish(sigil));
				subtext.setStyle(subtext.getStyle().withColor(Color.fromRgb(ColorUtils.blend(sigil.getBrighterColor(), sigil.getDarkerColor(), blend2))));
				text.append(subtext);
			}
		}

		return text;
	}

	private String getOrCreateGibberish(Sigil sigil){
		return gibberish.computeIfAbsent(sigil, GibberishFactory::createGibberish);
	}

	private static String createGibberish(Sigil sigil){
		ResourceLocation n = sigil.getRegistryName();
		if(n==null) return "";
		Random r = new Random(((long)n.getNamespace().hashCode())<<32|n.getPath().hashCode());
		StringBuilder stb = new StringBuilder();
		for(int i = 0; i<sigil.getPoint(); i++)
			stb.append(ABCDEFGHIJKLMNOPQRSTUVWXYZ.charAt(r.nextInt(ABCDEFGHIJKLMNOPQRSTUVWXYZ.length())));
		return stb.toString();
	}

	private static double getBlend(long t, long phase){
		return t/phase%2!=0 ? (phase-t%phase-1)/(double)phase : t%phase/(double)phase;
	}
}
