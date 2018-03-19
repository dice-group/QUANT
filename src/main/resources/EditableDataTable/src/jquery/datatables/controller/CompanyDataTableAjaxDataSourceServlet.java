package jquery.datatables.controller;

import java.io.IOException;

import java.util.Collections;
import java.util.Comparator;
import java.util.LinkedList;
import java.util.List;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import jquery.datatables.model.Company;
import jquery.datatables.model.DataRepository;
import jquery.datatables.controller.DataTableRequestParam;

/**
 * CompanyServlet provides data to the JQuery DataTables
 */
@WebServlet("/CompanyAjaxDataSource")
public class CompanyDataTableAjaxDataSourceServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {

		final DataTableRequestParam param = DataTablesParamUtility.getParam(request);
		
		String sEcho = param.sEcho;
		int iTotalRecords; // total number of records (unfiltered)
		int iTotalDisplayRecords;//value will be set when code filters companies by keyword
		JSONArray data = new JSONArray(); //data that will be shown in the table

		iTotalRecords = DataRepository.GetCompanies().size();
		List<Company> companies = new LinkedList<Company>();
		for(Company c : DataRepository.GetCompanies()){
			//Cannot search by column 0 (id)
			if(	param.bSearchable[1] &&
				c.getName().toLowerCase().contains(param.sSearchKeyword.toLowerCase())
				||
				param.bSearchable[2] &&
				c.getAddress().toLowerCase().contains(param.sSearchKeyword.toLowerCase())
				||
				param.bSearchable[3] &&
				c.getTown().toLowerCase().contains(param.sSearchKeyword.toLowerCase()))
			{
				companies.add(c); // Add a company that matches search criterion
			}
		}
		iTotalDisplayRecords = companies.size();//Number of companies that matches search criterion should be returned
		
		
		Collections.sort(companies, new Comparator<Company>(){
			@Override
			public int compare(Company c1, Company c2) {
				int result = 0;
				for(int i=0; i<param.iSortingCols; i++){
					int sortBy = param.iSortCol[i];
					if(param.bSortable[sortBy]){
						switch(sortBy){
							case 0:
								result =	0; //sort by id is not allowed
							break;
							case 1:
								result =	c1.getName().compareToIgnoreCase(c2.getName()) * 
											(param.sSortDir[i].equals("asc") ? -1 : 1);
								break;
							case 2:
								result =	c1.getAddress().compareToIgnoreCase(c2.getAddress()) * 
											(param.sSortDir[i].equals("asc") ? -1 : 1);
								break;
							case 3:
								result =	c1.getTown().compareToIgnoreCase(c2.getTown()) *
											(param.sSortDir[i].equals("asc") ? -1 : 1);
								break;
						}
					}
					if(result!=0)
						return result;
					else
						continue;
				}
				return result;
			}
		});
		
		if(companies.size()< param.iDisplayStart + param.iDisplayLength)
			companies = companies.subList(param.iDisplayStart, companies.size());
		else
			companies = companies.subList(param.iDisplayStart, param.iDisplayStart + param.iDisplayLength);
	
		
		try {
			JSONObject jsonResponse = new JSONObject();
			
			jsonResponse.put("sEcho", sEcho);
			jsonResponse.put("iTotalRecords", iTotalRecords);
			jsonResponse.put("iTotalDisplayRecords", iTotalDisplayRecords);
			
			for(Company c : companies){
				JSONArray row = new JSONArray();
				row.put(c.getId()).put(c.getName()).put(c.getAddress()).put(c.getTown());
				data.put(row);
			}
			jsonResponse.put("aaData", data);
			
			response.setContentType("application/json");
			response.getWriter().print(jsonResponse.toString());
		} catch (JSONException e) {
			e.printStackTrace();
			response.setContentType("text/html");
			response.getWriter().print(e.getMessage());
		}
	
	}

}
