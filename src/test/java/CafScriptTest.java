import org.apache.commons.io.FileUtils;
import org.junit.jupiter.api.DynamicTest;
import org.junit.jupiter.api.TestFactory;
import ttmp.cafscript.CafScriptEngine;

import java.io.File;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Objects;

public class CafScriptTest{
	@TestFactory
	public Collection<DynamicTest> generateTests(){
		CafScriptEngine script = new CafScriptEngine();

		List<DynamicTest> tests = new ArrayList<>();

		File file = new File("src/test/resources/scripts");
		for(File f : Objects.requireNonNull(file.listFiles())){
			if(f.isFile()){
				tests.add(DynamicTest.dynamicTest(f.getName(), () -> {
					script.debugCompile(FileUtils.readFileToString(f, StandardCharsets.UTF_8), System.out::println);
				}));
			}
		}
		return tests;
	}
}
