package ttmp.cafscript;

public interface ErrorHandler<E>{
	void handle(E error, CafScriptEngine engine, String script);
}
