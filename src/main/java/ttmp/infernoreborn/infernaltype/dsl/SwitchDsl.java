package ttmp.infernoreborn.infernaltype.dsl;

import among.report.ReportHandler;
import it.unimi.dsi.fastutil.doubles.Double2ObjectMap;
import it.unimi.dsi.fastutil.doubles.Double2ObjectOpenHashMap;
import ttmp.infernoreborn.infernaltype.InfernalGenContext;
import ttmp.infernoreborn.infernaltype.dsl.dynamic.Dynamic;
import ttmp.infernoreborn.infernaltype.dsl.dynamic.DynamicBool;
import ttmp.infernoreborn.infernaltype.dsl.dynamic.DynamicNumber;

import javax.annotation.Nullable;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;

public abstract class SwitchDsl<V>{
	@Nullable public static <V> Cases<V> caseFor(Dynamic value, Map<String, V> cases, @Nullable V defaultCase, @Nullable ReportHandler reportHandler, boolean exhaustiveSwitch){
		if(value instanceof DynamicNumber){
			if(exhaustiveSwitch&&defaultCase==null){
				reportNonExhaustiveSwitchWithNoDefault(reportHandler);
				return null;
			}
			return new NumCases<>(cases, reportHandler);
		}else if(value instanceof DynamicBool){
			Cases<V> c = new BoolCases<>(cases, reportHandler);
			if(exhaustiveSwitch&&c.cases().size()<2&&defaultCase==null){
				reportNonExhaustiveSwitchWithNoDefault(reportHandler);
				return null;
			}
			return c;
		}else{
			if(exhaustiveSwitch&&defaultCase==null){
				reportNonExhaustiveSwitchWithNoDefault(reportHandler);
				return null;
			}
			return new ObjCases<>(cases, reportHandler);
		}
	}

	protected final Dynamic value;
	protected final Cases<V> cases;
	private final V defaultCase;

	public SwitchDsl(Dynamic value, Cases<V> cases, V defaultCase){
		this.value = value;
		this.cases = cases;
		this.defaultCase = defaultCase;
	}

	@Nullable public V match(@Nullable InfernalGenContext context){
		Object evaluate = value.evaluate(context);
		V v = cases.match(evaluate);
		if(v!=null) return v;
		return defaultCase;
	}

	public V matchExhaustive(@Nullable InfernalGenContext context){
		V v = match(context);
		if(v!=null) return v;
		else throw new IllegalStateException("Non-exhaustive match in switch");
	}

	@Override public String toString(){
		return "switch("+value+", "+cases+(defaultCase!=null ? ", "+defaultCase+")" : ")");
	}

	public static abstract class Cases<V>{
		@Nullable protected abstract V match(Object value);

		public abstract Collection<V> cases();

		@SuppressWarnings("unchecked")
		@Nullable
		public <V2 extends Dynamic> Cases<V2> tryCastTo(Class<V2> clazz){
			return cases().stream().allMatch(clazz::isInstance) ? (Cases<V2>)this : null;
		}

		@Override public abstract String toString();
	}

	public static final class ObjCases<V> extends Cases<V>{
		private final Map<String, V> stringCases;
		@Nullable private final Double2ObjectMap<V> numberCases;
		@Nullable private final V trueCase, falseCase;

		private ObjCases(Map<String, V> cases, @Nullable ReportHandler reportHandler){
			Map<String, V> stringCases = new HashMap<>();
			@Nullable Double2ObjectMap<V> numberCases = null;
			@Nullable V trueCase = null, falseCase = null;
			for(Map.Entry<String, V> e : cases.entrySet()){
				String k = e.getKey();
				V v = e.getValue();
				stringCases.put(k, v);
				try{
					double i = Double.parseDouble(k);
					if(numberCases==null) numberCases = new Double2ObjectOpenHashMap<>();
					numberCases.put(i, v);
					continue;
				}catch(NumberFormatException ignored){}
				if("true".equalsIgnoreCase(k)){
					if(trueCase!=null) reportDuplicateCase(k, reportHandler);
					else trueCase = v;
				}else if("false".equalsIgnoreCase(k)){
					if(falseCase!=null) reportDuplicateCase(k, reportHandler);
					else falseCase = v;
				}
			}
			this.stringCases = stringCases;
			this.numberCases = numberCases;
			this.trueCase = trueCase;
			this.falseCase = falseCase;
		}

		@Nullable @Override protected V match(Object value){
			if(value instanceof Boolean)
				return (boolean)value ? trueCase : falseCase;
			if(value instanceof Number)
				return numberCases!=null ? numberCases.get(((Number)value).doubleValue()) : null;
			return value instanceof String ? stringCases.get(value) : null;
		}
		@Override public Collection<V> cases(){
			return stringCases.values();
		}

		@Override public String toString(){
			return "{"+stringCases.entrySet().stream().map(e -> e.getKey()+": "+e.getValue()).collect(Collectors.joining(", "))+"}";
		}
	}

	public static final class NumCases<V> extends Cases<V>{
		private final Double2ObjectMap<V> cases;

		private NumCases(Map<String, V> cases, @Nullable ReportHandler reportHandler){
			this.cases = new Double2ObjectOpenHashMap<>();
			for(Map.Entry<String, V> e : cases.entrySet()){
				try{
					double i = Double.parseDouble(e.getKey());
					this.cases.put(i, e.getValue());
				}catch(NumberFormatException ex){
					if(reportHandler!=null) reportHandler.reportError("Ignored case '"+e.getKey()+"'");
				}
			}
		}

		@Nullable @Override protected V match(Object value){
			return value instanceof Number ? cases.get(((Number)value).doubleValue()) : null;
		}
		@Override public Collection<V> cases(){
			return cases.values();
		}

		@Override public String toString(){
			return "{"+cases.double2ObjectEntrySet().stream().map(e -> e.getDoubleKey()+": "+e.getValue()).collect(Collectors.joining(", "))+"}";
		}
	}

	public static final class BoolCases<V> extends Cases<V>{
		@Nullable private final V trueCase, falseCase;

		private BoolCases(Map<String, V> cases, @Nullable ReportHandler reportHandler){
			@Nullable V trueCase = null, falseCase = null;
			for(Map.Entry<String, V> e : cases.entrySet()){
				String k = e.getKey();
				if("true".equalsIgnoreCase(k)){
					if(trueCase!=null) reportDuplicateCase(k, reportHandler);
					else trueCase = e.getValue();
				}else if("false".equalsIgnoreCase(k)){
					if(falseCase!=null) reportDuplicateCase(k, reportHandler);
					else falseCase = e.getValue();
				}else reportIgnoredCase(k, reportHandler);
			}
			this.trueCase = trueCase;
			this.falseCase = falseCase;
		}

		@Nullable @Override protected V match(Object value){
			return Boolean.TRUE.equals(value) ? trueCase :
					Boolean.FALSE.equals(value) ? falseCase :
							null;
		}
		@Override public Collection<V> cases(){
			return trueCase==null ?
					(falseCase==null ? Collections.emptyList() : Collections.singletonList(falseCase)) :
					(falseCase==null ? Collections.singletonList(trueCase) : Arrays.asList(trueCase, falseCase));
		}

		@Override public String toString(){
			StringBuilder stb = new StringBuilder().append("{");
			if(trueCase!=null) stb.append("true: ").append(trueCase);
			if(falseCase!=null){
				if(trueCase!=null) stb.append(", ");
				stb.append("false: ").append(falseCase);
			}
			return stb.append("}").toString();
		}
	}

	private static void reportIgnoredCase(String key, @Nullable ReportHandler reportHandler){
		if(reportHandler!=null) reportHandler.reportWarning("Ignored case '"+key+"'");
	}

	private static void reportDuplicateCase(String key, @Nullable ReportHandler reportHandler){
		if(reportHandler!=null) reportHandler.reportWarning("Duplicated case '"+key+"'");
	}

	private static void reportNonExhaustiveSwitchWithNoDefault(@Nullable ReportHandler reportHandler){
		if(reportHandler!=null) reportHandler.reportError("Non-exhaustive switch expressions require default value");
	}
}
