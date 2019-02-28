<%@include file="templates/layout.jsp" %>
    <div class="container-fluid">
        <div class="row">
            <div class="col-md-12 pt-2">
        <form method="POST" action="/changePassword" style="max-width: 300px">
            <h1 class="h3 mb-3 font-weight-normal">Change password of <c:out value="${logedInAs}"></c:out></h1>
            <div class="form-group">
                <label for="oldpassword" class="sr-only">Password</label>
                <input type="password" name="old-password" id="oldpassword" class="form-control" placeholder="Old Password" required>
            </div>
            <div class="form-group">
                <label for="newpassword" class="sr-only">Password</label>
                <input type="password" name="new-password" id="newpassword" class="form-control" placeholder="New Password" required>
            </div>
            <div class="form-group">
                <label for="confirm-password" class="sr-only">Confirm Password</label>
                <input type="password" name="confirm-password" id="confirm-password" class="form-control" placeholder="Confirm new Password" required>
            </div>
            <button class="btn btn-lg btn-primary btn-block" type="submit">Change</button>
        </form>
    </div>
        </div>
    </div>
<%@include file="templates/footer.jsp" %>