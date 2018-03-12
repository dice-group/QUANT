<%@ page language="java" contentType="text/html; charset=ISO-8859-1"
    pageEncoding="ISO-8859-1"%>
<%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="utf-8">
    <meta http-equiv="X-UA-Compatible" content="IE=edge">
    <meta name="viewport" content="width=device-width, initial-scale=1">
    <meta name="description" content="">
    <meta name="author" content="">

    <title>SB Admin 2 - Bootstrap Admin Theme</title>

    <!-- Bootstrap Core CSS -->
    <link href="<c:url value="/resources/vendor/bootstrap/css/bootstrap.min.css" />" rel="stylesheet">

    <!-- MetisMenu CSS -->
    <link href="<c:url value="/resources/vendor/metisMenu/metisMenu.min.css" />" rel="stylesheet">

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
        </nav>
        <div id="page-wrapper">
        	<div class="row">
                <div class="col-lg-12">
                    <a href="${pageContext.request.contextPath}/document-list" class="btn btn-default">BACK</a>
                </div>
                <!-- /.col-lg-12 -->
            </div>
            <div class="row">
                <div class="col-lg-12">
                    <h1 class="page-header">Question Detail</h1>
                </div>
                <!-- /.col-lg-12 -->
            </div>
            <!-- /.row -->
            <div class="row">
            	<div class="col-lg-12">
                    <div class="panel panel-default">
                        <div class="panel-heading" >
                        Question
                        </div>
                        <div class="panel-body">
                        	<div class="row">
                        		<div class="col-lg-12">
                        		<div class="form-group">
                                            <input class="form-control" value="${languageToQuestionEn}">
                                            <p class="font-italic">Question is displayed in English (as default)</p>
                                        </div>
                        		</div>
                        	</div>
                        </div>
                     </div>
                </div>
                <div class="col-lg-12">
                    <div class="panel panel-default">
                        <div class="panel-heading">
                        Question Attributes
                        </div>
                        <div class="panel-body">
                        
                            <div class="row">
                                <div class="col-lg-6">
                                    <form role="form">
                                        <div class="form-group">
                                            <label>SPARQL</label>
                                            <textarea class="form-control" rows="8">${sparqlQuery}</textarea>
                                            <p class="help-block"></p>
                                        </div>
                                        <div class="form-group">
                                            <label>Endpoint</label>
                                            <input class="form-control" value="">
                                            <p class="help-block"></p>
                                        </div>
                                    </form>
                                </div>
                                <!-- /.col-lg-6 (nested) -->
                                <div class="col-lg-6">
                                	<form role="form">
                                        <div class="form-group">
                                            <label>Answer</label>
                                            <textarea class="form-control" rows="8">${goldenAnswer}</textarea>
                                            <p class="help-block"></p>
                                        </div>
                                    </form>
                                    <div class="form-group">
                                            <label>Out of Scope</label>
                                           <select class="form-control">
                                                <option></option>
                                                <option value="true" ${outOfScope == true ? 'selected="selected"' : ''}>True</option>
                                                <option value="false" ${outOfScope == false ? 'selected="selected"' : ''}>False</option>
                                                
                                            </select>
                                            <p class="help-block"></p>
                                    </div>	
                                </div>
                                <!-- /.col-lg-6 (nested) -->
                            </div>
                            <!-- /.row (nested) -->
                            <!-- row -->
                            <div class="row">
                        		<div class="col-lg-6">
                        			<div class="form-group">
                                            <label>Answer Type</label>
                                            <input class="form-control" value="${answerType}">
                                            <p class="help-block"></p>
                                     </div>	
                        		</div>
                        		<div class="col-lg-6">
                        			<div class="form-group">
                                            <label>Aggregation</label>
                                           <select class="form-control">
                                                <option></option>
                                                <option value="true" ${aggregation == true ? 'selected="selected"' : ''}>True</option>
                                                <option value="false" ${aggregation == false ? 'selected="selected"' : ''}>False</option>
                                                
                                            </select>
                                            <p class="help-block"></p>
                                    </div>	
                        		</div>
                        	</div>
                        	<div class="row">
                        		<div class="col-lg-6">
                        			<div class="form-group">
                                            <label>Onlydbo</label>
                                           <select class="form-control">
                                                <option></option>
                                                <option value="true" ${onlydbo == true ? 'selected="selected"' : ''}>True</option>
                                                <option value="false" ${onlydbo == false ? 'selected="selected"' : ''}>False</option>
                                                
                                            </select>
                                            <p class="help-block"></p>
                                    </div>
                        		</div>
                        		<div class="col-lg-6">
                        			<div class="form-group">
                                            <label>Hybrid</label>
                                           <select class="form-control">
                                                <option></option>
                                                <option value="true" ${hybrid == true ? 'selected="selected"' : ''}>True</option>
                                                <option value="false" ${hybrid == false ? 'selected="selected"' : ''}>False</option>
                                                
                                            </select>
                                            <p class="help-block"></p>
                                    </div>	
                        		</div>
                        	</div>
                        	<div class="row">
                        		<div class="col-lg-12">
                        			<div class="form-group">
                        				<label>Multilingual Keyword List</label>
                        				<table width="100%" class="table table-striped table-bordered table-hover">
                        					<thead>
			                                    <tr>
			                                        <th class="text-center">Language</th>
			                                        <th class="text-center">Keywords</th>
			                                    </tr>
			                                </thead>
			                                <tbody>
			                                
			                                     <c:forEach items="${languageToKeyword}" var="map">
			                                     	
				                                    	<tr>
				                                    		<td>${map.getKey()}</td>
				                                    		<td>${map.getValue()}</td>
				                                    	</tr>
			                                    	
			                                    </c:forEach>
			                                </tbody>
                        				</table>
                        			</div>
                        		</div>
                        	</div>
                        	<div class="row">
                        		<div class="col-lg-12">
                        			<div class="form-group">
                        				<label>Multilingual Question List</label>
                        				<table width="100%" class="table table-striped table-bordered table-hover">
                        					<thead>
			                                    <tr>
			                                        <th class="text-center">Language</th>
			                                        <th class="text-center">Question</th>
			                                    </tr>
			                                </thead>
			                                <tbody>
			                                
			                                     <c:forEach items="${languageToQuestion}" var="map">
			                                     	
				                                    	<tr>
				                                    		<td>${map.getKey()}</td>
				                                    		<td>${map.getValue()}</td>
				                                    	</tr>
			                                    	
			                                    </c:forEach>
			                                </tbody>
                        				</table>
                        			</div>
                        		</div>
                        	</div>
                            <!-- /.row (nested) -->
                        </div>
                        <!-- /.panel-body -->
                    </div>
                    <!-- /.panel -->
                </div>
                <!-- /.col-lg-12 -->
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

    <!-- Custom Theme JavaScript -->
    <script src="<c:url value="/resources/dist/js/sb-admin-2.js" />"></script>

</body>

</html>