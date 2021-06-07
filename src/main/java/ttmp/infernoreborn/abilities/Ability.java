package ttmp.infernoreborn.abilities;

import ttmp.infernoreborn.util.Attribs;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import net.minecraft.client.resources.I18n;
import net.minecraft.entity.ai.attributes.Attribute;
import net.minecraft.entity.ai.attributes.AttributeModifier;
import net.minecraft.entity.ai.attributes.Attributes;
import net.minecraft.item.Rarity;
import net.minecraftforge.registries.ForgeRegistryEntry;

import javax.annotation.Nullable;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

public class Ability extends ForgeRegistryEntry<Ability> implements Comparable<Ability>{
    private String unlocalizedName = "";
    private UUID uuid = UUID.fromString("fddb6681-0911-479c-810e-0137ef180d50");
    private int roll = 6, color = 0xFFFFFF;
    private Rarity rarity = Rarity.COMMON;

    //private AttributeManager attrib;

    private Set<String> tags = Sets.newHashSet();
    private Map<String, Integer> match = Maps.newHashMap();
    private Map<Attribute, AttributeModifier> attrib = Maps.newHashMap();

    public Ability setUnlocalizedName(String newValue){
        this.unlocalizedName = newValue;
        return this;
    }

    public Ability setRoll(int newValue){
        this.roll = newValue;
        return this;
    }

    public boolean hasTag(String tag){
        if(isDefaultTag(tag)){
            switch(tag){
                case "COMMON":
                    return this.getRarity()==Rarity.COMMON;
                case "UNCOMMON":
                    return this.getRarity()==Rarity.UNCOMMON;
                case "RARE":
                    return this.getRarity()==Rarity.RARE;
                case "EPIC":
                    return this.getRarity()==Rarity.EPIC;
                case "EMPTY_MATCH":
                    return this.match.isEmpty();
                case "NO_COLOR":
                    return this.getColor()==0xFFFFFF;
               // case "TICKABLE":
                //    return this instanceof Tickable;
                default:
                    throw new RuntimeException("Unexpected default tag "+tag);
            }
        }
        return tags.contains(tag.toUpperCase());
    }

    public int getRoll(Set<String> tags){
        int roll = this.getRoll();
        for(String tag : tags){
            if(match.containsKey(tag)) roll += match.get(tag);
        }
        return roll;
    }

    public Ability addTag(String tag){
        if(isDefaultTag(tag)){
            //TUtils.LOGGER.info("Cannot attach default tag {} on Ability {}!", tag, this.getRegistryName());
        }else tags.add(tag.toUpperCase());
        return this;
    }

    public Ability addTagAndMatch(String tag, int rollAdd){
        if(!hasTag(tag)&&!isDefaultTag(tag)) addTag(tag);
        addMatch(tag, rollAdd);
        return this;
    }

    public Ability addMatch(String tag, int rollAdd){
        tag = tag.toUpperCase();
        if(rollAdd==0){
            match.remove(tag);
        }else match.put(tag, rollAdd);
        return this;
    }

    public void addTagTo(Set<String> tag){
        tag.addAll(this.tags);
        tag.add(this.getRarity().name());
        if(this.match.isEmpty()) tag.add("EMPTY_MATCH");
        if(this.getColor()==0xFFFFFF) tag.add("NO_COLOR");
        //if(this instanceof Tickable) tag.add("TICKABLE");
    }

    private static final String[] defaultTags = {"COMMON", "UNCOMMON", "RARE", "EPIC", "EMPTY_MATCH", "NO_COLOR", "TICKABLE"};

    protected static final boolean isDefaultTag(String tag){
        for(String s : defaultTags)
            if(s.equals(tag)) return true;
        return false;
    }

    public Ability setColor(int newValue){
        this.color = newValue&0xFFFFFF;
        return this;
    }

    public Ability setRarity(Rarity newValue){
        this.rarity = newValue;
        return this;
    }

    public Ability addHP(double amount, AttributeModifier.Operation operation){
        return addAttrib(Attributes.MAX_HEALTH, amount, operation);
    }

    public Ability addAtk(double amount, AttributeModifier.Operation operation){
        return addAttrib(Attributes.ATTACK_SPEED, amount, operation);
    }

    public Ability addKbres(double amount, AttributeModifier.Operation operation){
        return addAttrib(Attributes.KNOCKBACK_RESISTANCE, amount, operation);
    }

    public Ability addSpd(double amount, AttributeModifier.Operation operation){
        return addAttrib(Attributes.MOVEMENT_SPEED, amount, operation);
    }

    public Ability addTokenboost(double amount, AttributeModifier.Operation operation){
        return addAttrib(Attribs.TOKEN_BOOST, amount, operation);
    }

    public Ability addProt(double amount){
        return addAttrib(Attribs.PROT, amount, AttributeModifier.Operation.ADDITION);
    }

    public Ability addRangedAtk(double amount, AttributeModifier.Operation operation){
        return addAttrib(Attribs.RANGED_ATK, amount, operation);
    }

    public Ability addSpellPow(double amount, AttributeModifier.Operation operation){
        return addAttrib(Attribs.SPELL_POW, amount, operation);
    }

    public Ability addFallResist(double amount){
        return addAttrib(Attribs.FALLING_DMG_RESISTANCE, amount, AttributeModifier.Operation.MULTIPLY_TOTAL);
    }

    public Ability addAttrib(Attribute attrib, double amount, AttributeModifier.Operation operation){
        if(attrib==Attributes.ARMOR||attrib==Attributes.ARMOR_TOUGHNESS){
            //TUtils.LOGGER.warn("Ability {} trying to add invalid Attribute {}, this will not work.", this.getRegistryName(), TUtils.localizeAttrib(attrib.getName(), amount, operation, true, false));
        }else{
            //if(this.attrib==null) this.attrib = new AttributeManager();
            this.attrib.put(attrib, new AttributeModifier(uuid, ()->this.getRegistryName().toString(), amount, operation));
            //this.attrib.addAttribute(attrib, amount, operation);
        }
        return this;
    }

    public String getUnlocalizedName(){
        return "ability."+getRegistryName();
    }

    public String getLocalizedName(){
        return I18n.get(getUnlocalizedName());
    }

    public String getColoredName(){
        return getRarity().color+getLocalizedName();
    }

    public int getRoll(){
        return this.roll;
    }

    public int getColor(){
        return this.color;
    }

    public Rarity getRarity(){
        return this.rarity;
    }

    @Nullable
    public Map<Attribute, AttributeModifier> getAttrib(){
        return this.attrib;
    }

    public boolean hasAttrib(){
        return this.attrib!=null&&!this.attrib.isEmpty();
    }
/*
    public void onAttack(LivingEntity entity, LivingAttackEvent event, MobAbility cap){}

    public void onDamaged(LivingEntity entity, LivingAttackEvent event, MobAbility cap){}

    public void onAttack(LivingEntity entity, LivingHurtEvent event, MobAbility cap){}

    public void onDamaged(LivingEntity entity, LivingHurtEvent event, MobAbility cap){}

    public void onAttack(LivingEntity entity, LivingDamageEvent event, MobAbility cap){}

    public void onDamaged(LivingEntity entity, LivingDamageEvent event, MobAbility cap){}

    public void onKill(LivingEntity entity, LivingDeathEvent event, MobAbility cap){}

    public void onDeath(LivingEntity entity, LivingDeathEvent event, MobAbility cap){}

    public void modifyToken(LivingEntity entity, LivingEntity token, AttributeManager attrib, MobAbility cap){}
*/
    @Override
    public String toString(){
        return getUnlocalizedName();
    }

    @Override
    public int compareTo(Ability o){
        //return o.getRarity().ordinal()*Character.MAX_VALUE-this.rarity.ordinal()*Character.MAX_VALUE+o.getLocalizedName().compareTo(this.getLocalizedName());
        int d = o.getRarity().compareTo(this.getRarity());
        if(d==0) return o.getLocalizedName().compareTo(this.getLocalizedName());
        else return d;
    }
/*
    public void appendDetailedMessage(List<String> strings){
        if(this.getClass()!=Ability.class) strings.add("    Class : "+this.getClass().getName());
        strings.add("    Unlocalized Name : "+this.unlocalizedName);
        strings.add("    Rarity : "+this.rarity);
        strings.add("    Color : "+this.color+" (#"+Integer.toHexString(this.color)+")");
        if(this.attrib!=null)
            strings.add("    Attributes : "+Enumerator.enumerator(this.attrib, e -> e.localize(true)));
        if(!this.tags.isEmpty())
            strings.add("    Tags : "+Enumerator.enumerator(this.tags, e -> this.match.containsKey(e) ? this.match.get(e) : e));
        if(!this.match.isEmpty())
            strings.add("    Matches : "+Enumerator.enumerator(this.match.entrySet(), e -> e.getKey()+">"+e.getValue()));
    }*/
}