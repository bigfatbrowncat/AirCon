package il.aircon.model;

import il.aircon.controller.ArgumentCantBeNull;

import java.math.BigDecimal;
import java.util.Date;
import java.util.Currency;

import org.hibernate.engine.query.OrdinalParameterDescriptor;

/**
 * Заказ
 */
public class Order
{
	public enum StateType
	{
		
		STATE_NEW(0) 				   ,	//				вновь созданная заявка
		STATE_AFTER_INSPECTION(1) 	   ,	//				после проведения осмотра предполагаемого места установки
		STATE_COMPLETE(2)		 	   ,	//				после монтажа сплит-системы
		STATE_CANCELLED(3);		 	    //				заявка отменена
		
		private int value;

		StateType(int value) { this.value = value; }

		/**
		 * TODO: Использовать это для сериализации
		 * @return
		 */
		public int toInt() { return value; }

		/**
		 * TODO: Использовать это для сериализации
		 * @return
		 */
		public StateType fromInt(int value)
		{
			switch (value)
			{
			case 11: return STATE_NEW;
			case 22: return STATE_AFTER_INSPECTION;
			case 33: return STATE_COMPLETE;
			default: return STATE_CANCELLED;
			}
		}
		
		public String toString() 
		{
			switch (this)
			{
			case STATE_NEW: return "новый";
			case STATE_AFTER_INSPECTION: return "осмотрен";
			case STATE_COMPLETE: return "завершен";
			case STATE_CANCELLED: return "отменен";
			default:
				return null;
			}
		};
		
		
	}
	
	// Служебные поля
	private Long uid;									//				уникальный идентификатор заказа
	private Date created;								//				дата создания заявки
	private Date lastModified;							//				дата последнего изменения заявки
	
	private StateType state = StateType.STATE_NEW;		//				статус выполнения заявки

	// Основные поля (могут быть заполнены в состоянии new и after inspection)
	private String productManufacturerAndModel;			// 				данные о модели устанавливаемой сплит-системы
	private String customerName;						// 				наименование заказчика (ФИО для физического лица, либо полное наименование для организации)
	private String targetAddress;						// 				адрес проведения работ

	// Дополнительные поля (могут быть заполнены только в состоянии after inspection)
	private Float pipeLineLength;						// [метры]		длина трубопроводной магистрали между внутренним и внешним блоками
	private Float additionalCoolantAmount;				// [кг]			количество дозаправленного хдадагента (если пришлось дозаправлять)
	private Boolean pumpNeeded;							//				необходимость установки дренажой помпы

	// Автоматические поля (рассчитываются системой, пользовательский ввод невозможен)
	private BigDecimal fullCost;						//				стоимость всех работ по заказу
	
	public Order()
	{
		this.lastModified = new Date();
		this.created = lastModified;
	}
	
	public Date getCreated() {
		return created;
	}

	public Date getLastModified() {
		return lastModified;
	}

	public String getTargetAddress() {
		return targetAddress;
	}
	public void setTargetAddress(String targetAddress) throws FieldIsUnchangeable {
		if (state == StateType.STATE_COMPLETE || state == StateType.STATE_CANCELLED)
			throw new FieldIsUnchangeable("targetAddress");
		this.targetAddress = targetAddress;
		this.lastModified = new Date();
	}
	
	public String getCustomerName() {
		return customerName;
	}
	public void setCustomerName(String customerName) throws FieldIsUnchangeable, IncorrectValueException, ArgumentCantBeNull {
		if (state == StateType.STATE_COMPLETE || state == StateType.STATE_CANCELLED)
			throw new FieldIsUnchangeable("customerName");
		if (customerName == null) throw new ArgumentCantBeNull("customerName");
		if (customerName.length() > 255) throw new IncorrectValueException("customerName");
		this.customerName = customerName;
		this.lastModified = new Date();
	}
	
	public String getProductManufacturerAndModel() {
		return productManufacturerAndModel;
	}
	public void setProductManufacturerAndModel(
			String productManufacturerAndModel) throws FieldIsUnchangeable, IncorrectValueException, ArgumentCantBeNull {
		if (state == StateType.STATE_COMPLETE || state == StateType.STATE_CANCELLED)
			throw new FieldIsUnchangeable("productManufacturerAndModel");
		if (productManufacturerAndModel == null) throw new ArgumentCantBeNull("productManufacturerAndModel");
		if (productManufacturerAndModel.length() > 255) throw new IncorrectValueException("productManufacturerAndModel");
		this.productManufacturerAndModel = productManufacturerAndModel;
		this.lastModified = new Date();
	}
	
	public Float getPipeLineLength() {
		return pipeLineLength;
	}
	public void setPipeLineLength(Float pipeLineLength) throws IncorrectValueException, FieldIsUnchangeable, ArgumentCantBeNull {
		if (state == StateType.STATE_NEW || state == StateType.STATE_COMPLETE || state == StateType.STATE_CANCELLED)
			throw new FieldIsUnchangeable("pipeLineLength");
		if (pipeLineLength == null) throw new ArgumentCantBeNull("pipeLineLength");
		if (pipeLineLength <= 0) throw new IncorrectValueException("pipeLineLength");
		this.pipeLineLength = pipeLineLength;
		this.lastModified = new Date();
	}
	
	public Float getAdditionalCoolantAmount() {
		return additionalCoolantAmount;
	}
	public void setAdditionalCoolantAmount(Float additionalCoolantAmount) throws IncorrectValueException, FieldIsUnchangeable, ArgumentCantBeNull {
		if (state == StateType.STATE_NEW || state == StateType.STATE_COMPLETE || state == StateType.STATE_CANCELLED)
			throw new FieldIsUnchangeable("additionalCoolantAmount");
		if (additionalCoolantAmount == null) throw new ArgumentCantBeNull("additionalCoolantAmount");
		if (additionalCoolantAmount < 0) throw new IncorrectValueException("additionalCoolantAmount");
		this.additionalCoolantAmount = additionalCoolantAmount;
		this.lastModified = new Date();
	}
	
	public Boolean getPumpNeeded() {
		return pumpNeeded;
	}
	public void setPumpNeeded(Boolean pumpNeeded) throws FieldIsUnchangeable, ArgumentCantBeNull {
		if (state == StateType.STATE_NEW || state == StateType.STATE_COMPLETE || state == StateType.STATE_CANCELLED)
			throw new FieldIsUnchangeable("pumpNeeded");
		if (pumpNeeded == null) throw new ArgumentCantBeNull("pumpNeeded");
		this.pumpNeeded = pumpNeeded;
	}
	
	public BigDecimal getFullCost() {
		return fullCost;
	}
	public void setFullCost(BigDecimal fullCost) throws IncorrectValueException, FieldIsUnchangeable, ArgumentCantBeNull {
		if (state == StateType.STATE_NEW || state == StateType.STATE_COMPLETE || state == StateType.STATE_CANCELLED)
			throw new FieldIsUnchangeable("fullCost");
		if (fullCost == null) throw new ArgumentCantBeNull("fullCost");
		if (fullCost.compareTo(new BigDecimal(0)) < 0) throw new IncorrectValueException("fullCost can not be negative");
		this.fullCost = fullCost;
	}
	
	public StateType getState() {
		return state;
	}
	
	public void setState(StateType state) throws IncorrectOrderStateChange, ArgumentCantBeNull {
		if (state == null) throw new ArgumentCantBeNull("state");
		if (this.state == state) return;
		
		switch (this.state)
		{
		case STATE_NEW:
			if (state == StateType.STATE_COMPLETE) throw new IncorrectOrderStateChange(this.state, state);
			break;
		case STATE_AFTER_INSPECTION:
			if (state == StateType.STATE_NEW) throw new IncorrectOrderStateChange(this.state, state);
			break;
		case STATE_CANCELLED:
			if (state == StateType.STATE_COMPLETE) throw new IncorrectOrderStateChange(this.state, state);
			break;
		case STATE_COMPLETE:
			throw new IncorrectOrderStateChange(this.state, state);
		}
		
		this.state = state;
		this.lastModified = new Date();
	}
	public Long getUid() {
		return uid;
	}
}
