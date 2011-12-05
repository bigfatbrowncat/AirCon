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

    private void printForm(
    		PrintWriter pw, 
    		String searchRequest)
    {

    	pw.printf("<html><head>");
    	pw.printf("<style>");
		pw.printf("<title>Поиск заказа</title>\n");
		pw.printf("</head><body>");    	

		pw.printf("<form style=\"padding: 3\" method=\"get\">\n");
		pw.printf("<div style=\"padding: 3px 8px 3px 8px; background: #887055\">\n");
		pw.printf("Искать заказчика: <input name=\"customerName\" value=\"%1$s\"/>, " +
				  "марку и модель сплит-системы: <input name=\"productManufacturerAndModel\" value=\"%1$s\"/>, " +
				  "адрес установки: <input name=\"targetAddress\" value=\"%1$s\"/>, " +
				  "среди <input type=\"checkbox\" name=\"new\" />новых, " +
				  "<input type=\"checkbox\" name=\"inspected\" />осмотренных, " +
				  "<input type=\"checkbox\" name=\"completed\" />завершенных и " +
				  "<input type=\"checkbox\" name=\"cancelled\" />отмененных заказов. " +
				  "<input style=\"padding: 2px; margin: 2px; margin-top: 5px; \"type=\"submit\" value=\"Найти\" />", searchRequest);
		pw.printf("</div>\n");
		pw.printf("</form>\n");
		
		if (searchRequest != "" && searchRequest != null)
		{
			Order[] orders = OrdersManager.Search(searchRequest);

			pw.printf("<table cellpadding=\"3\" cellspacing=\"3\">\n");
			pw.printf("<tr style=\"color: white; background: #887055\"><td style=\"padding: 3px 6px 3px 6px; \">Код</td>" +
					"<td style=\"padding: 3px 8px 3px 8px; \">Наименование заказчика</td>" +
					"<td style=\"padding: 3px 8px 3px 8px; \">Марка и модель сплит-системы</td>" +
					"<td style=\"padding: 3px 8px 3px 8px; \">Адрес заказчика</td>" +
					"<td style=\"padding: 3px 8px 3px 8px; \">Статус</td></tr>");
			for (Order ord : orders)
			{
				pw.printf("<tr style=\"background: #eeeeee\"><td>%1$d</td><td>%2$s</td><td>%3$s</td><td>%4$s</td><td>%5$s</td></tr>", 
						ord.getUid(), 
						ord.getCustomerName(), 
						ord.getProductManufacturerAndModel(), 
						ord.getTargetAddress(), 
						ord.getState().toString());
			}
			
			pw.printf("</table>\n");
		}
		
		
		pw.printf("</body></html>");    	
    }    
    
	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		request.setCharacterEncoding("UTF-8");
		response.setContentType("text/html; charset=UTF-8");
		response.setCharacterEncoding("UTF-8");

		PrintWriter pw = response.getWriter();
		
		printForm(pw, "c b");
		
	}

}
