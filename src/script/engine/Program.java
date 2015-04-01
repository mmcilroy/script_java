// 
// Program
// Mark McIlroy
// 07/09/2009 
// 
// A Program represents a collection of Blocks (or functions)
// 

package script.engine;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import script.engine.instruction.Instruction;

public class Program
{
    public Block createBlock( String id )
    {
        Block newBlock = new Block( id );
        blockMap.put( id, newBlock );

        return newBlock;
    }

    public void merge( Program program )
    {
    	for( String s : program.blockMap.keySet() )
    	{
    		Block b = program.blockMap.get( s );
    		if( !blockMap.containsKey( s ) )
    		{
    			blockMap.put( s, b );
    			LOG.debug( "Merged block {} into program", s );
    		}
    		else
    		{
    			LOG.warn( "Could not merge block {} into program as it already exists", s );
    		}
    	}
    }

    public Block getBlock( String id )
    {
        return blockMap.get( id );
    }

    public void addBlock( String id, Instruction inst, String[] args )
    {
        Block b = createBlock( id );
        b.addInstruction( inst );

        if( args != null )
        {
            for( int i=0; i<args.length; i++ )
            {
                b.addArgument( args[i] );
            }
        }
    }

    public void dump( Logger log )
    {
        log.info( "PROGRAM RAW LISTING" );

        Set< String > blockIds = blockMap.keySet();
        Iterator< String > it = blockIds.iterator();

        while( it.hasNext() )
        {
            Block block = blockMap.get( it.next() );
            log.info( "BLOCK {}: {}", block.getName(), block.getArguments().toString() );

            Iterator< Instruction > iit = block.getFirstInstruction();
            while( iit.hasNext() )
            {
                log.info( iit.next().toString() );
            }
        }
    }

    private HashMap<String,Block> blockMap = new HashMap<String,Block>();

	private static Logger LOG = LoggerFactory.getLogger( Program.class );
}
