package ttmp.infernoreborn.network;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.network.NetworkDirection;
import net.minecraftforge.fml.network.NetworkEvent;
import net.minecraftforge.fml.network.NetworkRegistry;
import net.minecraftforge.fml.network.simple.SimpleChannel;
import ttmp.infernoreborn.capability.TickingTaskHandler;
import ttmp.infernoreborn.client.ParticlePlacingTask;
import ttmp.infernoreborn.client.screen.EssenceHolderScreen;
import ttmp.infernoreborn.contents.ability.Ability;
import ttmp.infernoreborn.contents.ability.generator.AbilityGenerators;
import ttmp.infernoreborn.contents.ability.holder.ClientAbilityHolder;
import ttmp.infernoreborn.contents.container.EssenceHolderContainer;
import ttmp.infernoreborn.util.EssenceHolder;
import ttmp.infernoreborn.util.EssenceType;

import java.util.Optional;
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
		CHANNEL.registerMessage(2, EssenceHolderSlotClickMsg.class,
				EssenceHolderSlotClickMsg::write, EssenceHolderSlotClickMsg::read,
				Server::handleEssenceHolderSlotClick, Optional.of(NetworkDirection.PLAY_TO_SERVER));
		CHANNEL.registerMessage(3, EssenceHolderScreenEssenceSyncMsg.class,
				EssenceHolderScreenEssenceSyncMsg::write, EssenceHolderScreenEssenceSyncMsg::new,
				Client::handleEssenceHolderScreenEssenceSyncMsg, Optional.of(NetworkDirection.PLAY_TO_CLIENT));
		CHANNEL.registerMessage(4, ParticleMsg.class,
				ParticleMsg::write, ParticleMsg::read,
				Client::handleParticle, Optional.of(NetworkDirection.PLAY_TO_CLIENT));
	}

	private static final class Server{
		private Server(){}

		public static void handleEssenceHolderSlotClick(EssenceHolderSlotClickMsg msg, Supplier<NetworkEvent.Context> ctx){
			ctx.get().setPacketHandled(true);
			ctx.get().enqueueWork(() -> {
				ServerPlayerEntity sender = ctx.get().getSender();
				if(sender==null) return;
				if(!(sender.containerMenu instanceof EssenceHolderContainer)){
					return;
				}
				EssenceHolderContainer container = (EssenceHolderContainer)sender.containerMenu;
				container.handleEssenceHolderSlotClick(msg.getSlot(), msg.getType(), msg.isShift());
			});
		}
	}

	private static final class Client{
		private Client(){}

		public static void handleSyncAbilityGeneratorList(SyncAbilitySchemeMsg msg, Supplier<NetworkEvent.Context> ctx){
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

		public static void handleEssenceHolderScreenEssenceSyncMsg(EssenceHolderScreenEssenceSyncMsg msg, Supplier<NetworkEvent.Context> ctx){
			ctx.get().setPacketHandled(true);
			ctx.get().enqueueWork(() -> {
				Screen screen = Minecraft.getInstance().screen;
				if(!(screen instanceof EssenceHolderScreen)) return;
				EssenceHolder h = ((EssenceHolderScreen)screen).getMenu().getEssenceHolder().getEssenceHolder();
				for(EssenceType type : EssenceType.values())
					h.setEssence(type, msg.getEssences()[type.ordinal()]);
			});
		}

		public static void handleParticle(ParticleMsg msg, Supplier<NetworkEvent.Context> ctx){
			ctx.get().setPacketHandled(true);
			ctx.get().enqueueWork(() -> {
				ClientWorld level = Minecraft.getInstance().level;
				if(level==null) return;
				TickingTaskHandler h = TickingTaskHandler.of(level);
				if(h==null) return;
				ParticlePlacingTask p = ParticlePlacingTask.from(msg);
				if(p!=null) h.add(p);
			});
		}
	}
}
