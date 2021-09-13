package ttmp.wtf.obj;

import java.util.Objects;

public final class RGB{
	public static final RGB WHITE = new RGB(0xFFFFFF);
	public static final RGB BLACK = new RGB(0);

	private final int rgb;

	public RGB(int rgb){
		this.rgb = rgb;
	}
	public RGB(int red, int blue, int green){
		this((red&0xFF)<<16|(blue&0xFF)<<8|green&0xFF);
	}

	public int getRgb(){
		return rgb;
	}

	public byte getRed(){
		return (byte)(rgb >> 16);
	}

	public byte getGreen(){
		return (byte)(rgb >> 8);
	}

	public byte getBlue(){
		return (byte)rgb;
	}

	@Override public boolean equals(Object o){
		if(this==o) return true;
		if(o==null||getClass()!=o.getClass()) return false;
		RGB rgb1 = (RGB)o;
		return rgb==rgb1.rgb;
	}

	@Override public int hashCode(){
		return Objects.hash(rgb);
	}

	@Override public String toString(){
		return String.format("#%6X", rgb);
	}
}
