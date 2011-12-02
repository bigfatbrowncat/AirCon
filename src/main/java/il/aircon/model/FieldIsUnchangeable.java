package il.aircon.model;

public class FieldIsUnchangeable extends Exception {
	private String fieldName;
	public String getFieldName()
	{
		return fieldName;
	}
	public FieldIsUnchangeable(String fieldName)
	{
		super("Can't change the field: " + fieldName);
		this.fieldName = fieldName;
	}
}
