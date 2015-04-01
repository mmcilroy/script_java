package script.engine.instruction;

public abstract class NativeInstruction extends Instruction
{
	public NativeInstruction( String name, String[] args )
	{
		super( args );
		this.name = name;
	}

	public String getName()
	{
		return name;
	}

	public String[] getArgs()
	{
		return arguments;
	}

	private String name;
}
