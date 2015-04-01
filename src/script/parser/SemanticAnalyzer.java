package script.parser;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Stack;

public class SemanticAnalyzer
{
	public enum IdentifierType
	{
		VARIABLE,
		FUNCTION,
		PROCEDURE
	}

	public SemanticAnalyzer( Parser parser ) {
		this.parser = parser;
	}

	public void beginScope() {
		variableIdStack.push( new HashMap<String, IdentifierType>() );
	}

	public void endScope()
	{
		HashMap<String, IdentifierType> map = variableIdStack.pop();
		map.clear();
	}

	public void newVariable( String name ) throws ParserError
	{
		if( !isVariableInScope( name ) ) {
			variableIdStack.peek().put( name, IdentifierType.VARIABLE );
		}
	}

	public void delVariable( String name ) throws ParserError {
		variableIdStack.remove( name );
	}

	public void newFunction( String name, Integer argC ) throws ParserError
	{
		if( funcIdStack.containsKey( name ) ) {
			throw new ParserError( parser, String.format( "Func identifier already exists: %s", name ) );
		}

		funcIdStack.put( name, IdentifierType.FUNCTION );
		argCounts.put( name, argC );
	}

	public void delFunction( String name ) throws ParserError {
		funcIdStack.remove( name );
	}

	public void newProcedure( String name, Integer argC ) throws ParserError
	{
		if( funcIdStack.containsKey( name ) ) {
			throw new ParserError( parser, String.format( "Proc identifier already exists: %s", name ) );
		}

		funcIdStack.put( name, IdentifierType.PROCEDURE );
		argCounts.put( name, argC );
	}

	public void delProcedure( String name ) throws ParserError {
		variableIdStack.remove( name );
	}

	public boolean isVariableInScope( String name )
	{
		Iterator<HashMap<String, IdentifierType>> it = variableIdStack.iterator();
		while( it.hasNext() )
		{
			if( it.next().containsKey( name ) ) {
				return true;
			}
		}

		return false;
	}

	public boolean isFunction( String name )
	{
		IdentifierType type = funcIdStack.get( name );
		return( type != null && type == IdentifierType.FUNCTION );
	}

	public boolean isProcedure( String name )
	{
		IdentifierType type = funcIdStack.get( name );
		return( type != null && type == IdentifierType.PROCEDURE );
	}

	public Integer getArgCount( String name ) {
		return argCounts.get( name );
	}

	private Parser parser;

	private Stack<HashMap<String, IdentifierType>> variableIdStack = new Stack<HashMap<String, IdentifierType>>();  

	private HashMap<String, IdentifierType> funcIdStack = new HashMap<String, IdentifierType>();  

	private HashMap<String, Integer> argCounts = new HashMap<String, Integer>();  
}
