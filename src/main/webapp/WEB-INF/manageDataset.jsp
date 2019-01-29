<%@include file="layout.jsp" %>

<p class="h4">Manage Dataset: ${DatasetName}
    <a href="/newQuestion/${Dataset.id}"><button class="ml-2 btn btn-success btn-sm"> Add new Question</button></a>

    <c:if test="${error != null}">
        <span class="alert alert-danger">${error}</span></c:if>
    <c:if test="${success != null}">
        <span class="alert alert-success">${success}</span></c:if>
</p>
<div class ="table-responsive">
    <form id ="manageDatasetForm" action="/manageDataset/${Dataset.id}" method="POST">

    <table id="table_id" class="display" style="width:100%" >
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
                <td>${question.id}</td>
                <c:choose>
                    <c:when test="${!empty question.translationsList[0].questionString}">
                        <td>${question.translationsList[0].questionString}</td>
                    </c:when>
                    <c:otherwise>
                        <td>empty</td>
                    </c:otherwise>
                </c:choose>

                <td align="center"><c:if test="${question.activeVersion}"><span class="oi oi-check" title="check" aria-hidden="true"></span></c:if></td>
                <td align="center"><c:if test="${question.anotated}"><span class="oi oi-check" title="check" aria-hidden="true"></span></c:if></td>
                <td align="center"><button type="submit" class="btn btn-danger btn-sm" id="deleteId" name="deleteId" value="${question.id}">Delete</button></td>

            </tr>
        </c:forEach>
        </tbody>
    </table>
    </form>
</div>




<%@include file="footer.jsp"%>
