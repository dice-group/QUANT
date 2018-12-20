<%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>
<%@ page language="java" contentType="text/html;charset=UTF-8"  %>
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="utf-8">
    <meta http-equiv="X-UA-Compatible" content="IE=edge">
    <meta name="viewport" content="width=device-width, initial-scale=1">
    <meta name="description" content="">
    <meta name="author" content="">

    <title>${Title}</title>

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
<%@include file="navbar.jsp"%>
<div class="container">
    <div class="row">
        <div class="col-md-12">
            <h4>Dataset List</h4>
            <div class ="table-responsive">
                <table id="table_id" class="display" style="width:100%" > <!--class="table table-bordred table-striped-->
                <thead>
                <tr>
                    <th>ID</th>
                    <th>Dataset Name</th>
                </tr>
                </thead>
                <tbody>

                    <c:forEach items="${Datasets}" var="dataset">
                        <tr class="dataset-row" dataset-id=${dataset.getId()}>
                        <td>${dataset.getId()}</td>
                            <td><a href="/questionslist/${dataset.getId()}">${dataset.getName()}</a> </td>
                        </tr>
                    </c:forEach>
                </tbody>
                </table>
            </div>
        </div>
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

</html>
