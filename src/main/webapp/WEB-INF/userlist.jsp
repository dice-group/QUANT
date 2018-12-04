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

    <title>QUANT-User List</title>

    <!-- Bootstrap Core CSS -->
    <link rel="stylesheet" href="webjars/bootstrap/4.1.3/css/bootstrap.min.css">
    <link href="../resources/css/main.css" rel="stylesheet" type="text/css">
    <script src="webjars/jquery/3.3.1/jquery.min.js"></script>
    <script src="webjars/bootstrap/4.1.3/js/bootstrap.min.js"></script>

    <!-- HTML5 Shim and Respond.js IE8 support of HTML5 elements and media queries -->
    <!-- WARNING: Respond.js doesn't work if you view the page via file:// -->
    <!--[if lt IE 9]>
    <script src="https://oss.maxcdn.com/libs/html5shiv/3.7.0/html5shiv.js"></script>
    <script src="https://oss.maxcdn.com/libs/respond.js/1.4.2/respond.min.js"></script>
    <![endif]-->
</head>
<body>

<div class="container">
    <div class="row">


        <div class="col-md-12">
            <h4>User List</h4>
            <div class="table-responsive">


                <table id="mytable" class="table table-bordred table-striped">

                    <thead>

                    <th><input type="checkbox" id="checkall" /></th>
                    <th>Email</th>
                    <th>Role</th>
                    <th>Edit</th>

                    <th>Delete</th>
                    <th>Activate</th>
                    </thead>
                    <tbody>
                    <c:forEach items="${Users}" var="user">
                        <tr class="user-row" data-user-id=${user.id}>
                            <td><input type="checkbox" class="checkthis" /></td>
                            <td>${user.email}</td>
                            <td>${user.role}</td>
                            <td><p data-placement="top" data-toggle="tooltip" title="Edit"><button class="btn btn-primary btn-xs" id="edit-button" data-title="Edit" data-toggle="modal" data-target="#edit" ><span class="glyphicon glyphicon-pencil"></span></button></p></td>
                            <td><p data-placement="top" data-toggle="tooltip" title="Delete"><button class="btn btn-danger btn-xs" data-title="Delete" data-toggle="modal" data-target="#delete" ><span class="glyphicon glyphicon-trash"></span></button></p></td>
                            <td>
                                <div class="material-switch pull-right">
                                    <input id="someSwitchOptionPrimary" name="someSwitchOption001" type="checkbox" checked/>
                                    <label for="someSwitchOptionPrimary" class="label-primary"></label>
                                </div>
                            </td>
                        </tr>

                    </c:forEach>

                    </tbody>

                </table>

                <div class="clearfix"></div>


            </div>

        </div>
    </div>
</div>


<div class="modal fade" id="edit" tabindex="-1" role="dialog" aria-labelledby="edit" aria-hidden="true">
    <div class="modal-dialog">
        <div class="modal-content">
           <div class="modal-header">
                <!--<button type="button" class="close" data-dismiss="modal" aria-hidden="true"><span class="glyphicon glyphicon-remove" aria-hidden="true"></span></button>-->
                <h4 class="modal-title custom_align" id="Heading">Edit Your Detail</h4>
            </div>
                <form method="POST" action="/editUser">
                    <div class="modal-body">
                        <input id="id-input" name="id-input" type="hidden" >
                        <div class="form-group">

                            <input class="form-control " name="user-name" type="text" id="edit-username">
                        </div>
                        <p>User Role
                            <select name="role">
                                <option value="ADMIN">Admin</option>
                                <option value="USER">User</option>
                            </select>
                        </p>
                        <div class="form-group">
                            <label for="admin-password" class="sr-only">Admin Password</label>
                            <input type="password" name="admin-password" id="admin-password" class="form-control" placeholder="Your password" required>
                        </div>
                    </div>
                    <div class="modal-footer ">
                        <button  type="submit" class="btn btn-primary btn-lg" style="width: 100%;"><span class="glyphicon glyphicon-ok-sign"></span> Update</button>
                    </div>
                </form>
            </div>

        <!-- /.modal-content -->
    </div>
    <!-- /.modal-dialog -->
</div>



<div class="modal fade" id="delete" tabindex="-1" role="dialog" aria-labelledby="edit" aria-hidden="true">
    <div class="modal-dialog">
        <div class="modal-content">
            <div class="modal-header">
                <button type="button" class="close" data-dismiss="modal" aria-hidden="true"><span class="glyphicon glyphicon-remove" aria-hidden="true"></span></button>
                <h4 class="modal-title custom_align" id="Heading">Delete this entry</h4>
            </div>
            <div class="modal-body">

                <div class="alert alert-danger"><span class="glyphicon glyphicon-warning-sign"></span> Are you sure you want to delete this Record?</div>

            </div>
            <div class="modal-footer ">
                <button type="button" class="btn btn-success" ><span class="glyphicon glyphicon-ok-sign"></span> Yes</button>
                <button type="button" class="btn btn-default" data-dismiss="modal"><span class="glyphicon glyphicon-remove"></span> No</button>
            </div>
        </div>
        <!-- /.modal-content -->
    </div>
    <!-- /.modal-dialog -->
</div>
</body>
<script>
    $("#edit-button").click(function() {
        var $row = $(this).closest("tr");
        var $id = $row.data("user-id");// Find the row
        var $email = $($row.find("td").get(1)).text();
        var $role = $($row.find("td").get(2)).text();
        $("#edit-username").val($email);
        $('#id-input').val($id);
        $('select option[value='+$role+']').attr("selected",true);
        console.log($id);
        console.log($email);
        console.log($role);

    });
    $('#someSwitchOptionPrimary').click(function() {
        if($(this).prop('checked')) {
            console.log("Checked Box Selected");
        } else {
            console.log("Checked Box deselect");
        }
    });
</script>
</html>
