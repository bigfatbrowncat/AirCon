package il.aircon.controller;

public class ArgumentShouldBeNull extends Exception
{
	private String argName;
	public String getArgName()
	{
		return argName;
	}
	public ArgumentShouldBeNull(String argName, String cause)
	{
		super("Argument " + argName + " should be null cause " + cause);
		this.argName = argName;
	}
}
