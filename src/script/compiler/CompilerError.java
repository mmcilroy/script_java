// 
// CompilerError
// Mark McIlroy
// 07/09/2009 
// 
// CompilerError exceptions are thrown when an error is encountered by a
// class inside the Compiler package
// 

package script.compiler;

@SuppressWarnings("serial")
public class CompilerError extends Exception
{
    public CompilerError( Compiler compiler, String message )
    {
        super( String.format( "%s (%d) - %s",
        		compiler.getParser().getLexer().getFileName(),
        		compiler.getParser().getLexer().getLineNumber(),
        		message ) );
    }
}
