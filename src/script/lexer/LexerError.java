package script.lexer;

@SuppressWarnings("serial")
public class LexerError extends Exception
{
    public LexerError( Lexer lexer, String message )
    {
        super( String.format( "%s (%d) - %s",
        		lexer.getFileName(),
        		lexer.getLineNumber(),
        		message ) );
    }
}
