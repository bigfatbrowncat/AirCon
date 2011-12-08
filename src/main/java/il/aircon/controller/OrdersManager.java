package il.aircon.controller;

import java.lang.reflect.Array;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;

import org.hibernate.Transaction;
import org.hibernate.Query;
import org.hibernate.Session;
import org.hibernate.SessionFactory;

import il.aircon.model.CalcProperties;
import il.aircon.model.FieldIsUnchangeable;
import il.aircon.model.HibernateUtil;
import il.aircon.model.IncorrectOrderStateChange;
import il.aircon.model.IncorrectValueException;
import il.aircon.model.Order;
import il.aircon.model.Order.StateType;

/**
 * <p>Класс предназначен для манипулирования данными модели на основании действий пользователя.
 * абстрагирован от представления данных внутри модели и от вида пользовательского интерфейса.</p>
 * 
 * <p>Зона ответственности:
 * <ol>
 * 	<li>Формирование и поиск сущностей модели на основании запроса слоя пользовательского интерфейса</li>
 * 	<li>Анализ запросов пользовательского интерфейса на предмет корректности. Иными словами, если пользователь
 *      вводит некоторое числовоее значение в строковое поле ввода, корректность введенного значения проверсется
 *      здесь.</li>
 * </ol>
 * </p> 
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
	 * @throws InvalidInputException В случае неверных входных данных
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
	 * @param additionalCoolantAmount_s Строка, содержащая количество дозаправленного хладагента в килограммах
	 * @param pumpNeeded Необходимость установки дренажой помпы
	 * @throws InvalidInputException В случае неверных входных данных
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
	
	private static String replaceWithPercents(String str)
	{
		if (str == null) return "%";
		
		StringBuilder sb = new StringBuilder("%");
		for (int i = 0; i < str.length(); i++)
		{
			char ch = str.charAt(i);
			if (ch == ' ' || ch == '\t') sb.append('%'); else sb.append(ch);
		}
		if (sb.toString().length() > 1) sb.append("%");
		return sb.toString();
	}
	
	public static Order[] SearchOrder(String customerNameSearchRequest,
			String productManufacturerAndModelSearchRequest,
			String targetAddressSearchRequest,
    		boolean searchNew, 
    		boolean searchInspected, 
    		boolean searchCompleted, 
    		boolean searchCancelled)
	{
		SessionFactory sessionFactory = HibernateUtil.getSessionFactory();
		Session session = sessionFactory.openSession();
        
		String qadd1 = "customerName like '" + replaceWithPercents(customerNameSearchRequest) + "'";
		String qadd2 = "productManufacturerAndModel like '" + replaceWithPercents(productManufacturerAndModelSearchRequest) + "'";
		String qadd3 = "targetAddress like '" + replaceWithPercents(targetAddressSearchRequest) + "'";
		
		String qadd4 = searchNew ? "state = '0'" : "";

		String orr = (qadd4.equals("") ? "" : " or "); 
		qadd4 += (searchInspected ? orr + " state = '1'" : "");
		
		orr = (qadd4.equals("") ? "" : " or ");
		qadd4 += (searchCompleted ? orr + " state = '2'" : "");
		
		orr = (qadd4.equals("") ? "" : " or ");
		qadd4 += (searchCancelled ? orr + " state = '3'" : "");
		
        Query query = session.createQuery("from Order where " + qadd1 + " and " + qadd2 + " and " + qadd3 +
        		(!qadd4.equals("") ? " and (" + qadd4 + ")" : ""));
        List list = query.list();
        
        session.close();
        return (Order[]) list.toArray(new Order[] {});
	}
	
	/**
	 * Функция вычислсет и вписывает полную стоимость выполнения заказа
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
	 * @throws InvalidInputException В случае неверных входных данных
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
		
		Session session = HibernateUtil.openSession();
		session.save(res);
		session.close();
		
		return res;
	}
	
	/**
	 * Создает новый заказ, предполагас, что место уже осмотрено. 
	 * Присваивает ему статус {@link il.aircon.model.Order.StateType.STATE_AFTER_INSPECTION}.
	 * Рассчитывает стоимость работ.
	 * 
	 * @param productManufacturerAndModel саименование производителс и модели устанавливаемого прибора
	 * @param customerName саименование заказчика
	 * @param targetAddress сдрес, по которому предполагаетсс произвести монтаж
	 * @param pipeLineLength_s Строка, содержащая длину трубопроводной магистрали между внутренним и внешним блоками в метрах
	 * @param additionalCoolantAmount_s Строка, содержащая количество дозаправленного хладагента в килограммах
	 * @param pumpNeeded необходимость установки дренажой помпы
	 * @return Возвращаетсс объект {@link il.aircon.model.Order}
	 * @throws InvalidInputException В случае неверных входных данных
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

		Session session = HibernateUtil.openSession();
		session.save(res);
		session.close();

		return res;
	}

	/**
	 * Изменсет состояние и/или свойства заказа
	 * @param productManufacturerAndModel саименование производителс и модели устанавливаемого прибора
	 * @param customerName саименование заказчика
	 * @param targetAddress сдрес, по которому предполагаетсс произвести монтаж
	 * @param pipeLineLength_s Строка, содержащая длину трубопроводной магистрали между внутренним и внешним блоками в метрах
	 * @param additionalCoolantAmount_s Строка, содержащая количество дозаправленного хладагента в килограммах
	 * @param pumpNeeded необходимость установки дренажой помпы
	 * @throws ArgumentCantBeNull В случае если один или несколько требуемых параметров имеют значение null
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
			Boolean pumpNeeded) throws ArgumentCantBeNull, IncorrectOrderStateChange, InvalidInputException, FieldIsUnchangeable, IncorrectValueException
	{
		if (order == null) throw new ArgumentCantBeNull("order");
		order.setState(stateType);
		
		if (stateType == StateType.STATE_NEW || stateType == StateType.STATE_AFTER_INSPECTION)
			setBasicOrderFields(order, productManufacturerAndModel, customerName, targetAddress);
		
		if (stateType == StateType.STATE_AFTER_INSPECTION)
			setAfterInspectionOrderFields(order, pipeLineLength_s, additionalCoolantAmount_s, pumpNeeded);
		
		Session session = HibernateUtil.openSession();
		session.merge(order);
		session.flush();
		session.close();
	}
	
	public static Order GetOrderById(int id)
	{
		SessionFactory sessionFactory = HibernateUtil.getSessionFactory();
		Session session = sessionFactory.openSession();
		
		Query query = session.createQuery("from Order where uid=" + id);
		List ords = query.list();
		
		session.close();
		if (ords.size() == 1) return (Order)ords.get(0);
		else
			return null;
	}
	
	public static CalcProperties GetCalcProperties()
	{
		SessionFactory sessionFactory = HibernateUtil.getSessionFactory();
		Session session = sessionFactory.openSession();
		
		Query query = session.createQuery("from CalcProperties");
		List ords = query.list();
		
		session.close();
		if (ords.size() == 1) return (CalcProperties)ords.get(0);
		else
			return null;
	}
	
	public static void SetCalcProperties(
			String modulesInstallationCost_s,
			String baseTubeLength_s,
			String tubeCost_s,
			String coolantCost_s,
			String pumpCost_s) throws IncorrectValueException, ArgumentCantBeNull, InvalidInputException
	{
		CalcProperties cpr;
		
		SessionFactory sessionFactory = HibernateUtil.getSessionFactory();
		Session session = sessionFactory.openSession();
		Transaction ta = session.beginTransaction();
		
		Query query = session.createQuery("from CalcProperties");
		List ords = query.list();
		
		if (ords.size() == 1) 
			cpr = (CalcProperties)ords.get(0);
		else
			cpr = new CalcProperties();	
		
		
		if (modulesInstallationCost_s == null) throw new ArgumentCantBeNull("modulesInstallationCost");
		try
		{
			BigDecimal modulesInstallationCost = new BigDecimal(modulesInstallationCost_s);
			cpr.setModulesInstallationCost(modulesInstallationCost);
		}
		catch (NumberFormatException ex)
		{
			throw new InvalidInputException("modulesInstallationCost");
		}		

		
		if (baseTubeLength_s == null) throw new ArgumentCantBeNull("baseTubeLength");
		try
		{
			float baseTubeLength = Float.parseFloat(baseTubeLength_s);
			cpr.setBaseTubeLength(baseTubeLength);
		}
		catch (NumberFormatException ex)
		{
			throw new InvalidInputException("baseTubeLength");
		}		

		
		if (tubeCost_s == null) throw new ArgumentCantBeNull("tubeCost");
		try
		{
			BigDecimal tubeCost = new BigDecimal(tubeCost_s);
			cpr.setTubeCost(tubeCost);
		}
		catch (NumberFormatException ex)
		{
			throw new InvalidInputException("tubeCost");
		}		

	
		if (coolantCost_s == null) throw new ArgumentCantBeNull("coolantCost");
		try
		{
			BigDecimal coolantCost = new BigDecimal(coolantCost_s);
			cpr.setCoolantCost(coolantCost);
		}
		catch (NumberFormatException ex)
		{
			throw new InvalidInputException("coolantCost");
		}	

		
		if (pumpCost_s == null) throw new ArgumentCantBeNull("pumpCost");
		try
		{
			BigDecimal pumpCost = new BigDecimal(pumpCost_s);
			cpr.setPumpCost(pumpCost);
		}
		catch (NumberFormatException ex)
		{
			throw new InvalidInputException("pumpCost");
		}
		
		session.saveOrUpdate(cpr);
		ta.commit();

		session.close();
	}

}
