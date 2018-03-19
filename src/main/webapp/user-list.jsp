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

    <title>SB Admin 2 - Bootstrap Admin Theme</title>

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
                    </ul>
                </div>
            </div>
            <!-- /.navbar sidebar -->
        
        </nav>
        <div id="page-wrapper">
            <div class="row">
                <div class="col-lg-12">
                    <h1 class="page-header">User List</h1>
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
	                        <div class="col-lg-12">
		                        <button type="button" class="add-user btn btn-xs btn-default" data-toggle="modal" data-target="#insert-user-modal" id="add-user">
						        	<span class="glyphicon glyphicon-plus"></span> Add User
						        </button>
					        </div>
                        </div>
                            <table width="100%" class="table table-striped table-bordered table-hover" id="dataTables-example">
                                <thead>
                                    <tr>
                                        <th width="10%" class="text-center">No.</th>
                                        <th class="text-center">Name</th>
                                        <th class="text-center">User Name</th>
                                        <th class="text-center">Role</th>
                                        <th class="text-center">e-Mail</th>
                                        <th></th>
                                    </tr>
                                </thead>
                                <tbody>
                                     <c:forEach var="users" items="${users}" varStatus="loop">
                                    	<tr>
                                    		<td>${loop.index+1}</td>
                                    		<td>${users.getName()}</td>
                                    		<td>${users.getUsername() }</td>
                                    		<td>${users.getRole()}</td>
                                    		<td>${users.getEmail()}</td>
                                    		<td>
                                    		<button type="button" class="edit-user-modal btn btn-xs btn-default" data-toggle="modal" data-target="#insert-user-modal">
				                                    				<span class="glyphicon glyphicon-eye-open"></span>
				                                    				<div class="hidden name-input">${users.getName()}</div>
													                <div class="hidden email-input">${users.getEmail()}</div>
													                <div class="hidden username-input">${users.getUsername()}</div>
				                                    			</button>
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
			        <div class="modal-body">
			        	<div class="form-group">
			                <label for="name-input" class="col-sm-2 control-label">Name </label>
			                <div class="col-sm-10">
			                  <input id="name-input" name="name-input" type="text" class="form-control" placeholder="Name">
			                </div>
			            </div>
			            
			            <div class="form-group">
			                <label for="email-input" class="col-sm-2 control-label">e-Mail </label>
			                <div class="col-sm-10">
			                  <input id="email-input" name="email-input" type="text" class="form-control" placeholder="Email address">
			                </div>
			            </div>
			            <div class="form-group">
			                <label for="username-input" class="col-sm-2 control-label">User Name </label>
			                <div class="col-sm-10">
			                  <input id="username-input" name="username-input" type="text" class="form-control" placeholder="Username">
			                </div>
			            </div>
			            
			            <div class="form-group">
			                <label for="password-input" class="col-sm-2 control-label">Password </label>
			                <div class="col-sm-10">
			                  <input id="password-input" name=password-input" type="text" class="form-control" placeholder="Password">
			                </div>
			            </div>
			            <div class="form-group">
			                <label for="role-input" class="col-sm-2 control-label">Role </label>
			                <div class="col-sm-10">
			                  <input id="role-input" name=role-input" type="text" class="form-control" placeholder="Role">
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
	    $("#insert-user-modal .modal-title").html("Add User");
	    $("#insert-user-modal button[type=submit]").html("Add User");
    rh.mq.hideNavbar();
  });
    </script>
	<!-- view and edit User -->
	<script>
 $(".edit-user-modal").click(function() {
	    name = $(this).find(".name-input").html();
	  	email = $(this).find(".email-input").html();
	    username = $(this).find(".username-input").html();
	    $("#insert-user-modal input[name=name-input]").val(name);
	    $("#insert-user-modal input[name=email-input]").val(email);
	    $("#insert-user-modal input[name=username-input]").val(username);
	    $("#insert-user-modal .modal-title").html("View/Edit User");
	    $("#insert-user-modal button[type=submit]").html("Edit User");
	  });
 </script>
</body>

</html>
