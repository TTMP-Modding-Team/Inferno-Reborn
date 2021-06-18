package ttmp.infernoreborn.client;

import it.unimi.dsi.fastutil.ints.Int2ObjectArrayMap;
import it.unimi.dsi.fastutil.ints.Int2ObjectMap;
import it.unimi.dsi.fastutil.longs.Long2ObjectArrayMap;
import it.unimi.dsi.fastutil.longs.Long2ObjectMap;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.Color;
import net.minecraft.util.text.StringTextComponent;
import net.minecraft.util.text.Style;
import net.minecraftforge.registries.ForgeRegistryEntry;
import ttmp.infernoreborn.contents.sigil.Sigil;
import ttmp.infernoreborn.contents.sigil.holder.SigilHolder;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;
import java.util.Random;

public class GibberishFactory{
	private static final String ABCDEFGHIJKLMNOPQRSTUVWXYZ = "abcdefghijklmnopqrstuvwxyz";
	private static final ResourceLocation ALT_FONT = new ResourceLocation("minecraft", "alt");
	private static final Style ROOT_STYLE = Style.EMPTY.withFont(ALT_FONT);
	private static final long PHASE = 1000;
	private static final long PHASE2 = 500;
	private static final int EMPTY_COLOR = 0x666666;
	private static final Color EMPTY_COLOR_INSTANCE = Color.fromRgb(EMPTY_COLOR);

	private final Long2ObjectMap<Int2ObjectMap<String>> gibberishCache = new Long2ObjectArrayMap<>();

	public StringTextComponent toText(SigilHolder h, @Nullable SigilHolder h2, int length){
		String gib = getOrCreateGibberish(h, length);

		StringTextComponent text = new StringTextComponent("");
		text.withStyle(ROOT_STYLE);
		List<Sigil> l = new ArrayList<>(h.getSigils());
		l.sort(Comparator.nullsLast(Comparator.comparing(ForgeRegistryEntry::getRegistryName)));

		long t = System.currentTimeMillis();
		double blend = getBlend(t, PHASE)*.5;

		int point = 0;
		for(int i = 0; i<l.size()&&point<gib.length(); i++){
			Sigil sigil = l.get(i);
			if(sigil.getPoint()<=0) continue;
			StringTextComponent subtext = new StringTextComponent(gib.substring(point, point = crunch(gib, point, sigil.getPoint())));
			subtext.setStyle(subtext.getStyle().withColor(Color.fromRgb(ItemColorUtils.blend(sigil.getBrighterColor(), sigil.getDarkerColor(), blend))));
			text.append(subtext);
		}
		if(h2!=null){
			double blend2 = getBlend(t, PHASE2);
			for(Iterator<Sigil> it = h2.getSigils().iterator(); it.hasNext()&&point<gib.length(); ){
				Sigil sigil = it.next();
				if(h.has(sigil)) continue;
				StringTextComponent subtext = new StringTextComponent(gib.substring(point, point = crunch(gib, point, sigil.getPoint())));
				subtext.setStyle(subtext.getStyle().withColor(Color.fromRgb(ItemColorUtils.blend(sigil.getBrighterColor(), sigil.getDarkerColor(), blend2))));
				text.append(subtext);
			}
		}
		if(point<gib.length()){
			StringTextComponent subtext = new StringTextComponent(gib.substring(point));
			subtext.setStyle(subtext.getStyle().withColor(EMPTY_COLOR_INSTANCE));
			text.append(subtext);
		}
		return text;
	}

	private String getOrCreateGibberish(SigilHolder h, int length){
		String cache = getGibberishCache(h, length);
		if(cache!=null) return cache;

		Random random = new Random(h.getGibberishSeed());
		StringBuilder stb = new StringBuilder();
		boolean first = true;
		for(int totalSyllable = 0; totalSyllable<length; ){
			if(first) first = false;
			else stb.append(' ');
			int syllable = Math.min(random.nextInt(3)+random.nextInt(3)+random.nextInt(3)+1, length-totalSyllable);
			for(int i = 0; i<syllable; i++)
				stb.append(ABCDEFGHIJKLMNOPQRSTUVWXYZ.charAt(random.nextInt(ABCDEFGHIJKLMNOPQRSTUVWXYZ.length())));
			totalSyllable += syllable;
		}
		String gib = stb.toString();
		setGibberishCache(h, length, gib);
		return gib;
	}

	@Nullable private String getGibberishCache(SigilHolder h, int length){
		Int2ObjectMap<String> m = gibberishCache.get(h.getGibberishSeed());
		if(m==null) return null;
		return m.get(length);
	}

	private void setGibberishCache(SigilHolder h, int length, String cache){
		long seed = h.getGibberishSeed();
		Int2ObjectMap<String> m = gibberishCache.get(seed);
		if(m==null) gibberishCache.put(seed, m = new Int2ObjectArrayMap<>());
		m.put(length, cache);
	}

	/**
	 * munch
	 */
	private static int crunch(String gib, int start, int length){
		for(int i = start; i<gib.length(); i++){
			if(gib.charAt(i)!=' '&&--length<=0) return i+1;
		}
		return gib.length();
	}

	private static double getBlend(long t, long phase){
		return t/phase%2!=0 ? (phase-t%phase-1)/(double)phase : t%phase/(double)phase;
	}
}
