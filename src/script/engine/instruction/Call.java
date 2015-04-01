package script.engine.instruction;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import script.engine.Engine;
import script.engine.EngineError;

public class Call extends Instruction
{
	public Call( String[] args ) throws EngineError
	{
		super( args );

		if( !( args != null && args.length == 1 ) )
		{
			throw new EngineError( "Invalid arguments" );
		}
	}

	public void execute( Engine engine ) throws EngineError
	{
        LOG.debug( "Execute: {}", toString() );

	    engine.executeBlock( arguments[0] );
	}

	private static Logger LOG = LoggerFactory.getLogger( Engine.class );
}
