package ttmp.wtf.exceptions;

import ttmp.wtf.obj.LineAndColumn;

import java.util.function.Consumer;

public final class WtfCompileException extends WtfException{
	public final int position;

	public WtfCompileException(int position, String message){
		super(message);
		this.position = position;
	}

	public void prettyPrint(String script){
		prettyPrint(script, System.out::println);
	}
	public void prettyPrint(String script, Consumer<String> printer){
		LineAndColumn lineAndColumn = LineAndColumn.calculate(script, position);
		printer.accept(position+"("+lineAndColumn+"): "+getMessage());
		String lineString = String.valueOf(lineAndColumn.line);

		int printEnd = Math.min(script.length(), position+15);
		int idx = script.indexOf('\n', position);
		if(idx>=0&&idx<printEnd) printEnd = idx;
		idx = script.indexOf('\r', position);
		if(idx>=0&&idx<printEnd) printEnd = idx;

		printer.accept(position-lineAndColumn.column+1 >= printEnd ?
				" "+lineString+" | " :
				" "+lineString+" | "+script.substring(position-lineAndColumn.column+1, printEnd));
		StringBuilder stb = new StringBuilder();
		for(int i = 1+lineString.length()+2+lineAndColumn.column; i>0; i--)
			stb.append(' ');
		printer.accept(stb.append('^').toString());
	}
}
