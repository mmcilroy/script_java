package script.engine;

@SuppressWarnings("serial")
public class EngineError extends Exception
{
	public EngineError( String message )
    {
        super( message );
    }

	public EngineError( String msg, Throwable throwable )
	{
		super( msg, throwable );
	}
}
