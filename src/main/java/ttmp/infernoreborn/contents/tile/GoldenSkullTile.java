package ttmp.infernoreborn.contents.tile;

import net.minecraft.block.BlockState;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.INameable;
import net.minecraft.util.text.ITextComponent;
import net.minecraftforge.common.util.Constants;
import ttmp.infernoreborn.contents.ModTileEntities;

import javax.annotation.Nullable;

public class GoldenSkullTile extends TileEntity implements INameable{
	@Nullable private ITextComponent customName;

	public GoldenSkullTile(){
		super(ModTileEntities.GOLDEN_SKULL.get());
	}

	@Override public ITextComponent getName(){
		return customName!=null ? customName : getDisplayName();
	}
	@Nullable @Override public ITextComponent getCustomName(){
		return customName!=null ? customName : null;
	}

	public void setCustomName(@Nullable ITextComponent customName){
		this.customName = customName;
	}

	@Override public CompoundNBT save(CompoundNBT tag){
		super.save(tag);
		if(customName!=null) tag.putString("Name", ITextComponent.Serializer.toJson(customName));
		return tag;
	}
	@Override public void load(BlockState state, CompoundNBT tag){
		super.load(state, tag);
		this.customName = tag.contains("Name", Constants.NBT.TAG_STRING) ? ITextComponent.Serializer.fromJson(tag.getString("Name")) : null;
	}
}
