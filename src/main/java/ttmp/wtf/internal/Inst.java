package ttmp.wtf.internal;

public interface Inst{
	// 0x00~0x1F Stack shits & Constants
	/**
	 * Put obj {1} to the stack
	 */
	byte PUSH = 0x0;
	/**
	 * Discard one from stack
	 */
	byte DISCARD = 0x1;
	/**
	 * Duplicate top of stack
	 */
	byte DUP = 0x2;

	byte TRUE = 0x10;
	byte FALSE = 0x11;

	/**
	 * Push int {4}
	 */
	byte I = 0x12;
	/**
	 * Push int 0
	 */
	byte I0 = 0x13;
	/**
	 * Push int 1
	 */
	byte I1 = 0x14;
	/**
	 * Push int -1
	 */
	byte IM1 = 0x15;

	/**
	 * Push number {8}
	 */
	byte D = 0x16;
	/**
	 * Push number 0.0
	 */
	byte D0 = 0x17;
	/**
	 * Push number 1.0
	 */
	byte D1 = 0x18;
	/**
	 * Push number -1.0
	 */
	byte DM1 = 0x19;

	// 0x20~0x3F Primitive Operations
	/**
	 * Pop two and push addition
	 */
	byte ADD = 0x20;
	/**
	 * Pop two and push subtraction
	 */
	byte SUBTRACT = 0x21;
	/**
	 * Pop two and push multiplication
	 */
	byte MULTIPLY = 0x22;
	/**
	 * Pop two and push division
	 */
	byte DIVIDE = 0x23;
	/**
	 * Pop one and push negation
	 */
	byte NEGATE = 0x24;
	/**
	 * Pop one and push inversion
	 */
	byte NOT = 0x25;
	byte EQ = 0x26;
	byte NEQ = 0x27;
	byte LT = 0x28;
	byte GT = 0x29;
	byte LTEQ = 0x2A;
	byte GTEQ = 0x2B;

	/**
	 * Pop one, add 1, push
	 */
	byte ADD1 = 0x2C;
	/**
	 * Pop one, subtract 1, push
	 */
	byte SUB1 = 0x2D;

	// Commas
	/**
	 * Consume 2 stack, bundle it, push 1
	 */
	byte BUNDLE2 = 0x30;
	/**
	 * Consume 3 stack, bundle it, push 1
	 */
	byte BUNDLE3 = 0x31;
	/**
	 * Consume 4 stack, bundle it, push 1
	 */
	byte BUNDLE4 = 0x32;
	/**
	 * Consume {1} stack, bundle it, push 1
	 */
	byte BUNDLEN = 0x33;

	/**
	 * Consume 2 stack, append it, push 1
	 */
	byte APPEND2 = 0x38;
	/**
	 * Consume 3 stack, append it, push 1
	 */
	byte APPEND3 = 0x39;
	/**
	 * Consume 4 stack, append it, push 1
	 */
	byte APPEND4 = 0x3A;
	/**
	 * Consume {1} stack, append it, push 1
	 */
	byte APPENDN = 0x3B;

	// 0x40~0x5F Advanced Operators
	/**
	 * Get property from {@link ttmp.wtf.definitions.initializer.Initializer Initializer} {1} below from top of stack, named with identifier {1}. Pushes 1.
	 */
	byte GET_PROPERTY = 0x40;
	/**
	 * Set property to {@link ttmp.wtf.definitions.initializer.Initializer Initializer} {1} below from top of stack, named with identifier {1}. Pops 1.
	 */
	byte SET_PROPERTY = 0x41;
	/**
	 * Set lazy property to {@link ttmp.wtf.definitions.initializer.Initializer Initializer} {1} below from top of stack, named with identifier {1}.<br>
	 * Property gets evaluated right away if the initializer doesn't accept lazy property initialization. In that case, new initializer is put into stack.
	 * Otherwise, jump to {2}.
	 */
	byte SET_PROPERTY_LAZY = 0x42;
	/**
	 * Apply 1 popped object to {@link ttmp.wtf.definitions.initializer.Initializer Initializer} {1} below from top of stack.
	 */
	byte APPLY = 0x43;

	/**
	 * Create range instance using two popped numbers
	 */
	byte RANGE = 0x46;

	/**
	 * Return random int between int #2 ~ #1 from stack, inclusive
	 */
	byte RAND = 0x47;
	/**
	 * Return random int between {4} ~ {4}, inclusive
	 */
	byte RANDN = 0x48;

	/**
	 * Create new {@link ttmp.wtf.definitions.initializer.Initializer Initializer} based on identifier {1} and pushes it
	 */
	byte NEW = 0x50;
	/**
	 * Pop 1 object (expects {@link ttmp.wtf.definitions.initializer.Initializer Initializer}) and pushes finalized object
	 */
	byte MAKE = 0x51;
	/**
	 * Pop 1 object, expect {@link Iterable}, make iterator and push it
	 */
	byte MAKE_ITERATOR = 0x52;
	/**
	 * Pop 2 objects (A, B) and pushes a boolean value indicating whether A is inside collection B
	 */
	byte IN = 0x53;

	// 0x60~0x7F Control Statements
	/**
	 * Increase ip by {2}
	 */
	byte JUMP = 0x60;
	/**
	 * Increase ip by {2} if top stack evaluates to true
	 */
	byte JUMPIF = 0x61;
	/**
	 * Increase ip by {2} if top stack evaluates to false
	 */
	byte JUMPELSE = 0x62;

	/**
	 * Peek number at top stack. Jump {2} if it's less than 1.
	 */
	byte JUMP_IF_LT1 = 0x63;
	/**
	 * Peek top stack as iterator. Push next if hasNext. Else pop and jump {2}
	 */
	byte JUMP_OR_NEXT = 0x64;

	/**
	 * Peek top of stack and print it
	 */
	byte DEBUG = 0x70;

	/**
	 * Pop an {@link ttmp.wtf.definitions.initializer.Initializer Initializer} from
	 * stack and call {@link ttmp.wtf.definitions.initializer.Initializer#finish(WtfExecutor) finish()}.
	 * The product is set to another {@link ttmp.wtf.definitions.initializer.Initializer Initializer} {1} below from top of stack, as property with identifier {1}.
	 */
	byte FINISH_PROPERTY_INIT = 0x7E;
	/**
	 * End the fucking program
	 */
	byte END = 0x7F;
}
