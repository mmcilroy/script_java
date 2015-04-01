package script.lexer;

public class Token
{
	Token( String d, Type t )
	{
		data = d;
		type = t;
	}

	public String data;

	public Type type;

	public String toString()
	{
	    return String.format( "'%s' (%s)", data, type.toString() );
	}

	public enum Type
	{
		Unknown,
		Null,
		Append,
		Comma,
		Plus,
		Minus,
		Multiply,
		Divide,
		Modulus,
		Equals,
		EqualTo,
		NotEqualTo,
		OpenBracket,
		CloseBracket,
		OpenBrace,
		CloseBrace,
		OpenIndex,
		CloseIndex,
		GreaterThan,
		LessThan,
		GreaterThanOrEqual,
		LessThanOrEqual,
		SemiColon,
		String,
		Integer,
		Identifier,
		KeywordInclude,
		KeywordFunc,
		KeywordProc,
		KeywordReturn,
		KeywordIf,
		KeywordElse,
		KeywordWhile
	};
}
