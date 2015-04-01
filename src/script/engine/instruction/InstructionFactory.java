package script.engine.instruction;

import script.engine.EngineError;

public class InstructionFactory
{
	public static Instruction create( InstructionType i, String[] args ) throws EngineError
	{
		switch( i )
		{
			case Call:         return new Call( args );
			case Return:       return new Return( args );
			case New:          return new New( args );
			case Delete:       return new Delete( args );
			case Evaluate:     return new Evaluate( args );
			case Push:         return new Push( args );
			case PushVariable: return new PushVariable( args );
			case Pop:          return new Pop( args );
			case Dupe:         return new Dupe( args );
			case Branch:       return new Branch( args );
			default:           return null;
		}
	}
}
