package ttmp.wtf.operation.impl;

import ttmp.wtf.obj.WtfApplicable;
import ttmp.wtf.operation.WtfConstructor;

import javax.annotation.Nullable;
import java.util.HashSet;
import java.util.Set;

public class SetConstructor implements WtfConstructor{
	@Override public Object provideInitialObject(){
		return new MutableSetObj(new HashSet<>());
	}
	@Override public Object finalizeObject(@Nullable Object object){
		//noinspection ConstantConditions
		return ((MutableSetObj)object).set;
	}

	public static final class MutableSetObj implements WtfApplicable{
		public final Set<Object> set;

		public MutableSetObj(Set<Object> set){
			this.set = set;
		}

		@Override public void apply(@Nullable Object v){
			this.set.add(v);
		}
	}
}
