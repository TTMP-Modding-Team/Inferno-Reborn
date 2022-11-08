package ttmp.infernoreborn.api.sigil;

import com.google.common.collect.ListMultimap;
import net.minecraft.client.renderer.model.RenderMaterial;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.ai.attributes.Attribute;
import net.minecraft.entity.ai.attributes.AttributeModifier;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.container.PlayerContainer;
import net.minecraft.item.Rarity;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.TextComponent;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.event.entity.living.LivingAttackEvent;
import net.minecraftforge.registries.ForgeRegistryEntry;
import ttmp.infernoreborn.api.sigil.context.ItemContext;
import ttmp.infernoreborn.api.sigil.context.SigilEventContext;
import ttmp.infernoreborn.api.sigil.page.SigilBookEntry;
import ttmp.infernoreborn.api.sigil.page.SigilPageBuilder;

import javax.annotation.Nullable;
import java.util.Collections;
import java.util.EnumSet;
import java.util.Objects;
import java.util.Set;

public class Sigil extends ForgeRegistryEntry<Sigil>{
	private static final Set<SigilSlot> ANY_SLOTS = Collections.unmodifiableSet(EnumSet.of(SigilSlot.ANY));

	private final int brighterColor, darkerColor;
	private final int point;
	private final Set<SigilSlot> applicableSlots;
	private final Rarity rarity;

	public Sigil(Properties properties){
		this.brighterColor = properties.brighterColor;
		this.darkerColor = properties.darkerColor;
		this.point = properties.point;
		this.applicableSlots = properties.applicableSlots.isEmpty()||properties.applicableSlots.contains(SigilSlot.ANY) ?
				ANY_SLOTS : Collections.unmodifiableSet(properties.applicableSlots);
		this.rarity = properties.rarity;
	}

	public int getBrighterColor(){
		return brighterColor;
	}
	public int getDarkerColor(){
		return darkerColor;
	}

	public int getPoint(){
		return point;
	}

	public Set<SigilSlot> getApplicableSlots(){
		return applicableSlots;
	}
	public Rarity getRarity(){
		return rarity;
	}

	public String getUnlocalizedName(){
		ResourceLocation n = Objects.requireNonNull(getRegistryName());
		return "sigil."+n.getNamespace()+"."+n.getPath();
	}

	public TextComponent getName(){
		return new TranslationTextComponent(getUnlocalizedName());
	}

	public boolean canBeAttachedTo(SigilEventContext context){
		ItemContext ic = context.getAsItemContext();
		if(ic==null)
			return getApplicableSlots().contains(SigilSlot.ANY)||getApplicableSlots().contains(SigilSlot.BODY);
		for(SigilSlot s : getApplicableSlots())
			if(s.isAvailableForItem(ic.stack()))
				return true;
		return false;
	}

	/**
	 * Apply attributes based on the slot and context
	 */
	public void applyAttributes(SigilEventContext ctx, SigilSlot slot, ListMultimap<Attribute, AttributeModifier> attributes){}

	/**
	 * On tick
	 */
	public void onTick(SigilEventContext ctx, SigilSlot slot, PlayerEntity player){}

	/**
	 * When holder attacks sth
	 */
	public void onAttack(SigilEventContext ctx, SigilSlot slot, LivingAttackEvent event, LivingEntity entity){}

	/**
	 * When holder is attacked by sth
	 */
	public void onAttacked(SigilEventContext ctx, SigilSlot slot, LivingAttackEvent event){}

	@Nullable private volatile ResourceLocation sigilTextureLocation;

	public ResourceLocation getSigilTextureLocation(){
		if(sigilTextureLocation==null){
			synchronized(this){
				if(sigilTextureLocation==null)
					sigilTextureLocation = createSigilTextureLocation();
			}
		}
		return Objects.requireNonNull(sigilTextureLocation);
	}

	protected ResourceLocation createSigilTextureLocation(){
		ResourceLocation id = Objects.requireNonNull(this.getRegistryName());
		return new ResourceLocation(id.getNamespace(), "sigil/"+id.getPath());
	}

	@Nullable private volatile SigilBookEntry sigilBookEntry;

	public SigilBookEntry getSigilBookEntryContent(){
		if(sigilBookEntry==null){
			synchronized(this){
				if(sigilBookEntry==null){
					SigilPageBuilder b = new SigilPageBuilder();
					createSigilBookEntryContent(b);
					this.sigilBookEntry = b.build();
				}
			}
		}
		return Objects.requireNonNull(sigilBookEntry);
	}

	protected void createSigilBookEntryContent(SigilPageBuilder builder){}

	@Override public boolean equals(Object obj){
		if(obj==this) return true;
		if(!(obj instanceof Sigil)) return false;
		return Objects.equals(((Sigil)obj).getRegistryName(), this.getRegistryName());
	}

	@Override public int hashCode(){
		ResourceLocation n = getRegistryName();
		return n==null ? 0 : n.hashCode();
	}

	@Override public String toString(){
		return getUnlocalizedName();
	}

	public static final class Properties{
		private final int brighterColor, darkerColor;
		private final int point;

		private final EnumSet<SigilSlot> applicableSlots = EnumSet.noneOf(SigilSlot.class);
		private Rarity rarity = Rarity.UNCOMMON;

		public Properties(int brighterColor, int darkerColor, int point){
			if(point<0) throw new IllegalArgumentException("point");
			this.brighterColor = brighterColor;
			this.darkerColor = darkerColor;
			this.point = point;
		}

		public Properties rarity(Rarity rarity){
			this.rarity = Objects.requireNonNull(rarity);
			return this;
		}

		public Properties allow(SigilSlot... slots){
			Collections.addAll(applicableSlots, slots);
			return this;
		}

		public Properties allowBody(){
			return allow(SigilSlot.BODY);
		}
		public Properties allowAnyItem(){
			return allow(SigilSlot.ITEM);
		}
		public Properties allowMainhand(){
			return allow(SigilSlot.MAINHAND);
		}
		public Properties allowOffhand(){
			return allow(SigilSlot.OFFHAND);
		}
		public Properties allowArmor(){
			return allow(SigilSlot.ARMOR);
		}
		public Properties allowCurio(){
			return allow(SigilSlot.CURIO);
		}
	}
}
