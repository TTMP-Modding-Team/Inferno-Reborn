package ttmp.wtf.operation.impl;

import ttmp.wtf.obj.WtfObject;
import ttmp.wtf.operation.WtfConstructor;

import javax.annotation.Nullable;

public class ObjectConstructor implements WtfConstructor{
	@Nullable @Override public Object provideInitialObject(){
		return new WtfObject.Mutable();
	}
	@Nullable @Override public Object finalizeObject(@Nullable Object object){
		//noinspection ConstantConditions
		return ((WtfObject.Mutable)object).object;
	}
}
