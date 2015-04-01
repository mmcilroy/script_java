package script.engine.instruction;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import script.engine.Data;
import script.engine.Engine;
import script.engine.EngineError;

public class Pop extends Instruction
{
	public Pop( String[] args ) throws EngineError
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

		if( arguments.length > 1 )
		{
			String name = arguments[1];
	
			if( name.charAt( 0 ) == '$' )
			{
				Data data;
				Data stackData;

				data = engine.getDataOrCreate( name.substring( 1 ) );
				stackData = engine.pop( stackId );
				data.equals( stackData );
			}
		}
		else
		{
			engine.pop( stackId );
		}
	}

    private static Logger LOG = LoggerFactory.getLogger( Engine.class );
}
