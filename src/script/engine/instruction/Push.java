package script.engine.instruction;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import script.engine.Data;
import script.engine.Engine;
import script.engine.EngineError;

public class Push extends Instruction
{
	public Push( String[] args ) throws EngineError
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

		String stackId = arguments[0];
		String value = arguments[1];

		Data data;
		if( value.length() > 0 && value.charAt( 0 ) == '$' )
		{
			data = engine.getDataOrFail( value.substring( 1 ) );
		}
		else
		{
			data = engine.allocate( value );
		}

		engine.push( stackId, data );
	}

    private static Logger LOG = LoggerFactory.getLogger( Engine.class );
}
