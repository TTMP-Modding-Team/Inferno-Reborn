package ttmp.infernoreborn;

import com.mojang.brigadier.Command;
import com.mojang.brigadier.LiteralMessage;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.exceptions.DynamicCommandExceptionType;
import com.mojang.brigadier.exceptions.SimpleCommandExceptionType;
import net.minecraft.command.CommandSource;
import net.minecraft.command.arguments.EntityArgument;
import net.minecraft.entity.item.ItemEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.SoundEvents;
import net.minecraft.util.text.StringTextComponent;
import net.minecraftforge.event.RegisterCommandsEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import ttmp.infernoreborn.capability.PlayerCapability;
import ttmp.infernoreborn.contents.item.ability.GeneratorAbilityItem;
import ttmp.infernoreborn.infernaltype.InfernalType;
import ttmp.infernoreborn.infernaltype.InfernalTypes;

import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

import static net.minecraft.command.Commands.argument;
import static net.minecraft.command.Commands.literal;
import static net.minecraft.command.ISuggestionProvider.suggest;
import static ttmp.infernoreborn.api.InfernoRebornApi.MODID;

@Mod.EventBusSubscriber(modid = MODID)
public final class InfernoRebornCommands{
	private InfernoRebornCommands(){}

	@SubscribeEvent
	public static void registerCommands(RegisterCommandsEvent event){
		event.getDispatcher().register(literal("infernoreborn")
				.requires(s -> s.hasPermission(2))
				.then(literal("infernalType")
						.then(literal("list").executes(context -> listInfernalTypes(context.getSource())))
						.then(literal("reload").executes(context -> reloadInfernalTypes(context.getSource())))
						.then(literal("gen")
								.then(argument("ability", StringArgumentType.string())
										.suggests((context, builder) -> suggest(InfernalTypes.getInfernalTypes()
												.stream().map(InfernalType::getName)
												.filter(Objects::nonNull), builder))
										.executes(context -> giveInfernalTypeGenerator(
												context.getSource(),
												StringArgumentType.getString(context, "ability"),
												Collections.singletonList(context.getSource().getPlayerOrException())))
										.then(argument("targets", EntityArgument.players())
												.executes(context -> giveInfernalTypeGenerator(
														context.getSource(),
														StringArgumentType.getString(context, "ability"),
														EntityArgument.getPlayers(context, "targets")))
										)
								)
						)
				).then(literal("judgement").then(literal("removeCooldown")
						.then(argument("player", EntityArgument.player())
								.executes(context -> removeJudgementCooldown(EntityArgument.getPlayer(context, "player")))
						)
				))
		);
	}

	private static final SimpleCommandExceptionType judgement_missingCapability =
			new SimpleCommandExceptionType(new LiteralMessage("Missing capability"));

	private static int removeJudgementCooldown(PlayerEntity player) throws CommandSyntaxException{
		PlayerCapability c = PlayerCapability.of(player);
		if(c==null) throw judgement_missingCapability.create();
		c.setJudgementCooldown(0);
		return Command.SINGLE_SUCCESS;
	}

	private static int listInfernalTypes(CommandSource source){
		List<InfernalType> infernalTypes = InfernalTypes.getInfernalTypes();
		source.sendSuccess(new StringTextComponent(infernalTypes.size()+" infernal type(s)"), false);
		for(InfernalType t : infernalTypes)
			source.sendSuccess(new StringTextComponent(t.toString()), false);
		return Command.SINGLE_SUCCESS;
	}

	private static int reloadInfernalTypes(CommandSource source){
		InfernalTypes.load(new InfernalTypes.LogHandler(){
			@Override public void logInfo(String message){
				source.sendSuccess(new StringTextComponent(message), true);
				InfernoReborn.LOGGER.warn(message);
			}
			@Override public void logInfo(String message, Throwable exception){
				source.sendSuccess(new StringTextComponent(message+": "+exception), true);
				InfernoReborn.LOGGER.warn(message, exception);
			}
			@Override public void logError(String message){
				source.sendFailure(new StringTextComponent(message));
				InfernoReborn.LOGGER.error(message);
			}
			@Override public void logError(String message, Throwable exception){
				source.sendFailure(new StringTextComponent(message+": "+exception));
				InfernoReborn.LOGGER.error(message, exception);
			}
		});
		return Command.SINGLE_SUCCESS;
	}

	private static final DynamicCommandExceptionType infernalType_noTypeNamed =
			new DynamicCommandExceptionType(a -> new LiteralMessage("No infernal type named '"+a+"'"));

	private static int giveInfernalTypeGenerator(CommandSource source, String infernalTypeId, Collection<ServerPlayerEntity> targets)
			throws CommandSyntaxException{
		InfernalType t = InfernalTypes.getInfernalType(infernalTypeId);
		if(t==null) throw infernalType_noTypeNamed.create(infernalTypeId);

		for(ServerPlayerEntity sp : targets){
			ItemStack stack = GeneratorAbilityItem.createItemStack(t);
			if(sp.inventory.add(stack)&&stack.isEmpty()){
				stack.setCount(1);
				ItemEntity e = sp.drop(stack, false);
				if(e!=null) e.makeFakeItem();
				sp.level.playSound(null,
						sp.getX(), sp.getY(), sp.getZ(),
						SoundEvents.ITEM_PICKUP, SoundCategory.PLAYERS, 0.2f,
						((sp.getRandom().nextFloat()-sp.getRandom().nextFloat())*0.7f+1)*2);
				sp.inventoryMenu.broadcastChanges();
			}else{
				ItemEntity e = sp.drop(stack, false);
				if(e!=null){
					e.setNoPickUpDelay();
					e.setOwner(sp.getUUID());
				}
			}
		}

		// if(targets.size()==1){ // TODO
		// 	source.sendSuccess(new TranslationTextComponent(
		// 			"commands.give.success.single",
		// 			pCount, pItem.createItemStack(pCount, false).getDisplayName(), targets.iterator().next().getDisplayName()), true);
		// }else{
		// 	source.sendSuccess(new TranslationTextComponent(
		// 			"commands.give.success.single",
		// 			pCount, pItem.createItemStack(pCount, false).getDisplayName(), targets.size()), true);
		// }

		return targets.size();
	}
}
