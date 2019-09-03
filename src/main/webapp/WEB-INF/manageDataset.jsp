<%@include file="templates/layout.jsp" %>

<script>
    function deleteQuestion(form) {
        if (window.confirm("Are you sure, you want to delete this question?")) {
            form.submit();
        } else {
            return false;
        }
    }

</script>
<div class="container-fluid">
    <div class="row">
        <div class="col-md-12  pt-2">
            <p class="h4">Manage Dataset: ${DatasetName}
                <a href="/newQuestion/${Dataset.id}">
                    <button class="ml-2 btn btn-success btn-sm"> Add new Question</button>
                </a>
                <a href ="/newBulkQuestions/${Dataset.id}">
                    <button clasS="ml-2 btn btn-success btn-sm">Add empty Questions</button>
                </a>
                <a href ="/changeEndpoint/${Dataset.id}">
                    <button clasS="ml-2 btn btn-success btn-sm">Change Endpoint</button>
                </a>
            </p>
            <div class="table-responsive">
                <form id="manageDatasetForm" action="/manageDataset/${Dataset.id}" method="POST"
                      onSubmit="return confirm('Are you sure you wish to delete?')">

                    <table id="table_id" class="display" style="width:100%">
                        <thead>
                        <tr>
                            <th>ID</th>
                            <th>Question</th>
                            <th class="text-center">Active version</th>
                            <th class="text-center">Anotated</th>
                            <th class="text-center">Delete</th>
                        </tr>
                        </thead>
                        <tbody>

                        <c:forEach items="${Questions}" var="question">
                            <tr class="dataset-row" dataset-id=${question.id}>
                                <td><c:out value="${question.id}"></c:out></td>
                                <td><c:out value="${question.getDefaultTranslation()}"></c:out></td>

                                <td align="center"><c:if test="${question.activeVersion}"><i class="material-icons">check_circle</i></c:if>
                                </td>
                                <td align="center"><c:if test="${question.anotated}"><i class="material-icons">check_circle</i></c:if>
                                </td>
                                <td align="center">
                                    <button type="submit" class="btn btn-danger btn-sm" id="deleteId" name="deleteId"
                                            value="${question.id}">Delete
                                    </button>
                                </td>

                            </tr>
                        </c:forEach>
                        </tbody>
                    </table>
                </form>
            </div>
        </div>
    </div>
</div>


<%@include file="templates/footer.jsp" %>

