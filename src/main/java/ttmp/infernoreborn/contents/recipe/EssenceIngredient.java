package ttmp.infernoreborn.contents.recipe;

import com.google.gson.JsonObject;
import net.minecraft.network.PacketBuffer;
import net.minecraft.util.JSONUtils;
import ttmp.infernoreborn.util.Essence;
import ttmp.infernoreborn.util.EssenceType;
import ttmp.infernoreborn.util.Essences;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.List;

public final class EssenceIngredient{
	private static final EssenceIngredient NOTHING = builder().build();

	public static EssenceIngredient nothing(){
		return NOTHING;
	}
	public static Builder builder(){
		return new Builder();
	}
	public static EssenceIngredient require(Essences essences){
		return builder().add(essences).build();
	}

	public static EssenceIngredient read(JsonObject object){
		Builder b = builder();
		for(EssenceType t : EssenceType.values())
			if(object.has(t.id))
				b.add(t, JSONUtils.getAsInt(object, t.id));
		b.any(JSONUtils.getAsInt(object, "any", 0));
		return b.build();
	}
	public static EssenceIngredient read(PacketBuffer buffer){
		Builder b = builder();
		for(int i = buffer.readVarInt(); i>0; i--) b.add(Essence.read(buffer));
		return b.any(buffer.readVarInt()).build();
	}

	private final int[] consumptions;
	private final int anyEssenceConsumption;

	private final long totalEssenceConsumption;

	private EssenceIngredient(int[] consumptions, int anyEssenceConsumption){
		this.consumptions = consumptions;
		this.anyEssenceConsumption = anyEssenceConsumption;
		long totalEssenceConsumption = anyEssenceConsumption;
		for(int c : consumptions) totalEssenceConsumption += c;
		this.totalEssenceConsumption = totalEssenceConsumption;
	}

	public int getEssenceConsumptionFor(EssenceType type){
		return consumptions[type.ordinal()];
	}
	public int getAnyEssenceConsumption(){
		return anyEssenceConsumption;
	}
	public long getTotalEssenceConsumption(){
		return totalEssenceConsumption;
	}

	public boolean isEmpty(){
		return totalEssenceConsumption==0;
	}

	public JsonObject toJsonObject(){
		JsonObject object = new JsonObject();
		for(int i = 0; i<consumptions.length; i++)
			if(consumptions[i]>0)
				object.addProperty(EssenceType.values()[i].id, consumptions[i]);
		if(anyEssenceConsumption>0) object.addProperty("any", anyEssenceConsumption);
		return object;
	}

	public void write(PacketBuffer buffer){
		Essence[] essences = essences();
		buffer.writeVarInt(essences.length);
		for(Essence essence : essences) essence.write(buffer);
		buffer.writeVarInt(anyEssenceConsumption);
	}

	@Nullable private Essence[] essences;
	private Essence[] essences(){
		if(essences==null){
			List<Essence> essenceList = new ArrayList<>();
			for(int i = 0; i<consumptions.length; i++)
				if(consumptions[i]>0)
					essenceList.add(new Essence(EssenceType.values()[i], consumptions[i]));
			this.essences = essenceList.toArray(new Essence[0]);
		}
		return essences;
	}

	public static final class Builder{
		private boolean empty;
		private final int[] consumptions = new int[EssenceType.values().length];
		private int anyEssenceConsumption;

		public Builder add(Essences essences){
			for(EssenceType t : EssenceType.values())
				consumptions[t.ordinal()] = essences.getEssence(t);
			return this;
		}

		public Builder add(Essence essence){
			return add(essence.getType(), essence.getAmount());
		}
		public Builder add(EssenceType type, int essence){
			if(essence>0){
				consumptions[type.ordinal()] += essence;
				empty = false;
			}
			return this;
		}

		public Builder any(int essence){
			if(essence>0){
				anyEssenceConsumption += essence;
				empty = false;
			}
			return this;
		}

		public EssenceIngredient build(){
			return empty ? nothing() : new EssenceIngredient(consumptions, this.anyEssenceConsumption);
		}
	}
}
