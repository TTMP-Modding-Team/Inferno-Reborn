package test;

import net.minecraft.util.ResourceLocation;
import org.apache.commons.io.FileUtils;
import org.junit.jupiter.api.DynamicTest;
import org.junit.jupiter.api.TestFactory;
import org.junit.jupiter.api.function.Executable;
import ttmp.cafscript.CafScript;
import ttmp.cafscript.CafScriptEngine;
import ttmp.cafscript.definitions.initializer.Initializer;
import ttmp.cafscript.definitions.initializer.TestInitializer;
import ttmp.cafscript.exceptions.CafCompileException;
import ttmp.cafscript.internal.CafDebugEngine;
import ttmp.cafscript.internal.CafInterpreter;
import ttmp.cafscript.obj.RGB;

import javax.annotation.Nullable;
import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.function.Supplier;

public class CafScriptTest{
	@TestFactory
	public Collection<DynamicTest> generateTests(){
		List<DynamicTest> tests = new ArrayList<>();

		CafDebugEngine engine = new CafDebugEngine();

		tests.add(DynamicTest.dynamicTest("Compile Test 1", compileTest(engine, "compile_test/1")));
		tests.add(DynamicTest.dynamicTest("Compile Test 2", compileTest(engine, "compile_test/2")));
		tests.add(DynamicTest.dynamicTest("Compile Test 3", compileTest(engine, "compile_test/3")));
		tests.add(DynamicTest.dynamicTest("Compile Test 4", compileTest(engine, "compile_test/4")));

		tests.add(DynamicTest.dynamicTest("Run Test: Debug", runTest(engine, "run_test/debug")));
		tests.add(DynamicTest.dynamicTest("Run Test: Defines", runTest(engine, "run_test/defines")));
		tests.add(DynamicTest.dynamicTest("Run Test: Empty", runTest(engine, "run_test/empty")));
		tests.add(DynamicTest.dynamicTest("Run Test: Init", runTest(engine, "run_test/init", () -> PrintInitializer.INSTANCE)));
		tests.add(DynamicTest.dynamicTest("Run Test: Init 2", runTest(engine, "run_test/init2")));

		tests.add(DynamicTest.dynamicTest("Operation Test: Constants",
				operationTest(engine, "operation_test/constants",
						new ResourceLocation("test:test"),
						new ResourceLocation("spiders:spiders"),
						new RGB(0x123456),
						new RGB(0xC8C8C8),
						123456576890.0,
						3.141592,
						true,
						false,
						0)));
		tests.add(DynamicTest.dynamicTest("Operation Test: Arithmetics",
				operationTest(engine, "operation_test/arithmetics",
						3, -1, 2, 1.0/2)));

		tests.add(DynamicTest.dynamicTest("Compile Error Test: Lex - Garbage", compileErrorTest(engine, "compile_error_test/lex_garbage")));
		tests.add(DynamicTest.dynamicTest("Compile Error Test: Lex - Invalid Chars", compileErrorTest(engine, "compile_error_test/lex_invalid_chars")));
		tests.add(DynamicTest.dynamicTest("Compile Error Test: Parse - Missing Brace", compileErrorTest(engine, "compile_error_test/parse_missing_brace")));
		tests.add(DynamicTest.dynamicTest("Compile Error Test: Parse - Wrong Type #1", compileErrorTest(engine, "compile_error_test/parse_wrong_type_1")));
		tests.add(DynamicTest.dynamicTest("Compile Error Test: Parse - Wrong Type #2", compileErrorTest(engine, "compile_error_test/parse_wrong_type_2")));

		return tests;
	}

	private Executable compileTest(CafScriptEngine engine, String filename){
		return () -> engine.compile(readScript(filename));
	}

	private Executable runTest(CafScriptEngine engine, String filename){
		return runTest(engine, filename, null);
	}
	private Executable runTest(CafScriptEngine engine, String filename, @Nullable Supplier<Initializer<?>> initializer){
		return () -> {
			CafScript script = engine.compile(readScript(filename));
			long t = System.currentTimeMillis();
			new CafInterpreter(engine, script).execute(initializer==null ? Initializer.EMPTY : initializer.get());
			System.out.println("Execution took "+(System.currentTimeMillis()-t)+" ms.");
		};
	}

	private Executable operationTest(CafScriptEngine engine, String filename, Object... expectedValues){
		return runTest(engine, filename, () -> new TestInitializer(expectedValues));
	}

	private Executable compileErrorTest(CafScriptEngine engine, String filename){
		return () -> {
			String script = readScript(filename);
			try{
				engine.compile(script);
			}catch(CafCompileException ex){
				ex.prettyPrint(script);
				ex.printStackTrace();
				System.out.println("Compilation successfully failed");
				return;
			}
			throw new RuntimeException("You're supposed to be dead?");
		};
	}

	private String readScript(String filename) throws IOException{
		long t = System.currentTimeMillis();
		String s = FileUtils.readFileToString(new File("src/test/resources/scripts/"+filename+".caf"), StandardCharsets.UTF_8);
		System.out.println("Reading script from file took "+(System.currentTimeMillis()-t)+" ms.");
		return s;
	}
}
