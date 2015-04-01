// 
// Instruction
// Mark McIlroy
// 07/09/2009 
// 
// Base class for all Instructions that can be executed by a VirtualMachine
// 

package script.engine.instruction;

import script.engine.Engine;
import script.engine.EngineError;

public abstract class Instruction
{
	public abstract void execute( Engine engine ) throws EngineError;

	public Instruction( String[] args )
	{
		arguments = args;
	}

	public void setSource( String s )
	{
		source = s;
	}

	public String getSource()
	{
		return source;
	}

	public String toString()
	{
		String str = "";

		if( source != null )
		{
			str += source + " ";
		}

		str += this.getClass().getSimpleName();

		if( arguments != null &&
		    arguments.length > 0 )
		{
			for( int i=0; i<arguments.length; i++ )
			{
				str += ( " " + arguments[i] );
			}
		}

		return str;
	}

	protected String[] arguments;

	private String source;
}
