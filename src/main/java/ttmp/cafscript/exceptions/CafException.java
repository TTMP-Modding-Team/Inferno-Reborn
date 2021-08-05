package ttmp.cafscript.exceptions;

public class CafException extends RuntimeException{
	public CafException(){
		super();
	}
	public CafException(String message){
		super(message);
	}
	public CafException(String message, Throwable cause){
		super(message, cause);
	}
	public CafException(Throwable cause){
		super(cause);
	}
	protected CafException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace){
		super(message, cause, enableSuppression, writableStackTrace);
	}
}
