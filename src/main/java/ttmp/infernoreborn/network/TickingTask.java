package ttmp.infernoreborn.network;

import javax.annotation.Nullable;

public class TickingTask{
	protected int ticks;

	protected TickingTask(int ticks){
		this.ticks = ticks;
	}

	public void onFinish(){}
	public void onTick(){
		--ticks;
	}

	public boolean isExpired(){
		return ticks<=0;
	}

	public static final class Simple extends TickingTask{
		@Nullable private final Runnable onFinish;
		@Nullable private final Runnable onTick;

		public Simple(int ticks, @Nullable Runnable onFinish, @Nullable Runnable onTick){
			super(ticks);
			this.onFinish = onFinish;
			this.onTick = onTick;
		}

		@Override public void onFinish(){
			if(onFinish!=null) onFinish.run();
		}
		@Override public void onTick(){
			super.onTick();
			if(onTick!=null) onTick.run();
		}
	}
}
