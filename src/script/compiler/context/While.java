// 
// While
// Mark McIlroy
// 07/09/2009
// 
// Compiles a while loop
// 

package script.compiler.context;

import script.compiler.Compiler;
import script.compiler.CompilerError;
import script.engine.instruction.InstructionType;

public class While extends Context
{
	public While( Compiler c )
	{
		super( c );
	}

	public void onOpenBracket( String s ) throws CompilerError
	{
		if( conditionBlockName == null )
		{
			conditionBlockName = newBlockName();
			bodyBlockName = newBlockName();

			compiler.compileInstruction( InstructionType.Call, new String[] { conditionBlockName } );
			compiler.beginBlock( conditionBlockName );
		}
	}

	public void onCloseBracket( String s ) throws CompilerError
	{
		compiler.compileInstruction( InstructionType.Branch, new String[] { bodyBlockName } );
		compiler.endBlock();
	}

	public void onOpenBrace( String s )
	{
		compiler.beginBlock( bodyBlockName );
	}
	
	public void onCloseBrace( String s ) throws CompilerError
	{
		compiler.compileInstruction( InstructionType.Call, new String[] { conditionBlockName } );
		compiler.endBlock();
	}

	private String newBlockName()
	{
		blockId++;
		return "_while#" + blockId.toString();
	}

	private String conditionBlockName;

	private String bodyBlockName;

	private static Integer blockId = 0;
}
