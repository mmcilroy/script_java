// 
// Assignment
// Mark McIlroy
// 07/09/2009
// 
// Compiles an assignment
// 

package script.compiler.context;

import java.util.Stack;

import script.compiler.Compiler;
import script.compiler.CompilerError;
import script.engine.Engine;
import script.engine.instruction.InstructionType;

public class Array extends Context
{
	public Array( Compiler c )
	{
		super( c );
	}

	public void onOpenBrace( String s ) throws CompilerError
	{
		// Work out the parent container and push onto the stack
		// At level 0 the parent is already in place
		if( indexStack.size() == 0 )
		{
		    compiler.compileInstruction( InstructionType.Delete, new String[] { "$_arr" } );
		    compiler.compileInstruction( InstructionType.New, new String[] { "$_arr" } );
            compiler.compileInstruction( InstructionType.PushVariable, new String[] { Engine.OPERAND_STACK, "_arr" } );
		}
		else
		{
			// Copy the current parent and work out the next parent
			compiler.compileInstruction( InstructionType.Dupe, new String[] { Engine.OPERAND_STACK } );
			compiler.compileInstruction( InstructionType.Push, new String[] { Engine.OPERATOR_STACK, "i" } );
			compiler.compileInstruction( InstructionType.Push, new String[] { Engine.OPERAND_STACK, indexStack.peek().toString() } );
			compiler.compileInstruction( InstructionType.Evaluate, new String[] {} );
		}

		// Keep track of our position in the RHS array expression
		indexStack.push( new Integer( 0 ) );
	}

	public void onComma( String s )
	{
		// Keep track of our position in the RHS array expression
		Integer i = indexStack.pop();
		indexStack.push( ++i );
	}

	public void onCloseBrace( String s ) throws CompilerError
	{
		// Keep track of our position in the RHS array expression
		indexStack.pop();

		// Pop the current parent container
		if( indexStack.size() > 0 )
		{
			compiler.compileInstruction( InstructionType.Pop, new String[] { Engine.OPERAND_STACK } );
		}
	}

	public void onBeginExpression() throws CompilerError
	{
		if( indexStack.size() > 0 )
		{
			// Handle assignment for an array RHS
			// Make a copy of the LHS variable
			// This is used for subsequent array indexing operations
			compiler.compileInstruction( InstructionType.Dupe, new String[] { Engine.OPERAND_STACK } );

			// Index until the required LHS container is calculated
			compiler.compileInstruction( InstructionType.Push, new String[] { Engine.OPERATOR_STACK, "i" } );
			compiler.compileInstruction( InstructionType.Push, new String[] { Engine.OPERAND_STACK, indexStack.peek().toString() } );
			compiler.compileInstruction( InstructionType.Evaluate, new String[] {} );

			// Handle the assignment operation for the current array element
			compiler.compileInstruction( InstructionType.Push, new String[] { Engine.OPERATOR_STACK, "=" } );
		}
	}

	public void evalExpression() throws CompilerError
	{
		compiler.compileInstruction( InstructionType.Evaluate, new String[] {} );
	}

	public void complete() throws CompilerError
	{
		// Clear down the arithmetic stacks so subsequent operations are not affected
        //compiler.compileInstruction( InstructionType.Push, new String[] { Engine.OPERATOR_STACK, "=" } );
        //compiler.compileInstruction( InstructionType.Evaluate, new String[] {} );
	}

	private Stack<Integer> indexStack = new Stack<Integer>();
}
