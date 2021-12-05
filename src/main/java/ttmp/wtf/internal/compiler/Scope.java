package ttmp.wtf.internal.compiler;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;

public final class Scope{
	public final List<Block> blocks = new ArrayList<>();
	public final Set<String> outerLocals = new HashSet<>();

	public Scope(){
		pushBlock();
	}

	public Block currentBlock(){
		return blocks.get(blocks.size()-1);
	}

	public Block pushBlock(){
		Block block = new Block();
		blocks.add(block);
		return block;
	}

	public Block popBlock(){
		if(blocks.isEmpty())
			throw new IllegalStateException("Trying to pop root block for scope");
		return blocks.remove(blocks.size()-1);
	}

	public boolean addLocal(String name, @Nullable Expression definition){
		return currentBlock().locals.putIfAbsent(name, new LocalDefinition(definition))!=null;
	}

	@Nullable public LocalDefinition resolveLocal(String name){
		for(int i = blocks.size()-1; i>=0; i--){
			Block block = blocks.get(i);
			LocalDefinition localDefinition = block.locals.get(name);
			if(localDefinition!=null) return localDefinition;
		}
		return null;
	}

	public static final class Block{
		public final Map<String, LocalDefinition> locals = new HashMap<>();
	}

	public static final class LocalDefinition{
		@Nullable public final Expression definition;
		public boolean referenced;

		public LocalDefinition(@Nullable Expression definition){
			this.definition = definition;
		}

		public boolean isConstant(){
			return definition!=null&&definition.isConstant();
		}
		@Nullable public Object getConstantObject(){
			return Objects.requireNonNull(definition).getConstantObject();
		}
	}
}
