package ttmp.wtf.exceptions;

public class WtfException extends RuntimeException{
	public WtfException(){
		super();
	}
	public WtfException(String message){
		super(message);
	}
	public WtfException(String message, Throwable cause){
		super(message, cause);
	}
	public WtfException(Throwable cause){
		super(cause);
	}
	protected WtfException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace){
		super(message, cause, enableSuppression, writableStackTrace);
	}
}
