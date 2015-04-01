package script.test;

import org.apache.log4j.PropertyConfigurator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import script.compiler.Compiler;
import script.engine.Engine;
import script.engine.Program;

public class TestFile
{
    public static void main( String[] args ) throws Exception
    {
		PropertyConfigurator.configure( LOG4J_CONFIG_FILE );
        Engine engine = new Engine();

        try
        {
	        Compiler compiler = new Compiler();
	        compiler.addProc( new Print() );
	        compiler.addFunc( new Length() );
	        Program program = compiler.compileFile( "prog.txt" );
	        program.dump( LOG );
	        engine.execute( program );
        }
    	catch( Exception e )
    	{
			e.printStackTrace();
			System.out.println( "Script stack trace" + engine.getStackTrace() );
		}
    }

	private static Logger LOG = LoggerFactory.getLogger( TestFile.class );

	private static final String LOG4J_CONFIG_FILE = "LogConfig.txt";
}
