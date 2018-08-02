<%@ page language="java" contentType="text/html;charset=UTF-8" %>
<%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="utf-8">
    <meta http-equiv="X-UA-Compatible" content="IE=edge">
    <meta name="viewport" content="width=device-width, initial-scale=1">
    <meta name="description" content="">
    <meta name="author" content="">

    <title>QUANT-Question with Attributes</title>

    <!-- Bootstrap Core CSS -->
    <link href="<c:url value="/resources/vendor/bootstrap/css/bootstrap.min.css" />" rel="stylesheet">

    <!-- MetisMenu CSS -->
    <link href="<c:url value="/resources/vendor/metisMenu/metisMenu.min.css" />" rel="stylesheet">

    <!-- Custom CSS -->
    <link href="<c:url value="/resources/dist/css/sb-admin-2.css" />" rel="stylesheet">

    <!-- Custom Fonts -->
    <link href="<c:url value="/resources/vendor/font-awesome/css/font-awesome.min.css" />" rel="stylesheet" type="text/css">
	
	<!-- DataTables CSS -->
    <link href="<c:url value="/resources/vendor/datatables-plugins/dataTables.bootstrap.css" />" rel="stylesheet">
    <link href="<c:url value="/resources/vendor/datatables/css/jquery.dataTables.min.css" />" rel="stylesheet">

    <!-- DataTables Responsive CSS -->
    <link href="<c:url value="/resources/vendor/datatables-responsive/dataTables.responsive.css" />" rel="stylesheet">
    
    
	
    <!-- HTML5 Shim and Respond.js IE8 support of HTML5 elements and media queries -->
    <!-- WARNING: Respond.js doesn't work if you view the page via file:// -->
    <!--[if lt IE 9]>
        <script src="https://oss.maxcdn.com/libs/html5shiv/3.7.0/html5shiv.js"></script>
        <script src="https://oss.maxcdn.com/libs/respond.js/1.4.2/respond.min.js"></script>
    <![endif]-->
	<!-- <script type="text/javascript" src="http://www.technicalkeeda.com/js/javascripts/plugin/jquery.js"></script> -->
	<!-- <script type="text/javascript" src="http://www.technicalkeeda.com/js/javascripts/plugin/json2.js"></script> -->
		
</head>

<body >
	
    <div id="wrapper">
    <!-- Navigation -->
        <nav class="navbar navbar-default navbar-static-top" role="navigation" style="margin-bottom: 0">
        	<!-- navbar header -->
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
                    	<c:if test="${role == 'administrator' }">
                    	<li>
                            <a href="${pageContext.request.contextPath}/dashboard"><i class="fa fa-dashboard fa-fw"></i> Dashboard</a>
                        </li>
                        </c:if>
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
                    	
                    	<%-- <li>
                            <a href="${pageContext.request.contextPath}/dashboard" style=" pointer-events: none;  cursor: default;"><i class="fa fa-dashboard fa-fw"></i> Dashboard</a>
                        </li>
                        <li>
                            <a href="${pageContext.request.contextPath}/document-list" style=" pointer-events: none;  cursor: default;"><i class="fa fa-list fa-fw"></i> Dataset</a>
                        </li>
                         <c:if test="${role=='administrator'}"> <!-- filter menu based on role -->
                         <li>
                            <a href="${pageContext.request.contextPath}/user-list" style=" pointer-events: none;  cursor: default;"><i class="fa fa-users"></i> Users</a>
                        </li>
                        </c:if>
                        <li>
                            <a href="#"><i class="fa fa-tasks"></i> User Activities<span class="fa arrow"></span></a>
                            <ul class="nav nav-second-level">
                                <li>
                                    <a href="${pageContext.request.contextPath}/user-dataset-correction" style=" pointer-events: none;  cursor: default;">Curated Questions</a>
                                </li>
                                <li>
                                    <a href="${pageContext.request.contextPath}/user/user-log-list" style=" pointer-events: none;  cursor: default;">Activity Log</a>
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
                            <a href="${pageContext.request.contextPath}/logout" style=" pointer-events: none;  cursor: default;"><i class="fa fa-power-off fa-fw"></i> Log out</a>
                        </li> --%>
                    </ul>
                </div>
            </div>
            <!-- /.navbar sidebar -->
        </nav>
        <div id="page-wrapper">
        	
            <div class="row">
                <div class="col-lg-12">                	
                    <h3 class="page-header">Question Detail</h3>
                    <label>ID </label> &nbsp;${id } &nbsp;<label>Dataset Version</label>&nbsp;${datasetVersion }
                </div> 
     			<%-- revision = ${revision }
     			sparqlOnly = ${sparqlOnly }
     			sparqlCase = ${sparqlCaseOnly }
     			Answer status = ${answerStatus } --%>
            </div>
            <!-- /.row -->
            <div class="row">
            	
                <div class="col-lg-12">
                    <div class="panel panel-default">
                    	<c:if test="${isExist=='yes'}">
                        <div class="panel-body">
                        <div class="panel-heading">
                        <div id = "alert_placeholder"></div>
                        <form role="form" method="post" name="headingForm" id="headingForm" action="${pageContext.request.contextPath}/document-list/curate/done/${id }/${datasetVersion}">
                        <div class="row">
                        	
                        	 	<div class="col-md-6" style="text-align: left">
                        	 	<button type="submit" name="startButton" id="startButton" class="btn btn-default" ${startButtonDisabled }>${startButton }</button>
                        	 	</div>
                        	 	<div class="col-md-6" style="text-align: right">                        	 	
		                        <a href="${pageContext.request.contextPath}/document-list/curate/remove-question/${id}/${datasetVersion}" class="btn btn-danger" >Remove Question</a>
<%--                                 <a href="${pageContext.request.contextPath}/document-list/curate/done/${id}/${datasetVersion}" class="btn btn-primary">Done</a> --%>
	                            <input type="submit" name="doneButton" id="doneButton" ${displayStatus} class="btn btn-primary" value="Done" />
		                        <a href="${pageContext.request.contextPath}/document-list/curate/cancel/${id}/${datasetVersion}" class="btn btn-warning" >Cancel</a>
		                        </div>                        	
                        </div>
                        </form>
                        </div>
                        <form role="form" method="post" name="documentForm" id="documentForm">
                            <div class="row">
                            	<div class="col-lg-12">
                        			<div class="form-group">                        					
	                                    <div class="col-lg-1" style="text-align:left; vertical-align: middle;">
	                                    	<label>Question</label>
	                                    </div>
	                                    <div class="col-lg-11" style="text-align:left">
		                                    <input class="form-control" value="${languageToQuestionEn}" id="languageToQuestion" disabled="disabled">
		                                    <input type="hidden" id="question" name="question" value="${languageToQuestionEn }">
		                                    <input type="hidden" id="revision" name="revision" value="${revision }">
	                                    </div>
                                    </div>
                        		</div>
                                <div class="col-lg-6">
                                    
                                        <div class="form-group">
                                            <label>SPARQL</label>
                                            <textarea class="form-control" rows="11" id="sparqlQuery" name="sparqlQuery" ${disabledForm }>${sparqlQuery}</textarea>     
                                            <%-- size list ${sparqlSugg.size() }
                                            Sparql: ${sparqlQuery }
                                            Status result : ${resultStatus }  --%>                          
                                            <c:if test="${not resultStatus}">
                                            	<c:if test="${sparqlAndCaseSugg.size()>0}">
                                            		<p class="help-block"><button type="button" class="btn btn-outline-primary" id="sparqlSugg" data-toggle="modal" data-target="#provideSparqlSuggestion">View SPARQL Suggestion</button></p>
                                            	</c:if>
                                        	</c:if>
                                        </div>                       
                                    
                                </div>
                                <!-- /.col-lg-6 (nested) -->
                                <div class="col-lg-6">
                                	
                                        <div class="form-group">
                                            <label>Answer from File</label>
                                            <textarea class="form-control" rows="4" id="goldenAnswer" name="goldenAnswer" ${disabledForm }>${goldenAnswer}</textarea>
                                            <p class="help-block"></p>
                                        </div>
                                    	<div class="form-group">
                                            <label>Answer from Current Endpoint</label>                                            
                                            <textarea class="form-control" rows="4" id="goldenAnswer" name="onlineAnswer" ${disabledForm }>${onlineAnswer}</textarea>
                                            <p class="help-block"></p>
                                        </div>
                                    
                                </div>
                                <!-- /.col-lg-6 (nested) -->
                            </div>
                            <!-- /.row (nested) -->
                            <!-- row -->
                            <div class="row">
                            	<div class="col-lg-4">
                            		<div class="form-group">
                                            <label>Endpoint</label>
                                            <input class="form-control" value="" id="endpoint" name="endpoint" ${disabledForm }>
                                            <p class="help-block"></p>
                                        </div>
                            	</div>
                        		<div class="col-lg-4">
                        			<div class="form-group">
                        				<c:if test="${isAnswerTypeCurated}">
                                           <span class="glyphicon glyphicon-check  "></span>
                                        </c:if>
                                            <label>Answer Type</label>
                                            <select class="form-control" id="answerType" name="answerType" ${disabledForm } 
                                            <c:if test="${not empty answerTypeSugg}">
    										style="background-color:#E6E6FA"	
											</c:if>
											>
											
												<option value="" <c:if test="${empty answerType}"> selected = "selected"</c:if>></option>
												<option value="boolean" ${answerType == "boolean" ? 'selected="selected"' : ''} >Boolean</option>
												<option value="date" ${answerType == "date" ? 'selected="selected"' : ''} >Date</option>
												<option value="number" ${answerType == "number" ? 'selected="selected"' : ''} >Number</option>
												<option value="resource" ${answerType == "resource" ? 'selected="selected"' : ''} >Resource</option>
												<option value="string" ${answerType == "string" ? 'selected="selected"' : ''} >String</option>
											</select>
                                            <p class="text-danger" id="answerTypeSugg"><i><c:if test="${not empty answerTypeSugg}">
    										Suggestion :	
											${fn:toUpperCase(fn:substring(answerTypeSugg, 0, 1))}${fn:toLowerCase(fn:substring(answerTypeSugg, 1,fn:length(answerTypeSugg)))}
											</c:if></i>
											<input type="hidden" value="${answerTypeSugg }" name="answerTypeSugg" id="answerTypeSugg" class="form-control" />
											</p>
                                     </div>	
                        		</div>
                        		<div class="col-lg-4">
                        			<div class="form-group">
                        				<c:if test="${isOutOfScopeCurated == true}">
                                           <span class="glyphicon glyphicon-check  "></span>
                                        </c:if>
                                            <label>Out of Scope</label>
                                           	<select class="form-control" id="outOfScope" name="outOfScope" ${disabledForm }
                                           	 <c:if test="${not empty outOfScopeSugg}" >
    										style="background-color:#E6E6FA"    											
											</c:if> >
                                                <option value=""  <c:if test="${empty outOfScope}">selected="selected"</c:if>></option> 
                                                <option value="true" ${outOfScope == "true" ? 'selected="selected"' : ''} >True</option>
                                                <option value="false" ${outOfScope == "false" ? 'selected="selected"' : ''}>False</option>                                                
                                            </select>
                                            
                                            <p class="text-danger" id="outOfScopeSugg"><c:if test="${not empty outOfScopeSugg}">
                                            <em>Suggestion :												
                                            ${fn:toUpperCase(fn:substring(outOfScopeSugg, 0, 1))}${fn:toLowerCase(fn:substring(outOfScopeSugg, 1,fn:length(outOfScopeSugg)))}</em>
                                            </c:if>
                                            <input type="hidden" value="${outOfScopeSugg }" name="outOfScopeSugg" id="outOfScopeSugg" class="form-control" />
                                            </p>    												
                                    </div>	
                        		</div>                        		
                        	</div>
                        	
                        	<div class="row">
                            	<div class="col-lg-4">
                        			<div class="form-group">
                                    	<c:if test="${isAggregationCurated == true}">
                                           <span class="glyphicon glyphicon-check  "></span>
                                        </c:if>
                                           <label>Aggregation</label>
                                           
                                           <select class="form-control" id="aggregation" name="aggregation" ${disabledForm } <c:if test="${not empty aggregationSugg}">
    										style="background-color:#E6E6FA"	
											</c:if> >
                                                <option></option>
                                                <option value="true" ${aggregation == true ? 'selected="selected"' : ''}>True</option>
                                                <option value="false" ${aggregation == false ? 'selected="selected"' : ''}>False</option>                                                
                                            </select>
                                            <p class="text-danger" id="aggregationSugg"><em><c:if test="${not empty aggregationSugg}">
    												Suggestion : </c:if>
    												${fn:toUpperCase(fn:substring(aggregationSugg, 0, 1))}${fn:toLowerCase(fn:substring(aggregationSugg, 1,fn:length(aggregationSugg)))}</em>
    											<input type="hidden" value="${aggregationSugg }" name="aggregationSugg" id="aggregationSugg" class="form-control" />
    										</p>
                                    </div>	
                        		</div>
                        		
                        		<div class="col-lg-4">
                        			<div class="form-group">
                        				<c:if test="${isOnlydboCurated == true}">
                                           <span class="glyphicon glyphicon-check  "></span>
                                        </c:if>
                                           <label>Onlydbo</label>
                                           
                                           <select class="form-control" id="onlydbo" name="onlydbo"  ${disabledForm } <c:if test="${not empty onlyDboSugg}">
    										style="background-color:#E6E6FA"	
											</c:if>>
                                                <option></option>
                                                <option value="true" ${onlydbo == true ? 'selected="selected"' : ''}>True</option>
                                                <option value="false" ${onlydbo == false ? 'selected="selected"' : ''}>False</option>
                                                
                                            </select>
                                            <p class="text-danger" id="onlyDboSugg">
                                            	<c:if test="${not empty onlyDboSugg}">
    												Suggestion : </c:if>${fn:toUpperCase(fn:substring(onlyDboSugg, 0, 1))}${fn:toLowerCase(fn:substring(onlyDboSugg, 1,fn:length(onlyDboSugg)))}
    										<input type="hidden" value="${onlyDboSugg }" name="onlyDboSugg" id="onlyDboSugg" class="form-control" />
    										</p>
                                    </div>
                        		</div>
                        		
                        		<div class="col-lg-4">
                        			<div class="form-group">
                        				<c:if test="${isHybridCurated == true}">
                                           <span class="glyphicon glyphicon-check  "></span>
                                        </c:if>
                                            <label>Hybrid</label>
                                           <select class="form-control" id="hybrid" name="hybrid" ${disabledForm } <c:if test="${not empty hybridSugg}">
    										style="background-color:#E6E6FA"	
											</c:if> >
                                                <option></option>
                                                <option value="true" ${hybrid == true ? 'selected="selected"' : ''}>True</option>
                                                <option value="false" ${hybrid == false ? 'selected="selected"' : ''}>False</option>                                                
                                            </select>
                                            
                                            <p class="text-danger" id="hybridSugg">
                                            	<c:if test="${not empty hybridSugg}">
    												Suggestion : </c:if>${fn:toUpperCase(fn:substring(hybridSugg, 0, 1))}${fn:toLowerCase(fn:substring(hybridSugg, 1,fn:length(hybridSugg)))}
    										<input type="hidden" value="${hybridSugg }" name="hybridSugg" id="hybridSugg" class="form-control" />
    										</p>
                                    </div>	
                        		</div>
                        	</div>
                        	
                        	<input type=hidden id="id" name="id" value="${id }">
                        	<input type=hidden id="datasetVersion" name="datasetVersion" value="${datasetVersion }">
                        	<input type=hidden id="pseudoSparqlQuery" name="pseudoSparqlQuery" value="${pseudoSparqlQuery }">
                        	<div class="row">
                        		<div class="col-lg-12">
                        			<div class="form-group">
                        				<c:if test="${isKeywordCurated == true}">
                                           <span class="glyphicon glyphicon-check  "></span>
                                        </c:if>
                        				<label>Multilingual Keyword</label>
                        				<table id="keywordTable" class="table table-striped table-bordered table-hover">
                        					<thead>
			                                    <tr>
			                                        <th class="text-center">Language</th>
			                                        <th class="text-center">Keywords</th>			                                    
			                                    </tr>
			                                </thead>
			                                <tbody>			                                
			                                     <c:forEach items="${languageToKeyword}" var="map">			                                     	
				                                    	<tr id="${map.getKey() }">
				                                    		<td>${map.getKey()}</td>
				                                    		<td>${map.getValue()}</td>
				                                    						                                    		
				                                    	</tr>		                                    	
			                                    </c:forEach>
			                                </tbody>
                        				</table>
                        				
                        				<c:if test="${addKeywordsSuggestionStatus}">
                        				<p class="help-block"><button type="button" class="btn btn-outline-primary" id="addKeywordsSuggestions" data-toggle="modal" data-target="#provideKeywordsSuggestions">View Keywords Suggestion</button></p>
                        				</c:if>
                        				
                        				<c:if test="${(not addKeywordsSuggestionStatus) and (addKeywordsTranslationsStatus)}">
                        				<p class="help-block"><button type="button" class="btn btn-outline-primary" id="addKeywordsTranslations" data-toggle="modal" data-target="#provideKeywordsTranslations">View Keywords Translations Suggestion</button></p>
                        				</c:if>
                        			</div>
                        		</div>
                        	</div>
                        	
                        	<div class="row">
                        		<div class="col-lg-12">
                        			<div class="form-group">
                        				<c:if test="${isQuestionTranslationCurated == true}">
                                           <span class="glyphicon glyphicon-check  "></span>
                                        </c:if>
                        				<label>Multilingual Question</label> &nbsp;&nbsp;
                        				<!--  
                        				<button type="button" class="btn btn-info btn-sm" data-toggle="modal" data-target="#myModal">Question Correction</button>
                        				<br />
                        				-->
                        				<table id="example"  class="table table-striped table-bordered table-hover">
                        					<thead>
			                                    <tr>
			                                        <th class="text-center">Language</th>
			                                        <th class="text-center">Question</th>
			                                        
			                                        
			                                    </tr>
			                                </thead>
			                                
			                                <tbody>			                                
			                                     <c:forEach items="${languageToQuestion}" var="map">			                                     	
				                                    	<tr id="${map.getKey() }">
				                                    		<td>${map.getKey()}</td>
				                                    		<td>${map.getValue()}</td>	
				                                    				                                    		
				                                    	</tr>			                                    	
			                                    </c:forEach>
			                                </tbody>
                        				</table>
                        				
                        				<c:if test="${addQuestionTranslationsStatus}">
                        				<p class="help-block"><button type="button" class="btn btn-outline-primary" id="addQuestionsTranslations" data-toggle="modal" data-target="#provideQuestionTranslations">View Question Translations Suggestion</button></p>
                        				</c:if>
                        			</div>
                        		</div>
                        	</div>                        
                            </form>
                        </div>                        
                        </c:if>
                        <c:if test="${isExist=='no'}">
                        	<div class="panel-body">
                        		<div class="row">
                        			Data is not available
                        		</div>
                        	</div>
                        </c:if>
                    </div>                   
                </div>                
            </div>            
        </div>
        
        <!-- /#page-wrapper -->
		<!-- start block myModal -->
	  <div class="modal fade" id="myModal" role="dialog">
	    <div class="modal-dialog">	    
	      <!-- Modal content-->
	      <div class="modal-content">
	        <div class="modal-header">
	          <button type="button" class="close" data-dismiss="modal">&times;</button>
	          <h4 class="modal-title">Question Correction</h4>
	        </div>
	        <div class="modal-body">
	          <div class="row">
	          	<div class="col-lg-12">
	            	<div class="form-group"> 
	            		<div class="col-lg-12">
	                        <div class="form-group">                        					
		                    	<div class="col-md-2" style="text-align:left; vertical-align: middle;">
		                        	<label>Question</label>
		                        </div>
		                        <div class="col-md-10" style="text-align:left">
			                    	<input class="form-control" value="${languageToQuestionEn}" id="languageToQuestion">            
		                        </div>
	                         </div>
	                     </div>
	                </div>
	            </div>
	          </div>
	        </div>
	        <div class="modal-footer">
	          <button type="button" class="btn btn-default" data-dismiss="modal">Close</button>
	        </div>
	      </div>      
    	</div>
    	</div>
    	
    	<!-- end block myModal -->
    	<!-- start block editKeywordModal -->
    	<div class="modal fade" id="provideSparqlSuggestion" role="dialog">
	    	<div class="modal-dialog" style="width:80%">
	    		<div class="modal-content">
	    			<div class="modal-header">
			        	<button type="button" class="close" data-dismiss="modal">&times;</button>
			          	<h4 class="modal-title">SPARQL Suggestion</h4>
			          	
			        </div>
			        <div class="modal-body">
			        <form method="get" action="${pageContext.request.contextPath}/document-detail-curate/save-sparql-suggestion/${id }/${datasetVersion}">
			        	<table class="table table-striped table-bordered table-hover" id="dataTables-example">
                                <thead>
                                    <tr>                                    	
                                        <th width="10%" class="text-center">No.</th>
                                        <th class="text-center">Suggestion</th>
                                        <th class="text-center">Answers from Current Endpoint</th>
                                        <th><input type="checkbox" id="checkBoxAllAnswer" /></th>                                      
                                    </tr>
                                </thead>
                                <tbody>
                                	<c:forEach var="sparqlSugg" items="${sparqlAndCaseSugg}" varStatus="loop">
<%--                                  		<c:forEach var="answerFromCurrentEndpoint" items="${answerFromVirtuosoList}" varStatus="loop"> --%>
                                    	<tr id="${sparqlSugg.getKey()}">                                    		
                                    		<td style="width:5%">${loop.index+1}</td>
				                            <td style="width:45%">
				                            <textarea class="form-control" rows="11" disabled="disabled">${sparqlSugg.getKey()}</textarea>
				                            </td>
				                            <td style="width:50%"><c:out value="${answerFromVirtuosoList}"/></td>
				                            <td>
				                            <input type="checkbox" class="chkCheckBoxId" value="${sparqlSugg.getKey()};${sparqlSugg.getValue()};${answerFromCurrentEndpoint}" name="sparqlAnswerValue" />
				                            </td> 
				                            <%-- <input type="hidden" id="sparqlAndCaseSuggestion" name="sparqlAndCaseSuggestion" value="${sparqlAndCaseSugg }" /> 
											<input type="hidden" id="suggestedAnswer" name="suggestedAnswer" value="${answerFromVirtuosoList }" /> --%>	
																									                                                             		                                  		
                                    	</tr>
<%--                                     	</c:forEach> --%>
                                    </c:forEach>                                    
                                </tbody>
                            </table>
                      	<div class="modal-footer">
                      	
			        		<input type="submit" class="btn btn-primary" value="Accept" onclick="return confirm('Are you sure you want to take the suggestion? ')" />
 			        		<button type="button" class="btn btn-default" data-dismiss="modal">Reject</button>
 			        		<input type="hidden" id="sparqlOnly" name="sparqlOnly" value="${sparqlOnly }" /> 
							<input type="hidden" id="sparqlCaseOnly" name="sparqlCaseOnly" value="${sparqlCaseOnly }" />
			        	</div>
			       </form> 
	    		</div>
	    	</div>
	    </div>
    	<!-- end block editKeywordModal -->
    </div>    
<%--     isKeywordCurated = ${isKeywordCurated } --%>
    <!-- start block keywords suggestions -->
    	<div class="modal fade" id="provideKeywordsSuggestions" role="dialog">
	    	<div class="modal-dialog" style="width:80%">
	    		<div class="modal-content">
	    			<div class="modal-header">
			        	<button type="button" class="close" data-dismiss="modal">&times;</button>
			          	<h4 class="modal-title">Keywords Suggestion</h4>
			        </div>
			        <div class="modal-body">
			        <form method="get" action="${pageContext.request.contextPath}/document-detail-curate/save-keywords-suggestion/${id }/${datasetVersion}">
			        	<table class="table table-striped table-bordered table-hover" id="dataTables-example">
                                <thead>
                                    <tr>                                    	
                                        <th width="10%" class="text-center">No.</th>                                        
                                        <th class="text-center">Keyword</th>
                                        <th><input type="checkbox" id="checkBoxAllKeyword" /></th>                                         
                                    </tr>
                                </thead>
                                <tbody>                               		 
                                     <c:forEach  items="${listKeywordSuggestion}" var="list" varStatus="loop">
                                    	<tr>                                    		
                                    		<td>${loop.index+1}</td>				                           
				                            <td><c:out value="${list}"/></td>
				                            <td><input type="checkbox" class="chkCheckBoxId" value="${list}" name="keywordTerm" /></td>		         
                                    		                                  		
                                    	</tr>
                                    </c:forEach>
                                </tbody>
                            </table>
			        <div class="modal-footer">
			        	<input type="submit" class="btn btn-primary" value="Add" onclick="return confirm('Are you sure you want to add keyword/s? ')" />
 			        	<button type="button" class="btn btn-default" data-dismiss="modal">Close</button>
			        </div>
			        </form>
	    		</div>
	    	</div>
	    </div>
	     </div>
    	<!-- end block keywords translation -->
    
    <!-- start block keywords translations -->
    	<div class="modal fade" id="provideKeywordsTranslations" role="dialog">
	    	<div class="modal-dialog" style="width:80%">
	    		<div class="modal-content">
	    			<div class="modal-header">
			        	<button type="button" class="close" data-dismiss="modal">&times;</button>
			          	<h4 class="modal-title">Keywords Translations Suggestion</h4>
			        </div>
			        <div class="modal-body">
			        <form method="get" action="${pageContext.request.contextPath}/document-detail-curate/save-keywords-translations/${id }/${datasetVersion}">
			        	<table class="table table-striped table-bordered table-hover" id="dataTables-example">
                                <thead>
                                	
                                    <tr>                                    	
                                        <th width="10%" class="text-center">No.</th>
                                        <th class="text-center">Language Code</th>
                                        <th class="text-center">Keywords Translation</th>
                                        <th><input type="checkbox" id="checkBoxAllKeywordTranslation" /></th>                                         
                                    </tr>
                                </thead>                                
                                <tbody>                                 	
                                     <c:forEach  items="${keywordsTranslations}" var="map" varStatus="loop">
                                    	<tr id="${map.getKey()}">                                    		                                    		
                                    		<td>${loop.index+1}</td>
				                            <td>${map.getKey()}</td>
				                            <td>${map.getValue()}</td>
				                            <td><input type="checkbox" class="chkCheckBoxId" value="${map.getKey()};${map.getValue()}" name="langId" /></td>                                      		                                  		
                                    	</tr>
                                    </c:forEach>
                                </tbody>
                            </table>
			        <div class="modal-footer">
			        	<input type="hidden" id="englishKeywordTranslation" name="englishKeywordTranslation" value="${englishKeywordTranslation }">
			        	<input type="submit" class="btn btn-primary" value="Add" onclick="return confirm('Are you sure you want to add keywords translation/s? ')" />			        	
 			        	<button type="button" class="btn btn-default" data-dismiss="modal">Close</button> 			        	
			        </div>
			        </form>
			        
	    		</div>
	    	</div>
	    </div>
	     </div>
    	<!-- end block keywords translation -->
    <!-- start block question translations -->
    	<div class="modal fade" id="provideQuestionTranslations" role="dialog">
	    	<div class="modal-dialog" style="width:80%">
	    		<div class="modal-content">
	    			<div class="modal-header">
			        	<button type="button" class="close" data-dismiss="modal">&times;</button>
			          	<h4 class="modal-title">Question Translations Suggestion</h4>
			        </div>
			        <div class="modal-body">
			        <form method="get" action="${pageContext.request.contextPath}/document-detail-curate/save-questionTranslations-suggestion/${id }/${datasetVersion}">
			        	<table class="table table-striped table-bordered table-hover" id="dataTables-example">
                                <thead>
                                    <tr>                                    	
                                        <th width="10%" class="text-center">No.</th>
                                        <th class="text-center">Language Code</th>
                                        <th class="text-center">Question Translation</th>
                                        <th><input type="checkbox" id="checkBoxAllQuestion" /></th>                                        
                                    </tr>
                                </thead>
                                <tbody>                                 	
                                     <c:forEach  items="${questionTranslation}" var="map" varStatus="loop">
                                    	<tr id="${map.getKey()}">                                    		
                                    		<td>${loop.index+1}</td>
				                            <td>${map.getKey()}</td>
				                            <td>${map.getValue()}</td>
				                            <td><input type="checkbox" class="chkCheckBoxId" value="${map.getKey()};${map.getValue()}" name="langId" /></td>                              		                                  		
                                    	</tr>
                                    </c:forEach>
                                </tbody>
                            </table>
			        <div class="modal-footer">
			        	<input type="submit" class="btn btn-primary" value="Add" onclick="return confirm('Are you sure you want to add question translation/s? ')" />
 			        	<button type="button" class="btn btn-default" data-dismiss="modal">Close</button>
			        </div>
			        </form>
	    		</div>
	    	</div>
	    </div>
	     </div>
    	<!-- end block question translation -->
    </div>
    
    <!-- /#wrapper -->

    <!-- jQuery -->
    <script src="<c:url value="/resources/vendor/jquery/jquery.min.js" />"></script>
    <script src="<c:url value="/resources/vendor/jquery/jquery.js" />"></script>

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
	<script type="text/javascript">
		$(document).ready(function(){
			$('#checkBoxAllAnswer').click(function(){
				if ($(this).is(':checked'))	
					$('.chkCheckBoxId').prop('checked', true);
				else
					$('.chkCheckBoxId').prop('checked', false);
			});
		});
	</script>
	<script type="text/javascript">
		$(document).ready(function(){
			$('#checkBoxAllKeyword').click(function(){
				if ($(this).is(':checked'))	
					$('.chkCheckBoxId').prop('checked', true);
				else
					$('.chkCheckBoxId').prop('checked', false);
			});
		});
	</script>
	<script type="text/javascript">
		$(document).ready(function(){
			$('#checkBoxAllKeywordTranslation').click(function(){
				if ($(this).is(':checked'))	
					$('.chkCheckBoxId').prop('checked', true);
				else
					$('.chkCheckBoxId').prop('checked', false);
			});
		});
	</script>	
	<script type="text/javascript">
		$(document).ready(function(){
			$('#checkBoxAllQuestion').click(function(){
				if ($(this).is(':checked'))	
					$('.chkCheckBoxId').prop('checked', true);
				else
					$('.chkCheckBoxId').prop('checked', false);
			});
		});
	</script>
	<script>
	  $(document).ready($('.form-control').change(function() {
	   $.ajax({
	    type : "post",
	    url : "${pageContext.request.contextPath}/document-list/curate/save",
	    cache : false,
	    data : $('#documentForm').serialize(),
	    success : function(response) {	    	
	    	window.location = "${pageContext.request.contextPath}/document-list/curate/curation-process/${id }/${datasetVersion}";
	    	$('#alert_placeholder').html('<div class="alert alert-success" role="alert">Data is saved</div>')
	    },
	    error : function() {
	     alert('Error while request..');
	    }
	   });
	  }));
 </script>
 <!-- Question Editing -->
 <script>
	 var editor;
	//Activate an inline edit on click of a table cell
	 $('#example').on( 'click', 'tbody td:not(:first-child)', function (e) {
	     editor.inline( this );
	 } );
	 
	 $("#example").dataTable({
	     "paging":   false,
	     "ordering": false,
	     "info":     false,
	     "filter" : false
	 }).makeEditable({
		 "sUpdateURL": "${pageContext.request.contextPath}/document-list/document/edit-question/${id}/${datasetVersion}",
		 "fnOnEdited" :function(response) {	    	
		    	window.location = "${pageContext.request.contextPath}/document-list/curate/curation-process/${id }/${datasetVersion}";
		    	$('#alert_placeholder').html('<div class="alert alert-success" role="alert">Data is saved</div>')
		    }
	}); 

 </script>
 <!-- Keyword Editing -->
 <script>
	 var editor;
	//Activate an inline edit on click of a table cell
	 $('#keywordTable').on( 'click', 'tbody td:not(:first-child)', function (e) {
	     editor.inline( this );
	 } );
	 
	 $("#keywordTable").dataTable({
	     "paging":   false,
	     "ordering": false,
	     "info":     false,
	     "filter" : false
	 }).makeEditable({
		 "sUpdateURL": "${pageContext.request.contextPath}/document-list/document/edit-keyword/${id}/${datasetVersion}",
		 "fnOnEdited" :function(response) {	    	
		    	window.location = "${pageContext.request.contextPath}/document-list/curate/curation-process/${id }/${datasetVersion}";
		    	$('#alert_placeholder').html('<div class="alert alert-success" role="alert">Data is saved</div>')
		    }
	}); 

 </script> 
 
 <script>
$(function(){
     $('#startButton').click(function(){
    	$('.form-control').prop('disabled', false);
        $('#startButton').val('CorrectionOnProgress');
        $('#startButton').prop("disable", true);
        document.getElementById("doneButton").style.display='';
        document.getElementById("chkLabel").style.display='';
     });
     $('#doneButton').click(function(){
     	$('.form-control').prop('disabled', true);
         $('#startButton').val('Start Curate');
         document.getElementById("doneButton").style.display='none';
         document.getElementById("chkLabel").style.display='none';
      });     
});
</script>


</body>

</html>