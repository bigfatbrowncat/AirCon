package il.aircon.view;

import il.aircon.controller.ArgumentCantBeNull;
import il.aircon.controller.InvalidInputException;
import il.aircon.controller.OrderIsCompleteException;
import il.aircon.controller.OrdersManager;
import il.aircon.model.FieldIsUnchangeable;
import il.aircon.model.HibernateUtil;
import il.aircon.model.IncorrectOrderStateChange;
import il.aircon.model.IncorrectValueException;
import il.aircon.model.Order;
import il.aircon.model.Order.StateType;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.Currency;

import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.hibernate.Session;
import org.hibernate.SessionFactory;

/**
 * Servlet implementation class CreateOrder
 */
@WebServlet(description = "User form for creating or editing order", urlPatterns = { "/edit/" })
public final class EditOrder extends HttpServlet {
	private static final long serialVersionUID = 1L;
      
	private static String pipeLineLength_INCORRECT_MESSAGE = "Значение длины трубопроводной магистрали между внутренним и внешним блоками должно быть положительным числом. "
			+ "Допускаются десятичные дроби.";
	private static String additionalCoolantAmount_INCORRECT_MESSAGE = "Количество дозаправленного хладагента должно быть положительным числом или нулём. "
			+ "Допускаются десятичные дроби.";
	
    /**
     * @see HttpServlet#HttpServlet()
     */
    public EditOrder() {
        super();
    }

    private void printForm(
    		PrintWriter pw, 
    		int id,
    		Order.StateType state,
    		String productManufacturerAndModel,
    		String customerName,
    		String targetAddress,
    		String pipeLineLength,
    		String additionalCoolantAmount,
    		Boolean pumpNeeded,
    		String fullCost,
    		
    		boolean pipeLineLength_incorrect,
    		boolean additionalCoolantAmount_incorrect,
    		
    		String message) throws UnsupportedOrderState
    {
    	boolean newOrder = id == -1;
    	
		pw.printf("<html><head>");
		if (newOrder)
		{
			pw.printf("<title>Создание заказа</title>\n");
		}
		else
		{
			pw.printf("<title>Редактирование заказа</title>\n");
		}

		pw.printf("<script language=\"javascript\">\n");
		
		pw.printf("function check_additional()\n");
		pw.printf("{\n");
		pw.printf("  var selstate = document.getElementById(\"state\");\n");
		pw.printf("  var editing_disabled = ((selstate.value == \"complete\") || (selstate.value == \"cancelled\")) ? \"disabled\" : \"\";");

		pw.printf("  document.getElementById(\"pipeLineLength_row\").style.visibility = ((selstate.value != \"new\") ? \"visible\" : \"hidden\");\n");
		pw.printf("  document.getElementById(\"additionalCoolantAmount_row\").style.visibility = ((selstate.value != \"new\") ? \"visible\" : \"hidden\");\n");
		pw.printf("  document.getElementById(\"pumpNeeded_row\").style.visibility = ((selstate.value != \"new\") ? \"visible\" : \"hidden\");\n");
		pw.printf("  document.getElementById(\"fullCost_row\").style.visibility = ((selstate.value != \"new\") ? \"visible\" : \"hidden\");\n");

		pw.printf("  document.getElementById(\"productManufacturerAndModel\").disabled = editing_disabled;\n");
		pw.printf("  document.getElementById(\"customerName\").disabled = editing_disabled;\n");
		pw.printf("  document.getElementById(\"targetAddress\").disabled = editing_disabled;\n");
		pw.printf("  document.getElementById(\"pipeLineLength\").disabled = editing_disabled;\n");
		pw.printf("  document.getElementById(\"additionalCoolantAmount\").disabled = editing_disabled;\n");
		pw.printf("  document.getElementById(\"pumpNeeded\").disabled = editing_disabled;\n");
		pw.printf("}\n");
		pw.printf("</script>\n");
		
		pw.printf("<style>\n");
		pw.printf("  input.incorrect { background: #ffaaaa }\n");
		pw.printf("  .incorrect_input_msg { color: #880000 }\n");
		pw.printf("</style>\n");
		pw.printf("</head><body>\n");
		pw.printf("<form method=\"post\">\n");
		
		if (!newOrder)
			pw.printf("<input type=\"hidden\" id=\"id\" value=\"%1$d\" />", id);
		
		pw.printf("<table>\n");

		pw.printf("<tr>\n");
		pw.printf("<td style=\"width: 300pt\">Состояние заказа:</td>");
		
		boolean new_selected = false, 
				after_insp_selected = false,
				complete_selected = false,
				cancelled_selected = false;
		
		if (state == StateType.STATE_NEW)
		{
			new_selected = true;
		}
		else if (state == StateType.STATE_AFTER_INSPECTION)
		{
			after_insp_selected = true;
		}
		else if (state == StateType.STATE_COMPLETE)
		{
			complete_selected = true;
		}
		else if (state == StateType.STATE_CANCELLED)
		{
			cancelled_selected = true;
		}
		else
			throw new UnsupportedOrderState(state);
			
		
		pw.printf("<td><select id=\"state\" name=\"state\" onchange=\"check_additional();\" style=\"width: 250pt\" %1$s>", complete_selected ? "disabled=\"disabled\"" : "");
		if (state == Order.StateType.STATE_NEW)
		{
			pw.printf("<option value=\"new\" %1$s>Новый</option>", new_selected ? "selected" : "");
		}
		pw.printf("<option value=\"after_insp\" %1$s>Осмотр произведен</option>", after_insp_selected ? "selected" : "");
		if (!newOrder)
		{
			if (state == Order.StateType.STATE_AFTER_INSPECTION)
			{
				pw.printf("<option value=\"complete\" %1$s>Завершен</option>", complete_selected ? "selected" : "");
			}
			if (state != Order.StateType.STATE_COMPLETE)
			{
				pw.printf("<option value=\"cancelled\" %1$s>Отменен</option>", cancelled_selected ? "selected" : "");
			}
		}
		pw.printf("</select></td>");
		pw.printf("</tr>\n");
		
		pw.printf("<tr id=\"productManufacturerAndModel_row\" >\n");
		pw.printf("<td>Производитель и модель:</td> <td><input style=\"width: 250pt\" name=\"productManufacturerAndModel\" id=\"productManufacturerAndModel\" type=\"text\" value=\"%1$s\" /></td>", productManufacturerAndModel);
		pw.printf("</tr>\n");

		pw.printf("<tr id=\"customerName_row\">\n");
		pw.printf("<td>Наименование заказчика:</td> <td><input style=\"width: 250pt\" name=\"customerName\" id=\"customerName\" type=\"text\" value=\"%1$s\" /></td>", customerName);
		pw.printf("</tr>\n");

		pw.printf("<tr id=\"targetAddress_row\">\n");
		pw.printf("<td>Адрес проведения работ:</td> <td><input style=\"width: 250pt\" name=\"targetAddress\" id=\"targetAddress\" type=\"text\" value=\"%1$s\" /></td>", targetAddress);
		pw.printf("</tr>\n");

		pw.printf("<tr id=\"pipeLineLength_row\" style=\"visibility: hidden\">\n");
		pw.printf("<td>Длина магистрали между блоками:</td> <td><input %2$s style=\"width: 250pt\" name=\"pipeLineLength\" id=\"pipeLineLength\" type=\"text\" value=\"%1$s\" />, <b>м</b></td>", 
				pipeLineLength, 
				pipeLineLength_incorrect ? "class=\"incorrect\"" : "");
		pw.printf("</tr>\n");

		pw.printf("<tr id=\"additionalCoolantAmount_row\" style=\"visibility: hidden\">\n");
		pw.printf("<td>Количество дозаправленного хладагента:</td> <td><input %2$s style=\"width: 250pt\" name=\"additionalCoolantAmount\" id=\"additionalCoolantAmount\" type=\"text\" value=\"%1$s\" />, <b>кг</b></td>", 
				additionalCoolantAmount, 
				additionalCoolantAmount_incorrect ? "class=\"incorrect\"" : "");
		pw.printf("</tr>\n");

		pw.printf("<tr id=\"pumpNeeded_row\" style=\"visibility: hidden\">\n");
		pw.printf("<td>Необходима установка дренажной помпы:</td><td><input style=\"width: 250pt\" name=\"pumpNeeded\" id=\"pumpNeeded\" type=\"checkbox\" %1$s></td>", ((pumpNeeded != null && pumpNeeded) ? "checked" : ""));
		pw.printf("</tr>\n");

		pw.printf("<tr id=\"fullCost_row\" style=\"visibility: hidden\">\n");
		pw.printf("<td>Общая стоимость:</td> <td><input readonly style=\"width: 250pt\" name=\"fullCost\" id=\"fullCost\" type=\"text\" value=\"%1$s\" />, <b>кг</b></td>", 
				fullCost);
		pw.printf("</tr>\n");

		pw.printf("</tr>\n");
		
		pw.printf("</table>\n");

		if (message != null && !message.equals(""))
		{
			pw.printf("<p class=\"incorrect_input_msg\"><b>%1$s</b></p>\n", message);		
		}
		
		pw.println("<input style=\"padding: 2px; margin: 2px; margin-top: 5px; \"type=\"submit\" value=\"Принять заказ\" />");		
		
		pw.printf("</form>");
		pw.printf("</body>");
		pw.printf("<script language=\"javascript\">\n");
		pw.printf("check_additional();\n");
		pw.printf("</script>");
		pw.printf("</html>");    	
    }
    
	private void printSuccess(PrintWriter pw) {
		pw.printf("<html><head>");
		pw.printf("<title>Редактирование заказа</title>\n");
		pw.printf("<style>\n");
		pw.printf("  .success { color: #008800; font-size: 200%%; }\n");
		pw.printf("</style>\n");
		pw.printf("</head><body>\n");
		pw.printf("<p class=\"success\">Заказ принят.</p>");
		pw.printf("</body>");
		pw.printf("</html>");    	
	}    

	private void printNotFound(PrintWriter pw) {
		pw.printf("<html><head>");
		pw.printf("<title>Создание заказа</title>\n");
		pw.printf("<style>\n");
		pw.printf("  .notfound { color: #880000; font-size: 200%%; }\n");
		pw.printf("</style>\n");
		pw.printf("</head><body>\n");
		pw.printf("<p class=\"notfound\">Заказ не найден.</p>");
		pw.printf("</body>");
		pw.printf("</html>");    	
	}    
	
	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		request.setCharacterEncoding("UTF-8");
		response.setContentType("text/html; charset=UTF-8");
		response.setCharacterEncoding("UTF-8");

		PrintWriter pw = response.getWriter();

		int id = -1;
		if (request.getParameter("id") != null && request.getParameter("id") != "")
		{
			try
			{
				id = Integer.parseInt(request.getParameter("id"));
			} 
			catch (NumberFormatException exc)
			{
				// Do nothing. Just go on...
			}
		}
		
		Order got = OrdersManager.GetOrderById(id); 

		
		if (id == -1)
		{
			// Форма создания нового заказа
			try {
				printForm(pw, id, StateType.STATE_NEW, 
						"", "", "", "0", "0", true, "0", false, false, null);
			} catch (UnsupportedOrderState e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		else if (got == null)
		{
			printNotFound(pw);
		}
		else
		{
			
			// Форма правки существующего заказа
			try {
				printForm(pw, id, 
						got.getState(), 
						got.getProductManufacturerAndModel(), 
						got.getCustomerName(), 
						got.getTargetAddress(), 
						got.getPipeLineLength() != null ? got.getPipeLineLength().toString() : null,
						got.getAdditionalCoolantAmount() != null ? got.getAdditionalCoolantAmount().toString() : null, 
						got.getPumpNeeded(), 
						got.getFullCost().toString(),
						false, false, null);
			} catch (UnsupportedOrderState e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}			
		}

	}
	
	@Override
	protected void doPost(HttpServletRequest req, HttpServletResponse resp)
			throws ServletException, IOException {
		req.setCharacterEncoding("UTF-8");
		resp.setContentType("text/html; charset=UTF-8");
		resp.setCharacterEncoding("UTF-8");
		
		String message = null;
		
		int id = -1;
		if (req.getParameter("id") != null && req.getParameter("id") != "")
		{
			try
			{
				id = Integer.parseInt(req.getParameter("id"));
			} 
			catch (NumberFormatException exc)
			{
				// Do nothing. Just go on...
			}
		}
		
		Order got = OrdersManager.GetOrderById(id); 
		
		try
		{
			String state = req.getParameter("state");
			StateType stateType;
			if (state.equals("new"))
			{
				stateType = StateType.STATE_NEW;
			}
			else if (state.equals("after_insp"))
			{
				stateType = StateType.STATE_AFTER_INSPECTION;
			}
			else if (state.equals("complete"))
			{
				stateType = StateType.STATE_COMPLETE;
			}
			else if (state.equals("cancelled"))
			{
				stateType = StateType.STATE_CANCELLED;
			}
			else
			{
				throw new UnsupportedOrderState(state);
			}
			
			String productManufacturerAndModel = req.getParameter("productManufacturerAndModel");
			String customerName = req.getParameter("customerName");
			String targetAddress = req.getParameter("targetAddress");
			String pipeLineLength = req.getParameter("pipeLineLength");
			String additionalCoolantAmount = req.getParameter("additionalCoolantAmount");
			Boolean pumpNeeded = (req.getParameter("pumpNeeded") != null && req.getParameter("pumpNeeded").equals("on"));
			String fullCost = req.getParameter("fullCost");
			
			PrintWriter pw = resp.getWriter();
			boolean pipeLineLength_incorrect = false, additionalCoolantAmount_incorrect = false;
			try
			{
				if (id == -1)
				{
					if (stateType == StateType.STATE_NEW)
					{
						OrdersManager.CreateNewOrder(productManufacturerAndModel, customerName, targetAddress);
					}
					else if (stateType == StateType.STATE_AFTER_INSPECTION)
					{
						OrdersManager.CreateInspectionCompleteOrder(productManufacturerAndModel, customerName, targetAddress, pipeLineLength, additionalCoolantAmount, pumpNeeded);
					}
					else
					{
						throw new UnsupportedOrderState(stateType);
					}
				}
				else
				{
					switch (stateType)
					{
					case STATE_NEW:
						OrdersManager.ModifyOrder(got, stateType, productManufacturerAndModel, customerName, targetAddress, null, null, null);
						break;
					case STATE_AFTER_INSPECTION:
						OrdersManager.ModifyOrder(got, stateType, productManufacturerAndModel, customerName, targetAddress, pipeLineLength, additionalCoolantAmount, pumpNeeded);
						break;
					case STATE_CANCELLED:
						OrdersManager.ModifyOrder(got, stateType, null, null, null, null, null, null);
						break;
					case STATE_COMPLETE:
						OrdersManager.ModifyOrder(got, stateType, null, null, null, null, null, null);
						break;
					default:
						throw new UnsupportedOrderState(stateType);
					}

					
				}
				
			}
			catch (InvalidInputException iie)
			{
				if (iie.getFieldName().equals("pipeLineLength")) 
				{
					pipeLineLength_incorrect = true;
					message = pipeLineLength_INCORRECT_MESSAGE;
				}
				if (iie.getFieldName().equals("additionalCoolantAmount")) 
				{
					additionalCoolantAmount_incorrect = true;
					message = additionalCoolantAmount_INCORRECT_MESSAGE;
				}
			} catch (IncorrectValueException ive) {
				if (ive.getFieldName().equals("pipeLineLength"))
				{
					pipeLineLength_incorrect = true;
					message = pipeLineLength_INCORRECT_MESSAGE;
				}
				if (ive.getFieldName().equals("additionalCoolantAmount"))
				{
					additionalCoolantAmount_incorrect = true;
					message = additionalCoolantAmount_INCORRECT_MESSAGE;
				}
			} catch (ArgumentCantBeNull e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (FieldIsUnchangeable e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (IncorrectOrderStateChange e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} 
			
			if (message == null)
			{
				message = "Заказ принят!";
			}

			if (got != null) fullCost = got.getFullCost().toPlainString();
			printForm(pw, id, stateType, productManufacturerAndModel, customerName, targetAddress, pipeLineLength, additionalCoolantAmount, pumpNeeded, 
					fullCost, pipeLineLength_incorrect, additionalCoolantAmount_incorrect, message);
		
		} catch (UnsupportedOrderState e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		//super.doPost(req, resp);
	}



}
