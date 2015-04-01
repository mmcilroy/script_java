// 
// Block
// Mark McIlroy
// 07/09/2009 
// 
// A Block represents a sequence of Instructions
// 

package script.engine;

import java.util.LinkedList;
import java.util.ListIterator;
import java.util.Vector;

import script.engine.instruction.Instruction;

public class Block
{
	public Block( String s )
	{
		name = s;
	}

	public void addInstruction( Instruction i )
	{
		instructions.add( i );
	}

	public void addArgument( String arg )
	{
		arguments.add( arg );
	}

    public String getName()
    {
        return name;
    }

    public Vector< String > getArguments()
	{
		return arguments; 
	}

	public ListIterator< Instruction > getFirstInstruction()
	{
		return instructions.listIterator();
	}

	private String name;
	
	private Vector< String > arguments = new Vector< String >();

	private LinkedList< Instruction > instructions = new LinkedList< Instruction >();
}
