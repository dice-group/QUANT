<%@include file="templates/layout.jsp" %>
<div class="container-fluid">
    <div class="row">
        <div class="col-md-12  pt-2">
            <div class="table-responsive">

                <form id="versionForm" action="/merge/${Set}/${Id}" method="POST">
                    <h4>Select Query and Metadata</h4>
                    <table id="merging_table" class="display" style="width:100%">
                        <!--  class="display" style="width:100%"-->
                        <thead>
                        <tr>
                            <c:forEach items="${Questions}" var="question">
                                <th><c:out
                                        value="Version: ${question.version} Annotator: ${question.anotatorUser.email}"></c:out></th>
                            </c:forEach>

                        </tr>
                        </thead>
                        <tbody>

                        <tr class="dataset-row" id="query">
                            <c:forEach items="${Questions}" var="question">
                                </p>
                                <c:choose>
                                    <c:when test="${question.activeVersion}">
                                        <td>
                                            <input type="radio" name="query" value="${question.id}" checked="checked">
                                            <c:out value="${question.sparqlQuery}"></c:out>
                                        </td>
                                    </c:when>
                                    <c:otherwise>
                                        <td>
                                            <input type="radio" name="query" value="${question.id}">
                                            <c:out value="${question.sparqlQuery}"></c:out>
                                        </td>
                                    </c:otherwise>
                                </c:choose>
                                </p>

                            </c:forEach>
                        </tr>
                        <tr class="dataset-row" id="metadata">
                            <c:forEach items="${Questions}" var="question">
                                <td>
                                    <c:choose>
                                        <c:when test="${question.activeVersion}">
                                            <input type="radio" name="metadata" value="${question.id}"
                                                   checked="checked">
                                            <c:out value="Answer type: ${question.answertype}"></c:out><br>
                                            <c:out value="Out of scope: ${question.outOfScope}"></c:out><br>
                                            <c:out value="Aggregation: ${question.aggregation}"></c:out><br>
                                            <c:out value="Only DBO: ${question.onlydb}"></c:out><br>
                                            <c:out value="Hybrid: ${question.hybrid}"></c:out>
                                        </c:when>
                                        <c:otherwise>
                                            <input type="radio" name="metadata" value="${question.id}">
                                            <c:out value="Answer type: ${question.answertype}"></c:out><br>
                                            <c:out value="Out of scope: ${question.outOfScope}"></c:out><br>
                                            <c:out value="Aggregation: ${question.aggregation}"></c:out><br>
                                            <c:out value="Only DBO: ${question.onlydb}"></c:out><br>
                                            <c:out value="Hybrid: ${question.hybrid}"></c:out>
                                        </c:otherwise>
                                    </c:choose>
                                </td>
                            </c:forEach>
                        </tr>

                        </tbody>
                    </table>
                    <br>
                    <h4>Select Translations</h4>
                    <table id="merging_translations" class="display" style="width:100%">
                        <!--  class="display" style="width:100%"-->
                        <thead>
                        <tr>
                            <th>Language</th>
                            <c:forEach items="${Questions}" var="question" varStatus="loop">
                                <th><c:out
                                        value="Version: ${question.version} Annotator: ${question.anotatorUser.email}"></c:out></th>
                                <c:if test="${question.activeVersion}">
                                    <c:set var="index" value="${loop.index}"/>
                                </c:if>
                            </c:forEach>

                        </tr>
                        </thead>
                        <tbody>
                        <c:forEach items="${MergingTranslationsMap}" var="entry">
                            <tr class="dataset-row" id="translation_${entry.key}">
                                <td>
                                    <c:out value="${entry.key}"></c:out>
                                </td>
                                <c:forEach items="${entry.value}" var="translation" varStatus="loop">

                                    <c:choose>
                                        <c:when test="${index eq loop.index}">
                                            <c:choose>
                                                <c:when test="${not empty translation}">
                                                    <td>
                                                        <input type="checkbox" class="${entry.key}" name="translation"
                                                               value="${Questions[loop.index].id}:${entry.key}" checked>
                                                        <c:out value="${translation}"></c:out>
                                                    </td>
                                                </c:when>
                                                <c:otherwise>
                                                    <td>
                                                        no translation
                                                    </td>
                                                </c:otherwise>
                                            </c:choose>
                                        </c:when>
                                        <c:otherwise>
                                            <c:choose>
                                                <c:when test="${not empty translation}">
                                                    <td>
                                                        <input type="checkbox" class="${entry.key}" name="translation"
                                                               value="${Questions[loop.index].id}:${entry.key}">
                                                        <c:out value="${translation}"></c:out>
                                                    </td>
                                                </c:when>
                                                <c:otherwise>
                                                    <td>
                                                        no translation
                                                    </td>
                                                </c:otherwise>
                                            </c:choose>
                                        </c:otherwise>
                                    </c:choose>

                                </c:forEach>
                            </tr>
                        </c:forEach>

                        </tbody>
                    </table>
                    <br>
                    <input type="checkbox" name="setActive" class="inline checkbox" value="true" checked>
                    Set Version active
                    <br>
                    <button class="btn btn-primary btn-sm ml-2" type="submit">Merge</button>
                </form>
                <script>
                    $(document).ready(function () {
                        $('#merging_table').DataTable({
                            "ordering": false,
                            "paging": false,
                            "bInfo": false,
                            "searching": false,
                            "select": true,
                            "items": 'cell'

                        });
                        $("input:checkbox").on('click', function () {
                            var $box = $(this);
                            if ($box.is(":checked")) {
                                // the name of the box is retrieved using the .attr() method
                                // as it is assumed and expected to be immutable
                                var group = "input:checkbox[class='" + $box.attr("class") + "']";
                                // the checked state of the group/box on the other hand will change
                                // and the current value is retrieved using .prop() method
                                $(group).prop("checked", false);
                                $box.prop("checked", true);
                            } else {
                                var checked = $("input[type=checkbox]:checked").length;
                                if (checked == 0) {
                                    $box.prop("checked", true);
                                } else {
                                    $box.prop("checked", false);
                                }
                            }
                        });
                        $('#merging_translations').DataTable({
                            "ordering": false,
                            "paging": false,
                            "bInfo": false,
                            "searching": false
                        });
                    });
                </script>
            </div>
        </div>
    </div>
</div>
<%@include file="templates/footer.jsp" %>