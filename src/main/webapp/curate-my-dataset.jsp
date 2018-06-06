<%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<!-- charset utf-8 for apply encoding multilingual -->
<%@ page language="java" contentType="text/html;charset=UTF-8" %>

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
                            <a href="${pageContext.request.contextPath}/document-list"><i class="fa fa-database"></i> Dataset</a>
                        </li>
                         <li>
                            <a href="${pageContext.request.contextPath}/user-list"><i class="fa fa-users"></i> Users</a>
                        </li>
                        <li>
                            <a href="#"><i class="fa fa-tasks"></i> User Activities<span class="fa arrow"></span></a>
                            <ul class="nav nav-second-level">
                                <li>
                                    <a href="${pageContext.request.contextPath}/user-dataset-correction">Curated Questions</a>
                                </li>
                                <li>
                                    <a href="#">Activity Log</a>
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
			<div class="col-lg-6">
				<div class="panel panel-default">
					<div class="panel-heading"></div>
						<div class="row">	
							<div class="panel-body">		                        
                        	</div>
							<form class="form-horizontal" method="POST" action="uploadFile" enctype="multipart/form-data">
				  				<div class="form-group">
						    		<label for="inputDatabaseVersion" class="control-label col-xs-4"><h5>Database Origin Version:</h5></label>
						    		<div class="col-xs-5">
						    			<input name="databaseVersion" type="text" class="form-control" id="databaseVersion" placeholder="Enter Database Origin Version">
						    		</div>			    		
						  		</div>
						  		<div class="form-group">
						    		<label for="inputSparqlEndpoint" class="control-label col-xs-4"><h5>SPARQL Endpoint:</h5></label>
						    		<div class="col-xs-5">
						    			<input name="sparqlEndpoint" type="text" class="form-control" id="sparqlEndpoint" placeholder="Enter SPARQL Endpoint URL">
						    		</div>
						 		</div>
						  		<div class="form-group">
						    		<label for="inputDatasetFile" class="control-label col-xs-4"><h5>Dataset File (.json):</h5></label>
						    		<div class="col-xs-5">
						    			<input name="file" type="file" class="filestyle" data-buttonText="Select a File">
						    		</div>
						 		</div>
						 		<div class="modal-footer">
			        				<button type="button" class="btn btn-default" data-dismiss="modal">Cancel</button>
			        				<button type="submit" class="btn btn-primary">Start to Curate</button>
			        			</div>
							</form>	
						</div>				
				</div>
				 <div class="form-group">
                        	<span style="font-style: italic; color: red">${message }</span>
                        	
                    	</div>
			</div>
		</div>
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
                                    		<td>${datasets.getLanguageToQuestion().get("en").toString()}</td>
                                    		<td>
                                    		
                                    		<c:forEach items="${datasets.getLanguageToKeyword()}" var="map">
			                                     	<c:if test="${map.getKey()=='en'}">
			                                     		${map.getValue().toString()}
			                                     	</c:if>
				                                    		
			                                    	
			                                    </c:forEach>
                                    		</td>
                                    		<td>${datasets.getDatasetVersion()}</td>
                                    		<td>
                                    			<a href="${pageContext.request.contextPath}/curate-my-dataset/detail-collection/${datasets.getId()}/${datasets.getDatasetVersion()}"><span class="fa fa-edit" title="View Details"></span></a>
                                    			
                                    		</td>
                                    	</tr>
                                    </c:forEach>
                                </tbody>
                        	</table>
                        </div>
				</div>
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