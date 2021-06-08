package ttmp.infernoreborn.network;

import net.minecraft.client.Minecraft;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.entity.Entity;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.network.NetworkEvent;
import net.minecraftforge.fml.network.NetworkRegistry;
import net.minecraftforge.fml.network.simple.SimpleChannel;
import ttmp.infernoreborn.ability.Ability;
import ttmp.infernoreborn.ability.generator.AbilityGenerators;
import ttmp.infernoreborn.capability.AbilityHolder;

import java.util.function.Supplier;

import static ttmp.infernoreborn.InfernoReborn.MODID;

public final class ModNet{
	private ModNet(){}

	public static final String NETVERSION = "1.0";
	public static final SimpleChannel CHANNEL = NetworkRegistry.newSimpleChannel(new ResourceLocation(MODID, "master"),
			() -> NETVERSION,
			NETVERSION::equals,
			NETVERSION::equals);

	public static void init(){
		CHANNEL.registerMessage(0, SyncAbilityGeneratorMsg.class,
				SyncAbilityGeneratorMsg::write, SyncAbilityGeneratorMsg::read,
				Client::handleSyncAbilityGeneratorList);
		CHANNEL.registerMessage(1, SyncAbilityHolderMsg.class,
				SyncAbilityHolderMsg::write, SyncAbilityHolderMsg::read,
				Client::handleSyncAbilityHolderMsg);
	}

	private static final class Client{
		private Client(){}

		public static void handleSyncAbilityGeneratorList(SyncAbilityGeneratorMsg msg, Supplier<NetworkEvent.Context> ctx){
			ctx.get().setPacketHandled(true);
			ctx.get().enqueueWork(() -> AbilityGenerators.setGeneratorIDs(msg.getAbilityGenerators()));
		}

		public static void handleSyncAbilityHolderMsg(SyncAbilityHolderMsg msg, Supplier<NetworkEvent.Context> ctx){
			ctx.get().setPacketHandled(true);
			ctx.get().enqueueWork(() -> {
				ClientWorld level = Minecraft.getInstance().level;
				if(level==null) return;
				Entity entity = level.getEntity(msg.getEntityId());
				if(entity==null) return;
				AbilityHolder h = AbilityHolder.of(entity);
				if(h==null) return;
				for(Ability a : msg.getAbilities()) h.add(a);
			});
		}
	}
}
