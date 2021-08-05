package ttmp.cafscript.exceptions;

public final class CafCompileException extends CafException{
	public static CafCompileException create(String script, int sourcePosition, String message){
		int line = 1, column = 1;
		for(int i = 0; i<sourcePosition; i++){
			if(i>=script.length()) break;
			if(script.charAt(i)=='\n'){
				line++;
				column = 1;
			}else column++;
		}
		return new CafCompileException(sourcePosition, line, column, "Line "+line+", Column "+column+": "+message);
	}

	public final int position;
	public final int line;
	public final int column;

	private CafCompileException(int position, int line, int column, String message){
		super(message);
		this.position = position;
		this.line = line;
		this.column = column;
	}
}
