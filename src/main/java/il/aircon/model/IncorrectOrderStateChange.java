package il.aircon.model;

import il.aircon.model.Order.StateType;


public class IncorrectOrderStateChange extends Exception 
{
	private StateType source;
	private StateType destination;
	
	public StateType getSource()
	{
		return source;
	}
	public StateType getDestination()
	{
		return destination;
	}
	public IncorrectOrderStateChange(StateType source, StateType destination)
	{
		super("Can't change order state from '" + source + "' to '" + destination + "'");
		this.source = source;
		this.destination = destination;
	}
}
