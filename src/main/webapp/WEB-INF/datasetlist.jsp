<%@include file="templates/layout.jsp" %>

<div class="container-fluid">
    <div class="row">
        <div class="col-md-12 pt-2">

            <h4>Dataset List</h4>

            <div class="table-responsive">
                <table id="table_id" class="display" style="width:100%"> <!--class="table table-bordred table-striped-->
                    <thead>
                    <tr>
                        <th>ID</th>
                        <th>Dataset Name</th>
                        <th>Anotate</th>
                        <th>Download</th>
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
                            <td><a href="/questionslist/${dataset.id}">
                                <button class="btn btn-outline-info btn-sm">Anotate</button>
                            </a></td>
                            <td><a href="/download/${dataset.id}">
                                <button class="btn btn-success btn-sm">Download</button>
                            </a></td>
                            <c:if test="${User.role =='ADMIN'}">
                                <td><a href="/manageDataset/${dataset.id}">
                                    <button class="btn btn-outline-info btn-sm">Manage</button>
                                </a></td>
                                <form id="deleteDataset" action="/deleteDataset" method="post"
                                      onSubmit="return confirm('Are you sure you wish to delete?')">
                                    <input type="hidden" name="datasetId" id="datasetId" value="${dataset.id}">
                                    <td>
                                        <button type="submit" class="btn btn-danger btn-sm">Delete</button>
                                    </td>
                                </form>
                            </c:if>
                        </tr>
                    </c:forEach>
                    </tbody>
                </table>
            </div>
            <div class="row mt-4">

                <div class="col-md-10">

                    <a href="/newDataset">
                        <button class="btn btn-success">New Dataset</button>
                    </a>

                </div>
            </div>
        </div>
    </div>
</div>

<%@include file="templates/footer.jsp" %>
