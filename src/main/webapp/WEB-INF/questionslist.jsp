<%@include file="layout.jsp" %>

<h4>Questions from Dataset: <c:out value="${DatasetName}"></c:out> </h4>
<div class ="table-responsive">
    <table id="table_id" class="display" style="width:100%" > <!--class="table table-bordered table-striped-->
        <thead>
        <tr>
            <th>ID</th>
            <th>Question</th>
            <th class="text-center">Version history</th>
            <th class="text-center">Anotate</th>
            <th class="text-center">Is Anotated</th>
        </tr>
        </thead>
        <tbody>

        <c:forEach items="${Questions}" var="question">
            <tr class="dataset-row" dataset-id=${question.id}>
                <td><c:out value="${question.id}"></c:out></td>
                <c:choose>
                <c:when test="${!empty question.translationsList[0].questionString}">

                    <td><c:out value="${question.translationsList[0].questionString}"></c:out></td>

                </c:when>
                <c:otherwise>
                    <td>empty</td>
                </c:otherwise>
                </c:choose>

                <td align="center"><a class="text-success" href="/questionVersionList/${question.datasetQuestion.id}/${question.questionSetId}"><i class="material-icons">history</i></a> </td>
                <td align="center"><a class="text-success" href ="/anotate/${question.id}"><i class="material-icons" >create</i></a></td>
                <c:if test="${question.anotatorUser.id == User.id}" var="anotatorUser"></c:if>
                <td align="center"><c:if test="${question.anotated && anotatorUser}"><i class="material-icons">check_circle</i></c:if></td>
            </tr>
        </c:forEach>
        </tbody>
    </table>
</div>
<%@include file="footer.jsp"%>

