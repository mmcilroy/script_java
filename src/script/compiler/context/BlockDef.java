// 
// FunctionDef
// Mark McIlroy
// 07/09/2009
// 
// Compiles a function definition
// 

package script.compiler.context;

import java.util.Vector;

import script.compiler.Compiler;
import script.compiler.CompilerError;
import script.engine.Block;
import script.engine.Engine;
import script.engine.instruction.InstructionType;

public class BlockDef extends Context
{
	public BlockDef( Compiler c )
	{
		super( c );
	}

	public void onIdentifier( String i )
	{
		if( blockName == null )
		{
			blockName = i;
			compiler.beginBlock( blockName );
			block = compiler.getBlock();
		}
		else
		{
			block.addArgument( i );
		}
	}

    public void onOpenBrace( String s ) throws CompilerError
    {
        Vector<String> args = block.getArguments();

        for( int i = block.getArguments().size() - 1; i >= 0; i-- ) {
            compiler.compileInstruction( InstructionType.Pop, new String[] { Engine.OPERAND_STACK, "$" + args.get( i ) } );
        }
    }

	public void onCloseBrace( String s ) throws CompilerError
	{
		Vector<String> args = block.getArguments();

		for( String arg : args ) {
			compiler.compileInstruction( InstructionType.Delete, new String[] { arg } );
		}

		compiler.endBlock();
	}

	private String blockName;

	private Block block;
}
