package script.test;

import script.engine.Data;
import script.engine.Engine;
import script.engine.EngineError;
import script.engine.instruction.NativeInstruction;

public class Length extends NativeInstruction
{
	public Length()
	{
		super( "length", new String[] { "s" } );
	}

	public void execute( Engine engine ) throws EngineError
	{
		Data data = engine.pop( Engine.OPERAND_STACK );
		engine.push( Engine.OPERAND_STACK, engine.allocate( String.format( "%d", data.getChildCount() ) ) );
	}
}
