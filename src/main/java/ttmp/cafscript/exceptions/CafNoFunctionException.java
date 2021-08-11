package ttmp.cafscript.exceptions;

public class CafNoFunctionException extends CafEvalException{
	public CafNoFunctionException(int line, String message){
		super(line, message);
	}
}
