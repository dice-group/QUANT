<%@ page language="java" contentType="text/html; charset=ISO-8859-1" pageEncoding="ISO-8859-1" %>
<%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>

<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
	<meta charset="utf-8">
    <meta http-equiv="X-UA-Compatible" content="IE=edge">
    <meta name="viewport" content="width=device-width, initial-scale=1">
    <meta name="description" content="">
    <meta name="author" content="">

    <title>QUANT-Curate my Dataset</title>

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
                        <a href="${pageContext.request.contextPath}/document-list"><i class="fa fa-list fa-fw"></i> Dataset</a>
                    </li>                    
                     <li>
                        <a href="${pageContext.request.contextPath}/user-list"><i class="fa fa-list fa-fw"></i> User List</a>
                    </li>
                    <li>
                            <a href="#"><i class="fa fa-bar-chart-o fa-fw"></i> User Activities<span class="fa arrow"></span></a>
                            <ul class="nav nav-second-level">
                                <li>
                                    <a href="${pageContext.request.contextPath}/user-dataset-correction">Dataset Correction</a>
                                </li>
                                <li>
                                    <a href="#">Log Activities</a>
                                </li>
                            </ul>
                            <!-- /.nav-second-level -->
                        </li>
                    <li>
                        <a href="${pageContext.request.contextPath}/curate-my-dataset"><i class="fa fa-edit"></i> Curate my Dataset</a>
                    </li>
                    <li>
                        <a href="${pageContext.request.contextPath}/logout"><i class="fa fa-power-off fa-fw"></i> Log out</a>
                    </li>
                </ul>
            </div>
        </div>
        <!-- /.navbar sidebar -->        
        </nav>
	</div>
	<div id="page-wrapper">
		 <div class="row">
                <div class="col-lg-12">
                    <h1 class="page-header">Curate my Dataset</h1>
                </div>                
         </div>      
	
		<div class="row">
			<div class="col-lg-12">
				<form class="form-horizontal">
	  				<div class="form-group">
			    		<label for="inputDatabaseVersion" class="control-label col-xs-2">Database Origin Version</label>
			    		<div class="col-xs-4">
			    			<input type="text" class="form-control" id="databaseVersion" placeholder="Enter Database Origin Version">
			    		</div>			    		
			  		</div>
			  		<div class="form-group">
			    		<label for="inputSparqlEndpoint" class="control-label col-xs-2">SPARQL Endpoint</label>
			    		<div class="col-xs-4">
			    			<input type="text" class="form-control" id="sparqlEndpoint" placeholder="Enter SPARQL Endpoint URL">
			    		</div>
			 		</div>
			  		<div class="form-group">
			    		<label for="inputDatasetFile" class="control-label col-xs-2">Dataset File (.json)</label>
			    		<div class="col-xs-4">
			    			<input type="file" class="filestyle" data-buttonText="Select a File">
			    		</div>
			 		</div>
				</form>
			</div>
			
		</div>
		
	
	
	
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
</body>
</html>