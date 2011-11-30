package il.aircon.controller;

import java.util.Date;

import il.aircon.model.IncorrectOrderStateChange;
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
	 */
	private static void setBasicOrderFields(
			Order order,
			String productManufacturerAndModel,
			String customerName, 
			String targetAddress) throws InvalidInputException, ArgumentCantBeNull
	{
		if (productManufacturerAndModel == null) throw new ArgumentCantBeNull("productManufacturerAndModel");
		if (productManufacturerAndModel.length() > 255) throw new InvalidInputException("productManufacturerAndModel");
		order.setProductManufacturerAndModel(productManufacturerAndModel);
		
		if (customerName == null) throw new ArgumentCantBeNull("customerName");
		if (customerName.length() > 255) throw new InvalidInputException("customerName");
		order.setCustomerName(customerName);
		
		if (targetAddress == null) throw new ArgumentCantBeNull("targetAddress");
		if (targetAddress.length() > 255) throw new InvalidInputException("targetAddress");
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
	 * @throws InvalidInputException
	 * @throws ArgumentCantBeNull
	 */
	private static void setAfterInspectionOrderFields(
			Order order,
			String pipeLineLength_s,
			String additionalCoolantAmount_s,
			Boolean pumpNeeded) throws InvalidInputException, ArgumentCantBeNull
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
		
		if (pumpNeeded == null) throw new ArgumentCantBeNull("pumpNeeded");
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
	 * @throws ArgumentCantBeNull В случае если один или несколько требуемых параметров имеют значение null
	 */
	public static Order CreateNewOrder(
			String productManufacturerAndModel,
			String customerName, 
			String targetAddress) throws InvalidInputException, ArgumentCantBeNull
	{
		Order res = new Order();
		res.setState(StateType.STATE_NEW);
		
		Date now = new Date();
		res.setCreated(now);
		
		setBasicOrderFields(res, productManufacturerAndModel, customerName, targetAddress);
		
		res.setLastModified(now);
		
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
	 */
	public static Order CreateInspectionCompleteOrder(
			String productManufacturerAndModel,
			String customerName, 
			String targetAddress, 
			String pipeLineLength_s,
			String additionalCoolantAmount_s,
			Boolean pumpNeeded) throws InvalidInputException, ArgumentCantBeNull
	{
		Order res = new Order();
		res.setState(StateType.STATE_AFTER_INSPECTION);
		
		Date now = new Date();
		res.setCreated(now);
		res.setLastModified(now);

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
	 * @throws IncorrectOrderStateChange В случае если сменить состояние указанным образом невозможно
	 * @throws InvalidInputException В случае неверных входных данных
	 */
	public static void ModifyOrder(
			Order order,
			StateType stateType,
			String productManufacturerAndModel,
			String customerName, 
			String targetAddress, 
			String pipeLineLength_s,
			String additionalCoolantAmount_s,
			Boolean pumpNeeded) throws ArgumentCantBeNull, ArgumentShouldBeNull, IncorrectOrderStateChange, InvalidInputException
	{
		if (order == null) throw new ArgumentCantBeNull("order");

		Date now = new Date();
		
		if (order.getState() == stateType)
		{
			switch (order.getState())
			{
			case STATE_CANCELLED:
			case STATE_COMPLETE:
				// Любые изменения запрещены
				if (productManufacturerAndModel != null) throw new ArgumentShouldBeNull("productManufacturerAndModel", "order is complete");
				if (customerName != null) throw new ArgumentShouldBeNull("customerName", "order is complete");
				if (targetAddress != null) throw new ArgumentShouldBeNull("targetAddress", "order is complete");
				if (pipeLineLength_s != null) throw new ArgumentShouldBeNull("pipeLineLength", "order is complete");
				if (additionalCoolantAmount_s != null) throw new ArgumentShouldBeNull("additionalCoolantAmount", "order is complete");
				if (pumpNeeded != null) throw new ArgumentShouldBeNull("pumpNeeded", "order is complete");
				break;
	
			case STATE_NEW:
				// Разрешена только правка основных полей

				if (pipeLineLength_s != null) throw new ArgumentShouldBeNull("pipeLineLength", "inspection isn't complete");
				if (additionalCoolantAmount_s != null) throw new ArgumentShouldBeNull("additionalCoolantAmount", "inspection isn't complete");
				if (pumpNeeded != null) throw new ArgumentShouldBeNull("pumpNeeded", "inspection isn't complete");

				setBasicOrderFields(order, productManufacturerAndModel, customerName, targetAddress);
				order.setLastModified(now);
				break;

			case STATE_AFTER_INSPECTION:
				// Разрешена правка любых полей 

				setBasicOrderFields(order, productManufacturerAndModel, customerName, targetAddress);
				setAfterInspectionOrderFields(order, pipeLineLength_s, additionalCoolantAmount_s, pumpNeeded);
				order.setLastModified(now);
				break;
			}				
		}
		else
		{
			if (order.getState() == StateType.STATE_NEW)
			{
				switch (stateType)
				{
				case STATE_CANCELLED:
					// При отмене заказа любая правка невозможна
					if (productManufacturerAndModel != null) throw new ArgumentShouldBeNull("productManufacturerAndModel", "order is cancelled");
					if (customerName != null) throw new ArgumentShouldBeNull("customerName", "order is cancelled");
					if (targetAddress != null) throw new ArgumentShouldBeNull("targetAddress", "order is cancelled");
					if (pipeLineLength_s != null) throw new ArgumentShouldBeNull("pipeLineLength", "order is cancelled");
					if (additionalCoolantAmount_s != null) throw new ArgumentShouldBeNull("additionalCoolantAmount", "order is cancelled");
					if (pumpNeeded != null) throw new ArgumentShouldBeNull("pumpNeeded", "order is cancelled");
					break;

				case STATE_COMPLETE:
					// Заказ не может быть переведен из состояния new сразу в complete
					throw new IncorrectOrderStateChange(order.getState(), stateType);

				case STATE_AFTER_INSPECTION:
					// При переводе из new в after inspection возможна любая правка полей заказа
					order.setLastModified(now);

					setBasicOrderFields(order, productManufacturerAndModel, customerName, targetAddress);
					setAfterInspectionOrderFields(order, pipeLineLength_s, additionalCoolantAmount_s, pumpNeeded);
					break;
				}
			}
			else if (order.getState() == StateType.STATE_AFTER_INSPECTION)
			{
				switch (stateType)
				{
				case STATE_CANCELLED:
					// При отмене заказа любая правка невозможна
					if (productManufacturerAndModel != null) throw new ArgumentShouldBeNull("productManufacturerAndModel", "order is cancelled");
					if (customerName != null) throw new ArgumentShouldBeNull("customerName", "order is cancelled");
					if (targetAddress != null) throw new ArgumentShouldBeNull("targetAddress", "order is cancelled");
					if (pipeLineLength_s != null) throw new ArgumentShouldBeNull("pipeLineLength", "order is cancelled");
					if (additionalCoolantAmount_s != null) throw new ArgumentShouldBeNull("additionalCoolantAmount", "order is cancelled");
					if (pumpNeeded != null) throw new ArgumentShouldBeNull("pumpNeeded", "order is cancelled");
					break;
				
				case STATE_NEW:
					// Заказ, для которого проведена инспекция, не может быть снова объявлен "новым".
					throw new IncorrectOrderStateChange(order.getState(), stateType);
				
				case STATE_COMPLETE:
					// Разрешена правка любых полей 
					setBasicOrderFields(order, productManufacturerAndModel, customerName, targetAddress);
					setAfterInspectionOrderFields(order, pipeLineLength_s, additionalCoolantAmount_s, pumpNeeded);
					order.setLastModified(now);
					break;

				}
			}
			else if (order.getState() == StateType.STATE_CANCELLED)
			{
				switch (stateType)
				{
				case STATE_NEW:
					// Разрешена только правка основных полей
					if (pipeLineLength_s != null) throw new ArgumentShouldBeNull("pipeLineLength", "inspection isn't complete");
					if (additionalCoolantAmount_s != null) throw new ArgumentShouldBeNull("additionalCoolantAmount", "inspection isn't complete");
					if (pumpNeeded != null) throw new ArgumentShouldBeNull("pumpNeeded", "inspection isn't complete");

					setBasicOrderFields(order, productManufacturerAndModel, customerName, targetAddress);
					order.setLastModified(now);
					break;

				case STATE_AFTER_INSPECTION:
					// Разрешена правка любых полей 
					setBasicOrderFields(order, productManufacturerAndModel, customerName, targetAddress);
					setAfterInspectionOrderFields(order, pipeLineLength_s, additionalCoolantAmount_s, pumpNeeded);
					order.setLastModified(now);
					break;
				case STATE_COMPLETE:
					// Отмененный заказ не может быть сразу объявлен завершенным успешно
					throw new IncorrectOrderStateChange(order.getState(), stateType);
				}				
			}
			else if (order.getState() == StateType.STATE_COMPLETE)
			{
				// Завершенный заказ нельзя править ни при каком условии
				if (productManufacturerAndModel != null) throw new ArgumentShouldBeNull("productManufacturerAndModel", "order is complete");
				if (customerName != null) throw new ArgumentShouldBeNull("customerName", "order is complete");
				if (targetAddress != null) throw new ArgumentShouldBeNull("targetAddress", "order is complete");
				if (pipeLineLength_s != null) throw new ArgumentShouldBeNull("pipeLineLength", "order is complete");
				if (additionalCoolantAmount_s != null) throw new ArgumentShouldBeNull("additionalCoolantAmount", "order is complete");
				if (pumpNeeded != null) throw new ArgumentShouldBeNull("pumpNeeded", "order is complete");
			}			
			order.setState(stateType);
		
		}
			
	}

}
