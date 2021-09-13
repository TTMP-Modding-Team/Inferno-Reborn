package ttmp.wtf.exceptions;

public class WtfNoPropertyException extends WtfEvalException{
	public WtfNoPropertyException(int line, String property){
		super(line, property);
	}
}
