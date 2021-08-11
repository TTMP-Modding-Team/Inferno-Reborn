package ttmp.cafscript.exceptions;

public class CafNoPropertyException extends CafEvalException{
	public CafNoPropertyException(int line, String property){
		super(line, property);
	}
}
