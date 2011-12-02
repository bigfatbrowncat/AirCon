package il.aircon.view;

import il.aircon.model.Order.StateType;

public class UnsupportedOrderState extends Exception 
{
	private String state;
	public String getState()
	{
		return state;
	}
	public UnsupportedOrderState(StateType state)
	{
		super("Unsupported order state: " + state.toString());
		this.state = state.toString();
	}	
	public UnsupportedOrderState(String state)
	{
		super("Unsupported order state: " + state);
		this.state = state;
	}	
}
