<%@include file="templates/layout.jsp" %>
<div class="container-fluid">
    <div class="row">
        <div class="col-md-12  pt-2">
        <form method="POST" action="/signIn" style="max-width: 300px">
            <h1 class="h3 mb-3 font-weight-normal">Please sign in</h1>
            <div class="form-group">
                <label for="email" class="sr-only">Email address</label>
                <input type="email" name="email" id="email" class="form-control" placeholder="Email address" required autofocus>
            </div>
            <div class="form-group">
                <label for="password" class="sr-only">Password</label>
                <input type="password" name="password" id="password" class="form-control" placeholder="Password" required>
            </div>
            <button class="btn btn-lg btn-primary btn-block" type="submit">Sign in</button>
        </form>
    </div>
    </div>
</div>

<%@include file="templates/footer.jsp" %>