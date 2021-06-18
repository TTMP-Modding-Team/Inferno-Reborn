package ttmp.infernoreborn.client;

import net.minecraft.util.math.MathHelper;
import ttmp.infernoreborn.contents.ability.Ability;

public final class ItemColorUtils{
	private ItemColorUtils(){}

	public static final int NULL_PRIMARY_COLOR = 0x3a3a3a;
	public static final int NULL_SECONDARY_COLOR = 0xff00ff;
	public static final int NULL_HIGHLIGHT_COLOR = NULL_PRIMARY_COLOR;

	private static final long BLEND_TIME = 2000;

	public static int getPrimaryColorBlend(Ability[] abilities){
		switch(abilities.length){
			case 0:
				return NULL_PRIMARY_COLOR;
			case 1:
				return abilities[0].getPrimaryColor();
			default:{
				long t = System.currentTimeMillis();
				int blendIndex = (int)(t/BLEND_TIME%abilities.length);
				double blend = t%BLEND_TIME/(double)BLEND_TIME;
				return blend(abilities[blendIndex].getPrimaryColor(), abilities[(blendIndex+1)%abilities.length].getPrimaryColor(), blend);
			}
		}
	}

	public static int getSecondaryColorBlend(Ability[] abilities){
		switch(abilities.length){
			case 0:
				return NULL_SECONDARY_COLOR;
			case 1:
				return abilities[0].getSecondaryColor();
			default:{
				long t = System.currentTimeMillis();
				int blendIndex = (int)(t/BLEND_TIME%abilities.length);
				double blend = t%BLEND_TIME/(double)BLEND_TIME;
				return blend(abilities[blendIndex].getSecondaryColor(), abilities[(blendIndex+1)%abilities.length].getSecondaryColor(), blend);
			}
		}
	}

	public static int getHighlightColorBlend(Ability[] abilities){
		switch(abilities.length){
			case 0:
				return NULL_HIGHLIGHT_COLOR;
			case 1:
				return abilities[0].getHighlightColor();
			default:{
				long t = System.currentTimeMillis();
				int blendIndex = (int)(t/BLEND_TIME%abilities.length);
				double blend = t%BLEND_TIME/(double)BLEND_TIME;
				return blend(abilities[blendIndex].getHighlightColor(), abilities[(blendIndex+1)%abilities.length].getHighlightColor(), blend);
			}
		}
	}

	/**
	 * It's just lerp of two RGB vectors
	 */
	public static int blend(int c1, int c2, double rate){
		int r1 = c1 >> 16&0xFF;
		int g1 = c1 >> 8&0xFF;
		int b1 = c1&0xFF;
		int r2 = c2 >> 16&0xFF;
		int g2 = c2 >> 8&0xFF;
		int b2 = c2&0xFF;

		int r = MathHelper.clamp(r1+(int)(rate*(r2-r1)), 0, 0xFF);
		int g = MathHelper.clamp(g1+(int)(rate*(g2-g1)), 0, 0xFF);
		int b = MathHelper.clamp(b1+(int)(rate*(b2-b1)), 0, 0xFF);
		return r<<16|g<<8|b;
	}

	public static int hslToRgb(double hue, double saturation, double lightness){
		double chroma = (1-Math.abs(lightness*2-1))*saturation;
		double m = lightness-chroma/2;

		double hue2 = hue/60.0;
		if(hue2<0||hue2>=6) return rgb(m, m, m);
		double x = chroma*(1-Math.abs(hue2%2-1));
		if(hue2<1) return rgb(chroma+m, x+m, m);
		if(hue2<2) return rgb(x+m, chroma+m, m);
		if(hue2<3) return rgb(m, chroma+m, x+m);
		if(hue2<4) return rgb(m, x+m, chroma+m);
		if(hue2<5) return rgb(x+m, m, chroma+m);
		return rgb(chroma+m, m, x+m);
	}

	public static int rgb(double red, double green, double blue){
		int r = MathHelper.clamp((int)Math.round(red*255), 0, 0xFF);
		int g = MathHelper.clamp((int)Math.round(green*255), 0, 0xFF);
		int b = MathHelper.clamp((int)Math.round(blue*255), 0, 0xFF);
		return r<<16|g<<8|b;
	}
}
