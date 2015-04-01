package script.compiler;

import java.util.Iterator;
import java.util.LinkedList;
import java.util.Stack;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import script.compiler.context.Array;
import script.compiler.context.Assignment;
import script.compiler.context.BlockCall;
import script.compiler.context.BlockDef;
import script.compiler.context.Context;
import script.compiler.context.Expression;
import script.compiler.context.IfElse;
import script.compiler.context.Return;
import script.compiler.context.While;
import script.engine.Block;
import script.engine.EngineError;
import script.engine.Program;
import script.engine.instruction.Instruction;
import script.engine.instruction.InstructionFactory;
import script.engine.instruction.InstructionType;
import script.engine.instruction.NativeInstruction;
import script.lexer.Lexer;
import script.lexer.Token;
import script.parser.Parser;
import script.parser.ParserCallback;
import script.parser.ParserError;

public class Compiler implements ParserCallback 
{
	public Program compileFile( String file ) throws Exception
	{
		LOG.info( "Compiling file {}", file );

		long start = System.nanoTime();

		beginBlock( "_init" );

		Lexer lexer = new Lexer();
		lexer.addFile( file );
		parser.parse( lexer, this );

		long time = ( System.nanoTime() - start ) / 1000000;

		LOG.info( "Compile completed in {} msecs", time );

		return program;
	}

	public Program compileString( String str ) throws Exception
	{
		LOG.info( "Compiling string {}", str );

		beginBlock( "_init" );

		Lexer lexer = new Lexer();
		lexer.addString( str );
		parser.parse( lexer, this );

		LOG.info( "Compile completed" );

		return program;
	}

	public void addVar( String var ) throws ParserError
	{
		parser.addVar( var );
	}

	public void addFunc( NativeInstruction i ) throws ParserError, CompilerError
	{
		String name = i.getName();
		String[] args = i.getArgs();
		parser.addFunc( name, args == null ? 0 : args.length );
		addBlock( i, name, args );
	}

	public void addProc( NativeInstruction i ) throws ParserError, CompilerError
	{
		String name = i.getName();
		String[] args = i.getArgs();
		parser.addProc( name, args == null ? 0 : args.length );
		addBlock( i, name, args );
	}

	private void addBlock( Instruction i, String name, String[] args ) throws CompilerError
	{
		program.addBlock( name, i, args );
		Block block = program.getBlock( name );
		blockStack.push( block );

		LOG.debug( "Current block in now: {}", block.getName() );

		for( int j=0; j<args.length; j++ )
		{
			compileInstruction( InstructionType.Delete, new String[] { args[j] } );
		}

		blockStack.pop();
	}

	public void compileInstruction( InstructionType it, String[] args ) throws CompilerError
	{
		Instruction i;

		try
		{
			i = InstructionFactory.create( it, args );
		}
		catch( EngineError e )
		{
		    throw new CompilerError( this, String.format( "Failed to create instruction: %s: %s", it, e.getMessage() ) );
		}

		getBlock().addInstruction( i );

		Lexer lexer = parser.getLexer();
		if( lexer != null )
		{
			i.setSource( String.format( "%s (%d)", lexer.getFileName(), lexer.getLineNumber() ) );
		}

		LOG.debug( "Compiled instruction: {}", i );
	}

	public void beginFunctionDefinition()
	{
		beginContext( new BlockDef( this ) );
	}

	public void endFunctionDefinition() throws CompilerError
	{
		endContext();
	}

	public void beginFunctionCall()
	{
		beginContext( new BlockCall( this ) );
	}

	public void endFunctionCall() throws CompilerError
	{
		endContext();
	}

	public void beginProcedureDefinition()
	{
		beginContext( new BlockDef( this ) );
	}

	public void endProcedureDefinition() throws CompilerError
	{
		endContext();
	}

	public void beginProcedureCall()
	{
		beginContext( new BlockCall( this ) );
	}

	public void endProcedureCall() throws CompilerError
	{
		endContext();
	}

	public void beginReturn()
	{
		beginContext( new Return( this ) );
	}

	public void endReturn() throws CompilerError
	{
		endContext();
	}

	public void beginIfElse() throws Exception
	{
		beginContext( new IfElse( this ) );
	}

	public void endIfElse() throws Exception
	{
		endContext();
	}

	public void beginWhile() throws Exception
	{
		beginContext( new While( this ) );
	}

	public void endWhile() throws Exception
	{
		endContext();
	}

	public void beginScope()
	{
		scopeStack.push( new LinkedList<String>() );
	}

	public void endScope() throws CompilerError
	{
		Iterator<String> vars = scopeStack.pop().iterator();
		while( vars.hasNext() )
		{
			this.compileInstruction( InstructionType.Delete, new String[] { "$" + vars.next() } );
		}
	}

	public void beginAssignment()
	{
		beginContext( new Assignment( this ) );
	}

	public void endAssignment() throws CompilerError
	{
		endContext();
	}

    public void beginArray() throws Exception
    {
        beginContext( new Array( this ) );
    }

    public void endArray() throws Exception
    {
        endContext();
    }

	public void beginParentExpression() throws CompilerError
	{
		if( !contextStack.isEmpty() ) contextStack.peek().onBeginExpression();
		contextStack.push( new Expression( this ) );
	}

	public void endParentExpression() throws CompilerError
	{
		endContext();
		if( !contextStack.isEmpty() )
		{
			contextStack.peek().onEndExpression();
		}
	}

	public void beginChildExpression() throws CompilerError
	{
		if( !contextStack.isEmpty() ) contextStack.peek().onBeginExpression();
		contextStack.push( new Expression( this ) );
	}

	public void endChildExpression() throws CompilerError
	{
		endContext();
		if( !contextStack.isEmpty() )
		{
			contextStack.peek().onEndExpression();
		}
	}

	public void evalExpression() throws CompilerError
	{
		if( !contextStack.isEmpty() ) contextStack.peek().evalExpression();
	}

	public void token( Token token ) throws CompilerError
	{
		if( !contextStack.isEmpty() )
		{
			Context context = contextStack.peek();

			LOG.debug( "Passing token {} to context {}", token, context.getClass().getSimpleName() );

			context.token( token );
		}
	}

	public Parser getParser()
	{
		return parser;
	}

	public Program getProgram()
	{
		return program;
	}

	public void beginBlock( String name )
	{
		blockStack.push( program.createBlock( name ) );
		LOG.debug( "Compilation block is now {} (PUSH)", name );
	}

	public void endBlock() throws CompilerError
	{
        if( blockStack.size() == 0 ) {
            throw new CompilerError( this, "Tried to end a block while the block stack was empty" );
        }

        blockStack.pop();

        if( blockStack.size() > 0 ) {
        	LOG.debug( "Compilation block is now {} (POP)", blockStack.peek().getName() );
        }
	}

	public Block getBlock()
	{
		return blockStack.peek();
	}

	private void beginContext( Context context )
	{
		contextStack.push( context );
	}

	private void endContext() throws CompilerError
	{
		if( !contextStack.isEmpty() )
		{
			contextStack.peek().complete();
			contextStack.pop();
		}
	}

	public void addScope( String name )
	{
		if( !inScope( name ) ) {
			scopeStack.peek().add( name );
		}
	}

	private boolean inScope( String name )
	{
		Iterator<LinkedList<String>> it = scopeStack.iterator();
		while( it.hasNext() )
		{
			if( it.next().contains( name ) ) {
				return true;
			}
		}

		return false;
	}

	private Parser parser = new Parser();

	private Program program = new Program();

	private Stack<LinkedList<String>> scopeStack = new Stack<LinkedList<String>>();  

	private Stack<Context> contextStack = new Stack<Context>();

    private Stack<Block> blockStack = new Stack<Block>();

	private static Logger LOG = LoggerFactory.getLogger( Compiler.class );
}
