package test;


import org.apache.commons.io.FileUtils;
import org.junit.jupiter.api.Test;
import ttmp.wtf.obj.LineAndColumn;

import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;

public class LineAndColumnTest{
	@Test
	public void printLineAndColumn() throws IOException{
		for(int i = 1; i<=3; i++){
			String s = FileUtils.readFileToString(new File("src/test/resources/scripts/line_and_column_test_"+i+".wtfs"), StandardCharsets.UTF_8);
			System.out.println("FILE "+i);
			for(int j = 0; j<=s.length(); j++){
				System.out.print(j);
				if(j<s.length()){
					char c = s.charAt(j);
					switch(c){
						case '\n':
							System.out.print(" (\\n) ");
							break;
						case '\r':
							System.out.print(" (\\r) ");
							break;
						default:
							System.out.print(" ('"+c+"')");
					}
				}
				System.out.println(" : "+LineAndColumn.calculate(s, j));
			}
		}
	}
}
