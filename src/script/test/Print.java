package script.test;

import script.engine.Data;
import script.engine.Engine;
import script.engine.EngineError;
import script.engine.instruction.NativeInstruction;

public class Print extends NativeInstruction
{
	public Print()
	{
		super( "print", new String[] { "s" } );
	}

	public void execute( Engine engine ) throws EngineError
	{
		Data data = engine.pop( Engine.OPERAND_STACK );
		System.out.println( data.asString() );
	}
}
