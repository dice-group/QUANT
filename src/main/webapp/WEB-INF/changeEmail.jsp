<%@include file="templates/layout.jsp" %>

<div class="container-fluid">
    <div class="row">
        <div class="col-md-12 pt-2">
        <form method="POST" action="/changeEmail" style="max-width: 300px">

            <h1 class="h3 mb-3 font-weight-normal">Change email of <c:out value="${logedInAs}"></c:out> </h1>
            <div class="form-group">
                <label for="newEmail" class="sr-only">Password</label>
                <input type="email" name="new-email" id="newEmail" class="form-control" placeholder=<c:out value="${logedInAs}"></c:out> required>
            </div>
            <div class="form-group">
                <label for="password" class="sr-only">Password</label>
                <input type="password" name="password" id="password" class="form-control" placeholder="Password" required>
            </div>
            <button class="btn btn-lg btn-primary btn-block" type="submit">Change</button>
        </form>
        </div>
    </div>
</div>

<%@include file="templates/footer.jsp" %>