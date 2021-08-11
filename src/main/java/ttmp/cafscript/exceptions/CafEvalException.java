package ttmp.cafscript.exceptions;

public class CafEvalException extends CafException{
	public final int line;

	public CafEvalException(int line, String message){
		super(message);
		this.line = line;
	}
}
