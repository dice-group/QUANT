<%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<!DOCTYPE html>

<html lang="en">
  <head>
    <meta charset="utf-8">
    <meta name="viewport" content="width=device-width, initial-scale=1, shrink-to-fit=no">
    <meta name="description" content="">
    <meta name="author" content="">
    <title>QUANT-Question Answering Curator</title>
    <!-- Bootstrap core CSS -->
    <link href="<c:url value="/resources/vendor/bootstrap/css/bootstrap.min.css" />" rel="stylesheet">
    <!-- Custom fonts for this template -->
    <link href="<c:url value="https://fonts.googleapis.com/css?family=Source+Sans+Pro:200,200i,300,300i,400,400i,600,600i,700,700i,900,900i" />" rel="stylesheet">
    <link href="<c:url value="https://fonts.googleapis.com/css?family=Merriweather:300,300i,400,400i,700,700i,900,900i" />" rel="stylesheet">
    <link href="<c:url value="/resources/vendor/font-awesome/css/font-awesome.min.css" />" rel="stylesheet">
    <!-- Custom styles for this template -->
    <link href="<c:url value="/resources/css/coming-soon.min.css" />" rel="stylesheet">
  </head>
  <body>
    <div class="overlay"></div>
    <div class="masthead" style="width:60.5rem">
      <div class="masthead-bg"></div>
      <div class="container h-100">
        <div class="row h-100">
          <div class="col-12 my-auto" style="position: relative; width:50%; display:block; font-weight: 300; flex: 0 0 100%; -webkit-box-flex: 0; max-width: 100%; margin-bottom: auto!important; margin-top: auto!important; box-sizing: border-box;">
            <div class="masthead-content text-white py-5 py-md-0">
              <h1 class="mb-5" style="color:#fff; font-family: Merriweather; font-weight: 700; font-size: 4rem">Question Answering Curator (QUANT)</h1>
              <p class="mb-7" style="color:#fff; font-size: 2.3rem; margin-bottom: 3rem!important; margin-top: 0; box-sizing: border-box;">It helps to define a SPARQL of a question so that it can be run against a given SPARQL endpoint and produce a semantically correct result set. 
                <strong>Please do sign in to use it</strong></p>
              <div class="input-group input-group-newsletter">
                <div class="input-group-append">
                  <a href="login" class="btn btn-primary" type="button">SIGN IN</a>
                </div>
              </div>
            </div>
          </div>
        </div>
      </div>
    </div>
    <div class="social-icons">
      <ul class="list-unstyled text-center mb-0">
        <li class="list-unstyled-item">
          <a href="#">
            <i class="fa fa-twitter"></i>
          </a>
        </li>
        <li class="list-unstyled-item">
          <a href="#">
            <i class="fa fa-facebook"></i>
          </a>
        </li>
        <li class="list-unstyled-item">
          <a href="#">
            <i class="fa fa-instagram"></i>
          </a>
        </li>
      </ul>
    </div>
    <!-- Bootstrap core JavaScript -->
    <script src="<c:url value="/resources/vendor/jquery/jquery.min.js" />"></script>
    <script src="<c:url value="/resources/vendor/bootstrap/js/bootstrap.bundle.min.js" />"></script>
    <!-- Plugin JavaScript -->
    <script src="<c:url value="/resources/vendor/vide/jquery.vide.min.js" />"></script>
    <!-- Custom scripts for this template -->
    <script src="<c:url value="/resources/js/coming-soon.min.js" />"></script>
  </body>
</html>