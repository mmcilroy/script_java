// 
// Expression
// Mark McIlroy
// 07/09/2009
// 
// Compiles an expression
// 

package script.compiler.context;

import script.compiler.Compiler;
import script.compiler.CompilerError;
import script.engine.Engine;
import script.engine.instruction.InstructionType;

public class Expression extends Context
{
	public Expression( Compiler c )
	{
		super( c );
	}

	public void onInteger( String i ) throws CompilerError
	{
		compiler.compileInstruction( InstructionType.Push, new String[] { Engine.OPERAND_STACK, i } );
	}

	public void onString( String s ) throws CompilerError
	{
		compiler.compileInstruction( InstructionType.Push, new String[] { Engine.OPERAND_STACK, s } );
	}

	public void onIdentifier( String i ) throws CompilerError
	{
		compiler.compileInstruction( InstructionType.PushVariable, new String[] { Engine.OPERAND_STACK, i } );
	}

	public void onEqualTo( String s ) throws CompilerError
	{
		compiler.compileInstruction( InstructionType.Push, new String[] { Engine.OPERATOR_STACK, s } );
	}

	public void onNotEqualTo( String s ) throws CompilerError
	{
		compiler.compileInstruction( InstructionType.Push, new String[] { Engine.OPERATOR_STACK, s } );
	}

	public void onAppend( String s ) throws CompilerError
	{
		compiler.compileInstruction( InstructionType.Push, new String[] { Engine.OPERATOR_STACK, s } );
	}

	public void onPlus( String s ) throws CompilerError
	{
		compiler.compileInstruction( InstructionType.Push, new String[] { Engine.OPERATOR_STACK, s } );
	}

	public void onMinus( String s ) throws CompilerError
	{
		compiler.compileInstruction( InstructionType.Push, new String[] { Engine.OPERATOR_STACK, s } );
	}

	public void onMultiply( String s ) throws CompilerError
	{
		compiler.compileInstruction( InstructionType.Push, new String[] { Engine.OPERATOR_STACK, s } );
	}

	public void onDivide( String s ) throws CompilerError
	{
		compiler.compileInstruction( InstructionType.Push, new String[] { Engine.OPERATOR_STACK, s } );
	}

	public void onGreaterThan( String s ) throws CompilerError
	{
		compiler.compileInstruction( InstructionType.Push, new String[] { Engine.OPERATOR_STACK, ">" } );
	}
	
	public void onLessThan( String s ) throws CompilerError
	{
		compiler.compileInstruction( InstructionType.Push, new String[] { Engine.OPERATOR_STACK, "<" } );
	}

	public void onOpenIndex( String s ) throws CompilerError
	{
		compiler.compileInstruction( InstructionType.Push, new String[] { Engine.OPERATOR_STACK, "i" } );
	}

	public void evalExpression() throws CompilerError
	{
		compiler.compileInstruction( InstructionType.Evaluate, new String[] {} );
	}
}
