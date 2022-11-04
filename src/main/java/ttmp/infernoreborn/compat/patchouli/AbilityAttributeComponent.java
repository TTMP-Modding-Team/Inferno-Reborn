package ttmp.infernoreborn.compat.patchouli;

import com.google.common.collect.ImmutableMultimap;
import com.google.common.collect.Multimap;
import net.minecraft.entity.ai.attributes.Attribute;
import net.minecraft.entity.ai.attributes.AttributeModifier;
import net.minecraft.util.ResourceLocation;
import ttmp.infernoreborn.api.ability.Ability;
import ttmp.infernoreborn.contents.Abilities;
import vazkii.patchouli.api.IVariable;

import javax.annotation.Nullable;
import java.util.function.UnaryOperator;

public class AbilityAttributeComponent extends BaseAttributeComponent{
	@Nullable private transient Ability ability;

	@Override public void onVariablesAvailable(UnaryOperator<IVariable> lookup){
		super.onVariablesAvailable(lookup);
		String ability = lookup.apply(IVariable.wrap("#ability#")).asString();
		if(!ability.isEmpty()){
			this.ability = Abilities.getRegistry().getValue(new ResourceLocation(ability));
		}else this.ability = null;
	}

	@Nullable @Override protected String getHeadText(){
		return "text.infernoreborn.ability.attribute";
	}
	@Override protected Multimap<Attribute, AttributeModifier> getAttributes(){
		return ability!=null ? ability.getAttributes() : ImmutableMultimap.of();
	}
}
