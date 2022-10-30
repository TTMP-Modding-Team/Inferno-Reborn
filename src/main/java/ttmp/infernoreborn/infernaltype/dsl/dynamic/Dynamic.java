package ttmp.infernoreborn.infernaltype.dsl.dynamic;

import among.ReportHandler;
import among.TypeFlags;
import among.construct.ConditionedConstructor;
import among.construct.ConstructRule;
import among.construct.Constructor;
import among.construct.Constructors;
import among.obj.Among;
import com.google.common.collect.Maps;
import ttmp.infernoreborn.infernaltype.InfernalGenContext;
import ttmp.infernoreborn.infernaltype.dsl.SwitchDsl;
import ttmp.infernoreborn.infernaltype.dsl.SwitchDsl.Cases;

import javax.annotation.Nullable;
import java.util.Map;

import static ttmp.infernoreborn.infernaltype.dsl.dynamic.DynamicUtil.*;

public interface Dynamic{
	Object evaluate(@Nullable InfernalGenContext context);
	boolean matches(Class<?> type);

	default boolean isConstant(){
		return false;
	}

	default Dynamic collapseConstant(){
		return isConstant() ? constant(evaluate(null)) : this;
	}

	static void expectContext(@Nullable InfernalGenContext context){
		if(context==null) throw new UnsupportedOperationException("Not a constant");
	}

	static Dynamic constant(Object o){
		return new DynamicConstant(o);
	}

	static DynamicBool constantBool(boolean b){
		return b ? DynamicBool.Const.TRUE : DynamicBool.Const.FALSE;
	}
	static DynamicInt constantInt(int i){
		return new DynamicInt.Const(i);
	}
	static DynamicNumber constantNumber(double d){
		return new DynamicNumber.Const(d);
	}

	static DynamicInt intAdd(DynamicInt a, DynamicInt b){
		return new IntArithmeticOp(a, b, ArithmeticOps.ADD);
	}
	static DynamicNumber numAdd(DynamicNumber a, DynamicNumber b){
		return new NumArithmeticOp(a, b, ArithmeticOps.ADD);
	}
	static DynamicInt intSubtract(DynamicInt a, DynamicInt b){
		return new IntArithmeticOp(a, b, ArithmeticOps.SUB);
	}
	static DynamicNumber numSubtract(DynamicNumber a, DynamicNumber b){
		return new NumArithmeticOp(a, b, ArithmeticOps.SUB);
	}
	static DynamicInt intMultiply(DynamicInt a, DynamicInt b){
		return new IntArithmeticOp(a, b, ArithmeticOps.MUL);
	}
	static DynamicNumber numMultiply(DynamicNumber a, DynamicNumber b){
		return new NumArithmeticOp(a, b, ArithmeticOps.MUL);
	}
	static DynamicInt intDivide(DynamicInt a, DynamicInt b){
		return new IntArithmeticOp(a, b, ArithmeticOps.DIV);
	}
	static DynamicNumber numDivide(DynamicNumber a, DynamicNumber b){
		return new NumArithmeticOp(a, b, ArithmeticOps.DIV);
	}
	static DynamicInt intPow(DynamicInt a, DynamicInt b){
		return new IntArithmeticOp(a, b, ArithmeticOps.POW);
	}
	static DynamicNumber numPow(DynamicNumber a, DynamicNumber b){
		return new NumArithmeticOp(a, b, ArithmeticOps.POW);
	}

	static DynamicInt intRange(DynamicInt a, DynamicInt b){
		return new IntRange(a, b);
	}

	static DynamicBool intGt(DynamicInt a, DynamicInt b){
		return new IntCompOp(a, b, CompOps.GT);
	}
	static DynamicBool numGt(DynamicNumber a, DynamicNumber b){
		return new NumCompOp(a, b, CompOps.GT);
	}
	static DynamicBool intLt(DynamicInt a, DynamicInt b){
		return new IntCompOp(a, b, CompOps.LT);
	}
	static DynamicBool numLt(DynamicNumber a, DynamicNumber b){
		return new NumCompOp(a, b, CompOps.LT);
	}
	static DynamicBool intGtEq(DynamicInt a, DynamicInt b){
		return new IntCompOp(a, b, CompOps.GTEQ);
	}
	static DynamicBool numGtEq(DynamicNumber a, DynamicNumber b){
		return new NumCompOp(a, b, CompOps.GTEQ);
	}
	static DynamicBool intLtEq(DynamicInt a, DynamicInt b){
		return new IntCompOp(a, b, CompOps.LTEQ);
	}
	static DynamicBool numLtEq(DynamicNumber a, DynamicNumber b){
		return new NumCompOp(a, b, CompOps.LTEQ);
	}

	static DynamicBool eq(Dynamic a, Dynamic b){
		return new EqOp(a, b, true);
	}
	static DynamicBool neq(Dynamic a, Dynamic b){
		return new EqOp(a, b, false);
	}
	static DynamicBool intEq(DynamicInt a, DynamicInt b){
		return new IntEqOp(a, b, true);
	}
	static DynamicBool intNeq(DynamicInt a, DynamicInt b){
		return new IntEqOp(a, b, false);
	}
	static DynamicBool numEq(DynamicNumber a, DynamicNumber b){
		return new NumEqOp(a, b, true);
	}
	static DynamicBool numNeq(DynamicNumber a, DynamicNumber b){
		return new NumEqOp(a, b, false);
	}

	static DynamicBool boolAnd(DynamicBool a, DynamicBool b){
		return boolAnd(a, b, true);
	}
	static DynamicBool boolAndNoShortCircuit(DynamicBool a, DynamicBool b){
		return boolAnd(a, b, false);
	}
	static DynamicBool boolAnd(DynamicBool a, DynamicBool b, boolean shortCircuit){
		return new BoolOp(a, b, shortCircuit ? BoolOps.AND_SS : BoolOps.AND);
	}
	static DynamicBool boolOr(DynamicBool a, DynamicBool b){
		return boolOr(a, b, true);
	}
	static DynamicBool boolOrNoShortCircuit(DynamicBool a, DynamicBool b){
		return boolOr(a, b, false);
	}
	static DynamicBool boolOr(DynamicBool a, DynamicBool b, boolean shortCircuit){
		return new BoolOp(a, b, shortCircuit ? BoolOps.OR_SS : BoolOps.OR);
	}
	static DynamicBool boolEq(DynamicBool a, DynamicBool b){
		return new BoolOp(a, b, BoolOps.EQ);
	}
	static DynamicBool boolNeq(DynamicBool a, DynamicBool b){
		return new BoolOp(a, b, BoolOps.NEQ);
	}

	static DynamicInt intNegate(DynamicInt i){
		return new IntNegate(i);
	}
	static DynamicNumber numNegate(DynamicNumber num){
		return new NumNegate(num);
	}
	static DynamicBool boolNot(DynamicBool bool){
		return new BoolNot(bool);
	}

	static Dynamic ifThen(DynamicBool condition, Dynamic ifThen, Dynamic elseThen){
		return new IfOp(condition, ifThen, elseThen);
	}
	static DynamicInt intIf(DynamicBool condition, DynamicInt ifThen, DynamicInt elseThen){
		return new IntIfOp(condition, ifThen, elseThen);
	}
	static DynamicNumber numIf(DynamicBool condition, DynamicNumber ifThen, DynamicNumber elseThen){
		return new NumIfOp(condition, ifThen, elseThen);
	}
	static DynamicBool boolIf(DynamicBool condition, DynamicBool ifThen, DynamicBool elseThen){
		return new BoolIfOp(condition, ifThen, elseThen);
	}

	@Nullable static Dynamic switchOp(Dynamic value, Map<String, Dynamic> cases, @Nullable Dynamic defaultCase, @Nullable ReportHandler reportHandler){
		Cases<Dynamic> c = SwitchDsl.caseFor(value, cases, defaultCase, reportHandler, true);
		if(c==null) return null;
		if(defaultCase==null||(defaultCase instanceof DynamicInt)){
			Cases<DynamicInt> c2 = c.tryCastTo(DynamicInt.class);
			if(c2!=null) return new IntSwitchOp(value, c2, (DynamicInt)defaultCase);
		}
		if(defaultCase==null||(defaultCase instanceof DynamicNumber)){
			Cases<DynamicNumber> c2 = c.tryCastTo(DynamicNumber.class);
			if(c2!=null) return new NumSwitchOp(value, c2, (DynamicNumber)defaultCase);
		}
		if(defaultCase==null||(defaultCase instanceof DynamicBool)){
			Cases<DynamicBool> c2 = c.tryCastTo(DynamicBool.class);
			if(c2!=null) return new BoolSwitchOp(value, c2, (DynamicBool)defaultCase);
		}
		return new SwitchOp(value, c, defaultCase);
	}

	ConstructRule<Dynamic> DYNAMIC = ConstructRule.make(_b -> _b
			.primitive((p, reportHandler) -> {
				Boolean bool = Constructors.BOOL.construct(p, null);
				if(bool!=null) return constantBool(bool);
				Integer i = Constructors.INT.construct(p, null);
				if(i!=null) return constantInt(i);
				Double num = Constructors.DOUBLE.construct(p, null);
				if(num!=null) return constantNumber(num);
				return constant(p.getValue());
			})
			.list("||", boolBiOp(Dynamic::boolOr))
			.list("&&", boolBiOp(Dynamic::boolAnd))
			.list("|", boolBiOp(Dynamic::boolOrNoShortCircuit))
			.list("&", boolBiOp(Dynamic::boolAndNoShortCircuit))
			.list(new String[]{"==", "="}, biOp(Dynamic::intEq, Dynamic::numEq, Dynamic::boolEq, Dynamic::eq))
			.list("!=", biOp(Dynamic::intNeq, Dynamic::numNeq, Dynamic::boolNeq, Dynamic::neq))
			.list(">", numberBiOp(Dynamic::intGt, Dynamic::numGt))
			.list("<", numberBiOp(Dynamic::intLt, Dynamic::numLt))
			.list(">=", numberBiOp(Dynamic::intGtEq, Dynamic::numGtEq))
			.list("<=", numberBiOp(Dynamic::intLtEq, Dynamic::numLtEq))
			.list("+", numberBiOp(Dynamic::intAdd, Dynamic::numAdd, __b -> __b
					.addStrictUnary((in, reportHandler) -> Dynamic.DYNAMIC_INT.construct(in, reportHandler))))
			.list("-", numberBiOp(Dynamic::intSubtract, Dynamic::numSubtract, __b -> __b
					.add(c -> c.size(1), (l, r) -> {
						DynamicNumber n = Dynamic.DYNAMIC_NUMBER.construct(l, r);
						return n==null ? null :
								n instanceof DynamicInt ? intNegate((DynamicInt)n) :
										numNegate(n);
					})))
			.list("*", numberBiOp(Dynamic::intMultiply, Dynamic::numMultiply))
			.list("/", numberBiOp(Dynamic::intDivide, Dynamic::numDivide))
			.list(new String[]{"^", "**"}, numberBiOp(Dynamic::intPow, Dynamic::numPow))
			.list("!", ConditionedConstructor.listCondition(c -> c.minSize(1), (l, r) -> {
				DynamicBool b = Dynamic.DYNAMIC_BOOL.construct(l.get(0), r);
				return b==null ? null : boolNot(b);
			}))
			.list("range", intBiOp(Dynamic::intRange))
			.list("if", ConditionedConstructor.listCondition(c -> c.minSize(3), (l, r) -> {
				DynamicBool condition = Dynamic.DYNAMIC_BOOL.construct(l.get(0), r);
				Dynamic ifThen = Dynamic.DYNAMIC.construct(l.get(0), r);
				Dynamic elseThen = Dynamic.DYNAMIC.construct(l.get(0), r);
				if(condition==null||ifThen==null||elseThen==null) return null;
				if(ifThen instanceof DynamicInt&&elseThen instanceof DynamicInt)
					return intIf(condition, (DynamicInt)ifThen, (DynamicInt)elseThen);
				if(ifThen instanceof DynamicNumber&&elseThen instanceof DynamicNumber)
					return numIf(condition, (DynamicNumber)ifThen, (DynamicNumber)elseThen);
				if(ifThen instanceof DynamicBool&&elseThen instanceof DynamicBool)
					return boolIf(condition, (DynamicBool)ifThen, (DynamicBool)elseThen);
				return ifThen(condition, ifThen, elseThen);
			}))
			.list("switch", ConditionedConstructor.listCondition(c -> c
							.minSize(2)
							.elementType(1, TypeFlags.OBJECT),
					(l, r) -> {
						Dynamic value = Dynamic.DYNAMIC.construct(l.get(0), r);
						if(value==null) return null;
						Map<String, Dynamic> cases = Maps.transformEntries(l.get(1).asObj().properties(),
								(k, v) -> v!=null ? Dynamic.DYNAMIC.construct(v, r) : null);
						for(Dynamic d : cases.values()) if(d==null) return null;
						Dynamic defaultValue;
						if(l.size()>=3){
							defaultValue = Dynamic.DYNAMIC.construct(l.get(2), r);
							if(defaultValue==null) return null;
						}else defaultValue = null;
						return switchOp(value, cases, defaultValue, r!=null ? r.reportAt(l.get(1).sourcePosition()) : null);
					}))
			.errorMessage("Invalid expression")
	);
	Constructor<Among, DynamicBool> DYNAMIC_BOOL = (among, reportHandler) -> {
		Dynamic d = DYNAMIC.construct(among, reportHandler);
		if(d==null||d instanceof DynamicBool) return (DynamicBool)d;
		if(reportHandler!=null) reportHandler.reportError("Expected boolean", among.sourcePosition());
		return null;
	};
	Constructor<Among, DynamicInt> DYNAMIC_INT = (among, reportHandler) -> {
		Dynamic d = DYNAMIC.construct(among, reportHandler);
		if(d==null||d instanceof DynamicInt) return (DynamicInt)d;
		if(reportHandler!=null) reportHandler.reportError("Expected int", among.sourcePosition());
		return null;
	};
	Constructor<Among, DynamicNumber> DYNAMIC_NUMBER = (among, reportHandler) -> {
		Dynamic d = DYNAMIC.construct(among, reportHandler);
		if(d==null||d instanceof DynamicNumber) return (DynamicNumber)d;
		if(reportHandler!=null) reportHandler.reportError("Expected number", among.sourcePosition());
		return null;
	};

	static void loadClass(){}
}
