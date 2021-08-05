package ttmp.cafscript.internal;

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

	byte TRUE = 0x10;
	byte FALSE = 0x11;
	byte N0 = 0x12;
	byte N1 = 0x13;
	byte N2 = 0x14;
	byte N3 = 0x15;
	byte N4 = 0x16;
	byte N5 = 0x17;
	byte NM1 = 0x18;

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

	// Commas
	byte BUNDLE2 = 0x30;
	byte BUNDLE3 = 0x31;
	byte BUNDLE4 = 0x32;
	byte BUNDLEN = 0x33;

	// 0x40~0x5F Advanced Operators
	/**
	 * Get property with identifier {1}
	 */
	byte GET_PROPERTY = 0x40;
	/**
	 * Set property with identifier {1} to 1 popped obj
	 */
	byte SET_PROPERTY = 0x41;
	/**
	 * Set lazy property with identifier {1} to 1 popped obj, expects CafScript object
	 */
	byte SET_PROPERTY_LAZY = 0x42;
	/**
	 * Pop one and apply
	 */
	byte APPLY = 0x43;

	/**
	 * Create new {@link ttmp.cafscript.definitions.Initializer Initializer} based on identifier {1} and pushes it
	 */
	byte NEW = 0x50;
	/**
	 * Pop 1 object (expects {@link ttmp.cafscript.definitions.Initializer Initializer}) and pushes finalized object
	 */
	byte MAKE = 0x51;

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
	 * End the fucking program
	 */
	byte END = 0x7F;
}
