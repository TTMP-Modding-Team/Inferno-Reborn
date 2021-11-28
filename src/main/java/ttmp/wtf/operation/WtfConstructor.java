package ttmp.wtf.operation;

import javax.annotation.Nullable;

public interface WtfConstructor{
	@Nullable Object provideInitialObject();
	@Nullable Object finalizeObject(@Nullable Object object);
}
