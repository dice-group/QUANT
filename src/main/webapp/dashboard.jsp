<%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<!-- charset utf-8 for apply encoding multilingual -->
<%@ page language="java" contentType="text/html;charset=UTF-8" %>

<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="utf-8">
    <meta http-equiv="X-UA-Compatible" content="IE=edge">
    <meta name="viewport" content="width=device-width, initial-scale=1">
    <meta name="description" content="">
    <meta name="author" content="">
    <link href='http://fonts.googleapis.com/css?family=Oswald:400,300,700' rel='stylesheet' type='text/css'>

    <title>QUANT-Dashboard</title>

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
                    <h1 class="page-header">Dashboard</h1>
                    <h4>Welcome ${name}. You are an ${role}</h4>
                    
                </div>
                <!-- /.col-lg-12 -->
            </div>
            <div class="row">
                <div class="col-lg-12">
                    <h1 class="page-header">Master Dataset</h1>
                    <h4></h4>
                    
                </div>
                <!-- /.col-lg-12 -->
            </div>
            <!-- /.row -->
            <div class="row">
                <div class="col-lg-3 col-md-6">
                    <div class="panel panel-primary">
                        <div class="panel-heading">
                            <div class="row">
                                <div class="col-xs-3">
                                    <i class="fa fa-tasks fa-5x"></i>
                                </div>
                                <div class="col-xs-9 text-right">
                                    <div class="huge">${qald1 }</div>
                                    <div>QALD1 (Test & Train)</div>
                                </div>
                            </div>
                        </div>
                        <a href="${pageContext.request.contextPath}/document-list/collections/QALD1_Test_dbpedia/QALD1_Train_dbpedia">                                               
                            <div class="panel-footer">
                                <span class="pull-left">View Details</span>
                                <%-- <a href="${pageContext.request.contextPath}/download-master-dataset/QALD1_Test_dbpedia/QALD1_Train_dbpedia"/><span class="pull-right"> <i class="fa fa-download"></i></span> --%>
                                <div class="clearfix"></div>
                            </div>
                        </a>
                    </div>
                </div>
                <div class="col-lg-3 col-md-6">
                    <div class="panel panel-green">
                        <div class="panel-heading">
                            <div class="row">
                                <div class="col-xs-3">
                                    <i class="fa fa-tasks fa-5x"></i>
                                </div>
                                <div class="col-xs-9 text-right">
                                    <div class="huge">${qald2 }</div>
                                    <div>QALD2 (Test & Train)</div>
                                </div>
                            </div>
                        </div>
                        <a href="${pageContext.request.contextPath}/document-list/collections/QALD2_Test_dbpedia/QALD2_Train_dbpedia">
                            <div class="panel-footer">
                                <span class="pull-left">View Details</span>
                                <%-- <a href="${pageContext.request.contextPath}/download-master-dataset/QALD2_Test_dbpedia/QALD2_Train_dbpedia"/><span class="pull-right"><i class="fa fa-download"></i></span> --%>
                                <div class="clearfix"></div>
                            </div>
                        </a>
                    </div>
                </div>
                <div class="col-lg-3 col-md-6">
                    <div class="panel panel-yellow">
                        <div class="panel-heading">
                            <div class="row">
                                <div class="col-xs-3">
                                    <i class="fa fa-tasks fa-5x"></i>
                                </div>
                                <div class="col-xs-9 text-right">
                                    <div class="huge">${qald3 }</div>
                                    <div>QALD3 (Test & Train)</div>
                                </div>
                            </div>
                        </div>
                        <a href="${pageContext.request.contextPath}/document-list/collections/QALD3_Test_dbpedia/QALD3_Train_dbpedia">
                            <div class="panel-footer">
                                <span class="pull-left">View Details</span>
<%--                                 <a href="${pageContext.request.contextPath}/download-master-dataset/QALD3_Test_dbpedia/QALD3_Train_dbpedia"/><span class="pull-right"><i class="fa fa-download"></i></span> --%>
                                <div class="clearfix"></div>
                            </div>
                        </a>
                    </div>
                </div>
                
                <c:if test="${role =='administrator'}">
                <div class="col-lg-3 col-md-6">
                    <div class="panel panel-red">
                        <div class="panel-heading">
                            <div class="row">
                                <div class="col-xs-3">
                                    <i class="fa fa-tasks fa-5x"></i>
                                </div>
                                <div class="col-xs-9 text-right">
                                    <div class="huge">${qald4 }</div>
                                    <div>QALD4 (Test & Train)</div>
                                </div>
                            </div>
                        </div>
                        <a href="${pageContext.request.contextPath}/document-list/collections/QALD4_Test_Multilingual/QALD4_Train_Multilingual">
                            <div class="panel-footer">
                                <span class="pull-left">View Details</span>
<%--                                 <a href="${pageContext.request.contextPath}/download-master-dataset/QALD4_Test_Multilingual/QALD4_Train_Multilingual"/><span class="pull-right"><i class="fa fa-download"></i></span> --%>
                                <div class="clearfix"></div>
                            </div>
                        </a>
                    </div>
                </div>
                <div class="col-lg-3 col-md-6">
                    <div class="panel panel-danger">
                        <div class="panel-heading">
                            <div class="row">
                                <div class="col-xs-3">
                                    <i class="fa fa-tasks fa-5x"></i>
                                </div>
                                <div class="col-xs-9 text-right">
                                    <div class="huge">${qald5 }</div>
                                    <div>QALD5 (Test & Train)</div>
                                </div>
                            </div>
                        </div>
                        <a href="${pageContext.request.contextPath}/document-list/collections/QALD5_Test_Multilingual/QALD5_Train_Multilingual">
                            <div class="panel-footer">
                                <span class="pull-left">View Details</span>
<%--                                 <a href="${pageContext.request.contextPath}/download-master-dataset/QALD5_Test_Multilingual/QALD5_Train_Multilingual"/><span class="pull-right"><i class="fa fa-download"></i></span> --%>
                                <div class="clearfix"></div>
                            </div>
                        </a>
                    </div>
                </div>
                <div class="col-lg-3 col-md-6">
                    <div class="panel panel-success">
                        <div class="panel-heading">
                            <div class="row">
                                <div class="col-xs-3">
                                    <i class="fa fa-tasks fa-5x"></i>
                                </div>
                                <div class="col-xs-9 text-right">
                                    <div class="huge">${qald6 }</div>
                                    <div>QALD6 (Test & Train)</div>
                                </div>
                            </div>
                        </div>
                        <a href="${pageContext.request.contextPath}/document-list/collections/QALD6_Test_Multilingual/QALD6_Train_Multilingual">
                            <div class="panel-footer">
                                <span class="pull-left">View Details</span>
<%--                                 <a href="${pageContext.request.contextPath}/download-master-dataset/QALD6_Test_Multilingual/QALD6_Train_Multilingual"/><span class="pull-right"><i class="fa fa-download"></i></span> --%>
                                <div class="clearfix"></div>
                            </div>
                        </a>
                    </div>
                </div>
                <div class="col-lg-3 col-md-6">
                    <div class="panel panel-warning">
                        <div class="panel-heading">
                            <div class="row">
                                <div class="col-xs-3">
                                    <i class="fa fa-tasks fa-5x"></i>
                                </div>
                                <div class="col-xs-9 text-right">
                                    <div class="huge">${qald7 }</div>
                                    <div>QALD7 (Test & Train)</div>
                                </div>
                            </div>
                        </div>
                        <a href="${pageContext.request.contextPath}/document-list/collections/QALD7_Test_Multilingual/QALD7_Train_Multilingual">
                            <div class="panel-footer">
                                <span class="pull-left">View Details</span>
<%--                                 <a href="${pageContext.request.contextPath}/download-master-dataset/QALD7_Test_Multilingual/QALD7_Train_Multilingual"/><span class="pull-right"><i class="fa fa-download"></i></span> --%>
                                <div class="clearfix"></div>
                            </div>
                        </a>
                    </div>
                </div>
                <div class="col-lg-3 col-md-6">
                    <div class="panel panel-default">
                        <div class="panel-heading">
                            <div class="row">
                                <div class="col-xs-3">
                                    <i class="fa fa-tasks fa-5x"></i>
                                </div>
                                <div class="col-xs-9 text-right">
                                    <div class="huge">${qald8 }</div>
                                    <div>QALD8 (Test & Train)</div>
                                </div>
                            </div>
                        </div>
                        <a href="${pageContext.request.contextPath}/document-list/collections/QALD8_Test_Multilingual/QALD8_Train_Multilingual">
                            <div class="panel-footer">
                                <span class="pull-left">View Details</span>
<%--                                 <a href="${pageContext.request.contextPath}/download-master-dataset/QALD8_Test_Multilingual/QALD8_Train_Multilingual"/><span class="pull-right"><i class="fa fa-download"></i></span> --%>
                                <div class="clearfix"></div>
                            </div>
                        </a>
                    </div>
                </div>                
            </div>
            </c:if>
            <c:if test="${role!='administrator'}"> <!-- filter menu based on role -->
            <!-- /.row -->
             <div class="row">
                <div class="col-lg-12">
                    <h1 class="page-header">Curated Dataset</h1>
                   
                </div>
                <!-- /.col-lg-12 -->
            </div>
            <div class="row">
            	<div class="col-lg-3 col-md-6">
                    <div class="panel panel-primary">
                        <div class="panel-heading">
                            <div class="row">
                                <div class="col-xs-3">
                                    <i class="fa fa-tasks fa-5x"></i>
                                </div>
                                <div class="col-xs-9 text-right">
                                    <div class="huge">${qald1Correction }</div>
                                    <div>QALD1 (Test & Train)</div>
                                </div>
                            </div>
                        </div>
                        <a href="${pageContext.request.contextPath}/document-list/curated-question/QALD1_Test_dbpedia/QALD1_Train_dbpedia">
                            <div class="panel-footer">
                                <span class="pull-left">View Details</span>
<%--                                 <a href="${pageContext.request.contextPath}/download-curated-dataset/QALD1_Test_dbpedia/QALD1_Train_dbpedia"/><span class="pull-right"><i class="fa fa-download"></i></span> --%>
                                <div class="clearfix"></div>
                            </div>
                        </a>
                    </div>
                </div>
                <div class="col-lg-3 col-md-6">
                    <div class="panel panel-green">
                        <div class="panel-heading">
                            <div class="row">
                                <div class="col-xs-3">
                                    <i class="fa fa-tasks fa-5x"></i>
                                </div>
                                <div class="col-xs-9 text-right">
                                    <div class="huge">${qald2Correction }</div>
                                    <div>QALD2 (Test & Train)</div>
                                </div>
                            </div>
                        </div>
                        <a href="${pageContext.request.contextPath}/document-list/curated-question/QALD2_Test_dbpedia/QALD2_Train_dbpedia">
                            <div class="panel-footer">
                                <span class="pull-left">View Details</span>
<%--                                 <a href="${pageContext.request.contextPath}/download-curated-dataset/QALD2_Test_dbpedia/QALD2_Train_dbpedia"/><span class="pull-right"><i class="fa fa-download"></i></span> --%>
                                <div class="clearfix"></div>
                            </div>
                        </a>
                    </div>
                </div>
                <div class="col-lg-3 col-md-6">
                    <div class="panel panel-yellow">
                        <div class="panel-heading">
                            <div class="row">
                                <div class="col-xs-3">
                                    <i class="fa fa-tasks fa-5x"></i>
                                </div>
                                <div class="col-xs-9 text-right">
                                    <div class="huge">${qald3Correction }</div>
                                    <div>QALD3 (Test & Train)</div>
                                </div>
                            </div>
                        </div>
                        <a href="${pageContext.request.contextPath}/document-list/curated-question/QALD3_Test_dbpedia/QALD3_Train_dbpedia">
                            <div class="panel-footer">
                                <span class="pull-left">View Details</span>
<%--                                 <a href="${pageContext.request.contextPath}/download-curated-dataset/QALD3_Test_dbpedia/QALD3_Train_dbpedia"/><span class="pull-right"><i class="fa fa-download"></i></span> --%>
                                <div class="clearfix"></div>
                            </div>
                        </a>
                    </div>
                </div>
                <%-- <div class="col-lg-3 col-md-6">
                    <div class="panel panel-red">
                        <div class="panel-heading">
                            <div class="row">
                                <div class="col-xs-3">
                                    <i class="fa fa-tasks fa-5x"></i>
                                </div>
                                <div class="col-xs-9 text-right">
                                    <div class="huge">${qald4Correction }</div>
                                    <div>QALD4 (Test & Train)</div>
                                </div>
                            </div>
                        </div>
                        <a href="${pageContext.request.contextPath}/document-list/curated-question/QALD4_Test_Multilingual/QALD4_Train_Multilingual">
                            <div class="panel-footer">
                                <span class="pull-left">View Details</span>
                                <a href="${pageContext.request.contextPath}/download-curated-dataset/QALD4_Test_Multilingual/QALD4_Train_Multilingual"/><span class="pull-right"><i class="fa fa-download"></i></span>
                                <div class="clearfix"></div>
                            </div>
                        </a>
                    </div>
                </div>
                <div class="col-lg-3 col-md-6">
                    <div class="panel panel-danger">
                        <div class="panel-heading">
                            <div class="row">
                                <div class="col-xs-3">
                                    <i class="fa fa-tasks fa-5x"></i>
                                </div>
                                <div class="col-xs-9 text-right">
                                    <div class="huge">${qald5Correction }</div>
                                    <div>QALD5 (Test & Train)</div>
                                </div>
                            </div>
                        </div>
                        <a href="${pageContext.request.contextPath}/document-list/curated-question/QALD5_Test_Multilingual/QALD5_Train_Multilingual">
                            <div class="panel-footer">
                                <span class="pull-left">View Details</span>
                                <a href="${pageContext.request.contextPath}/download-curated-dataset/QALD5_Test_Multilingual/QALD5_Train_Multilingual"/><span class="pull-right"><i class="fa fa-download"></i></span>
                                <div class="clearfix"></div>
                            </div>
                        </a>
                    </div>
                </div>
                <div class="col-lg-3 col-md-6">
                    <div class="panel panel-success">
                        <div class="panel-heading">
                            <div class="row">
                                <div class="col-xs-3">
                                    <i class="fa fa-tasks fa-5x"></i>
                                </div>
                                <div class="col-xs-9 text-right">
                                    <div class="huge">${qald6Correction }</div>
                                    <div>QALD6 (Test & Train)</div>
                                </div>
                            </div>
                        </div>
                        <a href="${pageContext.request.contextPath}/document-list/curated-question/QALD6_Test_Multilingual/QALD6_Train_Multilingual">
                            <div class="panel-footer">
                                <span class="pull-left">View Details</span>
                                <a href="${pageContext.request.contextPath}/download-curated-dataset/QALD6_Test_Multilingual/QALD6_Train_Multilingual"/><span class="pull-right"><i class="fa fa-download"></i></span>
                                <div class="clearfix"></div>
                            </div>
                        </a>
                    </div>
                </div>
                <div class="col-lg-3 col-md-6">
                    <div class="panel panel-warning">
                        <div class="panel-heading">
                            <div class="row">
                                <div class="col-xs-3">
                                    <i class="fa fa-tasks fa-5x"></i>
                                </div>
                                <div class="col-xs-9 text-right">
                                    <div class="huge">${qald7Correction }</div>
                                    <div>QALD7 (Test & Train)</div>
                                </div>
                            </div>
                        </div>
                        <a href="${pageContext.request.contextPath}/document-list/curated-question/QALD7_Test_Multilingual/QALD7_Train_Multilingual">
                            <div class="panel-footer">
                                <span class="pull-left">View Details</span>
                                <a href="${pageContext.request.contextPath}/download-curated-dataset/QALD7_Test_Multilingual/QALD7_Train_Multilingual"/><span class="pull-right"><i class="fa fa-download"></i></span>
                                <div class="clearfix"></div>
                            </div>
                        </a>
                    </div>
                </div>
                <div class="col-lg-3 col-md-6">
                    <div class="panel panel-default">
                        <div class="panel-heading">
                            <div class="row">
                                <div class="col-xs-3">
                                    <i class="fa fa-tasks fa-5x"></i>
                                </div>
                                <div class="col-xs-9 text-right">
                                    <div class="huge">${qald8Correction }</div>
                                    <div>QALD8 (Test & Train)</div>
                                </div>
                            </div>
                        </div>
                        <a href="${pageContext.request.contextPath}/document-list/curated-question/QALD8_Test_Multilingual/QALD8_Train_Multilingual">
                            <div class="panel-footer">
                                <span class="pull-left">View Details</span>
                                <a href="${pageContext.request.contextPath}/download-curated-dataset/QALD8_Test_Multilingual/QALD8_Train_Multilingual"/><span class="pull-right"><i class="fa fa-download"></i></span>
                                <div class="clearfix"></div>
                            </div>
                        </a>
                    </div>
                </div>
            </div> --%>
            </c:if>
            <!-- /.row -->
            <!-- /.row -->
            <c:if test="${role=='administrator'}"> <!-- filter menu based on role -->
             <div class="row">
                <div class="col-lg-12">
                    <h1 class="page-header">Summary</h1>
                   
                </div>
                <!-- /.col-lg-12 -->
                <div class="row">
                            <table width="100%" class="table table-striped table-bordered table-hover" id="dataTables-example">
                                <thead>
                                    <tr>
                                        <th width="10%" class="text-center">No.</th>
                                        <th class="text-center">Name</th>
                                        <th class="text-center">User Name</th>
                                        <th class="text-center">QALD1</th>
                                        <th class="text-center">QALD2</th>
                                        <th class="text-center">QALD3</th>
                                        <th class="text-center">QALD4</th>
                                        <th class="text-center">QALD5</th>
                                        <th class="text-center">QALD6</th>
                                        <th class="text-center">QALD7</th>
                                        <th class="text-center">QALD8</th>
                                        <th class="text-center">Curated</th>
                                        <th class="text-center">Removed</th>
                                        <th class="text-center">No Changes</th>
                                        <th></th>
                                    </tr>
                                </thead>
                                <tbody>
                                	<c:forEach var="users" items="${csList}" varStatus="loop">
                                    	<tr>
                                    		<td>${loop.index+1}</td>
                                    		<td>${users.getName()}</td>
                                    		<td>${users.getUsername() }</td>
                                    		<td>${users.getQald1() }</td>
                                    		<td>${users.getQald2() }</td>
                                    		<td>${users.getQald3() }</td>
                                    		<td>${users.getQald4() }</td>
                                    		<td>${users.getQald5() }</td>
                                    		<td>${users.getQald6() }</td>
                                    		<td>${users.getQald7() }</td>
                                    		<td>${users.getQald8() }</td>
                                    		
                                    	</tr>
                                    </c:forEach>
                                </tbody>
                                </table>
                 </div>
            </div>
            </c:if>
        </div>
     </div>
 <!-- /#wrapper -->

    <!-- jQuery -->
    <script src="<c:url value="/resources/vendor/jquery/jquery.min.js" />"></script>

    <!-- Bootstrap Core JavaScript -->
    <script src="<c:url value="/resources/vendor/bootstrap/js/bootstrap.min.js" />"></script>

    <!-- Metis Menu Plugin JavaScript -->
    <script src="<c:url value="/resources/vendor/metisMenu/metisMenu.min.js" />"></script>

    <!-- Custom Theme JavaScript -->
    <script src="<c:url value="/resources/dist/js/sb-admin-2.js" />"></script>
	
	 <!-- DataTables JavaScript -->
	 <script src="<c:url value="/resources/vendor/datatables-editor/jquery.dataTables.editable.js" />"></script>
    <script src="<c:url value="/resources/vendor/datatables-editor/jquery.jeditable.js" />"></script>
    <script src="<c:url value="/resources/vendor/datatables/js/jquery.dataTables.min.js" />"></script>
    <script src="<c:url value="/resources/vendor/datatables-plugins/dataTables.bootstrap.min.js" />"></script>
    <script src="<c:url value="/resources/vendor/datatables-responsive/dataTables.responsive.js" />"></script>
	<script src="<c:url value="/resources/vendor/datatables-editor/jquery.validate.js" />"></script>
	<script src="<c:url value="/resources/vendor/datatables/js/dataTables.jqueryui.js" />"></script>
	<script>
	  $(document).ready($('.form-control').change(function() {
	   $.ajax({
	    type : "post",
	    url : "${pageContext.request.contextPath}/document-list/document/save",
	    cache : false,
	    data : $('#documentForm').serialize(),
	    success : function(response) {
	    	
	    	 $('#alert_placeholder').html('<div class="alert alert-success" role="alert">Data saved</div>')
	    	    
	    },
	    error : function() {
	     alert('Error while request..');
	    }
	   });
	  }));
 </script>
 <script>
 var editor;
//Activate an inline edit on click of a table cell
 $('#example').on( 'click', 'tbody td:not(:first-child)', function (e) {
     editor.inline( this );
 } );
 
 $("#example").dataTable().makeEditable({"sUpdateURL": "${pageContext.request.contextPath}/document-list/document/update-question/${id}/${datasetVersion}"}); 

 </script>
 <!-- Page-Level Demo Scripts - Tables - Use for reference -->
    <script type="text/javascript">
    $(document).ready(function() {
        $('#dataTables-example').DataTable({
        	responsive: true,
            deferRender: true,
            stateSave: true
        });
    });
    </script>
</body>

</html>