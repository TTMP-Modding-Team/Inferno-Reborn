package ttmp.wtf.exceptions;

public class WtfEvalException extends WtfException{
	public final int line;

	public WtfEvalException(int line, String message){
		super(message);
		this.line = line;
	}
}
