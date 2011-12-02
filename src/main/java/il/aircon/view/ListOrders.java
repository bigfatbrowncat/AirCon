package il.aircon.view;

import il.aircon.controller.OrdersManager;
import il.aircon.model.Order;
import il.aircon.model.Order.StateType;

import java.io.IOException;
import java.io.PrintWriter;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * Servlet implementation class ListOrders
 */
@WebServlet(description = "Shows a table containing the current orders", urlPatterns = { "/" })
public class ListOrders extends HttpServlet {
	private static final long serialVersionUID = 1L;
       
    /**
     * @see HttpServlet#HttpServlet()
     */
    public ListOrders() {
        super();
        // TODO Auto-generated constructor stub
    }
/*
    private void printForm(
    		PrintWriter pw, 
    		String searchRequest)
    {
		pw.printf("<html><head>");
		pw.printf("<title>Пои�?к заказа</title>\n");

		pw.printf("<form method=\"get\">\n");
		pw.printf("<input name=\"search\" value=\"%1$s\"/> <input style=\"padding: 2px; margin: 2px; margin-top: 5px; \"type=\"submit\" value=\"И�?кать\" />", searchRequest);
		pw.printf("</form>\n");
		
		pw.printf("<table>\n");

		pw.printf("<tr>\n");
		pw.printf("<td style=\"width: 300pt\">Со�?то�?ние заказа:</td>");
		
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
		pw.printf("<option value=\"new\" %1$s>�?овый</option>", new_selected ? "selected" : "");
		pw.printf("<option value=\"after_insp\" %1$s>О�?мотр произведен</option>", after_insp_selected ? "selected" : "");
		pw.printf("</select></td>");
		pw.printf("</tr>\n");
		
		pw.printf("<tr id=\"productManufacturerAndModel_row\" >\n");
		pw.printf("<td>Производитель и модель:</td> <td><input style=\"width: 250pt\" name=\"productManufacturerAndModel\" id=\"productManufacturerAndModel\" type=\"text\" value=\"%1$s\" /></td>", productManufacturerAndModel);
		pw.printf("</tr>\n");

		pw.printf("<tr id=\"customerName_row\">\n");
		pw.printf("<td>�?аименование заказчика:</td> <td><input style=\"width: 250pt\" name=\"customerName\" id=\"customerName\" type=\"text\" value=\"%1$s\" /></td>", customerName);
		pw.printf("</tr>\n");

		pw.printf("<tr id=\"targetAddress_row\">\n");
		pw.printf("<td>�?дре�? проведени�? работ:</td> <td><input style=\"width: 250pt\" name=\"targetAddress\" id=\"targetAddress\" type=\"text\" value=\"%1$s\" /></td>", targetAddress);
		pw.printf("</tr>\n");

		pw.printf("<tr id=\"pipeLineLength_row\" style=\"visibility: hidden\">\n");
		pw.printf("<td>Длина маги�?трали между блоками:</td> <td><input %2$s style=\"width: 250pt\" name=\"pipeLineLength\" id=\"pipeLineLength\" type=\"text\" value=\"%1$s\" />, <b>м</b></td>", 
				pipeLineLength, 
				pipeLineLength_incorrect ? "class=\"incorrect\"" : "");
		pw.printf("</tr>\n");

		pw.printf("<tr id=\"additionalCoolantAmount_row\" style=\"visibility: hidden\">\n");
		pw.printf("<td>Количе�?тво дозаправленного хладагента:</td> <td><input %2$s style=\"width: 250pt\" name=\"additionalCoolantAmount\" id=\"additionalCoolantAmount\" type=\"text\" value=\"%1$s\" />, <b>кг</b></td>", 
				additionalCoolantAmount, 
				additionalCoolantAmount_incorrect ? "class=\"incorrect\"" : "");
		pw.printf("</tr>\n");

		pw.printf("<tr id=\"pumpNeeded_row\" style=\"visibility: hidden\">\n");
		pw.printf("<td>�?еобходима у�?тановка дренажной помпы:</td><td><input style=\"width: 250pt\" name=\"pumpNeeded\" id=\"pumpNeeded\" type=\"checkbox\" %1$s></td>", (pumpNeeded ? "checked" : ""));
		pw.printf("</tr>\n");
		pw.printf("</table>\n");

		if (incorrect_input_message != null && !incorrect_input_message.equals(""))
		{
			pw.printf("<p class=\"incorrect_input_msg\"><b>�?еверный ввод: </b>%1$s</p>\n", incorrect_input_message);		
		}
		
		pw.println("<input style=\"padding: 2px; margin: 2px; margin-top: 5px; \"type=\"submit\" value=\"Прин�?ть заказ\" />");		
		
		pw.printf("</form>");
		pw.printf("</body>");
		pw.printf("<script language=\"javascript\">\n");
		pw.printf("check_additional();\n");
		pw.printf("</script>");
		pw.printf("</html>");    	
    }    
    */
	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		
		
		Order[] orders = OrdersManager.Search("qwe asd");
		
	}

}
