package ttmp.wtf.internal;

import it.unimi.dsi.fastutil.objects.Object2ByteMap;
import it.unimi.dsi.fastutil.objects.Object2ByteOpenHashMap;
import ttmp.wtf.exceptions.WtfException;

public class WtfConstantPool{
	private final Object[] objects = new Object[255];
	private final String[] identifiers = new String[255];

	private final Object2ByteMap<Object> objectsMap = new Object2ByteOpenHashMap<>();
	private final Object2ByteMap<String> identifiersMap = new Object2ByteOpenHashMap<>();

	public WtfConstantPool(){
		objectsMap.defaultReturnValue((byte)-1);
		identifiersMap.defaultReturnValue((byte)-1);
	}

	public int getObjectSize(){
		return objectsMap.size();
	}
	public Object getObject(int i){
		if(i>=getObjectSize()) throw new IndexOutOfBoundsException(""+i);
		return objects[i];
	}

	public int getIdentifierSize(){
		return identifiersMap.size();
	}
	public String getIdentifier(int i){
		if(i>=getIdentifierSize()) throw new IndexOutOfBoundsException(""+i);
		return identifiers[i];
	}

	public byte mapObject(Object o){
		int size = objectsMap.size();
		byte prevIndex = objectsMap.putIfAbsent(o, (byte)size);
		if(objectsMap.size()==size) return prevIndex;
		if(size>255) throw new WtfException("Too many objects");
		objects[size] = o;
		return (byte)size;
	}

	public byte mapIdentifier(String identifier){
		int size = identifiersMap.size();
		byte prevIndex = identifiersMap.putIfAbsent(identifier, (byte)size);
		if(identifiersMap.size()==size) return prevIndex;
		if(size>255) throw new WtfException("Too many identifiers");
		identifiers[size] = identifier;
		return (byte)size;
	}
}
