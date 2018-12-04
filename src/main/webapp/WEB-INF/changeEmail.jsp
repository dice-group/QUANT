<!DOCTYPE html>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<html lang="en">
<head>
    <meta charset="utf-8">
    <meta name="viewport" content="width=device-width, initial-scale=1">
    <meta name="description" content="">
    <meta name="author" content="">
    <title>QUANT-Question Answering Curator</title>
    <link rel="stylesheet" href="webjars/bootstrap/4.1.3/css/bootstrap.min.css">
    <script src="webjars/jquery/3.3.1/jquery.min.js"></script>
    <script src="webjars/bootstrap/4.1.3/js/bootstrap.min.js"></script>

</head>
<body class="text-center">
<form method="POST" action="/changeEmail" style="max-width: 300px">
    <h1 class="h3 mb-3 font-weight-normal">ChangePassword of user ${logedInAs}</h1>

    <label for="newEmail" class="sr-only">Password</label>
    <input type="email" name="new-email" id="newEmail" class="form-control" placeholder=${logedInAs} required>
    <label for="password" class="sr-only">Password</label>
    <input type="password" name="password" id="password" class="form-control" placeholder="Password" required>
    <button class="btn btn-lg btn-primary btn-block" type="submit">Change</button>
    <p class="mt-5 mb-3 text-muted">&copy; 2017-2018</p>
</form>
<!-- Plugin JavaScript -->
</body>
<!-- Bootstrap core JavaScript -->

</html>