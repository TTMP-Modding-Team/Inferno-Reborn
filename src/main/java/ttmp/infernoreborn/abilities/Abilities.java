package ttmp.infernoreborn.abilities;

import ttmp.infernoreborn.InfernoReborn;
import net.minecraft.entity.ai.attributes.AttributeModifier;
import net.minecraft.entity.ai.attributes.Attributes;

public class Abilities {
    public static final Ability HEART_1;

    static{
        HEART_1 = (new Ability()).setRegistryName(InfernoReborn.MODID, "heart_1").setColor(16711680).addAttrib(Attributes.MAX_HEALTH, 0.5D, AttributeModifier.Operation.ADDITION);

    }
}
