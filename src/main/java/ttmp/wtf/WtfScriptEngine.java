package ttmp.wtf;

import ttmp.wtf.exceptions.WtfCompileException;
import ttmp.wtf.internal.WtfConstantPool;
import ttmp.wtf.internal.compiler.WtfCompiler;

import javax.annotation.Nullable;
import java.util.Random;
import java.util.function.Consumer;

/**
 * It's an engine.
 */
public class WtfScriptEngine{
	private static final Random DEFAULT_RANDOM = new Random();

	private final WtfConstantPool constantPool = new WtfConstantPool();

	@Nullable private Consumer<Object> printer;
	@Nullable private Random random;

	public WtfConstantPool getConstantPool(){
		return constantPool;
	}

	public WtfScriptEngine setDebugPrinter(Consumer<Object> printer){
		this.printer = printer;
		return this;
	}
	public WtfScriptEngine setRandom(Random random){
		this.random = random;
		return this;
	}

	@Nullable public WtfScript tryCompile(String script, ErrorHandler<WtfCompileException> compileExceptionHandler){
		return tryCompile(script, CompileContext.createDefault(this), compileExceptionHandler);
	}

	@Nullable public WtfScript tryCompile(String script, CompileContext context, ErrorHandler<WtfCompileException> compileExceptionHandler){
		try{
			return compile(script, context);
		}catch(WtfCompileException ex){
			compileExceptionHandler.handle(ex, this, script);
			return null;
		}
	}

	/**
	 * @throws WtfCompileException on compile error
	 */
	public WtfScript compile(String script){
		return compile(script, CompileContext.createDefault(this));
	}

	/**
	 * @throws WtfCompileException on compile error
	 */
	public WtfScript compile(String script, CompileContext context){
		return new WtfCompiler(script, context).parseAndCompile();
	}

	/**
	 * Debug print
	 */
	public void debug(@Nullable Object object){
		if(printer!=null) printer.accept(object);
		else System.out.println(object);
	}
	public Random getRandom(){
		return random!=null ? random : DEFAULT_RANDOM;
	}
}
