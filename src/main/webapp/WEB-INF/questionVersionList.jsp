<% request.setCharacterEncoding("utf-8"); %>
<%@include file="layout.jsp" %>

<h4>Version history</h4>
<div class ="table-responsive">
    <table id="table_id" class="display" style="width:100%" > <!--class="table table-bordred table-striped-->
        <thead>
        <tr>
            <th>ID</th>
            <th>Question</th>
            <th>Version Nr.</th>
            <th>active?</th>


        </tr>
        </thead>
        <tbody>

        <c:forEach items="${Questions}" var="question">
            <tr class="dataset-row" dataset-id=${question.id}>
                <td>${question.questionSetId}</td>
                <c:choose>
                    <c:when test="${!empty question.translationsList[0].questionString}">
                        <td>${question.translationsList[0].questionString}</td>
                    </c:when>
                    <c:otherwise>
                        <td>empty</td>
                    </c:otherwise>
                </c:choose>

                <td>${question.version} </td>
                <c:choose>
                    <c:when test="${question.activeVersion}">
                        <td><a href="anotate/${question.questionSetId}"><i style="color: darkseagreen" class="fa fa-circle fa-lg"  aria-hidden="true"></i></a></td>
                    </c:when>
                    <c:otherwise>
                        <td><i style="color: lightgrey" class="fa fa-circle fa-lg"  aria-hidden="true"></i></td>
                    </c:otherwise>
                </c:choose>

            </tr>
        </c:forEach>
        </tbody>
    </table>
</div>
<%@include file="footer.jsp"%>

