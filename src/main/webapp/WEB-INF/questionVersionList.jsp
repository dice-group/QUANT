<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>
<% request.setCharacterEncoding("utf-8"); %>
<%@include file="layout.jsp" %>

<h4>Version history</h4>
<div class ="table-responsive">

    <form id ="versionForm"  action="/questionVersionList/${Set}/${Id}" method="POST">
    <table id="table_id" class="display" style="width:100%"> <!--  class="display" style="width:100%"-->
        <thead>
        <tr>
            <th>ID</th>
            <th>Question</th>
            <th class="text-center">Version Nr.</th>
            <th class="text-center">Active</th>


        </tr>
        </thead>
        <tbody>

        <c:forEach items="${Questions}" var="question">
            <tr class="dataset-row" id="${question.id}">
                <td>${question.id}</td>
                <c:choose>
                    <c:when test="${!empty question.translationsList[0].questionString}">

                        <td><c:out value="${question.translationsList[0].questionString}"></c:out></td>
                    </c:when>
                    <c:otherwise>
                        <td>empty</td>
                    </c:otherwise>
                </c:choose>

                <td><c:out value="${question.version}"></c:out></td>
                <c:choose>
                    <c:when test="${question.activeVersion}">
                        <c:set var = "wasActive" value = "${question.id}"/>

                        <td class="form-check text-center"><input type="radio" class="form-check-input" id="version_${question.id}" name="versionControl" onchange="changeActiveVersion(${question.id})" checked></td>
                    </c:when>
                    <c:otherwise>
                        <td class="form-check text-center"><input type="radio" class="form-check-input" id="version_${question.id}" name="versionControl" onchange="changeActiveVersion(${question.id})"></td>
                    </c:otherwise>
                </c:choose>

            </tr>

        </c:forEach>
        </tbody>
    </table>
        <input type = "hidden" id ="nowActive" name="nowActive" value="">
        <input type="hidden" id="wasActive" name="wasActive" value="${wasActive}">
    </form>
</div>
<script>

function changeActiveVersion(nowActive) {
    $("#nowActive").val(nowActive);
    var  wasActive = Number(document.getElementById("wasActive").value);
    console.log(wasActive);
    console.log(nowActive)
    document.getElementById("versionForm").submit();
}

</script>
<%@include file="footer.jsp"%>

