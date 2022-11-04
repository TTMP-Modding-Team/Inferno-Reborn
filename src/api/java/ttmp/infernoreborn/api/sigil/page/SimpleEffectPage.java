package ttmp.infernoreborn.api.sigil.page;

import com.google.common.collect.ImmutableList;
import net.minecraft.util.text.ITextComponent;

import java.util.List;

public class SimpleEffectPage implements SigilBookEntry.EffectPage{
	private final List<ITextComponent> text;

	public SimpleEffectPage(List<ITextComponent> text){
		this.text = ImmutableList.copyOf(text);
	}

	@Override public List<ITextComponent> getText(){
		return text;
	}
}
