
package script.engine.instruction;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import script.engine.Data;
import script.engine.Engine;
import script.engine.EngineError;

public class Evaluate extends Instruction
{
	public Evaluate( String[] args ) throws EngineError
	{
		super( args );

		if( args != null && args.length != 0 )
		{
			throw new EngineError( "Invalid arguments" );
		}
	}

	public void execute( Engine engine ) throws EngineError
	{
        LOG.debug( "Execute: {}", toString() );
        LOG.trace( "Operand stack before:  {}", engine.getOperandStackAsString() );
        LOG.trace( "Operator stack before: {}", engine.getOperatorStackAsString() );

		Data r = engine.pop( Engine.OPERAND_STACK );
		Data l = engine.pop( Engine.OPERAND_STACK );
		Data o = engine.pop( Engine.OPERATOR_STACK );
		Data a = null;

		String opcode = o.asString();

		if( opcode.equals( "@" ) ) { a = l.append( r ); }
		else
		if( opcode.equals( "+" ) ) { a = l.add( r ); }
		else
		if( opcode.equals( "-" ) ) { a = l.subtract( r ); }
		else
		if( opcode.equals( "/" ) ) { a = l.divide( r ); }
		else
		if( opcode.equals( "*" ) ) { a = l.multiply( r ); }
		else
		if( opcode.equals( ">" ) ) { a = l.isGreaterThan( r ); }
		else
		if( opcode.equals( "<" ) ) { a = l.isLessThan( r ); }
		else
		if( opcode.equals( "==" ) ) { a = l.isEqualTo( r ); }
		else
		if( opcode.equals( "!=" ) ) { a = l.isNotEqualTo( r ); }
		else
		if( opcode.equals( "i" ) ) { a = l.index( r, engine ); }
		else
		if( opcode.equals( "=" ) ) { l.equals( r ); }
		else
		{
			throw new EngineError( String.format( "Unknown expression operator: %s", opcode ) );
		}

		if( a != null ) {
        	engine.push( Engine.OPERAND_STACK, a );
		}

        LOG.trace( "Operand stack after:   {}", engine.getOperandStackAsString() );
        LOG.trace( "Operator stack after:  {}", engine.getOperatorStackAsString() );
	}

    private static Logger LOG = LoggerFactory.getLogger( Engine.class );
}
