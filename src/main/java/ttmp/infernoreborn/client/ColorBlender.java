package ttmp.infernoreborn.client;

public abstract class ColorBlender{
	private final long expirationTime;

	private int currentColor;
	private long currentColorCreatedTime;
	private int previousColor;

	private boolean firstTick = true;

	public ColorBlender(long expirationTime){
		if(expirationTime<=0) throw new IllegalArgumentException("expirationTime");
		this.expirationTime = expirationTime;
	}

	protected abstract int createNewColor();

	public int nextColor(){
		if(firstTick){
			firstTick = false;
			currentColor = createNewColor();
			previousColor = createNewColor();
			return previousColor;
		}

		long t = System.currentTimeMillis();
		long timePassed = t-currentColorCreatedTime;
		if(timePassed>=expirationTime){
			previousColor = currentColor;
			currentColor = createNewColor();
			currentColorCreatedTime = t;
			return previousColor;
		}

		return ItemColorUtils.blend(previousColor, currentColor, (double)timePassed/expirationTime);
	}
}
