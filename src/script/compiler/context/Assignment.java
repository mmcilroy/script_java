// 
// Assignment
// Mark McIlroy
// 07/09/2009
// 
// Compiles an assignment
// 

package script.compiler.context;

import script.compiler.Compiler;
import script.compiler.CompilerError;
import script.engine.Engine;
import script.engine.instruction.InstructionType;

public class Assignment extends Context
{
	public Assignment( Compiler c )
	{
		super( c );
	}

	public void onIdentifier( String i ) throws CompilerError
	{
		// Create the LHS variable that the RHS is assigned to
		compiler.addScope( i );
		compiler.compileInstruction( InstructionType.New, new String[] { "$" + i } );
		compiler.compileInstruction( InstructionType.PushVariable, new String[] { Engine.OPERAND_STACK, i } );
	}

	public void onOpenIndex( String s ) throws CompilerError
	{
		// Handle indexing of the LHS variable
		compiler.compileInstruction( InstructionType.Push, new String[] { Engine.OPERATOR_STACK, "i" } );
	}

	public void onEquals( String s ) throws CompilerError
	{
	    compiler.compileInstruction( InstructionType.Push, new String[] { Engine.OPERATOR_STACK, "=" } );
	}

	public void evalExpression() throws CompilerError
	{
		compiler.compileInstruction( InstructionType.Evaluate, new String[] {} );
	}
}
