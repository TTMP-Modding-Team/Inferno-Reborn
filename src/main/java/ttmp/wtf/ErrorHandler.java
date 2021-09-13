package ttmp.wtf;

public interface ErrorHandler<E>{
	void handle(E error, WtfScriptEngine engine, String script);
}
