<%@include file="templates/layout.jsp" %>
<div class="container-fluid">
    <div class="row">
        <div class="col-md-12 pt-2">
            <h4>Questions from Dataset: <c:out value="${DatasetName}"></c:out></h4>
            <div class="table-responsive">
                <table id="table_id" class="display" style="width:100%">
                    <!--class="table table-bordered table-striped-->
                    <thead>
                    <tr>
                        <th>ID</th>
                        <th>Question</th>
                        <c:if test="${User.role =='ADMIN'}">
                            <th class="text-center">Version history</th>
                        </c:if>
                        <th class="text-center">Anotate</th>
                        <th class="text-center">Is Anotated</th>
                    </tr>
                    </thead>
                    <tbody>

                    <c:forEach items="${Questions}" var="question">
                        <tr class="dataset-row" dataset-id=${question.id}>
                            <td><c:out value="${question.questionSetId}"></c:out></td>
                            <td><c:out value="${question.getDefaultTranslation()}"></c:out></td>
                            <c:if test="${User.role =='ADMIN'}">
                                <td align="center"><a class="text-success"
                                                      href="/questionVersionList/${question.datasetQuestion.id}/${question.questionSetId}"><i
                                        class="material-icons">history</i></a></td>
                            </c:if>
                            <td align="center"><a class="text-success" href="/anotate/${question.id}"><i
                                    class="material-icons">create</i></a></td>
                            <c:if test="${question.anotatorUser.id == User.id}" var="anotatorUser"></c:if>
                            <td align="center"><c:if test="${question.anotated}"><i
                                    class="material-icons">check_circle</i></c:if></td>
                        </tr>
                    </c:forEach>
                    </tbody>
                </table>
            </div>
        </div>
    </div>
</div>
<%@include file="templates/footer.jsp" %>

