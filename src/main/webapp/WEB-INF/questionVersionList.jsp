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
            <th>Anotator</th>
            <th class="text-center">Version Nr.</th>
            <th class="text-center">Active</th>


        </tr>
        </thead>
        <tbody>

        <c:forEach items="${Questions}" var="question">
            <tr class="dataset-row" id="${question.id}">
                <td>${question.id}</td>

                        <td><c:out value="${question.getDefaultTranslation()}"></c:out></td>

                <td><c:out value=" ${question.anotatorUser.email}"></c:out></td>
                <td class="text-center"><c:out value="${question.version}"></c:out></td>

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
    <c:if test="${(User.role =='ADMIN') and Questions.size()>1}">
        <a href="/merge/${Set}/${Id}"><button class="btn btn-outline-info btn-sm">Merge</button></a>
    </c:if>
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

