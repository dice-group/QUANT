<%@ page language="java" contentType="text/html;charset=UTF-8" %>
<%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<%@ page import="app.dao.UserDatasetCorrectionDAO" %>
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="utf-8">
    <meta http-equiv="X-UA-Compatible" content="IE=edge">
    <meta name="viewport" content="width=device-width, initial-scale=1">
    <meta name="description" content="">
    <meta name="author" content="">

    <title>QUANT-Dataset</title>

    <!-- Bootstrap Core CSS -->
    <link href="<c:url value="/resources/vendor/bootstrap/css/bootstrap.min.css" />" rel="stylesheet">

    <!-- MetisMenu CSS -->
    <link href="<c:url value="/resources/vendor/metisMenu/metisMenu.min.css" />" rel="stylesheet">

    <!-- DataTables CSS -->
    <link href="<c:url value="/resources/vendor/datatables-plugins/dataTables.bootstrap.css" />" rel="stylesheet">

    <!-- DataTables Responsive CSS -->
    <link href="<c:url value="/resources/vendor/datatables-responsive/dataTables.responsive.css" />" rel="stylesheet">

    <!-- Custom CSS -->
    <link href="<c:url value="/resources/dist/css/sb-admin-2.css" />" rel="stylesheet">

    <!-- Custom Fonts -->
    <link href="<c:url value="/resources/vendor/font-awesome/css/font-awesome.min.css" />" rel="stylesheet" type="text/css">

    <!-- HTML5 Shim and Respond.js IE8 support of HTML5 elements and media queries -->
    <!-- WARNING: Respond.js doesn't work if you view the page via file:// -->
    <!--[if lt IE 9]>
        <script src="https://oss.maxcdn.com/libs/html5shiv/3.7.0/html5shiv.js"></script>
        <script src="https://oss.maxcdn.com/libs/respond.js/1.4.2/respond.min.js"></script>
    <![endif]-->
</head>
<body>
<% 
    UserDatasetCorrectionDAO udcDao = new UserDatasetCorrectionDAO();
%>
    <div id="wrapper">
     <!-- Navigation -->
        <nav class="navbar navbar-default navbar-static-top" role="navigation" style="margin-bottom: 0">
        <div class="navbar-header">
                <button type="button" class="navbar-toggle" data-toggle="collapse" data-target=".navbar-collapse">
                    <span class="sr-only">Toggle navigation</span>
                    <span class="icon-bar"></span>
                    <span class="icon-bar"></span>
                    <span class="icon-bar"></span>
                </button>
                <a class="navbar-brand" href="${pageContext.request.contextPath}">QALD Curator v1.0</a>
            </div>
            <!-- /.navbar-header -->
            <!-- sidebar -->
            <div class="navbar-default sidebar" role="navigation">
            	<div class="sidebar-nav navbar-collapse">
                    <ul class="nav" id="side-menu">
                    	
                    	<li>
                            <a href="${pageContext.request.contextPath}/dashboard"><i class="fa fa-dashboard fa-fw"></i> Dashboard</a>
                        </li>
                        <li>
                            <a href="${pageContext.request.contextPath}/document-list"><i class="fa fa-database"></i> Dataset</a>
                        </li>
                         <c:if test="${role=='administrator'}"> <!-- filter menu based on role -->
                         <li>
                            <a href="${pageContext.request.contextPath}/user-list"><i class="fa fa-users"></i> Users</a>
                        </li>
                        </c:if>
                        <li>
                            <a href="#"><i class="fa fa-tasks"></i> User Activities<span class="fa arrow"></span></a>
                            <ul class="nav nav-second-level">
                                <li>
                                    <a href="${pageContext.request.contextPath}/user-dataset-correction">Curated Questions</a>
                                </li>
                                <li>
                                    <a href="${pageContext.request.contextPath}/user/user-log-list">Activity Log</a>
                                </li>
                            </ul>
                            <!-- /.nav-second-level -->
                        </li>
                        <c:if test="${role == 'administrator' }">
                        <li>
                            <a href="${pageContext.request.contextPath}/curate-my-dataset"><i class="fa fa-edit"></i> Curate my Dataset</a>
                        </li>
                        </c:if>
                         <li>
                            <a href="${pageContext.request.contextPath}/logout"><i class="fa fa-power-off fa-fw"></i> Log out</a>
                        </li>
                    </ul>
                </div>
            </div>
            <!-- /.navbar sidebar -->
        
        </nav>
        <div id="page-wrapper">
            <div class="row">
                <div class="col-lg-12">
                    <h1 class="page-header">${datasetName } Dataset</h1>
                </div>
                <!-- /.col-lg-12 -->
            </div>
            <!-- /.row -->
            <div class="row">
                <div class="col-lg-12">
                    <div class="panel panel-default">
                        <div class="panel-heading">
                        </div>
                        <!-- /.panel-heading -->
                        <div class="panel-body">
                            <table width="100%" class="table table-striped table-bordered table-hover" id="dataTables-example">
                                <thead>
                                    <tr>
                                        <th width="10%" class="text-center">No.</th>
                                        <th class="text-center">Question</th>
                                        <th class="text-center">Keywords</th>
                                        <th class="text-center">Database Origin Version</th>
                                        <th width="5%" ></th>                                        
                                    </tr>
                                </thead>
                                <tbody>
                                     <c:forEach var="datasets" items="${datasets}" varStatus="loop">
                                    	<tr>
                                    		<td>${loop.index+1}</td>
                                    		<td>${datasets.getQuestion()}</td>
                                    		<td>
                                    		<c:forEach items="${datasets.getKeywords()}" var="map">
			                                     	<c:if test="${map.getKey()=='en'}">
			                                     		${map.getValue().toString()}
			                                     	</c:if>			                                    	
			                                    </c:forEach>
                                    		</td>
                                    		<td>${datasets.getDatasetVersion()}</td>
                                    		<td> 
                                    			
                                    			<c:if test="${datasets.getIsCurate()==false}">                                   		
                                    			<a href="${pageContext.request.contextPath}/document-list/detail-master/${datasets.getId()}/${datasets.getDatasetVersion()}"><span class="fa fa-eye" title="View Details"></span></a>
                                    			</c:if>                                    			
                                    		</td>                                    		                                    		
                                    	</tr>
                                    </c:forEach>
                                </tbody>
                            </table>
                            <!-- /.table-responsive -->
                            
                        </div>
                        <!-- /.panel-body -->
                    </div>
                    <!-- /.panel -->
                    
                </div>
                
                <div class="col-lg-12">
          			<a href="${pageContext.request.contextPath}/download-master-dataset/${qaldTest }/${qaldTrain }"/><button type="button" class="btn btn-primary">Generate Report</button>
          			<c:if test="${fExists==true}">
          			<a href="${pageContext.request.contextPath}/reports/${datasetName }.json"/><i class="fa fa-download"> Download Report</i>
          			</c:if>
        		</div>
            </div>
            <!-- /.row -->
                                   
        </div>
        <!-- /#page-wrapper -->

    </div>
    <!-- /#wrapper -->

    <!-- jQuery -->
    <script src="<c:url value="/resources/vendor/jquery/jquery.min.js" />"></script>

    <!-- Bootstrap Core JavaScript -->
    <script src="<c:url value="/resources/vendor/bootstrap/js/bootstrap.min.js" />"></script>

    <!-- Metis Menu Plugin JavaScript -->
    <script src="<c:url value="/resources/vendor/metisMenu/metisMenu.min.js" />"></script>

    <!-- DataTables JavaScript -->
    <script src="<c:url value="/resources/vendor/datatables/js/jquery.dataTables.min.js" />"></script>
    <script src="<c:url value="/resources/vendor/datatables-plugins/dataTables.bootstrap.min.js" />"></script>
    <script src="<c:url value="/resources/vendor/datatables-responsive/dataTables.responsive.js" />"></script>

    <!-- Custom Theme JavaScript -->
    <script src="<c:url value="/resources/dist/js/sb-admin-2.js" />"></script>

    <!-- Page-Level Demo Scripts - Tables - Use for reference -->
    <script type="text/javascript">
    $(document).ready(function() {
        $('#dataTables-example').DataTable({
            responsive: true
        });
    });
    </script>

</body>

</html>
