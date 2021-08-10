package ttmp.cafscript.exceptions;

public final class CafCompileException extends CafException{
	public final int position;

	public CafCompileException(int position, String message){
		super(message);
		this.position = position;
	}

	public LineAndColumn calculateLineAndColumn(String script){
		int line = 1, column = 1;
		for(int i = 0; i<position; i++){
			if(i>=script.length()) break;
			if(script.charAt(i)=='\n'){
				line++;
				column = 1;
			}else column++;
		}
		return new LineAndColumn(line, column);
	}

	public static final class LineAndColumn {
		public final int line, column;

		public LineAndColumn(int line, int column){
			this.line = line;
			this.column = column;
		}

		@Override public String toString(){
			return "Line "+line+", Column "+column;
		}
	}
}
