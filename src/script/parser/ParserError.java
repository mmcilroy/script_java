// 
// ParserError
// Mark McIlroy
// 07/09/2009 
// 
// ParserError exceptions are thrown when an error is encountered by a
// class inside the ParserError package
// 

package script.parser;

@SuppressWarnings("serial")
public class ParserError extends Exception
{
    public ParserError( Parser parser, String message )
    {
        super( String.format( "%s (%d) - %s",
        	   parser.getLexer().getFileName(),
        	   parser.getLexer().getLineNumber(),
        	   message ) );
    }
}
