// 
// VirtualMachine
// Mark McIlroy
// 07/09/2009 
// 
// Implementation of a very simple virtual machine. A VirtualMachine object
// consists of
//
// 1. A memory pool (global and local)
// 2. Expression evaluation stacks
// 3. An instruction set
//
// Programs can be loaded in the VirtualMachine and executed to  
// manipulate its state
// 

package script.engine;

import java.util.HashMap;
import java.util.Iterator;
import java.util.ListIterator;
import java.util.Stack;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import script.engine.instruction.Instruction;

public class Engine
{
	public Engine()
	{
	    globalData = new HashMap< String, Data >();
	    instructionStack = new Stack< ListIterator<Instruction>>();
	    stackMap = new HashMap< String, Stack< Data > >();
	}

	public void reset()
	{
		globalData.clear();
	    instructionStack.clear();
	    stackMap.clear();
	}

    public void execute( Program p ) throws EngineError
    {
		// Basic execution pattern
        // Call the main function. Don't pass any arguments to the program
		execute( p, "main", null );
    }

    public void execute( Program p, String entry, String[] args ) throws EngineError
    {
    	LOG.debug( "Executing program: {}", entry );

		long start = System.nanoTime();

		// Initialise
        program = p;

        // Find the block that defines the programs entry point
        Block entryBlock = program.getBlock( entry );
        if( entryBlock != null )
        {
            instructionStack.push( entryBlock.getFirstInstruction() );
        }

        // Find the block that defines the programs init section
        entryBlock = program.getBlock( "_init" );
        if( entryBlock != null )
        {
            instructionStack.push( entryBlock.getFirstInstruction() );
        }

        // Push any arguments onto the stack so they can be referenced
        // by the entry function
        if( args != null )
        {
            for( int i=0; i<args.length; i++ )
            {
                if( args[i].length() > 0 && args[i].charAt( 0 ) == '$' )
                {
                    push( OPERAND_STACK, getDataOrFail( args[i].substring( 1 ) ) );
                }
                else
                {
                    push( OPERAND_STACK, allocate( args[i] ) );
                }
            }
        }

        // The init and main blocks will be called first in that order
        // If they exist that is - they don't need to
        while( instructionStack.size() > 0 )
        {
            // Execute the instruction at the top of the stack
            Iterator<Instruction> it = instructionStack.peek();
            Integer stackSize = instructionStack.size();
            Instruction i;

            if( it.hasNext() )
            {
                i = it.next();
            	i.execute( this );
            }

            // If there are no more instructions in the current frame, pop
            // to the frame below and continue execution from there
            if( it.hasNext() == false )
            {
                // Only pop the current frame if the number of entries on the stack
                // has not changed. If it has then a function call must have taken
                // place and we should leave the stack as is. If we were to pop the
                // stack now the function call would be missed 
                if( stackSize == instructionStack.size() )
                {
                    instructionStack.pop();
                }
            }
        }

		long time = ( System.nanoTime() - start ) / 1000000;

		LOG.info( "Program executed in {} msecs", time );
    }

    public void executeBlock( String blockId ) throws EngineError
    {
        // Pass control of execution to the specified block
        // Once complete control will pass back to the current block
        Block block = program.getBlock( blockId );
        if( block == null )
        {
            throw new EngineError( String.format( "Invalid block: %s", blockId ) );
        }

        instructionStack.push( block.getFirstInstruction() );
    }

    public void returnBlock() throws EngineError
    {
    	if( instructionStack.size() == 0 )
    	{
    		throw new EngineError( "Nowhere to return to" );
    	}

    	instructionStack.pop();
    }

    public Program getProgram()
    {
        return program;
    }

    public Data allocate( String data )
    {
        // Create a new data container and return it
        // Ideally the container would be obtained from a statically allocated
        // pool (thereby allowing us to better control the memory footprint required
    	// by the virtual machine), but for now we will just create it on the heap

        return new Data( data );
    }

    public void release( Data dc )
    {
        // Not required yet
        // Management of allocated DataContainer will be handled by the
        // java garbage collector
    }

    public void addData( String key, Data dc )
    {
        globalData.put( key, dc );
    }

    public Data getDataOrCreate( String key )
    {
        Data dc = globalData.get( key );

        // If a data container for the supplied key does not already
        // exist, create one and add it to local memory
        if( dc == null )
        {
            dc = allocate( null );
            globalData.put( key, dc );
        }

        return dc;
    }

    public Data getDataOrCreate( String[] key )
    {
        Data dc = null;

        if( key.length > 0 )
        {
            // Search global memory for the parent container
            dc = getDataOrCreate( key[0] );

            // If found iterate through the children
            if( dc != null )
            {
                for( int i=1; i<key.length; i++ )
                {
                    dc = dc.getChildOrCreate( key[i], this );
                }
            }
        }

        return dc;
    }

    public Data getDataOrFail( String key ) throws EngineError
    {
        Data dc = globalData.get( key );

        // If a data container for the supplied key does not already
        // exist, create one and add it to the data map
        if( dc == null )
        {
            throw new EngineError( String.format( "Data undefined: %s", key ) );
        }

        return dc;
    }

    public Data getDataOrFail( String[] key ) throws EngineError
    {
        Data dc = null;

        if( key.length > 0 )
        {
            // Search global memory for the parent container
            dc = getDataOrFail( key[0] );

            // If found iterate through the children
            if( dc != null )
            {
                for( int i=1; i<key.length; i++ )
                {
                    dc = dc.getChildOrFail( key[i] );
                }
            }
        }

        return dc;
    }

    public void delete( String key )
    {
    	globalData.remove( key );
    }

    public Data peek( String stackId ) throws EngineError
    {
        Data dc = null;
        Stack< Data > stack = stackMap.get( stackId );

        if( stack == null ) {
            throw new EngineError( String.format( "Unknown stack: %s", stackId ) );
        }

        if( stack.size() == 0 ) {
            throw new EngineError( String.format( "Empty stack: %s", stackId ) );
        }

        dc = stack.peek();
        return dc;
    }

    public void push( String stackId, Data dc ) throws EngineError
    {
        Stack< Data > stack = stackMap.get( stackId );

        // If the stack doesn't exist create it before pushing dc
        if( stack == null )
        {
            stackMap.put( stackId, new Stack< Data >() );
            stack = stackMap.get( stackId );
        }

        stack.push( dc );
    }

    public Data pop( String stackId ) throws EngineError
    {
        Data dc = null;
        Stack< Data > stack = stackMap.get( stackId );

        if( stack == null ) {
            throw new EngineError( String.format( "Unknown stack: %s", stackId ) );
        }

        if( stack.size() == 0 ) {
            throw new EngineError( String.format( "Empty stack: %s", stackId ) );
        }

        dc = stack.pop();
        return dc;
    }

    public String getStackTrace()
    {
    	String trace = "";

    	for( ListIterator<Instruction> it : instructionStack )
    	{
    		if( it.hasPrevious() )
    		{
    			Instruction i = it.previous();
    			if( i.getSource() != null )
    			{
    				trace += String.format( "\n%s - %s", i.getSource(), i );
    			}
    		}
    	}

    	return trace;
    }

    public String getOperandStackAsString() {
        return getStackAsString( OPERAND_STACK );
    }

    public String getOperatorStackAsString() {
        return getStackAsString( OPERATOR_STACK );
    }

    public String getStackAsString( String s )
    {
        String ret = "";

        Stack<Data> stack = stackMap.get( s );
        Iterator<Data> it = stack.iterator();
        Data data;

        while( it.hasNext() )
        {
            data = it.next();

            try
            {
                ret += data.asString() + " ";
            }
            catch( EngineError e )
            {
                ret += data.getName() + " ";
            }
        }

        return ret;
    }

    private Program program;

    private HashMap< String, Data > globalData;

    private Stack< ListIterator< Instruction > > instructionStack;

    private HashMap< String, Stack< Data > > stackMap;

    public static final String OPERAND_STACK = "@odS"; 

    public static final String OPERATOR_STACK = "@orS";

	private static Logger LOG = LoggerFactory.getLogger( Engine.class );
}
