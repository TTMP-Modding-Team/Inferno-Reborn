package ttmp.infernoreborn.capability;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.EquipmentSlotType;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.nbt.ListNBT;
import net.minecraft.util.CombatRules;
import net.minecraft.util.Direction;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.ICapabilityProvider;
import net.minecraftforge.common.capabilities.ICapabilitySerializable;
import net.minecraftforge.common.util.Constants;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.fml.network.PacketDistributor;
import top.theillusivec4.curios.api.CuriosApi;
import top.theillusivec4.curios.api.type.capability.ICuriosItemHandler;
import top.theillusivec4.curios.api.type.inventory.ICurioStacksHandler;
import top.theillusivec4.curios.api.type.inventory.IDynamicStackHandler;
import ttmp.infernoreborn.InfernoReborn;
import ttmp.infernoreborn.contents.Sigils;
import ttmp.infernoreborn.contents.sigil.Sigil;
import ttmp.infernoreborn.contents.sigil.context.SigilEventContext;
import ttmp.infernoreborn.contents.sigil.holder.SigilHolder;
import ttmp.infernoreborn.network.ModNet;
import ttmp.infernoreborn.network.SyncShieldMsg;
import ttmp.infernoreborn.shield.ArmorShield;
import ttmp.infernoreborn.shield.MutableShield;
import ttmp.infernoreborn.shield.Shield;
import ttmp.infernoreborn.shield.ShieldModifier;
import ttmp.infernoreborn.util.SigilSlot;
import ttmp.infernoreborn.util.StupidUtils;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Objects;
import java.util.Set;

import static net.minecraft.inventory.EquipmentSlotType.Group.ARMOR;
import static net.minecraftforge.common.util.Constants.NBT.TAG_COMPOUND;
import static net.minecraftforge.common.util.Constants.NBT.TAG_LIST;

public class PlayerCapability implements SigilHolder, ICapabilitySerializable<CompoundNBT>{
	@SuppressWarnings("ConstantConditions") @Nullable public static PlayerCapability of(ICapabilityProvider provider){
		return provider.getCapability(Caps.playerCapability).orElse(null);
	}

	private final PlayerEntity player;
	private int judgementCooldown;

	private final Set<Sigil> sigils = new LinkedHashSet<>();

	private int totalPoint = 0;
	private boolean totalPointDirty;
	private boolean updateBodyShield;

	private boolean syncShield = true;

	@Nullable private ActiveShield bodyShield = null;
	@Nullable private ActiveShield defaultArmorShield = null;
	private final Map<String, ActiveShield> armorShields = new HashMap<>();
	private final Map<CurioSlot, ActiveShield> curioShields = new HashMap<>();

	private final List<ActiveShield> shieldList = new ArrayList<>();

	private final ItemStack[] armorCache = new ItemStack[4];
	private final Map<CurioSlot, ItemStack> curioCache = new HashMap<>();

	public PlayerCapability(PlayerEntity player){
		this.player = player;
		Arrays.fill(armorCache, ItemStack.EMPTY);
	}

	public boolean hasJudgementCooldown(){
		return judgementCooldown>0;
	}
	public int getJudgementCooldown(){
		return judgementCooldown;
	}
	public void setJudgementCooldown(int judgementCooldown){
		this.judgementCooldown = judgementCooldown;
	}

	@Override public Set<Sigil> getSigils(){
		return Collections.unmodifiableSet(sigils);
	}

	@Override public int getMaxPoints(){
		return 999; // TODO
	}
	@Override public int getTotalPoint(){
		if(totalPointDirty){
			totalPoint = 0;
			for(Sigil sigil : this.sigils)
				totalPoint += sigil.getPoint();
			totalPointDirty = false;
		}
		return totalPoint;
	}

	@Override public boolean has(Sigil sigil){
		return sigils.contains(sigil);
	}
	@Override public boolean add(Sigil sigil, boolean force){
		if(!force&&!canAdd(sigil)) return false;
		if(!sigils.add(sigil)) return false;
		totalPointDirty = true;
		if(sigil instanceof ShieldModifier) updateBodyShield = true;
		return true;
	}
	@Override public boolean remove(Sigil sigil){
		if(!sigils.remove(sigil)) return false;
		totalPointDirty = true;
		if(sigil instanceof ShieldModifier) updateBodyShield = true;
		return true;
	}
	@Override public boolean isEmpty(){
		return sigils.isEmpty();
	}
	@Override public void clear(){
		if(!sigils.isEmpty()){
			sigils.clear();
			totalPointDirty = true;
			updateBodyShield = true;
		}
	}

	@Override public SigilEventContext createContext(){
		return SigilEventContext.living(player, this);
	}

	@Override public long getGibberishSeed(){
		return player.getUUID().getMostSignificantBits()^player.getUUID().getLeastSignificantBits();
	}

	public void update(){
		if(!player.isAlive()) return;
		updateCurioSigilEffectAndShield();

		SyncShieldMsg msg = syncShield ? new SyncShieldMsg(player, shieldList.size()) : null;
		for(int i = 0; i<shieldList.size(); i++){
			ActiveShield activeShield = shieldList.get(i);
			activeShield.update(player.invulnerableTime<=0);
			if(syncShield||activeShield.needsSync){
				if(msg==null) msg = new SyncShieldMsg(player, shieldList.size());
				msg.add(i, activeShield);
				activeShield.needsSync = false;
			}
		}
		if(msg!=null) ModNet.CHANNEL.send(PacketDistributor.TRACKING_ENTITY_AND_SELF.with(() -> player), msg);
		syncShield = false;
	}

	// What the fuck did I just wrote
	private void updateCurioSigilEffectAndShield(){
		boolean sort = false;

		if(this.updateBodyShield){
			InfernoReborn.LOGGER.debug("Updating body shield");
			this.updateBodyShield = false;
			if(this.bodyShield!=null){
				this.shieldList.remove(this.bodyShield);
				this.syncShield = true;
			}
			MutableShield s = applySigilModifier(this, SigilSlot.BODY, ArmorShield.BODY_SHIELD);
			if(s!=null){
				this.shieldList.add(this.bodyShield = combine(s.toImmutable(), this.bodyShield));
				this.syncShield = sort = true;
			}else this.bodyShield = null;
		}

		boolean updateArmorShield = false;
		for(int i = 0; i<4; i++){
			ItemStack stack = player.getItemBySlot(EquipmentSlotType.byTypeAndIndex(ARMOR, i));
			if(ItemStack.matches(stack, armorCache[i])) continue;
			updateArmorShield = true;
			this.armorCache[i] = stack.copy();
		}
		if(updateArmorShield){
			InfernoReborn.LOGGER.debug("Updating armor shield");
			boolean[] usedArmor = new boolean[4];
			Map<String, ActiveShield> newArmorShields = new HashMap<>();
			for(ArmorShield ass : ArmorShield.getArmorShields()){
				if(!ass.armorSet.qualifies(player)) continue;
				MutableShield shield = new MutableShield(ass.shield);
				for(int i = 0; i<4; i++){
					EquipmentSlotType type = EquipmentSlotType.byTypeAndIndex(ARMOR, i);
					if(!ass.armorSet.uses(type)) continue;
					usedArmor[i] = true;
					applySigilModifier(player.getItemBySlot(type), SigilSlot.ARMOR, ass.shield, shield);
				}
				newArmorShields.put(ass.id, combine(shield.toImmutable(), this.armorShields.get(ass.id)));
			}
			if(!this.armorShields.equals(newArmorShields)){
				this.shieldList.removeAll(this.armorShields.values());
				this.armorShields.clear();
				this.armorShields.putAll(newArmorShields);
				this.shieldList.addAll(newArmorShields.values());
				this.syncShield = sort = true;
			}

			MutableShield defaultArmorShield = null;
			for(int i = 0; i<4; i++){
				if(usedArmor[i]) continue;
				EquipmentSlotType type = EquipmentSlotType.byTypeAndIndex(ARMOR, i);
				defaultArmorShield = applySigilModifier(player.getItemBySlot(type), SigilSlot.of(type), ArmorShield.DEFAULT_ARMOR_SHIELD, defaultArmorShield);
			}

			if(this.defaultArmorShield!=null){
				this.shieldList.remove(this.defaultArmorShield);
				this.syncShield = true;
			}

			if(defaultArmorShield!=null){
				this.shieldList.add(this.defaultArmorShield = combine(defaultArmorShield.toImmutable(), this.defaultArmorShield));
				this.syncShield = sort = true;
			}else this.defaultArmorShield = null;
		}

		//noinspection ConstantConditions
		ICuriosItemHandler curiosHandler = CuriosApi.getCuriosHelper().getCuriosHandler(player).orElse(null);
		//noinspection ConstantConditions
		if(curiosHandler!=null){
			for(Entry<String, ICurioStacksHandler> e : curiosHandler.getCurios().entrySet()){
				IDynamicStackHandler stacks = e.getValue().getStacks();
				for(int i = 0; i<stacks.getSlots(); i++){
					ItemStack stackInSlot = stacks.getStackInSlot(i);
					CurioSlot slot = new CurioSlot(e.getKey(), i);
					if(ItemStack.matches(curioCache.getOrDefault(slot, ItemStack.EMPTY), stackInSlot)) continue;
					if(stackInSlot.isEmpty()) curioCache.remove(slot);
					else curioCache.put(slot, stackInSlot.copy());
					InfernoReborn.LOGGER.debug("Updating curio slot "+slot);

					ActiveShield activeShield = curioShields.remove(slot);
					if(activeShield!=null){
						this.shieldList.remove(activeShield);
						this.syncShield = true;
					}
					Shield providedShield = getProvidedShield(stackInSlot);
					if(providedShield!=null){
						MutableShield s = applySigilModifier(stackInSlot, SigilSlot.CURIO, providedShield);
						ActiveShield combine = combine(s!=null ? s.toImmutable() : providedShield, activeShield);
						this.curioShields.put(slot, combine);
						this.shieldList.add(combine);
						this.syncShield = sort = true;
					}else{
						MutableShield s = applySigilModifier(stackInSlot, SigilSlot.CURIO, ArmorShield.DEFAULT_CURIO_SHIELD);
						if(s!=null){
							ActiveShield combine = combine(s.toImmutable(), activeShield);
							this.curioShields.put(slot, combine);
							this.shieldList.add(combine);
							this.syncShield = sort = true;
						}
					}
				}
			}
		}

		if(sort) this.shieldList.sort(Comparator.comparingDouble(o -> o.shield.getMaxDurability()));
	}

	@Nullable private static Shield getProvidedShield(ICapabilityProvider capabilityProvider){
		LazyOptional<ShieldProvider> c = capabilityProvider.getCapability(Caps.shieldProvider);
		//noinspection ConstantConditions
		ShieldProvider p = c.orElse(null);
		//noinspection ConstantConditions
		return p!=null ? p.getShield() : null;
	}

	private static ActiveShield combine(Shield shield, @Nullable ActiveShield activeShield){
		ActiveShield newShield = new ActiveShield(shield);
		if(activeShield!=null){
			newShield.durability = Math.min(activeShield.durability, shield.getMaxDurability());
			newShield.down = activeShield.down;
			newShield.tickCounter = activeShield.tickCounter;
		}
		return newShield;
	}

	@Nullable private static MutableShield applySigilModifier(ICapabilityProvider capabilityProvider, SigilSlot sigilSlot, Shield original){
		return applySigilModifier(capabilityProvider, sigilSlot, original, null);
	}
	@Nullable private static MutableShield applySigilModifier(ICapabilityProvider capabilityProvider, SigilSlot sigilSlot, Shield original, @Nullable MutableShield existing){
		SigilHolder h = SigilHolder.of(capabilityProvider);
		if(h!=null){
			for(Sigil sigil : h.getSigils()){
				if(!(sigil instanceof ShieldModifier)) continue;
				if(existing==null) existing = new MutableShield(original);
				((ShieldModifier)sigil).applyShieldModifier(sigilSlot, original, existing);
			}
		}
		return existing;
	}

	public float applyShieldReduction(float damage){
		for(ActiveShield shield : shieldList)
			damage = shield.applyReduction(damage);
		return damage;
	}

	public void copyTo(PlayerCapability other){
		other.setJudgementCooldown(getJudgementCooldown());
		for(Sigil sigil : sigils) other.add(sigil, true);
	}

	@Nullable private LazyOptional<PlayerCapability> self;

	@Nonnull @Override public <T> LazyOptional<T> getCapability(@Nonnull Capability<T> cap, @Nullable Direction side){
		if(cap==Caps.playerCapability||cap==Caps.sigilHolder){
			if(self==null) self = LazyOptional.of(() -> this);
			return self.cast();
		}
		return LazyOptional.empty();
	}

	@Override public CompoundNBT serializeNBT(){
		CompoundNBT nbt = new CompoundNBT();
		if(judgementCooldown>0) nbt.putInt("judgement", judgementCooldown);
		nbt.put("sigils", StupidUtils.writeToNbt(sigils, Sigils.getRegistry()));

		if(bodyShield!=null) nbt.put("bodyShield", bodyShield.save());
		if(defaultArmorShield!=null) nbt.put("defaultArmorShield", defaultArmorShield.save());
		if(!armorShields.isEmpty()){
			CompoundNBT armorShieldsNbt = new CompoundNBT();
			for(Entry<String, ActiveShield> e : armorShields.entrySet())
				armorShieldsNbt.put(e.getKey(), e.getValue().save());
			nbt.put("armorShields", armorShieldsNbt);
		}
		if(!curioShields.isEmpty()){
			ListNBT curioShieldsNbt = new ListNBT();
			for(Entry<CurioSlot, ActiveShield> e : curioShields.entrySet()){
				CompoundNBT curioShield = e.getValue().save();
				curioShield.putString("id", e.getKey().id);
				curioShield.putInt("index", e.getKey().index);
				curioShieldsNbt.add(curioShield);
			}
			nbt.put("curioShields", curioShieldsNbt);
		}
		return nbt;
	}

	@Override public void deserializeNBT(CompoundNBT nbt){
		judgementCooldown = nbt.getInt("judgement");
		sigils.clear();
		if(nbt.contains("sigils", TAG_LIST)){
			ListNBT list = nbt.getList("sigils", Constants.NBT.TAG_STRING);
			StupidUtils.read(list, Sigils.getRegistry(), sigils::add);
		}
		totalPointDirty = true;
		updateBodyShield = true;

		bodyShield = nbt.contains("bodyShield", TAG_COMPOUND) ?
				new ActiveShield(ArmorShield.BODY_SHIELD, nbt.getCompound("bodyShield")) : null;
		defaultArmorShield = nbt.contains("defaultArmorShield", TAG_COMPOUND) ?
				new ActiveShield(ArmorShield.DEFAULT_ARMOR_SHIELD, nbt.getCompound("defaultArmorShield")) : null;
		this.armorShields.clear();
		if(nbt.contains("armorShields", TAG_COMPOUND)){
			CompoundNBT armorShields = nbt.getCompound("armorShields");
			for(String key : armorShields.getAllKeys())
				this.armorShields.put(key, new ActiveShield(ArmorShield.DEFAULT_ARMOR_SHIELD, armorShields.getCompound(key)));
		}
		this.curioShields.clear();
		if(nbt.contains("curioShields", TAG_LIST)){
			ListNBT curioShieldsNbt = nbt.getList("curioShields", TAG_COMPOUND);
			for(int i = 0; i<curioShieldsNbt.size(); i++){
				CompoundNBT curioShield = curioShieldsNbt.getCompound(i);
				this.curioShields.put(
						new CurioSlot(curioShield.getString("id"), curioShield.getInt("index")),
						new ActiveShield(ArmorShield.DEFAULT_CURIO_SHIELD, curioShield));
			}
		}
	}

	public static final class ActiveShield{
		private final Shield shield;
		private double durability;
		private boolean down;

		private int tickCounter;
		private boolean needsSync = true;

		private ActiveShield(Shield shield){
			this.shield = shield;
		}
		private ActiveShield(Shield shield, CompoundNBT nbt){
			this.shield = shield;
			this.durability = nbt.getDouble("durability");
			this.down = nbt.getBoolean("down");
			this.tickCounter = Byte.toUnsignedInt(nbt.getByte("tc"));
		}

		public Shield getShield(){
			return shield;
		}
		public boolean isDown(){
			return down;
		}
		public double getDurability(){
			return durability;
		}

		private void update(boolean canRegenerate){
			if(down){
				if(canRegenerate&&--tickCounter<=0){
					this.tickCounter = 20;
					this.durability += shield.getRecovery();
					double durability = shield.getMaxDurability();
					if(this.durability>=durability){
						this.durability = durability;
						down = false;
					}
					needsSync = true;
				}
			}else{
				double maxShield = shield.getMaxDurability();
				if(durability>maxShield){
					durability = maxShield;
					needsSync = true;
				}else if(durability<maxShield&&canRegenerate&&--tickCounter<=0){
					tickCounter = 20;
					durability = Math.min(maxShield, durability+shield.getRegen());
					needsSync = true;
				}
			}
		}

		private float applyReduction(float damage){
			// Can't be blocked, also probably supposed to be unblockable so don't even bother wasting shield in that case
			if(isDown()||
					damage<=0||
					Float.isInfinite(damage)||
					Float.isNaN(damage)||
					Float.MAX_VALUE==damage)
				return damage;

			damage = CombatRules.getDamageAfterAbsorb(
					Math.max(damage*(1-(float)shield.getResistance()), 0),
					(float)shield.getArmor(),
					(float)shield.getToughness());

			if(durability<damage){
				float newDamage = (float)(damage-durability);
				durability = 0;
				down = true;
				tickCounter = 60;
				needsSync = true;
				return newDamage;
			}else if(durability==damage){
				durability = 0;
				down = true;
				tickCounter = 60;
				needsSync = true;
				return 0;
			}else{ // durability>damage
				durability -= damage;
				needsSync = true;
				return 0;
			}
		}

		private CompoundNBT save(){
			CompoundNBT nbt = new CompoundNBT();
			if(this.durability!=0) nbt.putDouble("durability", this.durability);
			if(isDown()) nbt.putBoolean("down", true);
			if(tickCounter>0) nbt.putByte("tc", (byte)tickCounter);
			return nbt;
		}
	}

	private static final class CurioSlot{
		private final String id;
		private final int index;
		private CurioSlot(String id, int index){
			this.id = id;
			this.index = index;
		}

		@Override public boolean equals(Object o){
			if(this==o) return true;
			if(o==null||getClass()!=o.getClass()) return false;
			CurioSlot curioSlot = (CurioSlot)o;
			return index==curioSlot.index&&id.equals(curioSlot.id);
		}
		@Override public int hashCode(){
			return Objects.hash(id, index);
		}

		@Override public String toString(){
			return id+"#"+index;
		}
	}
}
