package ttmp.wtf.internal;

import it.unimi.dsi.fastutil.ints.Int2IntAVLTreeMap;
import it.unimi.dsi.fastutil.ints.Int2IntMap;
import it.unimi.dsi.fastutil.ints.Int2IntSortedMap;
import it.unimi.dsi.fastutil.ints.IntArrayList;
import it.unimi.dsi.fastutil.ints.IntList;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public final class Lines{
	private final RunLength[] lines;

	public Lines(RunLength[] lines){
		this.lines = lines;
	}

	public int getLine(int index){
		int i = 0;
		for(RunLength line : lines){
			int p = line.length;
			i += p;
			if(index<i) return line.run;
		}
		return -1;
	}

	@Override public String toString(){
		return Arrays.toString(lines);
	}

	public static final class Builder{
		private final Int2IntSortedMap instToPosition = new Int2IntAVLTreeMap();

		public void line(int inst, int position){
			if(inst<0) throw new IllegalArgumentException("inst");
			if(position<0) throw new IllegalArgumentException("position");
			instToPosition.put(inst, position);
		}

		public Lines build(String script, int instSize){
			List<RunLength> lines = new ArrayList<>();
			int[] posForLines = getPosForLines(script);

			int prevInst = 0;
			int prevPos = 0;

			for(Int2IntMap.Entry e : instToPosition.int2IntEntrySet()){
				int inst = e.getIntKey();
				int pos = e.getIntValue();

				if(inst<prevInst) throw new RuntimeException("The map is supposed to be sorted...");
				else if(inst==prevInst) prevPos = pos;
				else{
					addLines(lines, getLine(posForLines, prevPos), inst-prevInst);
					prevInst = inst;
					prevPos = pos;
				}
			}

			if(instSize<=prevInst) throw new RuntimeException("Instruction index outside of range: "+prevInst);

			addLines(lines, getLine(posForLines, prevPos), instSize-prevInst);
			return new Lines(lines.toArray(new RunLength[0]));
		}

		private static void addLines(List<RunLength> list, int lines, int length){
			if(!list.isEmpty()){
				RunLength runLength = list.get(list.size()-1);
				if(runLength.run==lines){
					list.set(list.size()-1, new RunLength(lines, length+runLength.length));
					return;
				}
			}
			list.add(new RunLength(lines, length));
		}

		private static int getLine(int[] posForLines, int pos){
			for(int i = 0; i<posForLines.length; i++){
				int posForLine = posForLines[i];
				if(posForLine>=pos) return i+1;
			}
			return -1;
		}

		private static int[] getPosForLines(String script){
			IntList posForLines = new IntArrayList();
			int i = 0;
			while(true){
				int nextLineBreak = getNextLineBreak(script, i);
				if(nextLineBreak==-1){
					posForLines.add(script.length()-1);
					return posForLines.toIntArray();
				}
				i = nextLineBreak+1;
				posForLines.add(nextLineBreak);
			}
		}

		private static int getNextLineBreak(String script, int startingFrom){
			for(int i = startingFrom; i<script.length(); i++){
				char c = script.charAt(i);
				if(c=='\n') return i;
				else if(c=='\r') return script.length()>i+1&&script.charAt(i+1)=='\n' ? i+1 : i;
			}
			return -1;
		}
	}

	private static final class RunLength{
		public final int run, length;

		private RunLength(int run, int length){
			this.run = run;
			this.length = length;
		}

		@Override public String toString(){
			return run+"*"+length;
		}
	}
}
