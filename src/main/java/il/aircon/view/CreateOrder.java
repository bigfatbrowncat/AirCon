package il.aircon.view;

import il.aircon.controller.ArgumentCantBeNull;
import il.aircon.controller.InvalidInputException;
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
@WebServlet(description = "User form for creating of the new order", urlPatterns = { "/CreateOrder" })
public final class CreateOrder extends HttpServlet {
	private static final long serialVersionUID = 1L;
      
	private static String pipeLineLength_INCORRECT_MESSAGE = "Значение длины трубопроводной магистрали между внутренним и внешним блоками должно быть положительным числом. "
			+ "Допускаются десятичные дроби.";
	private static String additionalCoolantAmount_INCORRECT_MESSAGE = "Количество дозаправленного хладагента должно быть положительным числом или нулём. "
			+ "Допускаются десятичные дроби.";
	
    /**
     * @see HttpServlet#HttpServlet()
     */
    public CreateOrder() {
        super();
    }

    private void printForm(
    		PrintWriter pw, 
    		Order.StateType state,
    		String productManufacturerAndModel,
    		String customerName,
    		String targetAddress,
    		String pipeLineLength,
    		String additionalCoolantAmount,
    		Boolean pumpNeeded,
    		
    		boolean pipeLineLength_incorrect,
    		boolean additionalCoolantAmount_incorrect,
    		
    		String incorrect_input_message) throws UnsupportedOrderState
    {
		pw.printf("<html><head>");
		pw.printf("<title>Создание заказа</title>\n");

		pw.printf("<script language=\"javascript\">\n");
		pw.printf("function check_additional()\n");
		pw.printf("{\n");
		pw.printf("  var selstate = document.getElementById(\"state\");\n");
		pw.printf("  document.getElementById(\"pipeLineLength_row\").style.visibility = ((selstate.value != \"new\") ? \"visible\" : \"hidden\");\n");
		pw.printf("  document.getElementById(\"additionalCoolantAmount_row\").style.visibility = ((selstate.value != \"new\") ? \"visible\" : \"hidden\");\n");
		pw.printf("  document.getElementById(\"pumpNeeded_row\").style.visibility = ((selstate.value != \"new\") ? \"visible\" : \"hidden\");\n");
		pw.printf("}\n");
		pw.printf("</script>\n");
		pw.printf("<style>\n");
		pw.printf("  input.incorrect { background: #ffaaaa }\n");
		pw.printf("  .incorrect_input_msg { color: #880000 }\n");
		pw.printf("</style>\n");
		pw.printf("</head><body>\n");
		pw.printf("<form method=\"post\">\n");
		pw.printf("<table>\n");

		pw.printf("<tr>\n");
		pw.printf("<td style=\"width: 300pt\">Состояние заказа:</td>");
		
		boolean new_selected = false, after_insp_selected = false;
		if (state == StateType.STATE_NEW)
		{
			new_selected = true;
		}
		else if (state == StateType.STATE_AFTER_INSPECTION)
		{
			after_insp_selected = true;
		}
		else
			throw new UnsupportedOrderState(state);
			
		
		pw.printf("<td><select id=\"state\" name=\"state\" onchange=\"check_additional();\" style=\"width: 250pt\">");
		pw.printf("<option value=\"new\" %1$s>Новый</option>", new_selected ? "selected" : "");
		pw.printf("<option value=\"after_insp\" %1$s>Осмотр произведен</option>", after_insp_selected ? "selected" : "");
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
		pw.printf("<td>Необходима установка дренажной помпы:</td><td><input style=\"width: 250pt\" name=\"pumpNeeded\" id=\"pumpNeeded\" type=\"checkbox\" %1$s></td>", (pumpNeeded ? "checked" : ""));
		pw.printf("</tr>\n");
		pw.printf("</table>\n");

		if (incorrect_input_message != null && !incorrect_input_message.equals(""))
		{
			pw.printf("<p class=\"incorrect_input_msg\"><b>Неверный ввод: </b>%1$s</p>\n", incorrect_input_message);		
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
		pw.printf("<title>Создание заказа</title>\n");
		pw.printf("<style>\n");
		pw.printf("  .success { color: #008800; font-size: 200%%; }\n");
		pw.printf("</style>\n");
		pw.printf("</head><body>\n");
		pw.printf("<p class=\"success\">Заказ принят успешно!</p>");
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
		try {
			printForm(pw, StateType.STATE_NEW, 
					"", "", "", "0", "0", true, false, false, null);
		} catch (UnsupportedOrderState e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}
	
	@Override
	protected void doPost(HttpServletRequest req, HttpServletResponse resp)
			throws ServletException, IOException {
		req.setCharacterEncoding("UTF-8");
		resp.setContentType("text/html; charset=UTF-8");
		resp.setCharacterEncoding("UTF-8");
		
		String incorrect_input_message = null; 
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
			
			PrintWriter pw = resp.getWriter();
			boolean pipeLineLength_incorrect = false, additionalCoolantAmount_incorrect = false;
			try
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
			catch (InvalidInputException iie)
			{
				if (iie.getFieldName().equals("pipeLineLength")) 
				{
					pipeLineLength_incorrect = true;
					incorrect_input_message = pipeLineLength_INCORRECT_MESSAGE;
				}
				if (iie.getFieldName().equals("additionalCoolantAmount")) 
				{
					additionalCoolantAmount_incorrect = true;
					incorrect_input_message = additionalCoolantAmount_INCORRECT_MESSAGE;
				}
			} catch (IncorrectValueException ive) {
				if (ive.getFieldName().equals("pipeLineLength"))
				{
					pipeLineLength_incorrect = true;
					incorrect_input_message = pipeLineLength_INCORRECT_MESSAGE;
				}
				if (ive.getFieldName().equals("additionalCoolantAmount"))
				{
					additionalCoolantAmount_incorrect = true;
					incorrect_input_message = additionalCoolantAmount_INCORRECT_MESSAGE;
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
			
			if (incorrect_input_message != null)
			{
				printForm(pw, stateType, productManufacturerAndModel, customerName, targetAddress, pipeLineLength, additionalCoolantAmount, pumpNeeded, 
						pipeLineLength_incorrect, additionalCoolantAmount_incorrect, incorrect_input_message);
			}
			else
			{
				printSuccess(pw);
			}
		
		} catch (UnsupportedOrderState e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		//super.doPost(req, resp);
	}



}
