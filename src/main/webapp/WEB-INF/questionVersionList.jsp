<% request.setCharacterEncoding("utf-8"); %>
<%@include file="layout.jsp" %>

<h4>Version history from Dataset: ${DatasetName} </h4>
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
            <tr class="dataset-row" dataset-id=${question.getId()}>
                <td>${question.getQuestionSetId()}</td>
                <c:choose>
                    <c:when test="${!empty question.getTranslationsList()[0].getQuestionString()}">
                        <td>${question.getTranslationsList()[0].getQuestionString()}</td>
                    </c:when>
                    <c:otherwise>
                        <td>empty</td>
                    </c:otherwise>
                </c:choose>

                <td>${question.getVersion()} </td>
                <c:choose>
                    <c:when test="${question.isActiveVersion()}">
                        <td><i style="color: darkseagreen" class="fa fa-circle fa-lg"  aria-hidden="true"></i></td>
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

