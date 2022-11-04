package ttmp.infernoreborn.network;

import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.player.ClientPlayerEntity;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.util.NonNullList;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.common.ForgeHooks;
import net.minecraftforge.fml.LogicalSide;
import net.minecraftforge.fml.network.NetworkDirection;
import net.minecraftforge.fml.network.NetworkEvent;
import net.minecraftforge.fml.network.NetworkRegistry;
import net.minecraftforge.fml.network.simple.SimpleChannel;
import ttmp.infernoreborn.api.TickingTaskHandler;
import ttmp.infernoreborn.api.ability.Ability;
import ttmp.infernoreborn.api.essence.EssenceHolder;
import ttmp.infernoreborn.api.essence.EssenceType;
import ttmp.infernoreborn.api.sigil.Sigil;
import ttmp.infernoreborn.api.sigil.SigilHolder;
import ttmp.infernoreborn.capability.ClientAbilityHolder;
import ttmp.infernoreborn.capability.ClientPlayerCapability;
import ttmp.infernoreborn.client.ParticlePlacingTask;
import ttmp.infernoreborn.client.screen.AbilityColorPickerScreen;
import ttmp.infernoreborn.client.screen.EssenceHolderScreen;
import ttmp.infernoreborn.client.screen.ScrapperScreen;
import ttmp.infernoreborn.client.screen.SigilScreen;
import ttmp.infernoreborn.contents.ModItems;
import ttmp.infernoreborn.contents.container.EssenceHolderContainer;
import ttmp.infernoreborn.contents.container.SigilScrapperContainer;
import ttmp.infernoreborn.contents.container.StigmaScrapperContainer;
import ttmp.infernoreborn.contents.container.StigmaTableContainer;
import ttmp.infernoreborn.contents.item.ability.AbilityColorPickerItem;
import ttmp.infernoreborn.util.damage.Damages;

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
		CHANNEL.registerMessage(5, ScrapSigilMsg.class,
				ScrapSigilMsg::write, ScrapSigilMsg::read,
				Server::handleScrapSigil, Optional.of(NetworkDirection.PLAY_TO_SERVER));
		CHANNEL.registerMessage(6, SyncShieldMsg.class,
				SyncShieldMsg::write, SyncShieldMsg::read,
				Client::handleSyncShield, Optional.of(NetworkDirection.PLAY_TO_CLIENT));
		CHANNEL.registerMessage(7, EngraveBodySigilMsg.class,
				(m, b) -> {}, b -> new EngraveBodySigilMsg(),
				Server::handleEngraveBodySigil, Optional.of(NetworkDirection.PLAY_TO_SERVER));
		CHANNEL.registerMessage(8, SyncBodySigilMsg.class,
				SyncBodySigilMsg::write, SyncBodySigilMsg::read,
				Client::handleSyncBodySigil, Optional.of(NetworkDirection.PLAY_TO_CLIENT));
		CHANNEL.registerMessage(9, SyncSigilScreenMsg.class,
				SyncSigilScreenMsg::write, SyncSigilScreenMsg::read,
				Client::handleSyncSigilScreen, Optional.of(NetworkDirection.PLAY_TO_CLIENT));
		CHANNEL.registerMessage(10, SyncScrapperScreenMsg.class,
				SyncScrapperScreenMsg::write, SyncScrapperScreenMsg::read,
				Client::handleSyncScrapperScreen, Optional.of(NetworkDirection.PLAY_TO_CLIENT));
		CHANNEL.registerMessage(11, AbilityColorPickerMsg.class,
				AbilityColorPickerMsg::write, AbilityColorPickerMsg::read,
				ModNet::handleOpenAbilityColorPicker);
	}

	public static void handleOpenAbilityColorPicker(AbilityColorPickerMsg msg, Supplier<NetworkEvent.Context> ctx){
		if(ctx.get().getDirection().getReceptionSide()==LogicalSide.CLIENT)
			Client.handleOpenAbilityColorPicker(msg, ctx);
		else Server.handleOpenAbilityColorPicker(msg, ctx);
	}

	private static final class Server{
		private Server(){}

		public static void handleEssenceHolderSlotClick(EssenceHolderSlotClickMsg msg, Supplier<NetworkEvent.Context> ctx){
			ctx.get().setPacketHandled(true);
			ctx.get().enqueueWork(() -> {
				ServerPlayerEntity sender = ctx.get().getSender();
				if(sender==null||!(sender.containerMenu instanceof EssenceHolderContainer)) return;
				EssenceHolderContainer container = (EssenceHolderContainer)sender.containerMenu;
				container.handleEssenceHolderSlotClick(msg.getSlot(), msg.getType(), msg.isShift());
			});
		}

		public static void handleScrapSigil(ScrapSigilMsg msg, Supplier<NetworkEvent.Context> ctx){
			ctx.get().setPacketHandled(true);
			if(msg.getSigil()!=null) ctx.get().enqueueWork(() -> {
				ServerPlayerEntity sender = ctx.get().getSender();
				if(sender==null) return;
				if(sender.containerMenu instanceof SigilScrapperContainer){
					SigilScrapperContainer container = (SigilScrapperContainer)sender.containerMenu;
					SigilHolder h = container.getSigilHolder();
					if(h!=null) h.remove(msg.getSigil());
				}else if(sender.containerMenu instanceof StigmaScrapperContainer){
					StigmaScrapperContainer container = (StigmaScrapperContainer)sender.containerMenu;
					SigilHolder h = container.getSigilHolder();
					if(h.has(msg.getSigil())){
						sender.hurt(Damages.engraving(), msg.getSigil().getPoint()*2);
						if(!sender.isAlive()) return;
						h.remove(msg.getSigil());
					}
				}
			});
		}

		public static void handleEngraveBodySigil(EngraveBodySigilMsg msg, Supplier<NetworkEvent.Context> ctx){
			ctx.get().setPacketHandled(true);
			ctx.get().enqueueWork(() -> {
				ServerPlayerEntity sender = ctx.get().getSender();
				if(sender==null||!(sender.containerMenu instanceof StigmaTableContainer)) return;
				StigmaTableContainer container = (StigmaTableContainer)sender.containerMenu;
				if(container.getCurrentRecipe()==null) return;

				SigilHolder h = SigilHolder.of(sender);
				if(h==null) return;
				Sigil sigil = container.getCurrentRecipe().tryEngrave(h, container.getInventory());
				if(sigil==null) return;
				sender.hurt(Damages.engraving(), sigil.getPoint()*2);
				if(!sender.isAlive()) return;

				h.add(sigil);
				ForgeHooks.setCraftingPlayer(sender); // TODO Copied from SigilEngravingResultSlot, probably need some refactor
				NonNullList<ItemStack> remainingItems = container.getCurrentRecipe().getRemainingItems(container.getInventory());
				ForgeHooks.setCraftingPlayer(null);
				for(int i = 0; i<remainingItems.size(); ++i){
					ItemStack stackIn = container.getInventory().getItem(i);
					ItemStack remaining = remainingItems.get(i);
					if(!stackIn.isEmpty()){
						container.getInventory().removeItem(i, 1);
						stackIn = container.getInventory().getItem(i);
					}

					if(!remaining.isEmpty()){
						if(stackIn.isEmpty()){
							container.getInventory().setItem(i, remaining);
						}else if(ItemStack.isSame(stackIn, remaining)&&ItemStack.tagMatches(stackIn, remaining)){
							remaining.grow(stackIn.getCount());
							container.getInventory().setItem(i, remaining);
						}else if(!sender.inventory.add(remaining)){
							sender.drop(remaining, false);
						}
					}
				}
			});
		}
		public static void handleOpenAbilityColorPicker(AbilityColorPickerMsg msg, Supplier<NetworkEvent.Context> ctx){
			ctx.get().setPacketHandled(true);
			ctx.get().enqueueWork(() -> {
				ServerPlayerEntity sender = ctx.get().getSender();
				if(sender==null||
						msg.getInventoryIndex()<0||
						msg.getInventoryIndex()>=sender.inventory.getContainerSize()) return;
				ItemStack stack = sender.inventory.getItem(msg.getInventoryIndex());
				if(stack.isEmpty()||stack.getItem()!=ModItems.ABILITY_COLOR_PICKER.get()) return;
				AbilityColorPickerItem.set(stack,
						msg.getPrimaryColor(),
						msg.getSecondaryColor(),
						msg.getHighlightColor());
			});
		}
	}

	private static final class Client{
		private Client(){}

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
				h.setEffects(msg.getEffects());
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

		@SuppressWarnings("ConstantConditions")
		public static void handleSyncShield(SyncShieldMsg msg, Supplier<NetworkEvent.Context> ctx){
			ctx.get().setPacketHandled(true);
			ctx.get().enqueueWork(() -> {
				ClientWorld level = Minecraft.getInstance().level;
				if(level==null) return;
				PlayerEntity player = level.getPlayerByUUID(msg.playerId);
				if(player==null) return;
				ClientPlayerCapability c = player.getCapability(ClientPlayerCapability.clientPlayerShield).orElse(null);
				if(c!=null) c.update(msg);
			});
		}
		public static void handleSyncBodySigil(SyncBodySigilMsg msg, Supplier<NetworkEvent.Context> ctx){
			ctx.get().setPacketHandled(true);
			ctx.get().enqueueWork(() -> {
				ClientPlayerEntity player = Minecraft.getInstance().player;
				if(player==null) return;
				SigilHolder h = SigilHolder.of(player);
				if(h==null) return;
				h.clear();
				for(Sigil sigil : msg.getSigils()){
					h.forceAdd(sigil);
				}
			});
		}

		public static void handleSyncSigilScreen(SyncSigilScreenMsg msg, Supplier<NetworkEvent.Context> ctx){
			ctx.get().setPacketHandled(true);
			ctx.get().enqueueWork(() -> {
				Screen screen = Minecraft.getInstance().screen;
				if(screen instanceof SigilScreen)
					((SigilScreen)screen).sync(msg.getCurrentSigils(), msg.getNewSigils());
			});
		}
		public static void handleSyncScrapperScreen(SyncScrapperScreenMsg msg, Supplier<NetworkEvent.Context> ctx){
			ctx.get().setPacketHandled(true);
			ctx.get().enqueueWork(() -> {
				Screen screen = Minecraft.getInstance().screen;
				if(screen instanceof ScrapperScreen)
					((ScrapperScreen)screen).sync(msg.getMaxSigils(), msg.getSigils());
			});
		}

		public static void handleOpenAbilityColorPicker(AbilityColorPickerMsg msg, Supplier<NetworkEvent.Context> ctx){
			ctx.get().setPacketHandled(true);
			ctx.get().enqueueWork(() -> {
				Minecraft.getInstance().setScreen(new AbilityColorPickerScreen(
						msg.getInventoryIndex(),
						msg.getPrimaryColor(),
						msg.getSecondaryColor(),
						msg.getHighlightColor()));
			});
		}
	}
}
