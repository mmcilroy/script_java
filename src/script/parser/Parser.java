package script.parser;

import java.util.Vector;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import script.lexer.Lexer;
import script.lexer.Token;

public class Parser
{
	public void parse( Lexer lexer, ParserCallback callback ) throws Exception
	{
		LOG.info( "Beginning parse" );

		long start = System.nanoTime();

		this.lexer = lexer;
		this.callback = callback;
		this.currToken = lexer.next();

		program();

		long time = ( System.nanoTime() - start ) / 1000000;

		LOG.info( "Parse completed in {} msecs", time );
	}

	public Lexer getLexer() {
		return lexer;
	}

	public void addProc( String name, Integer argC ) throws ParserError {
		semantics.newProcedure( name, argC );
	}

	public void delProc( String name ) throws ParserError {
		semantics.delProcedure( name );
	}

	public void addFunc( String name, Integer argC ) throws ParserError {
		semantics.newFunction( name, argC );
	}

	public void delFunc( String name ) throws ParserError {
		semantics.delFunction( name );
	}

	public void addVar( String var ) throws ParserError {
		semantics.newVariable( var );
	}

	public void beginScope() throws Exception
	{
		semantics.beginScope();
		callback.beginScope();
	}

	public void endScope() throws Exception
	{
		semantics.endScope();
		callback.endScope();
	}

	private void program() throws Exception
	{
		LOG.debug( "Parsing program" );

		beginScope();

		while( !endOfProgram() )
		{
			switch( currToken.type )
			{
				case KeywordInclude : include();             break;
				case KeywordFunc    : functionDefinition();  break;
				case KeywordProc    : procedureDefinition(); break;
				default             : 
				{
					if( semantics.isProcedure( currToken.data ) ) {
						procedureCall();
					} else {
						assignment();
					}
					break;
				}
			}
		}

		//endScope();

		LOG.debug( "Parsed program" );
	}

	private void statement() throws Exception
	{
		LOG.debug( "Parsing statement" );

		switch( currToken.type )
		{
			case KeywordIf      : ifElseBlock(); break;
			case KeywordWhile   : whileBlock(); break;
			default             : 
			{
				if( semantics.isProcedure( currToken.data ) ) {
					procedureCall();
				} else {
					assignment();
				}
				break;
			}
		}

        LOG.debug( "Parsed statement" );
	}

	private void include() throws Exception
	{
		LOG.debug( "Parsing include" );

		expect( Token.Type.KeywordInclude );
		expect( Token.Type.OpenBracket );
		expect( Token.Type.String );
		String file = prevToken.data;
		expect( Token.Type.CloseBracket );

		try
		{
			lexer.addFile( file );
		}
		catch( Exception e )
		{
			throw new ParserError( this, String.format( "Failed to include file: %s", file ) );
		}

		expect( Token.Type.SemiColon );

        LOG.debug( "Parsed include" );
	}

	private void functionDefinition() throws Exception
	{
		LOG.debug( "Parsing function definition" );

		callback.beginFunctionDefinition();
		expect( Token.Type.KeywordFunc );
		expect( Token.Type.Identifier );

		String funcName = prevToken.data;
		Vector<String> funcArgs = new Vector<String>();
		expect( Token.Type.OpenBracket );

		if( accept( Token.Type.Identifier ) )
		{
			semantics.newVariable( prevToken.data );
			funcArgs.add( prevToken.data );

			while( accept( Token.Type.Comma ) )
			{
				expect( Token.Type.Identifier );
				semantics.newVariable( prevToken.data );
				funcArgs.add( prevToken.data );
			}
		}

		semantics.newFunction( funcName, funcArgs.size() );
		expect( Token.Type.CloseBracket );
		expect( Token.Type.OpenBrace );
		beginScope();

		boolean gotReturn = false;

		while( !peek( Token.Type.CloseBrace ) )
		{
			if( peek( Token.Type.KeywordReturn ) )
			{
				returnS();
				gotReturn = true;
			}
			else
			{
				statement();
			}
		}

		endScope();
		expect( Token.Type.CloseBrace );

		if( !gotReturn ) {
			throw new ParserError( this, String.format( "Func (%s) does not contain a return statement", funcName ) );
		}

		callback.endFunctionDefinition();

		for( String arg : funcArgs ) {
			semantics.delVariable( arg );
		}

		LOG.debug( "Parsed function definition" );
	}

	private void functionCall() throws Exception
	{
		LOG.debug( "Parsing function call" );

		callback.beginFunctionCall();
		expect( Token.Type.Identifier );

		String funcName = prevToken.data;
		if( !semantics.isFunction( funcName ) )
		{
			throw new ParserError( this, String.format( "Expected function identifier: ", prevToken ) );
		}

		expect( Token.Type.OpenBracket );

		int argCount = 0;
		if( !peek( Token.Type.CloseBracket ) )
		{
		    expressionOrExpressionArray();
			++ argCount;
			while( accept( Token.Type.Comma ) )
			{
			    expressionOrExpressionArray();
				++ argCount;
			}
		}

		Integer argC = semantics.getArgCount( funcName );
		if( argC != argCount )
		{
			throw new ParserError( this, String.format( "Invalid arguments passed to func: %s", funcName ) );
		}

		expect( Token.Type.CloseBracket );
		callback.endFunctionCall();
		
        LOG.debug( "Parsed function call" );
	}

	private void procedureDefinition() throws Exception
	{
		LOG.debug( "Parsing procedure definition" );

		callback.beginProcedureDefinition();
		expect( Token.Type.KeywordProc );
		expect( Token.Type.Identifier );

		String procName = prevToken.data;
		Vector<String> procArgs = new Vector<String>();
		expect( Token.Type.OpenBracket );

		if( accept( Token.Type.Identifier ) )
		{
			semantics.newVariable( prevToken.data );
			procArgs.add( prevToken.data );

			while( accept( Token.Type.Comma ) )
			{
				expect( Token.Type.Identifier );
				semantics.newVariable( prevToken.data );
				procArgs.add( prevToken.data );
			}
		}

		semantics.newProcedure( procName, procArgs.size() );
		expect( Token.Type.CloseBracket );
		expect( Token.Type.OpenBrace );
		beginScope();

		while( !peek( Token.Type.CloseBrace ) ) {
			statement();
		}

		endScope();
		expect( Token.Type.CloseBrace );

		callback.endProcedureDefinition();

		for( String arg : procArgs ) {
			semantics.delVariable( arg );
		}

		LOG.debug( "Parsed procedure definition" );
	}

	private void procedureCall() throws Exception
	{
		LOG.debug( "Parsing procedure call" );

		callback.beginProcedureCall();
		expect( Token.Type.Identifier );

		String procName = prevToken.data;
		if( !semantics.isProcedure( procName ) )
		{
			throw new ParserError( this, String.format( "Expected function identifier: ", prevToken ) );
		}

		expect( Token.Type.OpenBracket );

		int argCount = 0;
		if( !peek( Token.Type.CloseBracket ) )
		{
		    expressionOrExpressionArray();
			++argCount;
			while( accept( Token.Type.Comma ) )
			{
			    expressionOrExpressionArray();
				++argCount;
			}
		}

		Integer argC = semantics.getArgCount( procName );
		if( argC != argCount )
		{
			throw new ParserError( this, String.format( "Invalid arguments passed to proc: %s", procName ) );
		}

		expect( Token.Type.CloseBracket );
		expect( Token.Type.SemiColon );
		callback.endProcedureCall();

		LOG.debug( "Parsed procedure call" );
	}

	private void whileBlock() throws Exception
	{
		LOG.debug( "Parsing while" );

		callback.beginWhile();

		expect( Token.Type.KeywordWhile );
		expect( Token.Type.OpenBracket );
		expression( ExpressionType.Parent );
		expect( Token.Type.CloseBracket );

		expect( Token.Type.OpenBrace );
		beginScope();

		while( !peek( Token.Type.CloseBrace ) ) {
			statement();
		}

		endScope();
		expect( Token.Type.CloseBrace );

		callback.endWhile();

		LOG.debug( "Parsed while" );
	}

	private void ifElseBlock() throws Exception
	{
		LOG.debug( "Parsing if else" );

		callback.beginIfElse();

		expect( Token.Type.KeywordIf );
		expect( Token.Type.OpenBracket );
		expression( ExpressionType.Parent );
		expect( Token.Type.CloseBracket );
		expect( Token.Type.OpenBrace );
		beginScope();

		while( !peek( Token.Type.CloseBrace ) ) {
			statement();
		}

		endScope();
		expect( Token.Type.CloseBrace ); 

		if( accept( Token.Type.KeywordElse ) )
		{
			expect( Token.Type.OpenBrace );
			beginScope();

			while( !peek( Token.Type.CloseBrace ) ) {
				statement();
			}

			endScope();
			expect( Token.Type.CloseBrace ); 
		}

		callback.endIfElse();

		LOG.debug( "Parsed if else" );
	}

	private void returnS() throws Exception
	{
		LOG.debug( "Parsing return" );

		callback.beginReturn();

		expect( Token.Type.KeywordReturn );
		expect( Token.Type.OpenBracket );
		expression( ExpressionType.Parent );
		expect( Token.Type.CloseBracket );
		expect( Token.Type.SemiColon );

		callback.endReturn();

		LOG.debug( "Parsed return" );
	}

	private void assignment() throws Exception
	{
		LOG.debug( "Parsing assignment" );

		callback.beginAssignment();
		accept( Token.Type.Identifier );		
		semantics.newVariable( prevToken.data );

		while( accept( Token.Type.OpenIndex ) )
		{
			expression( ExpressionType.Parent );
			expect( Token.Type.CloseIndex );
			callback.evalExpression();
		}

		expect( Token.Type.Equals );
        expressionOrExpressionArray();
		expect( Token.Type.SemiColon );
        callback.evalExpression();
		callback.endAssignment();

        LOG.debug( "Parsed assignment" );
	}

	private void expressionOrExpressionArray() throws Exception
	{
        LOG.debug( "Parsing expression or expression array" );

        if( peek( Token.Type.OpenBrace ) )
        {
            expressionArray( ExpressionType.Parent );
        }
        else
        {
            expression( ExpressionType.Parent );
        }

        LOG.debug( "Parsed expression or expression array" );
	}

    // The parser defines two kinds of expression. A child expression is one that is defined
    // from within another expression.
    private enum ExpressionType
    {
        Parent,
        Child
    };

    private void expressionArray( ExpressionType type ) throws Exception
    {
        LOG.debug( "Parsing expression array" );

        if( type == ExpressionType.Parent ) {
            callback.beginArray();
        }

        if( accept( Token.Type.OpenBrace ) )
        {
            if( !peek( Token.Type.CloseBrace ) ) {
                expressionArray( ExpressionType.Child );
            }

            expect( Token.Type.CloseBrace );
        }
        else
        {
            expression( ExpressionType.Parent );
            callback.evalExpression();
        }

        if( accept( Token.Type.Comma ) ) {
            expressionArray( ExpressionType.Child );
        }

        if( type == ExpressionType.Parent ) {
            callback.endArray();
        }

        LOG.debug( "Parsed expression array" );
    }

	private void expression( ExpressionType type ) throws Exception
	{
		LOG.debug( "Parsing expression of type {}", type );

		if( type == ExpressionType.Parent ) {
			callback.beginParentExpression();
		} else {
			callback.beginChildExpression();
		}

		condition();

		while( accept( Token.Type.GreaterThan        ) ||
			   accept( Token.Type.GreaterThanOrEqual ) ||
			   accept( Token.Type.LessThanOrEqual    ) ||
			   accept( Token.Type.LessThan           ) ||
			   accept( Token.Type.EqualTo            ) ||
			   accept( Token.Type.NotEqualTo         ) )
		{
			condition();
			callback.evalExpression();
		}

		if( type == ExpressionType.Parent ) {
			callback.endParentExpression();
		} else {
			callback.endChildExpression();
		}

		LOG.debug( "Parsed expression of type {}", type );
	}

	private void condition() throws Exception
	{
		LOG.debug( "Parsing condition" );
		
		term();

		while( accept( Token.Type.Append ) ||
               accept( Token.Type.Plus ) ||
			   accept( Token.Type.Minus ) )
		{
			term();
			callback.evalExpression();
		}

		LOG.debug( "Parsed condition" );
	}

	private void term() throws Exception
	{
		LOG.debug( "Parsing term" );

		factor();

		while( accept( Token.Type.Multiply ) ||
			   accept( Token.Type.Divide ) )
		{
			factor();
			callback.evalExpression();
		}

		LOG.debug( "Parsed term" );
	}

	private void factor() throws Exception
	{
		LOG.debug( "Parsing factor" );

		if( accept( Token.Type.OpenBracket ) )
		{
			expression( ExpressionType.Child );
			expect( Token.Type.CloseBracket );
		}
		else
		if( accept( Token.Type.Integer ) ||
			accept( Token.Type.String ) )
		{
		}
		else
		if( peek( Token.Type.Identifier ) )
		{
			if( semantics.isFunction( currToken.data ) )
			{
				functionCall();
			}
			else
			if( semantics.isProcedure( prevToken.data ) )
			{
				throw new ParserError( this, String.format( "Cannot embed a procedure call in an expression: %s", prevToken.data ) );
			}
			else
			{
				accept( Token.Type.Identifier );

				if( semantics.isVariableInScope( prevToken.data ) )
				{
					while( accept( Token.Type.OpenIndex ) )
					{
						expression( ExpressionType.Child );
						expect( Token.Type.CloseIndex );
						callback.evalExpression();
					}
				}
				else
				{
					throw new ParserError( this, String.format( "Unknown identifier: %s", prevToken.data ) );
				}
			}
		}
		else
		{
			throw new ParserError( this, String.format( "Invalid factor: %s", currToken ) );
		}

		LOG.debug( "Parsed factor" );
	}

	private boolean endOfProgram()
	{
		return peek( Token.Type.Null );
	}

	private boolean peek( Token.Type t )
	{
		boolean peek = false;

		if( t == Token.Type.Null && currToken == null )
		{
			peek = true;
		}
		else
		{
			if( currToken != null )
			{
				peek = ( currToken.type == t );
			}
		}

		return peek;
	}

	private boolean accept( Token.Type t ) throws Exception
	{
		boolean accept = false;
		
		if( t != null )
		{
			accept = peek( t );

			if( accept )
			{
				LOG.debug( "Accepted {}", t );

				callback.token( currToken );
				prevToken = currToken;
				currToken = lexer.next();

				LOG.debug( "Next token is {}", currToken );
			}
		}

		return accept;
	}

	private void expect( Token.Type t ) throws Exception
	{
		LOG.debug( "Expecting {}", t );

		if( currToken != null )
		{
			if( currToken.type == t )
			{
				callback.token( currToken );
				prevToken = currToken;
				currToken = lexer.next();

				LOG.debug( "Next token is {}", currToken );
			}
			else
			{
				throw new ParserError( this, String.format( "Expected token: %s, Got: %s", t, currToken ) );
			}
		}
		else
		{
			throw new ParserError( this, String.format( "Expected token: %s, Got: null", t ) );
		}
	}

    private Lexer lexer;

	private Token currToken;

	private Token prevToken;

	private ParserCallback callback;

	private SemanticAnalyzer semantics = new SemanticAnalyzer( this );

	private static Logger LOG = LoggerFactory.getLogger( Parser.class );
}
