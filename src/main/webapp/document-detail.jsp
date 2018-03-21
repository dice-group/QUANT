<%@ page language="java" contentType="text/html; charset=ISO-8859-1"
    pageEncoding="ISO-8859-1"%>
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
	<script type="text/javascript" language="javascript" src="http://www.technicalkeeda.com/js/javascripts/plugin/jquery.js"></script>
	<script type="text/javascript" src="http://www.technicalkeeda.com/js/javascripts/plugin/json2.js"></script>
		
</head>

<body>

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
        <div id="page-wrapper">
        	<div class="row">
                <div class="col-lg-12">
                    <a href="${pageContext.request.contextPath}/document-list/detail/${idPrevious}/${datasetVersion}" class="btn btn-default"><< PREVIOUS</a>
                    <a href="${pageContext.request.contextPath}/document-list/detail/${idNext}/${datasetVersion}" class="btn btn-default">NEXT >></a>
                </div>
                <!-- /.col-lg-12 -->
            </div>
            <div class="row">
                <div class="col-lg-12">
                	<div id = "alert_placeholder"></div>
                    <h3 class="page-header">Question Detail</h3>
                    <label>ID </label> &nbsp;${id } &nbsp;<label>Dataset Version</label>&nbsp;${datasetVersion }
                </div>
                
                <!-- /.col-lg-12 -->
            </div>
            <!-- /.row -->
            <div class="row">
            	
                <div class="col-lg-12">
                    <div class="panel panel-default">
                    	<c:if test="${isExist=='yes'}">
                        <div class="panel-body">
                        <input type="submit"" name=startButton' id='startButton' value='Start Correction'/>
                        <form role="form" method="post" name="documentForm" id="documentForm">
                            <div class="row">
                            	<div class="col-lg-12">
                        			<div class="form-group">                        					
	                                    <div class="col-lg-1" style="text-align:left; vertical-align: middle;">
	                                    	<label>Question</label>
	                                    </div>
	                                    <div class="col-lg-11" style="text-align:left">
		                                    <input class="form-control" value="${languageToQuestionEn}" id="languageToQuestion" readonly="readonly">
		                                    <!--  <p class="font-italic">Question is displayed in English (as default)</p> -->
	                                    </div>
                                    </div>
                        		</div>
                                <div class="col-lg-6">
                                    
                                        <div class="form-group">
                                            <label>SPARQL</label>
                                            <textarea class="form-control" rows="11" id="sparqlQuery" name="sparqlQuery" disabled="disabled">${sparqlQuery}</textarea>
                                            <p class="help-block"></p>
                                        </div>
                                        
                                    
                                </div>
                                <!-- /.col-lg-6 (nested) -->
                                <div class="col-lg-6">
                                	
                                        <div class="form-group">
                                            <label>Answer from File</label>
                                            <textarea class="form-control" rows="4" id="goldenAnswer" name="goldenAnswer" readonly="readonly">${goldenAnswer}</textarea>
                                            <p class="help-block"></p>
                                        </div>
                                    	<div class="form-group">
                                            <label>Answer from Current Endpoint</label> 
                                            <!--  
                                            <a href="#change-answer" class="change-answer btn btn-xs btn-default">
                                            	<span class="glyphicon glyphicon-upload"></span>
                                            </a>
                                            -->
                                            <textarea class="form-control" rows="4" id="goldenAnswer" name="onlineAnswer" readonly="readonly">${onlineAnswer}</textarea>
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
                                            <input class="form-control" value="" id="endpoint" name="endpoint" disabled="disabled">
                                            <p class="help-block"></p>
                                        </div>
                            	</div>
                        		<div class="col-lg-4">
                        			<div class="form-group">
                                            <label>Answer Type</label>
                                            <input class="form-control" value="${fn:toUpperCase(fn:substring(answerType, 0, 1))}${fn:toLowerCase(fn:substring(answerType, 1,fn:length(answerType)))}" id="answerType" name="answerType" 
                                            <c:if test="${not empty answerTypeSugg}">
    										style="background-color:#E6E6FA"	
											</c:if>
											disabled="disabled"
 											>
                                            <p class="text-danger" id="answerTypeSugg"><i></i><c:if test="${not empty answerTypeSugg}">
    										Suggestion :	
											${fn:toUpperCase(fn:substring(answerTypeSugg, 0, 1))}${fn:toLowerCase(fn:substring(answerTypeSugg, 1,fn:length(answerTypeSugg)))}
											</c:if></i></p>
                                     </div>	
                        		</div>
                        		<div class="col-lg-4">
                        			<div class="form-group">
                                            <label>Out of Scope</label>
                                           	<select class="form-control" id="outOfScope" name="outOfScope" <c:if test="${not empty outOfScopeSugg}" >
    										style="background-color:#E6E6FA"    											
											</c:if> disabled="disabled">
                                                <option></option> 
                                                <option value="true" ${outOfScope == true ? 'selected="selected"' : ''} >True</option>
                                                <option value="false" ${outOfScope == false ? 'selected="selected"' : ''}>False</option>                                                
                                            </select>
                                            <p class="text-danger" id="outOfScopeSugg"><c:if test="${not empty outOfScopeSugg}">
                                            Suggestion :												
                                            ${fn:toUpperCase(fn:substring(outOfScopeSugg, 0, 1))}${fn:toLowerCase(fn:substring(outOfScopeSugg, 1,fn:length(outOfScopeSugg)))}
                                            </c:if></p>    												
                                    </div>	
                        		</div>
                        		
                        	</div>
                        	<div class="row">
                            	<div class="col-lg-4">
                        			<div class="form-group">
                                            <label>Aggregation</label>
                                           <select class="form-control" id="aggregation" name="aggregation" <c:if test="${not empty aggregationSugg}">
    										style="background-color:#E6E6FA"	
											</c:if> disabled="disabled">
                                                <option></option>
                                                <option value="true" ${aggregation == true ? 'selected="selected"' : ''}>True</option>
                                                <option value="false" ${aggregation == false ? 'selected="selected"' : ''}>False</option>                                                
                                            </select>
                                            <p class="text-danger" id="aggregationSugg"><c:if test="${not empty aggregationSugg}">
    												Suggestion : </c:if>
    												${fn:toUpperCase(fn:substring(aggregationSugg, 0, 1))}${fn:toLowerCase(fn:substring(aggregationSugg, 1,fn:length(aggregationSugg)))}</p>
                                    </div>	
                        		</div>
                        		<div class="col-lg-4">
                        			<div class="form-group">
                                            <label>Onlydbo</label>
                                           <select class="form-control" id="onlydbo" name="onlydbo" <c:if test="${not empty onlyDboSugg}">
    										style="background-color:#E6E6FA"	
											</c:if> disabled="disabled">
                                                <option></option>
                                                <option value="true" ${onlydbo == true ? 'selected="selected"' : ''}>True</option>
                                                <option value="false" ${onlydbo == false ? 'selected="selected"' : ''}>False</option>
                                                
                                            </select>
                                            <p class="text-danger" id="onlyDboSugg">
                                            	<c:if test="${not empty onlyDboSugg}">
    												Suggestion : </c:if>${fn:toUpperCase(fn:substring(onlyDboSugg, 0, 1))}${fn:toLowerCase(fn:substring(onlyDboSugg, 1,fn:length(onlyDboSugg)))}</p>
                                    </div>
                        		</div>
                        		<div class="col-lg-4">
                        			<div class="form-group">
                                            <label>Hybrid</label>
                                           <select class="form-control" id="hybrid" name="hybrid" <c:if test="${not empty hybridSugg}">
    										style="background-color:#E6E6FA"	
											</c:if> disabled="disabled">
                                                <option></option>
                                                <option value="true" ${hybrid == true ? 'selected="selected"' : ''}>True</option>
                                                <option value="false" ${hybrid == false ? 'selected="selected"' : ''}>False</option>
                                                
                                            </select>
                                            <p class="text-danger" id="hybridSugg">
                                            	<c:if test="${not empty hybridSugg}">
    												Suggestion : </c:if>${fn:toUpperCase(fn:substring(hybridSugg, 0, 1))}${fn:toLowerCase(fn:substring(hybridSugg, 1,fn:length(hybridSugg)))}</p>
                                    </div>	
                        		</div>
                        	</div>
                        	<input type=hidden id="id" name="id" value="${id }">
                        	<input type=hidden id="datasetVersion" name="datasetVersion" value="${datasetVersion }">
                        	<input type=hidden id="pseudoSparqlQuery" name="pseudoSparqlQuery" value="${pseudoSparqlQuery }">
                        	<div class="row">
                        		<div class="col-lg-12">
                        			<div class="form-group">
                        				<label>Multilingual Keyword List</label>
                        				<table id="keywordTable" width="100%" class="table table-striped table-bordered table-hover">
                        					<thead>
			                                    <tr>
			                                        <th class="text-center">Language</th>
			                                        <th class="text-center">Keywords</th>
<!-- 			                                    <th></th> -->
			                                    </tr>
			                                </thead>
			                                <tbody>			                                
			                                     <c:forEach items="${languageToKeyword}" var="map">			                                     	
				                                    	<tr id="${map.getKey() }">
				                                    		<td>${map.getKey()}</td>
				                                    		<td>${map.getValue()}</td>
				                                    		<%-- <td>
				                                    			<button type="button" class="edit-movie-quote btn btn-xs btn-default" data-toggle="modal" data-target="#insert-keyword-modal">
				                                    				<span class="glyphicon glyphicon-pencil"></span>
				                                    				<div class="hidden entity-key">{{ moviequote.key.urlsafe() }}</div>
													                <div class="hidden keyword-key">${map.getKey()}</div>
													                <div class="hidden keyword-value">${map.getValue()}</div>
				                                    			</button>
				                                    		</td>  --%>
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
                        				<label>Multilingual Question List</label> &nbsp;&nbsp;
                        				<!--  
                        				<button type="button" class="btn btn-info btn-sm" data-toggle="modal" data-target="#myModal">Question Correction</button>
                        				<br />
                        				-->
                        				<table id="example"  width="100%" class="table table-striped table-bordered table-hover">
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
                        			</div>
                        		</div>
                        	</div>
                            <!-- /.row (nested) -->
                            </form>
                        </div>
                        <!-- /.panel-body -->
                        </c:if>
                        <c:if test="${isExist=='no'}">
                        	<div class="panel-body">
                        		<div class="row">
                        			Data is not available
                        		</div>
                        	</div>
                        </c:if>
                    </div>
                    <!-- /.panel -->
                </div>
                <!-- /.col-lg-12 -->
            </div>
            <!-- /.row -->
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
    	<div class="modal fade" id="insert-keyword-modal" role="dialog">
	    	<div class="modal-dialog">
	    		<div class="modal-content">
	    			<div class="modal-header">
			        	<button type="button" class="close" data-dismiss="modal">&times;</button>
			          	<h4 class="modal-title">Keyword Correction</h4>
			        </div>
			        <div class="modal-body">
			        	<div class="form-group">
			                <label for="quote-input" class="col-sm-2 control-label">Language:</label>
			                <div class="col-sm-10">
			                  <input id="keyword_key-input" name="keyword_key" type="text" class="form-control" placeholder="Quote">
			                </div>
			            </div>
			            
			            <div class="form-group">
			                <label for="movie-input" class="col-sm-2 control-label">Movie: </label>
			                <div class="col-sm-10">
			                  <input id="keyword_value-input" name="keyword_value" type="text" class="form-control" placeholder="Title">
			                </div>
			            </div>
			        </div>
			        <div class="modal-footer">
			        	<button type="button" class="btn btn-default" data-dismiss="modal">Close</button>
			        </div>
	    		</div>
	    	</div>
	    </div>
    	<!-- end block editKeywordModal -->
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
	var url = "${pageContext.request.contextPath}/document-list/detail-correction/${id}/${datasetVersion}";
	  $(document).ready($('.form-control').change(function() {
	   $.ajax({
	    type : "post",
	    url : "${pageContext.request.contextPath}/document-list/document/save",
	    cache : false,
	    data : $('#documentForm').serialize(),
	    success : function(response) {
	    	
	    	window.location = url;
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
	 }).makeEditable({"sUpdateURL": "${pageContext.request.contextPath}/document-list/document/edit-question/${id}/${datasetVersion}"}); 

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
	 }).makeEditable({"sUpdateURL": "${pageContext.request.contextPath}/document-list/document/edit-keyword/${id}/${datasetVersion}"}); 

 </script>
 <script>
 $(".edit-movie-quote").click(function() {
	    keywordKey = $(this).find(".keyword-key").html();
	    keywordValue = $(this).find(".keyword-value").html();
	    entityKey = $(this).find(".entity-key").html();
	    $("#insert-keyword-modal input[name=keyword_key]").val(keywordKey);
	    $("#insert-keyword-modal input[name=keyword_value]").val(keywordValue);
	    $("#insert-keyword-modal input[name=entity_key]").val(entityKey).prop("disabled", false);
	    $("#insert-keyword-modal .modal-title").html("Edit this MovieQuote");
	    $("#insert-keyword-modal button[type=submit]").html("Edit Quote");
	  });
 </script>
 <!-- Change answer from file -->
 <script>
 $(".billing").on('click',function(){
	    $.ajax({url:"/document-list/document/change-answer-from-file/${id}/${datasetVersion}", success: function(result){

	    }});
	}) 
 </script>
 <script>
$(function(){
     $('#startButton').click(function(){
    	 $('.form-control').prop('disabled', false);
        $('#startButton').val('CorrectionOnProgress');
     });
});
</script>
</body>

</html>