package ttmp.infernoreborn.util;

import net.minecraft.entity.ai.attributes.Attribute;
import net.minecraft.entity.ai.attributes.RangedAttribute;

public final class Attribs{
    private Attribs(){}

    /** 이 어트리뷰트가 붙은 엔티티가 능력에 의해 다른 엔티티를 소환할 때, 그 엔티티에게 똑같은 어트리뷰트를 물려주고 엔티티의 능력 등급을 N단계 올림 */
    public static final Attribute TOKEN_BOOST = new RangedAttribute("mobabilities.tokenboost", 0, -1000000, 1000000);
    /** 낙하에 의한 피해를 입을 때, 피해량 a에 (a의 N%)를 차감시킴 */
    public static final Attribute FALLING_DMG_RESISTANCE = new RangedAttribute("mobabilities.fall_dmg_resistance", 0, 0, 4);

    /** 1초마다 자신의 체력을 N만큼 회복 */
    public static final Attribute REGENERATION = new RangedAttribute( "mobabilities.regen", 0, 0, 1024);
    /** 최대 N까지 회복되는 보호막을 얻음. 1초마다 0.025씩 회복 */
    public static final Attribute SHIELD = new RangedAttribute( "mobabilities.shield", 0, 0, 1024);
    /** 직접적이지 않은, 마법이 아닌 모든 종류의 피해량이 N만큼 상승 */
    public static final Attribute RANGED_ATK = new RangedAttribute("mobabilities.ranged_atk", 0, 0, 2048);
    /** 마법 피해량이 N만큼 상승 */
    public static final Attribute SPELL_POW = new RangedAttribute( "mobabilities.spell_pow", 0, 0, 2048);
    /** 방어 관통이 아닌 피해를 입을 때, 피해량 a에 ((N-1)/1)a를 차감시킴. 해당 어트리뷰트 값이 1보다 작으면 피해량이 올라감 */
    public static final Attribute PROT = new RangedAttribute("mobabilities.prot", 1, 0.6, 1.8);
}