package script.lexer;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;
import java.io.StreamTokenizer;
import java.io.StringReader;
import java.util.HashMap;
import java.util.Stack;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Lexer
{
	public Lexer()
	{
        tokenMap.put( "@",     		Token.Type.Append );
        tokenMap.put( "+",     		Token.Type.Plus );
        tokenMap.put( "-",     		Token.Type.Minus );
        tokenMap.put( "*",     		Token.Type.Multiply );
        tokenMap.put( "/",     		Token.Type.Divide );
        tokenMap.put( "%",     		Token.Type.Modulus );
        tokenMap.put( "(",     		Token.Type.OpenBracket );
        tokenMap.put( ")",     		Token.Type.CloseBracket );
        tokenMap.put( "{",     		Token.Type.OpenBrace );
        tokenMap.put( "}",     		Token.Type.CloseBrace );
        tokenMap.put( "[",     		Token.Type.OpenIndex );
        tokenMap.put( "]",     		Token.Type.CloseIndex );
        tokenMap.put( ">",     		Token.Type.GreaterThan );
        tokenMap.put( "<",     		Token.Type.LessThan );
        tokenMap.put( ">=",     	Token.Type.GreaterThanOrEqual );
        tokenMap.put( "<=",     	Token.Type.LessThanOrEqual );
        tokenMap.put( "==",     	Token.Type.EqualTo );
        tokenMap.put( "!=",     	Token.Type.NotEqualTo );
        tokenMap.put( "=",     		Token.Type.Equals );
        tokenMap.put( ";",     		Token.Type.SemiColon );
        tokenMap.put( ",",     		Token.Type.Comma );
        tokenMap.put( "include",	Token.Type.KeywordInclude );
        tokenMap.put( "func",  		Token.Type.KeywordFunc );
        tokenMap.put( "proc",  		Token.Type.KeywordProc );
        tokenMap.put( "return",		Token.Type.KeywordReturn );
        tokenMap.put( "if",    		Token.Type.KeywordIf );
        tokenMap.put( "else",  		Token.Type.KeywordElse );
        tokenMap.put( "while", 		Token.Type.KeywordWhile );
	}

	public void addFile( String file ) throws FileNotFoundException
	{
		Reader reader = new BufferedReader( new InputStreamReader( new FileInputStream( file ) ) );
		tokenizerStack.push( newStreamTokenizer( reader ) );
		filenameStack.push( file );

		LOG.debug( "Adding file {}. Stack size now {}", file, tokenizerStack.size() );
	}

	public void addString( String str )
	{
		Reader reader = new StringReader( str );
		tokenizerStack.push( newStreamTokenizer( reader ) );

		LOG.debug( "Adding string {}. Stack size now {}", str, tokenizerStack.size() );
	}

	public Token next() throws IOException, LexerError
	{
		Token next = null;

		if( tokenizerStack.size() > 0 )
		{
			StreamTokenizer tokenizer = tokenizerStack.peek();
			int tt = tokenizer.nextToken();

			if( tt != StreamTokenizer.TT_EOF )
			{
				switch( tt )
				{
					case StreamTokenizer.TT_NUMBER:
					{
						// Assume all numbers are integers for now
						// Ideally we should handle floats here as well
						next = new Token( String.format( "%d", (int)tokenizer.nval ), Token.Type.Integer );
						break;
					}
		
					case StreamTokenizer.TT_WORD:
					{
						next = newToken( tokenizer.sval );
						break;
					}
	
					default:
					{
						if( isQuoteChar( (char)tokenizer.ttype ) )
						{
							next = new Token( tokenizer.sval, Token.Type.String );
						}
						else
						{
							next = newToken( String.format( "%c", tokenizer.ttype ) );

							Token specialToken = handleSpecialTokens( tokenizer, next );
							if( specialToken != null )
							{
								next = specialToken;
							}
						}
						break;
					}
				}
			}
			else
			{
				tokenizerStack.pop();

				if( !filenameStack.isEmpty() ) {
					filenameStack.pop();
				}

				if( !tokenizerStack.isEmpty() ) {
					next = next();
				}
			}
		}
		else
		{
			LOG.debug( "Token stack is empty" );
		}

		if( next != null )
		{
		    next = handlePreProcessor( next );
		}

		return next;
	}

	public int getLineNumber()
	{
		return tokenizerStack.isEmpty() ? -1 : tokenizerStack.peek().lineno();
	}

	public String getFileName()
	{
		return filenameStack.isEmpty() ? "*" : filenameStack.peek();
	}

	private Token newToken( String str ) throws LexerError
	{
		Token token = null;
		Token.Type type = tokenMap.get( str );

		if( type != null )
		{
			token = new Token( str, type );
		}
		else
		{
			// If we weren't able to determine what the token is assume
			// it is an identifier
			if( isReservedIdentifier( str ) )
			{
				// Identifiers beginning with an underscore are reserved
				throw new LexerError( this, String.format( "Invalid identifier (%s)", str ) );
			}

			token = new Token( str, Token.Type.Identifier );
		}

		return token;
	}

	private StreamTokenizer newStreamTokenizer( Reader reader )
	{
		StreamTokenizer tokenizer = new StreamTokenizer( reader );
		tokenizer.eolIsSignificant( false );
		tokenizer.slashSlashComments( true );
		tokenizer.slashStarComments( true );
		tokenizer.quoteChar( '\'' );
		tokenizer.quoteChar( '\"' );
		tokenizer.ordinaryChar( '/' );
		tokenizer.ordinaryChar( '-' );
        tokenizer.ordinaryChar( '_' );

		return tokenizer;
	}

	private boolean isQuoteChar( char c )
	{
		return( c == '\'' || c == '\"' );
	}

	private boolean isReservedIdentifier( String str )
	{
		return( str.charAt( 0 ) == '_' );
	}

	private Token handleSpecialTokens( StreamTokenizer tokenizer, Token token ) throws IOException, LexerError
	{
		Token specialToken = null;

		if( token.data.equals( "=" ) )
		{
			int tt = tokenizer.nextToken();
			if( tt != StreamTokenizer.TT_EOF && tokenizer.ttype == '=' ) {
				specialToken = newToken( "==" );
			} else {
				tokenizer.pushBack();
			}
		}
		else
		if( token.data.equals( ">" ) )
		{
			int tt = tokenizer.nextToken();
			if( tt != StreamTokenizer.TT_EOF && tokenizer.ttype == '=' ) {
				specialToken = newToken( ">=" );
			} else {
				tokenizer.pushBack();
			}
		}
		else
		if( token.data.equals( "<" ) )
		{
			int tt = tokenizer.nextToken();
			if( tt != StreamTokenizer.TT_EOF && tokenizer.ttype == '=' ) {
				specialToken = newToken( "<=" );
			} else {
				tokenizer.pushBack();
			}
		}
		else
		if( token.data.equals( "!" ) )
		{
			int tt = tokenizer.nextToken();
			if( tt != StreamTokenizer.TT_EOF && tokenizer.ttype == '=' ) {
				specialToken = newToken( "!=" );
			} else {
				tokenizer.pushBack();
			}
		}

		return specialToken;
	}

	private Token handlePreProcessor( Token token )
	{
	    Token ret = token;

	    if( token.type == Token.Type.String )
	    {
    	    int hashIdx  = ret.data.indexOf( '#' );
    	    int openIdx  = ret.data.indexOf( '(' );
            int closeIdx = ret.data.indexOf( ')' );

            while( hashIdx != -1 &&
                   openIdx != -1 && 
                   closeIdx != -1 && 
                   hashIdx == openIdx-1 &&
                   closeIdx > openIdx )
            {
                LOG.debug( "Pre processing string {}", ret.data );

                String beginStr = ret.data.substring( 0, hashIdx );
                String endStr = ret.data.substring( closeIdx+1, token.data.length() );
                String midStr = ret.data.substring( openIdx+1, closeIdx );

                LOG.debug( "Looking up system property {}", midStr );

                midStr = System.getProperty( midStr );

                ret = new Token( beginStr + midStr + endStr, Token.Type.String );

                LOG.debug( "String token converted to {}", ret.data );

                hashIdx  = ret.data.indexOf( '#' );
                openIdx  = ret.data.indexOf( '(' );
                closeIdx = ret.data.indexOf( ')' );
            }
	    }

        return ret;
	}

	private Stack<StreamTokenizer> tokenizerStack = new Stack<StreamTokenizer>();

	private Stack<String> filenameStack = new Stack<String>(); 

    private HashMap<String, Token.Type> tokenMap = new HashMap<String, Token.Type>();

    private static Logger LOG = LoggerFactory.getLogger( Lexer.class );
}
