package ttmp.infernoreborn.client;

import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.Color;
import net.minecraft.util.text.IFormattableTextComponent;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.StringTextComponent;
import net.minecraft.util.text.Style;
import ttmp.infernoreborn.client.color.ColorUtils;
import ttmp.infernoreborn.contents.sigil.Sigil;

import java.util.Collection;
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

	public ITextComponent toText(Collection<Sigil> currentSigils, Collection<Sigil> newSigils){
		IFormattableTextComponent text = new StringTextComponent("")
				.withStyle(ROOT_STYLE);
		boolean first = true;
		long t = System.currentTimeMillis();

		if(!currentSigils.isEmpty()){
			double blend = getBlend(t, PHASE)*.5;
			for(Sigil sigil : currentSigils){
				if(sigil.getPoint()<=0) continue;
				if(first) first = false;
				else text.append(" ");
				StringTextComponent subtext = new StringTextComponent(getOrCreateGibberish(sigil));
				subtext.setStyle(subtext.getStyle().withColor(Color.fromRgb(ColorUtils.blend(sigil.getBrighterColor(), sigil.getDarkerColor(), blend))));
				text.append(subtext);
			}
		}
		if(!newSigils.isEmpty()){
			double blend2 = getBlend(t, PHASE2);
			for(Sigil sigil : newSigils){
				if(sigil.getPoint()<=0||currentSigils.contains(sigil)) continue;
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
