<% request.setCharacterEncoding("utf-8"); %>
<%@include file="layout.jsp" %>

<h4>Questions from Dataset: ${DatasetName} </h4>
<div class ="table-responsive">
    <table id="table_id" class="display" style="width:100%" > <!--class="table table-bordred table-striped-->
        <thead>
        <tr>
            <th>ID</th>
            <th>Question</th>
            <th>Version history</th>
            <th>Anotate</th>
        </tr>
        </thead>
        <tbody>

        <c:forEach items="${Questions}" var="question">
            <tr class="dataset-row" dataset-id=${question.getId()}>
                <td>${question.getId()}</td>
                <c:choose>
                <c:when test="${!empty question.translationsList[0].questionString}">

                    <td>${question.translationsList[0].questionString}</td>

                </c:when>
                <c:otherwise>
                    <td>empty</td>
                </c:otherwise>
                </c:choose>

                <td align="center"><a href="/questionVersionList/${question.datasetQuestion.id}/${question.questionSetId}"><i class="fa fa-eye fa-lg" aria-hidden="true"></i></a> </td>
                <td align="center"><a href ="/anotate/${question.id}"><i class="fa fa-pencil-square-o fa-lg"></i></a></td>
            </tr>
        </c:forEach>
        </tbody>
    </table>
</div>
<%@include file="footer.jsp"%>

