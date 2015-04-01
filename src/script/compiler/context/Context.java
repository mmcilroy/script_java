package script.compiler.context;

import script.compiler.Compiler;
import script.compiler.CompilerError;
import script.lexer.Token;

public class Context
{
	public Context( Compiler compiler )
	{
		this.compiler = compiler;
	}

	public void token( Token t ) throws CompilerError
	{
		switch( t.type )
		{
			case Unknown:         onUnknown( t.data ); break;
			case Null:            onNull( t.data ); break;
			case Comma:           onComma( t.data ); break;
			case Append:          onAppend( t.data ); break;
			case Plus:            onPlus( t.data ); break;
			case Minus:           onMinus( t.data ); break;
			case Multiply:        onMultiply( t.data ); break;
			case Divide:          onDivide( t.data ); break;
			case Modulus:         onModulus( t.data ); break;
			case Equals:          onEquals( t.data ); break;
			case EqualTo:         onEqualTo( t.data ); break;
			case NotEqualTo:      onNotEqualTo( t.data ); break;
			case OpenBracket:     onOpenBracket( t.data ); break;
			case CloseBracket:    onCloseBracket( t.data ); break;
			case OpenBrace:       onOpenBrace( t.data ); break;
			case CloseBrace:      onCloseBrace( t.data ); break;
			case OpenIndex:       onOpenIndex( t.data ); break;
			case CloseIndex:      onCloseIndex( t.data ); break;
			case GreaterThan:     onGreaterThan( t.data ); break;
			case LessThan:        onLessThan( t.data ); break;
			case SemiColon:       onSemiColon( t.data ); break;
			case String:          onString( t.data ); break;
			case Integer:         onInteger( t.data ); break;
			case Identifier:      onIdentifier( t.data ); break;
			case KeywordIf:       onKeywordIf( t.data ); break;
			case KeywordElse:     onKeywordElse( t.data ); break;
			case KeywordWhile:    onKeywordWhile( t.data ); break;
		}
	}

    public void onBeginExpression() throws CompilerError {}

	public void onEndExpression() throws CompilerError {}

	public void evalExpression() throws CompilerError {}

	public void complete() throws CompilerError {}

	public void onUnknown( String s ) throws CompilerError {}

	public void onNull( String s ) throws CompilerError {}
	
	public void onComma( String s ) throws CompilerError {}
	
	public void onAppend( String s ) throws CompilerError {}

	public void onPlus( String s ) throws CompilerError {}
	
	public void onMinus( String s ) throws CompilerError {}
	
	public void onMultiply( String s ) throws CompilerError {}
	
	public void onDivide( String s ) throws CompilerError {}
	
	public void onModulus( String s ) throws CompilerError {}
	
	public void onEquals( String s ) throws CompilerError {}

	public void onEqualTo( String s ) throws CompilerError {}

	public void onNotEqualTo( String s ) throws CompilerError {}
	
	public void onOpenBracket( String s ) throws CompilerError {}
	
	public void onCloseBracket( String s ) throws CompilerError {}
	
	public void onOpenBrace( String s ) throws CompilerError {}
	
	public void onCloseBrace( String s ) throws CompilerError {}
	
	public void onOpenIndex( String s ) throws CompilerError {}
	
	public void onCloseIndex( String s ) throws CompilerError {}

	public void onGreaterThan( String s ) throws CompilerError {}
	
	public void onLessThan( String s ) throws CompilerError {}

	public void onSemiColon( String s ) throws CompilerError {}
	
	public void onString( String s ) throws CompilerError {}
	
	public void onInteger( String s ) throws CompilerError {}
	
	public void onIdentifier( String s ) throws CompilerError {}

	public void onKeywordIf( String s ) throws CompilerError {}

	public void onKeywordElse( String s ) throws CompilerError {}

	public void onKeywordWhile( String s ) throws CompilerError {}

	protected Compiler compiler;
}
