package ttmp.wtf.obj;

public final class LineAndColumn{
	public static LineAndColumn calculate(String script, int position){
		int line = 1, lineStart = 0;
		while(true){
			int lineStartCache = script.indexOf('\n', lineStart);
			if(lineStartCache==-1||lineStartCache>=position) break;
			line++;
			lineStart = lineStartCache+1;
		}
		return new LineAndColumn(line, position-lineStart+1);
	}

	public final int line, column;

	public LineAndColumn(int line, int column){
		this.line = line;
		this.column = column;
	}

	@Override public String toString(){
		return "Line "+line+", Column "+column;
	}
}
