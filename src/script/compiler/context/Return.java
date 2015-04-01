package script.compiler.context;

import script.compiler.Compiler;
import script.compiler.CompilerError;
import script.engine.instruction.InstructionType;

public class Return extends Context
{
	public Return( Compiler c )
	{
		super( c );
	}

	public void complete() throws CompilerError
	{
		compiler.compileInstruction( InstructionType.Return, new String[] {} );
	}
}
