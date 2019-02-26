<%@include file="templates/layout.jsp" %>
<div class="container-fluid">
    <div class="row">
        <div class="col-md-12  pt-2">
            <form method="POST" action="/register" style="max-width: 300px">
                <h1 class="h3 mb-3 font-weight-normal">Register new User</h1>
                <div class="form-group">
                    <label for="email" class="sr-only">Email address</label>
                    <input type="email" name="email" id="email" class="form-control" placeholder="Email address"
                           required autofocus>
                </div>
                <div class="form-group">
                    <label for="password" class="sr-only">Password</label>
                    <input type="password" name="password" id="password" class="form-control" placeholder="Password"
                           required>
                </div>
                <div class="form-group">
                    <label for="confirm-password" class="sr-only">Confirm Password</label>
                    <input type="password" name="confirm-password" id="confirm-password" class="form-control"
                           placeholder="Confirm Password" required>
                </div>
                <div class="form-group">
                    <p>User Role
                        <select name="role">
                            <option value="ADMIN">Admin</option>
                            <option value="USER">User</option>
                        </select>
                    </p>
                </div>
                <button class="btn btn-lg btn-primary btn-block" type="submit">Sign in</button>
            </form>
        </div>
    </div>
</div>
<%@include file="templates/footer.jsp" %>