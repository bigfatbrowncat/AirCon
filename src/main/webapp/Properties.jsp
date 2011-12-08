<%@page import="il.aircon.model.IncorrectValueException" %>
<%@page import="il.aircon.controller.InvalidInputException" %>
<%@page import="il.aircon.controller.OrdersManager"%>
<%@page import="il.aircon.model.CalcProperties"%>
<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
	<meta http-equiv="Content-Type" content="text/html; charset=ISO-8859-1">
	<title>Настройки подсчета стоимости</title>
	<%!
		private static String modulesInstallationCost_INCORRECT_MESSAGE = "Значение стоимости установки блоков должно быть положительным числом или нулём. "
				+ "Допускаются десятичные дроби.";
		private static String baseTubeLength_INCORRECT_MESSAGE = "Длина соединительной трубы, входящей в стоимость заказа, должна быть положительным числом или нулём."
				+ "Допускаются десятичные дроби.";
		private static String tubeCost_INCORRECT_MESSAGE = "Значение погонной стоимости соединительной трубы должно быть положительным числом или нулём."
				+ "Допускаются десятичные дроби.";
		private static String coolantCost_INCORRECT_MESSAGE = "Значение цены охладителя должно быть положительным числом или нулём."
				+ "Допускаются десятичные дроби.";
		private static String pumpCost_INCORRECT_MESSAGE = "Значение цены помпы должно быть положительным числом или нулём."
				+ "Допускаются десятичные дроби.";
	%>
	<%
		// Берем параметры

		int notPresent = 0;
		String incorrect_input_message = null;
		
		boolean modulesInstallationCost_incorrect = false;
		boolean baseTubeLength_incorrect = false;
		boolean tubeCost_incorrect = false;
		boolean coolantCost_incorrect = false;
		boolean pumpCost_incorrect = false;
		
		boolean dont_edit = false;
		
		String modulesInstallationCost = request.getParameter("modulesInstallationCost");
		if (modulesInstallationCost == null)
		{
			modulesInstallationCost = "0";
			notPresent ++;
		}

		String baseTubeLength = request.getParameter("baseTubeLength");
		if (baseTubeLength == null) 
		{
			baseTubeLength = "0";
			notPresent ++;
		}

		String tubeCost = request.getParameter("tubeCost");
		if (tubeCost == null) 
		{
			tubeCost = "0";
			notPresent ++;
		}

		String coolantCost = request.getParameter("coolantCost");
		if (coolantCost == null) 
		{
			coolantCost = "0";
			notPresent ++;
		}

		String pumpCost = request.getParameter("pumpCost");
		if (pumpCost == null)
		{
			pumpCost = "0";
			notPresent ++;
		}

		if (notPresent == 5)
		{
			// Ни одного параметра не передано
			// В этом случае параметры просто загружаются из базы
			CalcProperties cpr = OrdersManager.GetCalcProperties();
			if (cpr != null)
			{
				modulesInstallationCost = cpr.getModulesInstallationCost().toPlainString();
				baseTubeLength = Float.toString(cpr.getBaseTubeLength());
				tubeCost = cpr.getTubeCost().toPlainString();
				coolantCost = cpr.getCoolantCost().toPlainString();
				pumpCost = cpr.getPumpCost().toPlainString();
			}
		}
		else if (notPresent == 0)
		{
			// Все параметры указаны, записываем значения в базу
			try
			{
				OrdersManager.SetCalcProperties(modulesInstallationCost, baseTubeLength, tubeCost, coolantCost, pumpCost);
				dont_edit = true;
			}
			catch (InvalidInputException iie)
			{
				if (iie.getFieldName().equals("modulesInstallationCost")) 
				{
					modulesInstallationCost_incorrect = true;
					incorrect_input_message = modulesInstallationCost_INCORRECT_MESSAGE;
				}
				if (iie.getFieldName().equals("baseTubeLength")) 
				{
					baseTubeLength_incorrect = true;
					incorrect_input_message = baseTubeLength_INCORRECT_MESSAGE;
				}
				if (iie.getFieldName().equals("tubeCost"))
				{
					tubeCost_incorrect = true;
					incorrect_input_message = tubeCost_INCORRECT_MESSAGE;
				}
				if (iie.getFieldName().equals("coolantCost"))
				{
					coolantCost_incorrect = true;
					incorrect_input_message = coolantCost_INCORRECT_MESSAGE;
				}
				if (iie.getFieldName().equals("pumpCost"))
				{
					pumpCost_incorrect = true;
					incorrect_input_message = pumpCost_INCORRECT_MESSAGE;
				}
			} catch (IncorrectValueException ive) {
				if (ive.getFieldName().equals("modulesInstallationCost")) 
				{
					modulesInstallationCost_incorrect = true;
					incorrect_input_message = modulesInstallationCost_INCORRECT_MESSAGE;
				}
				if (ive.getFieldName().equals("baseTubeLength")) 
				{
					baseTubeLength_incorrect = true;
					incorrect_input_message = baseTubeLength_INCORRECT_MESSAGE;
				}
				if (ive.getFieldName().equals("tubeCost"))
				{
					tubeCost_incorrect = true;
					incorrect_input_message = tubeCost_INCORRECT_MESSAGE;
				}
				if (ive.getFieldName().equals("coolantCost"))
				{
					coolantCost_incorrect = true;
					incorrect_input_message = coolantCost_INCORRECT_MESSAGE;
				}
				if (ive.getFieldName().equals("pumpCost"))
				{
					pumpCost_incorrect = true;
					incorrect_input_message = pumpCost_INCORRECT_MESSAGE;
				}				
			}
		}
			
	%>
	<style>
		input.incorrect { background: #ffaaaa }
		.incorrect_input_msg { color: #880000 }
		.success { color: #008800; font-size: 200%; }
  	</style>	
</head>
<body>
<% 
	if (!dont_edit) 
	{ 
%>
		<form method="post">
			<table>
				<tr>
					<td style="width: 300pt">Стоимость установки блоков:</td>
					<td><input
						<%= modulesInstallationCost_incorrect ? "class=\"incorrect\"" : "" %>
						style="width: 250pt" 
						name="modulesInstallationCost" 
						id="modulesInstallationCost" 
						type="text" 
						value="<%= modulesInstallationCost %>" />, <b>руб</b>
					</td>
				</tr>
				<tr>
					<td style="width: 300pt">Длина магистрали, включенная в стоимость установки:</td>
					<td><input 
						style="width: 250pt" 
						name="baseTubeLength" 
						id="baseTubeLength" 
						type="text" 
						value="<%= baseTubeLength %>" />, <b>м</b>
					</td>
				</tr>
				<tr>
					<td style="width: 300pt">Стоимость прокладки магистрали:</td>
					<td><input 
						style="width: 250pt" 
						name="tubeCost" 
						id="tubeCost" 
						type="text" 
						value="<%= tubeCost %>" />, <b>руб/м</b></td>
				</tr>
				<tr>
					<td style="width: 300pt">Стоимость хладагента:</td>
					<td><input 
						style="width: 250pt" 
						name="coolantCost" 
						id="coolantCost" 
						type="text" 
						value="<%= coolantCost %>" />, <b>руб/кг</b></td>
				</tr>
				<tr>
					<td style="width: 300pt">Стоимость дренажной помпы:</td>
					<td><input 
						style="width: 250pt" 
						name="pumpCost" 
						id="pumpCost" 
						type="text" 
						value="<%= pumpCost %>" />, <b>руб</b></td>
				</tr>
			</table>
			<input style="padding: 2px; margin: 2px; margin-top: 5px; "type="submit" value="Сохранить" />		
		</form>
<%	} 
	else 
	{ 
%>
		<p class="success">Параметры сохранены.</p>
<%
	} 
%>
</body>
</html>