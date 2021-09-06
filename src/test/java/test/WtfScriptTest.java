package test;

import net.minecraft.util.ResourceLocation;
import org.apache.commons.io.FileUtils;
import org.junit.jupiter.api.DynamicTest;
import org.junit.jupiter.api.TestFactory;
import org.junit.jupiter.api.function.Executable;
import ttmp.wtf.CompileContext;
import ttmp.wtf.EvalContext;
import ttmp.wtf.WtfScript;
import ttmp.wtf.WtfScriptEngine;
import ttmp.wtf.definitions.initializer.AssertInitializer;
import ttmp.wtf.definitions.initializer.Initializer;
import ttmp.wtf.definitions.initializer.TestInitializer;
import ttmp.wtf.exceptions.WtfCompileException;
import ttmp.wtf.exceptions.WtfEvalException;
import ttmp.wtf.WtfDebugEngine;
import ttmp.wtf.obj.RGB;

import javax.annotation.Nullable;
import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Objects;
import java.util.function.Supplier;

public class WtfScriptTest{
	@TestFactory
	public Collection<DynamicTest> generateTests(){
		List<DynamicTest> tests = new ArrayList<>();

		WtfDebugEngine engine = new WtfDebugEngine(false, false);

		tests.add(DynamicTest.dynamicTest("Compile Test 1", compileTest(engine, "compile_test/1")));
		tests.add(DynamicTest.dynamicTest("Compile Test 2", compileTest(engine, "compile_test/2")));
		tests.add(DynamicTest.dynamicTest("Compile Test 3", compileTest(engine, "compile_test/3")));
		tests.add(DynamicTest.dynamicTest("Compile Test 4", compileTest(engine, "compile_test/4")));

		tests.add(DynamicTest.dynamicTest("Run Test: Debug", runTest(engine, "run_test/debug")));
		tests.add(DynamicTest.dynamicTest("Run Test: Defines", runTest(engine, "run_test/defines")));
		tests.add(DynamicTest.dynamicTest("Run Test: Empty", runTest(engine, "run_test/empty")));
		tests.add(DynamicTest.dynamicTest("Run Test: Init", runTest(engine, "run_test/init", () -> PrintInitializer.INSTANCE)));
		tests.add(DynamicTest.dynamicTest("Run Test: Init 2", runTest(engine, "run_test/init2")));
		tests.add(DynamicTest.dynamicTest("Run Test: Loopers", runTest(engine, "run_test/loopers")));
		tests.add(DynamicTest.dynamicTest("Run Test: Loopies", runTest(engine, "run_test/loopies")));
		tests.add(DynamicTest.dynamicTest("Run Test: Negation", runTest(engine, "run_test/negation")));
		tests.add(DynamicTest.dynamicTest("Run Test: Random", runTest(engine, "run_test/random")));
		tests.add(DynamicTest.dynamicTest("Run Test: Ternary", runTest(engine, "run_test/ternary")));
		tests.add(DynamicTest.dynamicTest("Run Test: What the fuck did you just fucking say about me, you little bitch?", runTest(engine, "run_test/whatthefuckdidyousaytome")));

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
						3, -1, 2, 1/2, .5, .5)));
		tests.add(DynamicTest.dynamicTest("Operation Test: Strings",
				operationTest(engine, "operation_test/strings",
						"Test",
						"Hello world I guess",
						"ABCDEFGabcdefg12345678901234568058908342",
						"Yeah, that's some good test suite \uD83D\uDE0E",
						"Press L if you agree",
						"L",
						".",
						"",
						" ",
						"\t",
						"\n",
						"\t\n\t",
						"\"\"",
						"Should I write another test case with exact same script but using ' instead of \"?",
						"Nah")));
		{
			Object o1 = new Object(), o2 = new Object();
			tests.add(DynamicTest.dynamicTest("Operation Test: Constants 2: Electric Boogaloo",
					operationTest(engine, "operation_test/constants_2", CompileContext.builder()
									.addStaticConstant("S1", 1.0)
									.addStaticConstant("S2", "Hello World")
									.addStaticConstant("S3", new ResourceLocation("grass"))
									.addStaticConstant("S4", o1)
									.addDynamicConstant("C1", Double.class)
									.addDynamicConstant("C2", String.class)
									.addDynamicConstant("C3", ResourceLocation.class)
									.addDynamicConstant("C4", Object.class)
									.build(),
							s -> {
								switch(s){
									case "C1":
										return 3.0;
									case "C2":
										return "ASDF";
									case "C3":
										return new ResourceLocation("zinfernoreborn", "abilities");
									case "C4":
										return o2;
									default:
										return null;
								}
							},
							1,
							"Hello World",
							new ResourceLocation("grass"),
							o1,
							3,
							"ASDF",
							new ResourceLocation("zinfernoreborn", "abilities"),
							o2)));
		}

		tests.add(DynamicTest.dynamicTest("Assertion Test: Operation Optimization",
				assertTest(engine, "assertion_test/operation_optimization")));
		tests.add(DynamicTest.dynamicTest("Assertion Test: Operation Optimization #2",
				assertTest(engine, "assertion_test/operation_optimization_2")));

		tests.add(DynamicTest.dynamicTest("Compile Error Test: Lex - Garbage", compileErrorTest(engine, "compile_error_test/lex_garbage")));
		tests.add(DynamicTest.dynamicTest("Compile Error Test: Lex - Invalid Chars", compileErrorTest(engine, "compile_error_test/lex_invalid_chars")));
		tests.add(DynamicTest.dynamicTest("Compile Error Test: Parse - Missing Brace", compileErrorTest(engine, "compile_error_test/parse_missing_brace")));
		tests.add(DynamicTest.dynamicTest("Compile Error Test: Parse - Wrong Type #1", compileErrorTest(engine, "compile_error_test/parse_wrong_type_1")));
		tests.add(DynamicTest.dynamicTest("Compile Error Test: Parse - Wrong Type #2", compileErrorTest(engine, "compile_error_test/parse_wrong_type_2")));
		tests.add(DynamicTest.dynamicTest("Compile Error Test: Parse - Wrong Type #3", compileErrorTest(engine, "compile_error_test/parse_wrong_type_3")));
		{
			CompileContext intTestCompileContext = CompileContext.builder()
					.addDynamicConstant("I5", Number.class)
					.addDynamicConstant("D5", Number.class)
					.build();
			EvalContext intTestEvalContext = s -> {
				switch(s){
					case "I5":
						return 5;
					case "D5":
						return 5.0;
					default:
						return null;
				}
			};
			tests.add(DynamicTest.dynamicTest("Error Test: Int #1", errorTest(engine,
					"error_test/int_1",
					intTestCompileContext,
					intTestEvalContext,
					null,
					3)));
			tests.add(DynamicTest.dynamicTest("Error Test: Int #2", errorTest(engine,
					"error_test/int_2",
					intTestCompileContext,
					intTestEvalContext,
					null,
					2)));
			tests.add(DynamicTest.dynamicTest("Error Test: Int #3", errorTest(engine,
					"error_test/int_3",
					intTestCompileContext,
					intTestEvalContext,
					null,
					2)));
		}


		return tests;
	}

	private Executable compileTest(WtfScriptEngine engine, String filename){
		return () -> engine.tryCompile(readScript(filename), this::logAndError);
	}

	private Executable runTest(WtfScriptEngine engine, String filename){
		return runTest(engine, filename, CompileContext.DEFAULT, EvalContext.DEFAULT);
	}
	private Executable runTest(WtfScriptEngine engine, String filename, @Nullable Supplier<Initializer<?>> initializer){
		return runTest(engine, filename, CompileContext.DEFAULT, EvalContext.DEFAULT, initializer);
	}
	private Executable runTest(WtfScriptEngine engine, String filename, CompileContext compileContext, EvalContext evalContext){
		return runTest(engine, filename, compileContext, evalContext, null);
	}
	private Executable runTest(WtfScriptEngine engine, String filename, CompileContext compileContext, EvalContext evalContext, @Nullable Supplier<Initializer<?>> initializer){
		return () -> {
			WtfScript script = engine.tryCompile(readScript(filename), compileContext, this::logAndError);
			long t = System.currentTimeMillis();
			if(initializer==null) engine.execute(Objects.requireNonNull(script), Initializer.EMPTY, evalContext);
			else engine.execute(Objects.requireNonNull(script), initializer.get(), evalContext);
			System.out.println("Execution took "+(System.currentTimeMillis()-t)+" ms.");
		};
	}

	private Executable operationTest(WtfScriptEngine engine, String filename, Object... expectedValues){
		return runTest(engine, filename, () -> new TestInitializer(expectedValues));
	}
	private Executable operationTest(WtfScriptEngine engine, String filename, CompileContext compileContext, EvalContext evalContext, Object... expectedValues){
		return runTest(engine, filename, compileContext, evalContext, () -> new TestInitializer(expectedValues));
	}

	private Executable assertTest(WtfScriptEngine engine, String filename){
		return runTest(engine, filename, AssertInitializer::new);
	}

	private Executable compileErrorTest(WtfScriptEngine engine, String filename){
		return () -> {
			String script = readScript(filename);
			try{
				engine.compile(script);
			}catch(WtfCompileException ex){
				ex.prettyPrint(script);
				ex.printStackTrace();
				System.out.println("Compilation successfully failed");
				return;
			}
			throw new RuntimeException("You're supposed to be dead?");
		};
	}

	private Executable errorTest(WtfScriptEngine engine, String filename, int expectedErrorLine){
		return errorTest(engine, filename, CompileContext.DEFAULT, EvalContext.DEFAULT, null, expectedErrorLine);
	}
	private Executable errorTest(WtfScriptEngine engine, String filename, CompileContext compileContext, EvalContext evalContext, @Nullable Supplier<Initializer<?>> initializer, int expectedErrorLine){
		return () -> {
			WtfScript script = engine.tryCompile(readScript(filename), compileContext, this::logAndError);
			Initializer<?> i = initializer!=null ? initializer.get() : Initializer.EMPTY;
			try{
				engine.execute(Objects.requireNonNull(script), i, evalContext);
			}catch(WtfEvalException ex){
				if(ex.line!=expectedErrorLine) throw new RuntimeException("Error occurred in line "+ex.line+" instead of line "+expectedErrorLine, ex);
				System.out.println("Evaluation successfully failed");
				ex.printStackTrace(System.out);
				return;
			}
			throw new RuntimeException("You're supposed to be dead?");
		};
	}

	private String readScript(String filename) throws IOException{
		long t = System.currentTimeMillis();
		String s = FileUtils.readFileToString(new File("src/test/resources/scripts/"+filename+".wtfs"), StandardCharsets.UTF_8);
		System.out.println("Reading script from file took "+(System.currentTimeMillis()-t)+" ms.");
		return s;
	}

	private void logAndError(WtfCompileException exception, WtfScriptEngine engine, String script){
		exception.prettyPrint(script);
		throw new RuntimeException("Compile failed", exception);
	}
}
