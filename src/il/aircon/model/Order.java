package il.aircon.model;

import il.aircon.controller.ArgumentCantBeNull;
import il.aircon.controller.IncorrectValueException;

import java.math.BigDecimal;
import java.util.Date;
import java.util.Currency;

/**
 * Заказ
 */
public class Order
{
	public enum StateType
	{
		STATE_NEW 				   ,	//				вновь созданная заявка
		STATE_AFTER_INSPECTION 	   ,	//				после проведения осмотра предполагаемого места установки
		STATE_COMPLETE		 	   ,	//				после монтажа сплит-системы
		STATE_CANCELLED;		 	    //				заявка отменена
		
		public String toString() 
		{
			switch (this)
			{
			case STATE_NEW: return "new";
			case STATE_AFTER_INSPECTION: return "after inspection";
			case STATE_COMPLETE: return "complete";
			case STATE_CANCELLED: return "cancelled";
			default:
				return null;
			}
		};
	}
	
	// Служебные поля
	private Long uid;									//				уникальный идентификатор заказа
	private Date created;								//				дата создания заявки
	private Date lastModified;							//				дата последнего изменения заявки
	
	private StateType state;							//				статус выполнения заявки

	// Основные поля (могут быть заполнены в состоянии new и after inspection)
	private String productManufacturerAndModel;			// 				данные о модели устанавливаемой сплит-системы
	private String customerName;						// 				наименование заказчика (ФИО для физического лица, либо полное наименование для организации)
	private String targetAddress;						// 				адрес проведения работ

	// Дополнительные поля (могут быть заполнены только в состоянии after inspection)
	private Float pipeLineLength;						// [метры]		длина трубопроводной магистрали между внутренним и внешним блоками
	private Float additionalCoolantAmount;				// [кг]			количество дозаправленного хдадагента (если пришлось дозаправлять)
	private Boolean pumpNeeded;							//				необходимость установки дренажой помпы

	// Автоматические поля (рассчитываются системой, пользовательский ввод невозможен)
	private BigDecimal fullCost;							//				стоимость всех работ по заказу
	
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
	}
	
	public String getCustomerName() {
		return customerName;
	}
	public void setCustomerName(String customerName) throws FieldIsUnchangeable {
		if (state == StateType.STATE_COMPLETE || state == StateType.STATE_CANCELLED)
			throw new FieldIsUnchangeable("customerName");
		this.customerName = customerName;
	}
	
	public String getProductManufacturerAndModel() {
		return productManufacturerAndModel;
	}
	public void setProductManufacturerAndModel(
			String productManufacturerAndModel) throws FieldIsUnchangeable {
		if (state == StateType.STATE_COMPLETE || state == StateType.STATE_CANCELLED)
			throw new FieldIsUnchangeable("productManufacturerAndModel");
		this.productManufacturerAndModel = productManufacturerAndModel;
	}
	
	public Float getPipeLineLength() {
		return pipeLineLength;
	}
	public void setPipeLineLength(Float pipeLineLength) throws IncorrectValueException, FieldIsUnchangeable {
		if (state == StateType.STATE_NEW || state == StateType.STATE_COMPLETE || state == StateType.STATE_CANCELLED)
			throw new FieldIsUnchangeable("pipeLineLength");
		if (pipeLineLength <= 0) throw new IncorrectValueException("pipeLineLength should be positive");
		this.pipeLineLength = pipeLineLength;
	}
	
	public Float getAdditionalCoolantAmount() {
		return additionalCoolantAmount;
	}
	public void setAdditionalCoolantAmount(Float additionalCoolantAmount) throws IncorrectValueException, FieldIsUnchangeable {
		if (state == StateType.STATE_NEW || state == StateType.STATE_COMPLETE || state == StateType.STATE_CANCELLED)
			throw new FieldIsUnchangeable("additionalCoolantAmount");
		if (additionalCoolantAmount < 0) throw new IncorrectValueException("additionalCoolantAmount can not be negative");
		this.additionalCoolantAmount = additionalCoolantAmount;
	}
	
	public Boolean getPumpNeeded() {
		return pumpNeeded;
	}
	public void setPumpNeeded(Boolean pumpNeeded) throws FieldIsUnchangeable {
		if (state == StateType.STATE_NEW || state == StateType.STATE_COMPLETE || state == StateType.STATE_CANCELLED)
			throw new FieldIsUnchangeable("pumpNeeded");
		this.pumpNeeded = pumpNeeded;
	}
	
	public BigDecimal getFullCost() {
		return fullCost;
	}
	public void setFullCost(BigDecimal fullCost) throws IncorrectValueException, FieldIsUnchangeable {
		if (state == StateType.STATE_NEW || state == StateType.STATE_COMPLETE || state == StateType.STATE_CANCELLED)
			throw new FieldIsUnchangeable("fullCost");
		if (fullCost.compareTo(new BigDecimal(0)) < 0) throw new IncorrectValueException("fullCost can not be negative");
		this.fullCost = fullCost;
	}
	
	public StateType getState() {
		return state;
	}
	
	public void setState(StateType state) throws IncorrectOrderStateChange {
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
	}
	public Long getUid() {
		return uid;
	}
	public void setUid(Long uid) {
		this.uid = uid;
	}
}
