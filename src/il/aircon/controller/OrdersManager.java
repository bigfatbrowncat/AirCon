package il.aircon.controller;

import java.util.Date;

import il.aircon.model.FieldIsUnchangeable;
import il.aircon.model.IncorrectOrderStateChange;
import il.aircon.model.IncorrectValueException;
import il.aircon.model.Order;
import il.aircon.model.Order.StateType;

/**
 * <p>Класс предназначен для манипулирования данными модели на основании действий пользователя.
 * Абстрагирован от представления данных внутри модели и от вида пользовательского интерфейса.</p>
 * 
 * <p>Зона ответственности:
 * <ol>
 * 	<li>Формирование и поиск сущностей модели на основании запроса слоя пользовательского интерфейса</li>
 * 	<li>Анализ запросов пользовательского интерфейса на предмет корректности. Иными словами если пользователь
 *      вводит некоторое числовоее значение в строковое поле ввода, корректность введенного значения проверяется
 *      здесь.</li>
 * </ol>
 * </p> 
 * @author Илья Майзус
 *
 */
public final class OrdersManager 
{
	/**
	 * Отвечает за ввод основных полей заказа. 
	 * Правка основных полей в обход этой функции нежелательна.
	 * @param order Заказ
	 * @param productManufacturerAndModel Наименование производителя и модели устанавливаемого прибора
	 * @param customerName Наименование заказчика
	 * @param targetAddress Адрес, по которому предполагается произвести монтаж
	 * @throws InvalidInputException В влучае неверных входных данных
	 * @throws ArgumentCantBeNull В случае если один или несколько переданных параметров имеют значение null
	 * @throws FieldIsUnchangeable В случае если данное поле не может быть изменено при данном состоянии заказа
	 * @throws IncorrectValueException В случае некорректного значения
	 */
	private static void setBasicOrderFields(
			Order order,
			String productManufacturerAndModel,
			String customerName, 
			String targetAddress) throws InvalidInputException, ArgumentCantBeNull, FieldIsUnchangeable, IncorrectValueException
	{
		order.setProductManufacturerAndModel(productManufacturerAndModel);
		order.setCustomerName(customerName);
		order.setTargetAddress(targetAddress);		
	}

	/**
	 * Отвечает за ввод дополнительных полей заказа.
	 * Запускает расчет полной стоимости выполнения заказа. 
	 * Правка дополнительных полей в обход этой функции нежелательна.
	 * @param order Заказ
	 * @param pipeLineLength_s Строка, содержащая длину трубопроводной магистрали между внутренним и внешним блоками в метрах
	 * @param additionalCoolantAmount_s Строка, содержащая количество дозаправленного хдадагента в килограммах
	 * @param pumpNeeded Необходимость установки дренажой помпы
	 * @throws InvalidInputException В влучае неверных входных данных
	 * @throws ArgumentCantBeNull В случае если один или несколько переданных параметров имеют значение null
	 * @throws FieldIsUnchangeable В случае если данное поле не может быть изменено при данном состоянии заказа
	 * @throws IncorrectValueException В случае некорректного значения
	 */
	private static void setAfterInspectionOrderFields(
			Order order,
			String pipeLineLength_s,
			String additionalCoolantAmount_s,
			Boolean pumpNeeded) throws InvalidInputException, ArgumentCantBeNull, IncorrectValueException, FieldIsUnchangeable
	{
		if (pipeLineLength_s == null) throw new ArgumentCantBeNull("pipeLineLength");
		try
		{
			float pipeLineLength = Float.parseFloat(pipeLineLength_s);
			order.setPipeLineLength(pipeLineLength);
		}
		catch (NumberFormatException ex)
		{
			throw new InvalidInputException("pipeLineLength");
		}
		
		if (additionalCoolantAmount_s == null) throw new ArgumentCantBeNull("additionalCoolantAmount");
		try
		{
			float additionalCoolantAmount = Float.parseFloat(additionalCoolantAmount_s);
			order.setAdditionalCoolantAmount(additionalCoolantAmount);
		}
		catch (NumberFormatException ex)
		{
			throw new InvalidInputException("additionalCoolantAmount");
		}
		
		order.setPumpNeeded(pumpNeeded);
		
		calculateFullCost(order);		
	}
	
	/**
	 * Функция вычисляет и вписывает полную стоимость выполнения заказа
	 * @param order Заказ
	 */
	private static void calculateFullCost(Order order) {
		// TODO Auto-generated method stub
		
	}
	
	/**
	 * Создает новый заказ. Присваивает ему статус {@link il.aircon.model.Order.StateType.STATE_NEW}.
	 * @param productManufacturerAndModel Наименование производителя и модели устанавливаемого прибора
	 * @param customerName Наименование заказчика
	 * @param targetAddress Адрес, по которому предполагается произвести монтаж
	 * @return Возвращается объект {@link il.aircon.model.Order}
	 * @throws InvalidInputException В влучае неверных входных данных
	 * @throws ArgumentCantBeNull В случае если один или несколько переданных параметров имеют значение null
	 * @throws FieldIsUnchangeable В случае если данное поле не может быть изменено при данном состоянии заказа
	 * @throws IncorrectValueException В случае некорректного значения
	 * @throws IncorrectOrderStateChange В случае если сменить состояние указанным образом невозможно
	 */
	public static Order CreateNewOrder(
			String productManufacturerAndModel,
			String customerName, 
			String targetAddress) throws InvalidInputException, ArgumentCantBeNull, FieldIsUnchangeable, IncorrectOrderStateChange, IncorrectValueException
	{
		Order res = new Order();
		res.setState(StateType.STATE_NEW);
		setBasicOrderFields(res, productManufacturerAndModel, customerName, targetAddress);
		return res;
	}
	
	/**
	 * Создает новый заказ, предполагая, что место уже осмотрено. 
	 * Присваивает ему статус {@link il.aircon.model.Order.StateType.STATE_AFTER_INSPECTION}.
	 * Рассчитывает стоимость работ.
	 * 
	 * @param productManufacturerAndModel Наименование производителя и модели устанавливаемого прибора
	 * @param customerName Наименование заказчика
	 * @param targetAddress Адрес, по которому предполагается произвести монтаж
	 * @param pipeLineLength_s Строка, содержащая длину трубопроводной магистрали между внутренним и внешним блоками в метрах
	 * @param additionalCoolantAmount_s Строка, содержащая количество дозаправленного хдадагента в килограммах
	 * @param pumpNeeded Необходимость установки дренажой помпы
	 * @return Возвращается объект {@link il.aircon.model.Order}
	 * @throws InvalidInputException В влучае неверных входных данных
	 * @throws ArgumentCantBeNull В случае если один или несколько требуемых параметров имеют значение null
	 * @throws FieldIsUnchangeable В случае если данное поле не может быть изменено при данном состоянии заказа
	 * @throws IncorrectValueException В случае неверного ввода данных
	 * @throws IncorrectOrderStateChange В случае если сменить состояние указанным образом невозможно
	 */
	public static Order CreateInspectionCompleteOrder(
			String productManufacturerAndModel,
			String customerName, 
			String targetAddress, 
			String pipeLineLength_s,
			String additionalCoolantAmount_s,
			Boolean pumpNeeded) throws InvalidInputException, ArgumentCantBeNull, IncorrectOrderStateChange, FieldIsUnchangeable, IncorrectValueException
	{
		Order res = new Order();
		res.setState(StateType.STATE_AFTER_INSPECTION);
		
		setBasicOrderFields(res, productManufacturerAndModel, customerName, targetAddress);
		setAfterInspectionOrderFields(res, pipeLineLength_s, additionalCoolantAmount_s, pumpNeeded);

		return res;
	}

	/**
	 * Изменяет состояние и/или свойства заказа
	 * @param productManufacturerAndModel Наименование производителя и модели устанавливаемого прибора
	 * @param customerName Наименование заказчика
	 * @param targetAddress Адрес, по которому предполагается произвести монтаж
	 * @param pipeLineLength_s Строка, содержащая длину трубопроводной магистрали между внутренним и внешним блоками в метрах
	 * @param additionalCoolantAmount_s Строка, содержащая количество дозаправленного хдадагента в килограммах
	 * @param pumpNeeded Необходимость установки дренажой помпы
	 * @throws ArgumentCantBeNull В случае если один или несколько требуемых параметров имеют значение null
	 * @throws ArgumentShouldBeNull В случае если указаны неиспользуемые параметры
	 * @throws InvalidInputException В случае неверных входных данных
	 * @throws FieldIsUnchangeable В случае если данное поле не может быть изменено при данном состоянии заказа
	 * @throws IncorrectValueException В случае некорректного значения
	 * @throws IncorrectOrderStateChange В случае если сменить состояние указанным образом невозможно
	 */
	public static void ModifyOrder(
			Order order,
			StateType stateType,
			String productManufacturerAndModel,
			String customerName, 
			String targetAddress, 
			String pipeLineLength_s,
			String additionalCoolantAmount_s,
			Boolean pumpNeeded) throws ArgumentCantBeNull, ArgumentShouldBeNull, IncorrectOrderStateChange, InvalidInputException, FieldIsUnchangeable, IncorrectValueException
	{
		if (order == null) throw new ArgumentCantBeNull("order");
		order.setState(stateType);
		
		if (stateType == StateType.STATE_NEW || stateType == StateType.STATE_AFTER_INSPECTION)
			setBasicOrderFields(order, productManufacturerAndModel, customerName, targetAddress);
		
		if (stateType == StateType.STATE_AFTER_INSPECTION)
			setAfterInspectionOrderFields(order, pipeLineLength_s, additionalCoolantAmount_s, pumpNeeded);
			
	}

}
