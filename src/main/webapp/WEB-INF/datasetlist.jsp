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
                    <th>Anotate</th>
                    <c:if test="${User.role =='ADMIN'}">
                    <th>Manage</th>
                    <th>Delete</th>
                    </c:if>
                </tr>
                </thead>
                <tbody>

                    <c:forEach items="${Datasets}" var="dataset">
                        <tr class="dataset-row" dataset-id=${dataset.id}>
                            <td><c:out value="${dataset.id}"></c:out></td>
                        <td><c:out value="${dataset.getName()}"></c:out></td>
                            <td><a href="/questionslist/${dataset.id}"><button class="btn btn-outline-info btn-sm">Anotate</button></a></td>
                        <c:if test="${User.role =='ADMIN'}">
                            <td><a href="/manageDataset/${dataset.id}"><button class="btn btn-outline-info btn-sm">Manage</button></a></td>
                            <form id ="deleteDataset" action="/deleteDataset" method="post" onSubmit="return confirm('Are you sure you wish to delete?')">
                                <input type ="hidden" name="datasetId" id ="datasetId" value ="${dataset.id}">
                            <td><button type ="submit"  class="btn btn-danger btn-sm">Delete</button></td>
                            </form>
                        </c:if>
                        </tr>
                    </c:forEach>
                </tbody>
                </table>
            </div>
            <div class="row mt-4">

                <div class="col-md-10">
                    <hr />
                    <h4>Upload new Dataset</h4>

                <form id="formUpload" action = "#" method="post" enctype="multipart/form-data">
                    <div class="form-group row">
                        <label for ="file" class="col-sm-2 col-form-label">Upload Dataset:</label>
                        <div class="col-sm-10">
                        <input type ="file" class="form-control-file" name="file" id ="file" required>
                        </div>
                    </div>
                    <div class="form-group row">
                        <label for ="endpoint" class="col-sm-2 col-form-label" >Endpoint:</label>
                        <div class="col-sm-3">
                        <input type ="text" class= "form-control" name ="endpoint" id ="endpoint" placeholder="http://dbpedia.org/sparql" required>
                        </div>
                    </div>
                    <div class="form-group row">
                        <label for ="defaultLanguage" class="col-sm-2 col-form-label">Default Language: </label>
                        <div class="col-sm-3">
                           <select class="form-control" name ="defaultLanguage" id ="defaultLanguage" required>
                            <option value="en">English</option>
                            <option value="de">Deutsch</option>
                            <option value="es">Espanol</option>
                            <option value="fr">Francais</option>
                           </select>

                        </div>
                    </div>
                        <button type ="submit" class ="btn btn-primary">Submit</button>
                    <c:if test="${error != null}">
                        <span class="alert alert-danger">${error}</span></c:if>
                    <c:if test="${success != null}">
                        <span class="alert alert-success">${success}</span></c:if>
                </form>
                </div>
            </div>
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
