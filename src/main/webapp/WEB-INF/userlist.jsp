<%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
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

    <link rel="stylesheet" href="webjars/datatables/1.10.19/css/jquery.dataTables.min.css">
    <link rel="stylesheet" href="webjars/bootstrap/4.1.3/css/bootstrap.min.css">
    <link href="../resources/css/main.css" rel="stylesheet" type="text/css">

    <!-- HTML5 Shim and Respond.js IE8 support of HTML5 elements and media queries -->
    <!-- WARNING: Respond.js doesn't work if you view the page via file:// -->
    <!--[if lt IE 9]>
    <![endif]-->
</head>
<body>

<div class="container">
    <div class="row">


        <div class="col-md-12">
            <h4>User List</h4>
            <div class="table-responsive">


                <table id="table_id" class="display" style="width:100%" > <!--class="table table-bordred table-striped-->

                    <thead>
                    <tr>
                        <th>Email</th>
                        <th>Role</th>
                        <th>Edit</th>
                        <th>Reset Password</th>
                        <th>Activate</th>
                    </tr>
                    </thead>
                    <tbody>
                    <c:forEach items="${Users}" var="user">
                        <tr class="user-row" data-user-id=${user.id}>
                            <td>${user.email}</td>
                            <td>${user.role}</td>
                            <td><p data-placement="top" data-toggle="tooltip" title="Edit"><button class="btn btn-primary btn-xs" id="edit-button" data-title="Edit" data-toggle="modal" data-target="#edit" ><span class="glyphicon glyphicon-pencil"></span></button></p></td>
                            <td><p data-placement="top" data-toggle="tooltip" title="Reset Password"><button class="btn btn-danger btn-xs" data-title="reset-password" data-toggle="modal" data-target="#reset-password" ><span class="glyphicon glyphicon-trash"></span></button></p></td>
                            <td>
                                    <div class="material-switch pull-right">
                                        <c:choose>
                                            <c:when test="${user.activated}">
                                                <input id="someSwitchOptionPrimary/${user.id}" name="someSwitchOption001" type="checkbox" checked/>
                                            </c:when>
                                            <c:otherwise>
                                                <input id="someSwitchOptionPrimary/${user.id}" name="someSwitchOption001" type="checkbox"/>
                                            </c:otherwise>
                                        </c:choose>
                                        <label for="someSwitchOptionPrimary/${user.id}" class="label-primary"></label>
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
    </div>
</div>



<div class="modal fade" id="reset-password" tabindex="-1" role="dialog" aria-labelledby="edit" aria-hidden="true">
    <div class="modal-dialog">
        <div class="modal-content">
            <div class="modal-header">
                <!--<button type="button" class="close" data-dismiss="modal" aria-hidden="true"><span class="glyphicon glyphicon-remove" aria-hidden="true"></span></button>-->
                <h4 class="modal-title custom_align" id="Heading-Reset"></h4>
            </div>
            <form method="POST" action="/resetPassword">
                <div class="modal-body">
                    <input id="id-input-reset" name="id-input" type="hidden" >
                    <div class="form-group">
                        <label for="new-password" class="sr-only">Admin Password</label>
                        <input type="password" name="new-password" id="new-password" class="form-control" placeholder="New Password" required>
                    </div>
                    <div class="form-group">
                        <label for="confirm-new-password" class="sr-only">Admin Password</label>
                        <input type="password" name="confirm-new-password" id="confirm-new-password" class="form-control" placeholder="Confirm new Password" required>
                    </div>

                    <div class="form-group">
                        <label for="admin-password-reset" class="sr-only">Admin Password</label>
                        <input type="password" name="admin-password" id="admin-password-reset" class="form-control" placeholder="Admin password" required>
                    </div>
                </div>
                <div class="modal-footer ">
                    <button  type="submit" class="btn btn-primary btn-lg" style="width: 100%;"><span class="glyphicon glyphicon-ok-sign"></span> Update</button>
                </div>
            </form>
        </div>

        <!-- /.modal-content -->
    </div>

</div>
</body>
<script src="webjars/jquery/3.3.1/jquery.min.js"></script>
<script src="webjars/datatables/1.10.19/js/jquery.dataTables.min.js"></script>
<script src="webjars/bootstrap/4.1.3/js/bootstrap.min.js"></script>
<script>
    $(document).ready( function () {
        $('#table_id').DataTable();
    } );
</script>
<script>
    $(document).ready( function () {
        $("button[data-title='Edit']").click(function() {
            var $row = $(this).closest("tr");
            $("#edit-username").val($($row.find("td").get(0)).text());
            $('#id-input').val($row.data("user-id"));
            if($($row.find("td").get(1)).text() == 'ADMIN') {
                $('select option[value=ADMIN]').attr("selected", true);
                $('select option[value=USER]').attr("selected", false);
            }
            if($($row.find("td").get(1)).text() == 'USER') {
                $('select option[value=ADMIN]').attr("selected", false);
                $('select option[value=USER]').attr("selected", true);
            }
        });
    } );
</script>
<script>
    $(document).ready( function () {
        $("button[data-title='reset-password']").click(function() {
            var $row = $(this).closest("tr");
            $("#Heading-Reset").text('Reset password of '+$($row.find("td").get(0)).text());
            $('#id-input-reset').val($row.data("user-id"));
        });
    } );
</script>
<script>
    $(document).ready( function () {
        $('.label-primary').click(function() {
            var $row = $(this).closest("tr");
            var $email = $($row.find("td").get(0)).text();
            var $checkbox = $(this).parent().find('input:checkbox:first');
            console.log($checkbox)

            if(!$checkbox.prop('checked')) {

                console.log("Checked Box Selected");
                console.log($email);
                $.post("/setActivation",
                    {
                        name: $email,
                        action: "activate"
                    },
                    function(data, status){
                        if(status != 'success') {
                            $checkbox.prop('checked',false);
                            console.log("Data: " + data + "\nStatus: " + status);
                        }
                    });

            } else {
                console.log("Checked Box DeSelected");
                console.log($email);
                $.post("/setActivation",
                    {
                        name: $email,
                        action: "deactivate"
                    },
                    function(data, status){
                        if(status != 'success') {
                            $checkbox.prop('checked',true);
                            console.log("Data: " + data + "\nStatus: " + status);
                        }
                    });
            }
        });
    } );
</script>
</html>
