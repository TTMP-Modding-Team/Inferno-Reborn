package ttmp.infernoreborn.infernaltype.dsl.abilitygen;

import net.minecraft.util.ResourceLocation;
import ttmp.infernoreborn.InfernoReborn;
import ttmp.infernoreborn.api.ability.Ability;
import ttmp.infernoreborn.contents.Abilities;
import ttmp.infernoreborn.infernaltype.InfernalGenContext;

import javax.annotation.Nullable;
import java.util.Collections;
import java.util.List;

import static ttmp.infernoreborn.api.InfernoRebornApi.MODID;

public final class OneAbilityGen implements AbilityGen{
	private final ResourceLocation abilityId;

	public OneAbilityGen(ResourceLocation abilityId){
		this.abilityId = abilityId;
	}

	@Nullable private Ability getAbility(){
		return Abilities.getRegistry().getValue(abilityId);
	}

	@Override public List<Ability> generate(InfernalGenContext context){
		Ability ability = getAbility();
		return ability==null ? Collections.emptyList() : Collections.singletonList(ability);
	}

	@Override public void validate(){
		if(getAbility()==null)
			InfernoReborn.LOGGER.warn("Ability ID '{}' specified in the infernal generator is invalid", abilityId);
	}

	@Override public String toString(){
		return "\""+(abilityId.getNamespace().equals(MODID) ? abilityId.getPath() : abilityId)+"\"";
	}
}
