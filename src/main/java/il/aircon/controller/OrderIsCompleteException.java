package il.aircon.controller;

public class OrderIsCompleteException extends Exception 
{
	public OrderIsCompleteException(String message)
	{
		super(message + " Order is in the Complete state.");
	}
}
