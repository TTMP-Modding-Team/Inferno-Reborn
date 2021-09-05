package ttmp.wtf.exceptions;

public class WtfEvalException extends WtfException{
	public final int line;

	public WtfEvalException(int line, String message){
		super("Line "+line+": "+message);
		this.line = line;
	}

	public WtfEvalException(int line, String message, Throwable cause){
		super("Line "+line+": "+message, cause);
		this.line = line;
	}
}
