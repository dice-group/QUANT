package jquery.datatables.controller;

import javax.servlet.http.HttpServletRequest;

public class DataTablesParamUtility {
	
	public static DataTableRequestParam getParam(HttpServletRequest request)
	{
		if(request.getParameter("sEcho")!=null && request.getParameter("sEcho")!= "")
		{
			DataTableRequestParam param = new DataTableRequestParam();
			param.sEcho = request.getParameter("sEcho");
			param.sSearchKeyword = request.getParameter("sSearch");
			param.bRegexKeyword = Boolean.parseBoolean(request.getParameter("bRegex"));
			param.sColumns = request.getParameter("sColumns");
			param.iDisplayStart = Integer.parseInt( request.getParameter("iDisplayStart") );
			param.iDisplayLength = Integer.parseInt( request.getParameter("iDisplayLength") );
			param.iColumns = Integer.parseInt( request.getParameter("iColumns") );
			param.sSearch = new String[param.iColumns];
			param.bSearchable = new boolean[param.iColumns];
			param.bSortable = new boolean[param.iColumns];
			param.bRegex = new boolean[param.iColumns];
			for(int i=0; i<param.iColumns; i++){
				param.sSearch[i] = request.getParameter("sSearch_"+i);
				param.bSearchable[i] = Boolean.parseBoolean(request.getParameter("bSearchable_"+i));
				param.bSortable[i] = Boolean.parseBoolean(request.getParameter("bSortable_"+i));
				param.bRegex[i] = Boolean.parseBoolean(request.getParameter("bRegex_"+i));
			}
			
			param.iSortingCols = Integer.parseInt( request.getParameter("iSortingCols") );
			param.sSortDir = new String[param.iSortingCols];
			param.iSortCol = new int[param.iSortingCols];
			for(int i=0; i<param.iSortingCols; i++){
				param.sSortDir[i] = request.getParameter("sSortDir_"+i);
				param.iSortCol[i] = Integer.parseInt(request.getParameter("iSortCol_"+i));
			}
			return param;
		}else
			return null;
	}
}
