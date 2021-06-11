package ttmp.infernoreborn.item;

import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemGroup;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.util.ActionResult;
import net.minecraft.util.ActionResultType;
import net.minecraft.util.Hand;
import net.minecraft.util.NonNullList;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.Style;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraft.world.World;
import net.minecraftforge.common.capabilities.ICapabilityProvider;
import ttmp.infernoreborn.capability.EssenceHolder;
import ttmp.infernoreborn.container.EssenceHolderContainerProvider;
import ttmp.infernoreborn.util.EssenceType;
import ttmp.infernoreborn.util.ExpandKey;
import vazkii.patchouli.api.PatchouliAPI;

import javax.annotation.Nullable;
import java.util.List;
import java.util.Objects;

/**
 * The book. You know what it is.
 */
public class TheBookItem extends Item{
	public TheBookItem(Properties properties){
		super(properties);
	}

	@Nullable @Override public ICapabilityProvider initCapabilities(ItemStack stack, @Nullable CompoundNBT nbt){
		return new EssenceHolder();
	}

	@Override public void fillItemCategory(ItemGroup itemGroup, NonNullList<ItemStack> itemStacks){
		if(allowdedIn(itemGroup)){
			itemStacks.add(new ItemStack(this));
			ItemStack stack = new ItemStack(this);
			setHasEssenceHolder(stack, true);
			itemStacks.add(stack);
		}
	}

	@Override
	public ActionResult<ItemStack> use(World world, PlayerEntity player, Hand hand){
		ItemStack stack = player.getItemInHand(hand);
		if(player instanceof ServerPlayerEntity){
			if(player.isCrouching()&&hasEssenceHolder(stack)){
				player.openMenu(new EssenceHolderContainerProvider(
						new TranslationTextComponent("container.infernoreborn.book_of_the_unspeakable"),
						player,
						hand));
			}else{
				PatchouliAPI.get().openBookGUI((ServerPlayerEntity)player, Objects.requireNonNull(this.getRegistryName()));
			}
		}
		return new ActionResult<>(ActionResultType.SUCCESS, stack);
	}

	@Override public void appendHoverText(ItemStack stack, @Nullable World world, List<ITextComponent> text, ITooltipFlag flag){
		if(!hasEssenceHolder(stack)) return;

		text.add(new TranslationTextComponent("item.infernoreborn.book_of_the_unspeakable.desc.essence_holder").setStyle(Style.EMPTY.applyFormat(TextFormatting.YELLOW)));
		if(ExpandKey.SHIFT.isKeyDown()){
			stack.getCapability(EssenceHolder.capability).ifPresent(essenceHolder -> {
				for(EssenceType type : EssenceType.values()){
					int essence = essenceHolder.getEssence(type);
					if(essence>0) text.add(new TranslationTextComponent("item.infernoreborn.essence_holder.desc.essences."+type.id, essence));
				}
			});
		}else text.add(ExpandKey.SHIFT.getCollapsedText());
	}

	@Nullable @Override public CompoundNBT getShareTag(ItemStack stack){
		CompoundNBT nbt = stack.getTag();
		@SuppressWarnings("ConstantConditions")
		EssenceHolder h = stack.getCapability(EssenceHolder.capability).orElse(null);
		//noinspection ConstantConditions
		if(h!=null){
			CompoundNBT capNbt = h.serializeNBT();
			if(!capNbt.isEmpty()){
				if(nbt==null) nbt = new CompoundNBT();
				nbt.put("Essence", capNbt);
			}
		}
		return nbt;
	}

	@Override public void readShareTag(ItemStack stack, @Nullable CompoundNBT nbt){
		stack.setTag(nbt);
		stack.getCapability(EssenceHolder.capability).ifPresent(h -> {
			if(nbt!=null) h.deserializeNBT(nbt.getCompound("Essence"));
			else h.clear();
		});
	}

	public static boolean hasEssenceHolder(ItemStack stack){
		CompoundNBT tag = stack.getTag();
		return tag!=null&&tag.getBoolean("HasEssenceHolder");
	}

	public static void setHasEssenceHolder(ItemStack stack, boolean hasEssenceHolder){
		CompoundNBT tag = stack.getOrCreateTag();
		if(hasEssenceHolder) tag.putBoolean("HasEssenceHolder", true);
		else tag.remove("HasEssenceHolder");
	}
}
