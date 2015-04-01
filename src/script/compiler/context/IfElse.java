// 
// IfElse
// Mark McIlroy
// 07/09/2009
// 
// Compiles an if else block
// 

package script.compiler.context;

import script.compiler.Compiler;
import script.compiler.CompilerError;
import script.engine.instruction.InstructionType;

public class IfElse extends Context
{
	public IfElse( Compiler c )
	{
		super( c );
	}

	public void onOpenBrace( String s )
	{
		if( elseBlock )
		{
			elseBlockName = newBlockName();
			compiler.beginBlock( elseBlockName );
		}
		else
		{
			ifBlockName = newBlockName();
			compiler.beginBlock( ifBlockName );
		}
	}

	public void onCloseBrace( String s ) throws CompilerError
	{
		compiler.endBlock();
	}

	public void onKeywordElse( String s )
	{
		elseBlock = true;
	}

	public void complete() throws CompilerError
	{
		if( elseBlock )
		{
			compiler.compileInstruction( InstructionType.Branch, new String[] { ifBlockName, elseBlockName } );
		}
		else
		{
			compiler.compileInstruction( InstructionType.Branch, new String[] { ifBlockName } );
		}
	}

	private String newBlockName()
	{
		blockId++;
		return "_if#" + blockId.toString();
	}

	private String ifBlockName;

	private String elseBlockName;

	private boolean elseBlock = false;

	private static Integer blockId = 0;
}
