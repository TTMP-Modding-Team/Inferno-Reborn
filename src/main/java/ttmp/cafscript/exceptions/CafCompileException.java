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
			char c = script.charAt(i);
			if(c!='\n'){
				if(c=='\r'){
					if(i+1<position&&i+1<script.length()&&script.charAt(i+1)=='\n') i++; // very pretty code, thanks tictim
				}else{
					column++;
					continue;
				}
			}
			line++;
			column = 1;
		}
		return new LineAndColumn(line, column);
	}

	public void prettyPrint(String script){
		CafCompileException.LineAndColumn lineAndColumn = calculateLineAndColumn(script);
		System.out.println(position+"("+lineAndColumn+"): "+getMessage());
		String lineString = String.valueOf(lineAndColumn.line);

		int printEnd = Math.min(script.length(), position+15);
		int idx = script.indexOf('\n', position);
		if(idx>=0&&idx<printEnd) printEnd = idx;
		idx = script.indexOf('\r', position);
		if(idx>=0&&idx<printEnd) printEnd = idx;

		System.out.println(" "+lineString+" | "+script.substring(position-lineAndColumn.column+1, printEnd));
		for(int i = 1+lineString.length()+2+lineAndColumn.column; i>0; i--)
			System.out.print(" ");
		System.out.println("^");
	}

	public static final class LineAndColumn{
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
