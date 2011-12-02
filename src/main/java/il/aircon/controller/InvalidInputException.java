package il.aircon.controller;

public class InvalidInputException extends Exception {
	private String fieldName;
	public String getFieldName()
	{
		return fieldName;
	}
	public InvalidInputException(String fieldName)
	{
		super("Invalid input in the field: " + fieldName);
		this.fieldName = fieldName;
		
		
	}
}
