package ttmp.wtf.obj;

import javax.annotation.Nullable;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;

public final class WtfObject implements WtfPropertyHolder{
	private final Map<String, Object> properties = new HashMap<>();

	@Nullable @Override public Object getProperty(String name){
		return properties.get(name);
	}
	@Override public boolean setProperty(String name, @Nullable Object value){
		return false;
	}

	@Override public String toString(){
		return "{" + properties.entrySet().stream()
				.map(e -> e.getKey()+": "+e.getValue())
				.collect(Collectors.joining(", ")) +"}";
	}

	public static final class Mutable implements WtfPropertyHolder{
		public final WtfObject object = new WtfObject();

		@Nullable @Override public Object getProperty(String name){
			return object.getProperty(name);
		}
		@Override public boolean setProperty(String name, @Nullable Object value){
			object.properties.put(name, value);
			return true;
		}

		@Override public String toString(){
			return object.toString();
		}
	}
}
