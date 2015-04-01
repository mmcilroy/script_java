// 
// DataContainer
// Mark McIlroy
// 07/09/2009 
// 
// A DataContainer object is the unit of storage for all data stored by a
// VirtualMachine. A DataContainer may contain zero or more child containers,
// each referenced by a unique key
// 

package script.engine;

import java.util.HashMap;
import java.util.Set;

public class Data
{
	Data() {
	}

	Data( Integer data ) {
        this.data = data.toString();
	}

	Data( String data ) {
        this.data = data;
	}

	public void setName( String name ) {
	    this.name = name;
	}

	public String getName() {
	    return name;
	}

	public Data add( Data d ) throws EngineError {
		return new Data( asInteger() + d.asInteger() );
	}

	public Data subtract( Data d ) throws EngineError {
		return new Data( asInteger() - d.asInteger() );
	}

	public Data multiply( Data d ) throws EngineError {
		return new Data( asInteger() * d.asInteger() );
	}

	public Data divide( Data d ) throws EngineError {
		return new Data( asInteger() / d.asInteger() );
	}

	public Data append( Data d ) throws EngineError {
		return new Data( asString() + d.asString() );
	}

	public Data isGreaterThan( Data d ) throws EngineError { 
		return asInteger() > d.asInteger() ? new Data( TRUE ) : new Data( FALSE );
	}

	public Data isLessThan( Data d ) throws EngineError {
		return asInteger() < d.asInteger() ? new Data( TRUE ) : new Data( FALSE );
	}

	public Data isEqualTo( Data d ) throws EngineError {
		return asString().equals( d.asString() ) ? new Data( TRUE ) : new Data( FALSE );
	}

	public Data isNotEqualTo( Data d ) throws EngineError {
		return asString().equals( d.asString() ) ? new Data( FALSE ) : new Data( TRUE );
	}

	public boolean isTrue() throws EngineError { 
		return asString().equals( TRUE );
	}

	public boolean isFalse() throws EngineError {
		return data == null || asString().equals( FALSE );
	}

	public Data index( Data d, Engine engine ) throws EngineError 	{ 
		return getChildOrCreate( d.asString(), engine );
	}

	public Data equals( Data d )
	{
		this.data = d.data;
		this.children = d.children;
		return this;
	}

	public Data createChild( String key, Engine engine )
	{
		if( children == null )
		{
			children = new HashMap< String, Data >();
		}

	    Data dc = engine.allocate( null );
		children.put( key, dc );

		return dc;
	}

	public Data getChild( String key )
	{
		Data child = null;
		if( children != null )
		{
			child = children.get( key );
		}

		return child;
	}

	public Data getChildOrCreate( String key, Engine engine )
	{
		Data child = null;
		if( children != null )
		{
			child = children.get( key );
		}

		if( child == null )
		{
			child = createChild( key, engine );
		}

		return child;
	}

	public Data getChildOrFail( String key ) throws EngineError
	{
		Data child = null;
		if( children != null )
		{
			child = children.get( key );
		}

		if( child == null )
		{
            throw new EngineError( String.format( "Child data undefined: %s", key ) );
		}

		return child;
	}

	public int getChildCount()
	{
	    return children == null ? 0 : children.size();
	}

	public Set<String> getChildKeys()
	{
	    return children == null ? null : children.keySet();
	}

	public void setData( String d )
	{
	    data = d;
	}

    public String toString()
    {
    	return data;
    }

    public String asString() throws EngineError
	{
    	if( data == null )
    	{
    		throw new EngineError( "String conversion failed. Data is null" );
    	}

		return data;
	}

    public Integer asInteger() throws EngineError
	{
    	if( data == null )
    	{
    		throw new EngineError( "Integer conversion failed. Data is null" );
    	}

    	try
    	{
    		return Integer.parseInt( data );
    	}
    	catch( NumberFormatException e )
    	{
    		throw new EngineError( String.format( "Integer conversion failed. Data is not a number: %s", data ) );
    	}
	}

    private String data;

    private String name = "?";

	private HashMap<String, Data> children;

	private static final String TRUE = "1";

	private static final String FALSE = "0";
}
