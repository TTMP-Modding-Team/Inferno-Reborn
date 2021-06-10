package ttmp.infernoreborn.network;

import net.minecraft.client.Minecraft;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.entity.Entity;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.network.NetworkEvent;
import net.minecraftforge.fml.network.NetworkRegistry;
import net.minecraftforge.fml.network.simple.SimpleChannel;
import ttmp.infernoreborn.InfernoReborn;
import ttmp.infernoreborn.ability.Ability;
import ttmp.infernoreborn.ability.generator.AbilityGenerators;
import ttmp.infernoreborn.capability.ClientAbilityHolder;

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
		CHANNEL.registerMessage(0, SyncAbilitySchemeMsg.class,
				SyncAbilitySchemeMsg::write, SyncAbilitySchemeMsg::read,
				Client::handleSyncAbilityGeneratorList);
		CHANNEL.registerMessage(1, SyncAbilityHolderMsg.class,
				SyncAbilityHolderMsg::write, SyncAbilityHolderMsg::read,
				Client::handleSyncAbilityHolderMsg);
	}

	private static final class Client{
		private Client(){}

		public static void handleSyncAbilityGeneratorList(SyncAbilitySchemeMsg msg, Supplier<NetworkEvent.Context> ctx){
			InfernoReborn.LOGGER.debug("Re-syncing ability schemes");
			ctx.get().setPacketHandled(true);
			ctx.get().enqueueWork(() -> AbilityGenerators.setSchemes(msg.getSchemes()));
		}

		public static void handleSyncAbilityHolderMsg(SyncAbilityHolderMsg msg, Supplier<NetworkEvent.Context> ctx){
			ctx.get().setPacketHandled(true);
			ctx.get().enqueueWork(() -> {
				ClientWorld level = Minecraft.getInstance().level;
				if(level==null) return;
				Entity entity = level.getEntity(msg.getEntityId());
				if(entity==null) return;
				ClientAbilityHolder h = ClientAbilityHolder.of(entity);
				if(h==null) return;
				h.clear();
				for(Ability a : msg.getAbilities()) h.add(a);
				h.setAppliedGeneratorScheme(msg.getAppliedGeneratorScheme());
			});
		}
	}
}
