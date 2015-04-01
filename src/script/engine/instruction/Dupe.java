package script.engine.instruction;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import script.engine.Data;
import script.engine.Engine;
import script.engine.EngineError;

public class Dupe extends Instruction
{
	public Dupe( String[] args ) throws EngineError
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

		String stackId = arguments[0];
		Data dc = engine.peek( stackId );
		engine.push( stackId, dc );
	}

    private static Logger LOG = LoggerFactory.getLogger( Engine.class );
}
