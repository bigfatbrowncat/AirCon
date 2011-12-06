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
    		String customerNameSearchRequest,
    		String productManufacturerAndModelSearchRequest,
    		String targetAddressSearchRequest,
    		boolean searchNew, 
    		boolean searchInspected, 
    		boolean searchCompleted, 
    		boolean searchCancelled)
    {

    	pw.printf("<html><head>");
    	pw.printf("<style>");
    	pw.printf("  .box1 { padding: 2px 6px 2px 6px; background: #EEDDCC }");
    	pw.printf("  .box2 { padding: 4px 4px 4px 4px; border: 1px dotted #887055 }");
    	pw.printf("</style>");
		pw.printf("<title>Поиск заказа</title>\n");
		pw.printf("</head><body>");    	

		pw.printf("<form method=\"get\">\n");
		pw.printf("<div class=\"box1\">\n");
		pw.printf("Искать <span class=\"box2\">заказчика: <input name=\"customerNameSearchRequest\" value=\"%1$s\"/></span>, " +
				  "<span class=\"box2\">марку и модель сплит-системы: <input name=\"productManufacturerAndModelSearchRequest\" value=\"%2$s\"/></span>, " +
				  "<span class=\"box2\">адрес установки: <input name=\"targetAddressSearchRequest\" value=\"%3$s\"/></span>, " +
				  "среди " +
				  "<span class=\"box2\"><input type=\"checkbox\" name=\"searchNew\" %4$s/>новых</span>, " +
				  "<span class=\"box2\"><input type=\"checkbox\" name=\"searchInspected\" %5$s/>осмотренных</span>, " +
				  "<span class=\"box2\"><input type=\"checkbox\" name=\"searchCompleted\" %6$s/>завершенных</span> и " +
				  "<span class=\"box2\"><input type=\"checkbox\" name=\"searchCancelled\" %7$s/>отмененных</span> заказов. " +
				  "<input style=\"padding: 2px; margin: 2px; margin-top: 5px; \"type=\"submit\" value=\"Найти\" />", 
				  customerNameSearchRequest != null ? customerNameSearchRequest : "",
				  productManufacturerAndModelSearchRequest != null ? productManufacturerAndModelSearchRequest : "",
				  targetAddressSearchRequest != null ? targetAddressSearchRequest : "",
				  (searchNew ? "checked" : ""),
				  (searchInspected ? "checked" : ""),
				  (searchCompleted ? "checked" : ""),
				  (searchCancelled ? "checked" : "")
				  );
		pw.printf("</div>\n");
		pw.printf("</form>\n");
		
		
		Order[] orders = OrdersManager.SearchOrder(customerNameSearchRequest,
	    		productManufacturerAndModelSearchRequest,
	    		targetAddressSearchRequest,
	    		searchNew, 
	    		searchInspected, 
	    		searchCompleted, 
	    		searchCancelled);

		pw.printf("<table cellpadding=\"3\" cellspacing=\"3\">\n");
		pw.printf("<tr class=\"box1\"><td style=\"padding: 3px 6px 3px 6px; \">Код</td>" +
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
		pw.printf("</body></html>");    	
	}    
    
	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		request.setCharacterEncoding("UTF-8");
		response.setContentType("text/html; charset=UTF-8");
		response.setCharacterEncoding("UTF-8");
		
		String customerNameSearchRequest = request.getParameter("customerNameSearchRequest");
		String productManufacturerAndModelSearchRequest = request.getParameter("productManufacturerAndModelSearchRequest");
		String targetAddressSearchRequest = request.getParameter("targetAddressSearchRequest");
		boolean searchNew = (request.getParameter("searchNew") != null && request.getParameter("searchNew").equals("on"));
		boolean searchInspected = (request.getParameter("searchInspected") != null && request.getParameter("searchInspected").equals("on"));
		boolean searchCompleted = (request.getParameter("searchCompleted") != null && request.getParameter("searchCompleted").equals("on"));
		boolean searchCancelled = (request.getParameter("searchCancelled") != null && request.getParameter("searchCancelled").equals("on"));
		
		PrintWriter pw = response.getWriter();
		
		printForm(pw, 
				customerNameSearchRequest, 
				productManufacturerAndModelSearchRequest, 
				targetAddressSearchRequest, 
				searchNew, searchInspected, searchCompleted, searchCancelled);
		
	}

}
