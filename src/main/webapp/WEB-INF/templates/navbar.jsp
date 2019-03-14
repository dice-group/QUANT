
<nav class="navbar navbar-expand-lg navbar-light bg-light">
    <a class="navbar-brand" href="/">QUANT</a>
    <button class="navbar-toggler" type="button" data-toggle="collapse" data-target="#navbarText" aria-controls="navbarText" aria-expanded="false" aria-label="Toggle navigation">
        <span class="navbar-toggler-icon"></span>
    </button>

    <div class="collapse navbar-collapse" id="navbarText">
        <ul class="navbar-nav mr-auto">
            <c:if test="${User != null}">
            <li class="nav-item dropdown">
                <a class="nav-link dropdown-toggle" href="#" id="dropdown09" data-toggle="dropdown" aria-haspopup="true" aria-expanded="false">User</a>
                <div class="dropdown-menu" aria-labelledby="dropdown09">
                    <a class="dropdown-item" href="/changeEmail">Change Email Address</a>
                    <a class="dropdown-item" href="/changePassword">Change Password</a>
                    <c:if test="${User.role =='ADMIN'}">
                        <a class="dropdown-item" href="/register">Register</a>
                        <a class="dropdown-item" href="/userlist">User List</a>
                    </c:if>
                    <a class="dropdown-item" href="/logout">Logout</a>
                </div>
            </li>
            <li class ="nav-item">
                <a class = "nav-link" href="/datasetlist">Datasets</a>
            </li>
            </c:if>


    <c:if test="${error != null}" var="Message">
        <li></lid><span class="alert alert-danger"><c:out value="${error}"></c:out></span></c:if></li>
    <c:if test="${success != null}" var="Message">
        <li><span class="alert alert-success "><c:out value="${success}"></c:out></span></c:if></li>

</ul>
</div>
</nav>
