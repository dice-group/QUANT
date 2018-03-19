package jquery.datatables.controller;

import java.io.IOException;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import jquery.datatables.model.Company;
import jquery.datatables.model.DataRepository;

/**
 * Handler for the update cell action
 */
@WebServlet("/UpdateData")
public class UpdateData extends HttpServlet {
	private static final long serialVersionUID = 1L;

	/**
	 * This servlet handles post request from the JEditable and updates company property that is edited
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		
		int id = Integer.parseInt(request.getParameter("id"));
		//int columnId = Integer.parseInt(request.getParameter("columnId"));
		int columnPosition = Integer.parseInt(request.getParameter("columnPosition"));
		//int rowId = Integer.parseInt(request.getParameter("rowId"));
		String value = request.getParameter("value");
		//String columnName = request.getParameter("columnName");
		
		for(Company company: DataRepository.GetCompanies())
		{
			if(company.getId()==id)
			{
				switch (columnPosition)
	            {
	                case 0:
	                    company.setName(value);
	                    break;
	                case 1:
	                    company.setAddress(value);
	                    break;
	                case 2:
	                    company.setTown(value);
	                    break;
	                default:
	                    break;
	            }
				response.getWriter().print(value);
				return;
			}
		}
		response.getWriter().print("Error - company cannot be found");
	}


}
