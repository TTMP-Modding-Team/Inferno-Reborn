import net.minecraft.util.ResourceLocation;
import org.apache.commons.io.FileUtils;
import org.junit.jupiter.api.DynamicTest;
import org.junit.jupiter.api.TestFactory;
import org.junit.jupiter.api.function.Executable;
import ttmp.cafscript.CafScriptEngine;
import ttmp.cafscript.definitions.initializer.TestInitializer;
import ttmp.cafscript.internal.CafDebugEngine;
import ttmp.cafscript.internal.CafInterpreter;

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

		CafDebugEngine engine = new CafDebugEngine();

		tests.add(DynamicTest.dynamicTest("Compile Test 1", compileTest(engine, "compile_test/1")));
		tests.add(DynamicTest.dynamicTest("Compile Test 2", compileTest(engine, "compile_test/2")));

		tests.add(DynamicTest.dynamicTest("Operation Test: Constants",
				operationTest(engine, "operation_test/constants",
						new ResourceLocation("test:test"),
						new ResourceLocation("spiders:spiders"),
						0x123456,
						0xC8C8C8,
						123456576890.0,
						3.141592,
						true,
						false,
						0.0)));
		tests.add(DynamicTest.dynamicTest("Operation Test: Arithmetics",
				operationTest(engine, "operation_test/arithmetics",
						3.0, -1.0, 2.0, 1.0/2)));

		return tests;
	}

	private Executable compileTest(CafScriptEngine engine, String filename){
		return () -> engine.compile(readScript(filename));
	}

	private Executable operationTest(CafScriptEngine engine, String filename, Object... expectedValues){
		return () -> new CafInterpreter(engine, engine.compile(readScript(filename)), new TestInitializer(expectedValues)).execute();
	}

	private String readScript(String filename) throws IOException{
		return FileUtils.readFileToString(new File("src/test/resources/scripts/"+filename+".caf"), StandardCharsets.UTF_8);
	}
}
