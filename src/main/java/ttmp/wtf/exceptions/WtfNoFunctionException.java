package ttmp.wtf.exceptions;

public class WtfNoFunctionException extends WtfEvalException{
	public WtfNoFunctionException(int line, String message){
		super(line, message);
	}
}
