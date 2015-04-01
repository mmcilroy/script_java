package script.engine.instruction;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import script.engine.Data;
import script.engine.Engine;
import script.engine.EngineError;

public class Branch extends Instruction
{
	public Branch( String[] args ) throws EngineError
	{
		super( args );

		if( !( args != null && ( args.length == 1 || args.length == 2 ) ) )
		{
			throw new EngineError( "Invalid arguments" );
		}
	}

	public void execute( Engine engine ) throws EngineError
	{
	    LOG.debug( "Execute: {}", toString() );

		Data d = engine.pop( Engine.OPERAND_STACK );
		if( d.isTrue() )
		{
			engine.executeBlock( arguments[0] );
		}
		else
		{
			if( arguments.length == 2 )
			{
				engine.executeBlock( arguments[1] );
			}
		}
	}

    private static Logger LOG = LoggerFactory.getLogger( Engine.class );
}
