package ttmp.infernoreborn.network;

import net.minecraft.network.PacketBuffer;

public final class AbilityColorPickerMsg{
	public static AbilityColorPickerMsg read(PacketBuffer buf){
		return new AbilityColorPickerMsg(buf.readVarInt(), buf.readInt(), buf.readInt(), buf.readInt());
	}

	private final int inventoryIndex, primaryColor, secondaryColor, highlightColor;

	public AbilityColorPickerMsg(int inventoryIndex, int primaryColor, int secondaryColor, int highlightColor){
		this.inventoryIndex = inventoryIndex;
		this.primaryColor = primaryColor;
		this.secondaryColor = secondaryColor;
		this.highlightColor = highlightColor;
	}

	public int getInventoryIndex(){
		return inventoryIndex;
	}
	public int getPrimaryColor(){
		return primaryColor;
	}
	public int getSecondaryColor(){
		return secondaryColor;
	}
	public int getHighlightColor(){
		return highlightColor;
	}

	public void write(PacketBuffer buf){
		buf.writeVarInt(inventoryIndex);
		buf.writeInt(primaryColor);
		buf.writeInt(secondaryColor);
		buf.writeInt(highlightColor);
	}
}
