<%@ page language="java" contentType="text/html; charset=ISO-8859-1" pageEncoding="ISO-8859-1" %>
<%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="utf-8">
    <meta http-equiv="X-UA-Compatible" content="IE=edge">
    <meta name="viewport" content="width=device-width, initial-scale=1">
    <meta name="description" content="">
    <meta name="author" content="">

    <title>QUANT-User List</title>

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
        <div id="page-wrapper">
            <div class="row">
                <div class="col-lg-12">
                    <h1 class="page-header">User Log List</h1>
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
                        
                        <div class="row">
                            <table width="100%" class="table table-striped table-bordered table-hover" id="dataTables-example">
                                <thead>
                                    <tr>
                                        <th width="10%" class="text-center">No.</th>
                                        <th class="text-center">Tanggal</th>
                                        <th class="text-center">Log Type</th>
                                        <th class="text-center">Log Info</th>
                                        <th></th>
                                    </tr>
                                </thead>
                                <tbody>
                                     <c:forEach var="userLogs" items="${userLogs}" varStatus="loop">
                                    	<tr>
                                    		<td>${loop.index+1}</td>
                                    		<td>${userLogs.getLogDate()}</td>
                                    		<td>${userLogs.getLogType() }</td>
                                    		<td>${userLogs.getLogInfo().toString()}</td>
                                    		<td>
                                    		        			
                                    		</td>
                                    	</tr>
                                    </c:forEach>
                                </tbody>
                            </table>
                            <!-- /.table-responsive -->
                            </div>
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
		<!-- start block insert-user-modal -->
    	<div class="modal fade" id="insert-user-modal" role="dialog">
	    	<div class="modal-dialog">
	    		<div class="modal-content">
	    			<div class="modal-header">
			        	<button type="button" class="close" data-dismiss="modal">&times;</button>
			          	<h4 class="modal-title">Add User Form</h4>
			        </div>
			        <form action="user-list/user/insert-user" method="POST" class="form-horizontal" role="form">
            		<input id="id-input" name="id-input" type="text" class="hidden" >
			        <div class="modal-body">
			        	<div class="form-group">
			                <label for="name-input" class="col-sm-2 control-label"><h5>Name</h5> </label>
			                <div class="col-sm-10">
			                  <input id="name-input" name="name-input" type="text" class="form-control" placeholder="Name">
			                </div>
			            </div>
			            
			            <div class="form-group">
			                <label for="email-input" class="col-sm-2 control-label"><h5>e-Mail</h5> </label>
			                <div class="col-sm-10">
			                  <input id="email-input" name="email-input" type="text" class="form-control" placeholder="e-Mail address">
			                </div>
			            </div>
			            <div class="form-group">
			                <label for="role-input" class="col-sm-2 control-label"><h5>Role</h5> </label>
			                <div class="col-sm-10">
			                  <input id="role-user-input" name="role-user-input" type="text" class="form-control" placeholder="Type in administrator or evaluator">
			                </div>
			            </div>
			            <div class="form-group">
			                <label for="username-input" class="col-sm-2 control-label"><h5>User Name</h5> </label>
			                <div class="col-sm-10">
			                  <input id="username-input" name="username-input" type="text" class="form-control" placeholder="User name">
			                </div>
			            </div>
			            
			            <div class="form-group">
			                <label for="password-input" class="col-sm-2 control-label"><h5>Password</h5> </label>
			                <div class="col-sm-10">
			                  <input id="password-user-input" name="password-user-input" type="text" class="form-control" placeholder="Password">
			                </div>
			            </div>
			            
			        </div>
			       
			        <div class="modal-footer">
			        	<button type="button" class="btn btn-default" data-dismiss="modal">Cancel</button>
			        	<button type="submit" class="btn btn-primary">Add User</button>
			        </div>
	    		</div>
	    		 </form>
	    	</div>
	    </div>
    	<!-- end block insert-user-modal -->
    	<!-- start delete-user-modal -->
		  <div id='delete-user-modal' class="modal fade" tabindex="-1" role="dialog" aria-labelledby="Delete User" aria-hidden="true">
		    <div class="modal-dialog">
		      <div class="modal-content">
		        <div class="modal-header">
		          <button type="button" class="close" data-dismiss="modal" aria-hidden="true">&times;</button>
		          <h4 class="modal-title">Delete User</h4>
		        </div>
		        <form id="formDelete" class="form-horizontal" action="user-list/user/delete-user" method="POST">
		          <input type="text" name="id-input-delete" class="hidden">
		          <div class="modal-body">
		            <div>Are you sure you want to delete this user?</div>
		          </div>
		          <div class="modal-footer">
		            <button type="button" class="btn btn-default" data-dismiss="modal">Cancel</button>
		            <button type="submit" class="btn btn-danger">Delete User</button>
		          </div>
		        </form>
		      </div>
		      <!-- /.modal-content -->
		    </div>
		    <!-- /.modal-dialog -->
		  </div>
		  <!-- /.modal -->
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
    <!-- add User -->
    <script>
    $("#add-user").click(function() {
    
	    $("#insert-user-modal input[name=name-input]").val("");
	    $("#insert-user-modal input[name=email-input]").val("");
	    $("#insert-user-modal input[name=username-input]").val("");
	    $("#insert-user-modal input[name=password-user-input]").val("");
	    $("#insert-user-modal input[name=role-user-input]").val("");
	    $("#insert-user-modal input[name=id-input]").val("");
	    $("#insert-user-modal .modal-title").html("Add User");
	    $("#insert-user-modal button[type=submit]").html("Add User");
    
  });
    </script>
	<!-- view and edit User -->
	<script>
 	$(".edit-user-modal").click(function() {
	    name = $(this).find(".name-input").html();
	  	email = $(this).find(".email-input").html();
	    username = $(this).find(".username-input").html();
	    password = $(this).find(".password-input").html();
	    role = $(this).find(".role-input").html();
	    id= $(this).find(".id-input").html();
	    $("#insert-user-modal input[name=name-input]").val(name);
	    $("#insert-user-modal input[name=email-input]").val(email);
	    $("#insert-user-modal input[name=username-input]").val(username);
	    $("#insert-user-modal input[name=password-user-input]").val(password);
	    $("#insert-user-modal input[name=role-user-input]").val(role);
	    $("#insert-user-modal input[name=id-input]").val(id);
	    $("#insert-user-modal .modal-title").html("View/Edit User");
	    $("#insert-user-modal button[type=submit]").html("Edit User");
	  });
 	</script>
 	<!-- Delete User -->
 	<script>
 	$(".delete-user").click(function() {
 	    id = $(this).find(".id-input-delete").html();
 	    $("#delete-user-modal input[name=id-input-delete]").val(id);
 	  });
 	</script>
</body>

</html>
