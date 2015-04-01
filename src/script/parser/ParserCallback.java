package script.parser;

import script.lexer.Token;

public interface ParserCallback
{
	public void beginFunctionDefinition() throws Exception;
	public void endFunctionDefinition() throws Exception;

	public void beginFunctionCall() throws Exception;
	public void endFunctionCall() throws Exception;

	public void beginProcedureDefinition() throws Exception;
	public void endProcedureDefinition() throws Exception;

	public void beginProcedureCall() throws Exception;
	public void endProcedureCall() throws Exception;

	public void beginIfElse() throws Exception;
	public void endIfElse() throws Exception;

	public void beginWhile() throws Exception;
	public void endWhile() throws Exception;

	public void beginReturn() throws Exception;
	public void endReturn() throws Exception;

    public void beginArray() throws Exception;
    public void endArray() throws Exception;

    public void beginScope() throws Exception;
    public void endScope() throws Exception;

    public void beginAssignment() throws Exception;
	public void beginParentExpression() throws Exception;
	public void beginChildExpression() throws Exception;

	public void endAssignment() throws Exception;
	public void endParentExpression() throws Exception;
	public void endChildExpression() throws Exception;

	public void evalExpression() throws Exception;

	public void token( Token token ) throws Exception;
}
