package il.aircon.model;

public class IncorrectValueException extends Exception {
	private String fieldName;
	public String getFieldName()
	{
		return fieldName;
	}
	public IncorrectValueException(String fieldName)
	{
		super("Incorrect field " + fieldName + " value");
		this.fieldName = fieldName;
		
		
	}
}
