// 
// FunctionCall
// Mark McIlroy
// 07/09/2009
// 
// Compiles a function call
// 

package script.compiler.context;

import script.compiler.Compiler;
import script.compiler.CompilerError;
import script.engine.instruction.InstructionType;

public class BlockCall extends Context
{
	public BlockCall( Compiler c )
	{
		super( c );
	}

	public void onIdentifier( String i )
	{
		blockName = i;
	}

	public void onEndExpression()
	{
		++argCount;
	}

	public void onCloseBracket( String s ) throws CompilerError
	{
		compiler.compileInstruction( InstructionType.Call, new String[] { blockName } );
	}

	private String blockName;

	private Integer argCount = 0;
}
