import org.apache.commons.io.FileUtils;
import org.junit.jupiter.api.DynamicTest;
import org.junit.jupiter.api.TestFactory;
import org.junit.jupiter.api.function.Executable;
import ttmp.cafscript.CafScriptEngine;

import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class CafScriptTest{
	@TestFactory
	public Collection<DynamicTest> generateTests(){
		List<DynamicTest> tests = new ArrayList<>();

		tests.add(DynamicTest.dynamicTest("Compile Test 1", compileTest(new CafScriptEngine(), "compile_test/1")));
		tests.add(DynamicTest.dynamicTest("Compile Test 2", compileTest(new CafScriptEngine(), "compile_test/2")));

		return tests;
	}

	private Executable compileTest(CafScriptEngine engine, String filename){
		return () -> engine.debugCompile(readScript(filename), System.out::println);
	}

	private String readScript(String filename) throws IOException{
		return FileUtils.readFileToString(new File("src/test/resources/scripts/"+filename+".caf"), StandardCharsets.UTF_8);
	}
}
