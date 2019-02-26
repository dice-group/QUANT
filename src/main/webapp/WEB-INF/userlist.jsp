<%@include file="templates/layout.jsp" %>
<div class="container-fluid">
    <div class="row">
        <div class="col-md-12  pt-2">
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

                                        <c:choose>
                                            <c:when test="${user.activated}">
                                                <input id="someSwitchOptionPrimary/${user.id}" name="someSwitchOption001" type="checkbox" checked/>
                                            </c:when>
                                            <c:otherwise>
                                                <input id="someSwitchOptionPrimary/${user.id}" name="someSwitchOption001" type="checkbox"/>
                                            </c:otherwise>
                                        </c:choose>
                                        <label for="someSwitchOptionPrimary/${user.id}" class="label-primary" name ="label-primary"></label>

                            </td>
                        </tr>

                    </c:forEach>

                    </tbody>

                </table>

                <div class="clearfix"></div>


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
                        <button  type="submit" class="btn btn-primary btn-lg" style="width: 100%;"><span class="glyphicon glyphicon-ok-sign"></span>Update</button>
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
                    <button  type="submit" class="btn btn-primary btn-lg" style="width: 100%;"><span class="glyphicon glyphicon-ok-sign"></span>Update</button>
                </div>
            </form>
        </div>

        <!-- /.modal-content -->
    </div>

</div>
        </div>
    </div>
</div>


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
        console.log("doc ready")
        $('input[type=checkbox]').click(function() {
            console.log("label-primary")
            var $row = $(this).closest("tr");
            var $email = $($row.find("td").get(0)).text();
            var $checkbox = $(this).parent().find('input:checkbox:first');
            console.log($checkbox)

            if($checkbox.prop('checked')) {

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
<%@include file="templates/footer.jsp" %>
