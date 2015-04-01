package script.test;

import java.io.IOException;

import org.apache.log4j.BasicConfigurator;

import script.compiler.Compiler;
import script.engine.Engine;
import script.engine.Program;

public class TestExpr
{
	public static void testcase( String name, String prog, String[] dc, String data ) throws IOException
	{
        try
        {
            Engine engine = new Engine();
            Program program = new Compiler().compileString( prog );
            engine.execute( program );

            if( engine.getDataOrCreate( dc ).toString().compareTo( data ) == 0 )
            {
                System.out.println( name + " OKAY!\n" );
            }
            else
            {
                System.out.println( name + " FAIL! LOSER!\n" );
                System.exit( 1 );
            }
        }
	    catch( Exception e )
        {
            e.printStackTrace();
            System.exit( 1 );
        }
	}

	public static void testExpressions() throws IOException
	{
		Integer i = 1;

		testcase( i.toString(), "x='mark'@1;", new String[] { "x" }, "mark1" ); ++i;

		testcase( i.toString(), "x='1'@'2';", new String[] { "x" }, "12" ); ++i;

		testcase( i.toString(), "x=1;", new String[] { "x" }, "1" ); ++i;

		testcase( i.toString(), "x=1+2;", new String[] { "x" }, "3" ); ++i;

		testcase( i.toString(), "x=(1+2)*3;", new String[] { "x" }, "9" ); ++i;

		testcase( i.toString(), "x=(1+2)*(3+1);", new String[] { "x" }, "12" ); ++i;

		testcase( i.toString(), "x['mark']='mcilroy';", new String[] { "x", "mark" }, "mcilroy" ); ++i;

		testcase( i.toString(), "x[1]=2;", new String[] { "x", "1" }, "2" ); ++i;

		testcase( i.toString(), "x[1+1]=(1+2)*3+1;", new String[] { "x", "2" }, "10" ); ++i;

		testcase( i.toString(), "x=1;y=2;", new String[] { "y" }, "2" ); ++i;

		testcase( i.toString(), "x=1;y=x+1;z=x+y;", new String[] { "z" }, "3" ); ++i;

		testcase( i.toString(), "x={1};", new String[] { "x", "0" }, "1" ); ++i;

		testcase( i.toString(), "x={1,2,3};", new String[] { "x", "0" }, "1" ); ++i;

		testcase( i.toString(), "x={1,2,3};", new String[] { "x", "1" }, "2" ); ++i;

		testcase( i.toString(), "x={1,2,3};", new String[] { "x", "2" }, "3" ); ++i;

		testcase( i.toString(), "x={1,{2,3}};", new String[] { "x", "0" }, "1" ); ++i;

		testcase( i.toString(), "x={1,{2,3}};", new String[] { "x", "1", "0" }, "2" ); ++i;
	
		testcase( i.toString(), "x={1,{2,3}};", new String[] { "x", "1", "1" }, "3" ); ++i;

		testcase( i.toString(), "x={{1,2},3};", new String[] { "x", "0", "0" }, "1" ); ++i;

		testcase( i.toString(), "x={{1,2},3};", new String[] { "x", "0", "1" }, "2" ); ++i;

		testcase( i.toString(), "x={{1,2},3};", new String[] { "x", "1" }, "3" ); ++i;

		testcase( i.toString(), "x={1,2};y={3,x};", new String[] { "y", "1","1" }, "2" ); ++i;

		testcase( i.toString(), "x={1,{2,{3,4,5},6},7};", new String[] { "x", "0" }, "1" ); ++i;

		testcase( i.toString(), "x={1,{2,{3,4,5},6},7};", new String[] { "x", "1", "0" }, "2" ); ++i;

		testcase( i.toString(), "x={1,{2,{3,4,5},6},7};", new String[] { "x", "1", "1", "0" }, "3" ); ++i;

		testcase( i.toString(), "x={1,{2,{3,4,5},6},7};", new String[] { "x", "1", "1", "1" }, "4" ); ++i;

		testcase( i.toString(), "x={1,{2,{3,4,5},6},7};", new String[] { "x", "1", "1", "2" }, "5" ); ++i;
		
		testcase( i.toString(), "x={1,{2,{3,4,5},6},7};", new String[] { "x", "1", "2" }, "6" ); ++i;
		
		testcase( i.toString(), "x={1,{2,{3,4,5},6},7};", new String[] { "x", "2" }, "7" ); ++i;
	}

	public static void main( String[] args ) throws IOException
	{
		BasicConfigurator.configure(); 
		testExpressions();
	}
}
