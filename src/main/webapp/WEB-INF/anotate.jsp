<%@include file="templates/layout.jsp" %>

<script>
    function setRadioButton(buttonname, value) {
        if (value) {

            $("input[name='" + buttonname + "'][value='" + value + "']").prop('checked', true);
            return true;

        } else {
            $("input[name='" + buttonname + "'][value='false']").prop('checked', true);
            return true;
        }
    }


    function setSelectAnswertype(answertype) {
        document.getElementById('answertype').value = answertype;
        console.log(answertype)

    }

    function sparqlQuery(sparqlQuery) {
        console.log(sparqlQuery)
        $.ajax({
            data: {query: sparqlQuery},
            dataType: "json",
            url: "${Question.datasetQuestion.endpoint}"
        })
            .done(function (data) {
                writeAnswers(data)
            })

            .fail(function (xhr) {
                console.log('error', xhr);
            })
    }

    function writeAnswers(result) {
        console.log("Result:")
        console.log(result)
        var resultList = [];
        var varsList = [];
        if (result['boolean'] != null) {
            resultList.push(result['boolean'])
        } else {
            $.each(result['head']['vars'], function (index, vars) {
                varsList.push(vars)
            });
            $.each(result['results']['bindings'], function (index, value) {
                for (i in varsList) {
                    resultList.push(value[varsList[i]]['value'])
                }
            });
        }
        $("#endpoint_answer").val(resultList.join('\n'));


    }

    function addTranslationRow() {
        var div = document.createElement('div');

        div.className = 'form-group row mt-2';
        div.innerHTML = '<div class="col-1">\n' +
            '            <input type ="text" class="form-control" name ="trans_lang" value="" >\n' +
            '        </div>\n' +
            '        <div class="col-5">\n' +
            '            <input type ="text" class="form-control" name ="trans_question"   value="" >\n' +
            '        </div>\n' +
            '\n' +
            '            <div class="col-6">\n' +
            '                <input type ="text" class="form-control" name ="trans_keywords"  value="" >\n' +
            '            </div>'
        document.getElementById('trans_wrapper').appendChild(div);

    }

    function setSparqlSuggestion() {
        var newSuggestion = document.getElementById('suggestedSparql').innerText;
        document.getElementById('sparql').value = newSuggestion;
        sparqlQuery(newSuggestion)

    }
</script>

<script>
    $(document).ready(function () {
        sparqlQuery("${Question.sparqlQuery}");
        setRadioButton('optscope', ${Question.outOfScope});
        setRadioButton('optaggregation', ${Question.aggregation});
        setRadioButton('optdbpedia', ${Question.onlydb});
        setRadioButton('opthybrid', ${Question.hybrid});
        setSelectAnswertype("${Question.answertype}");


    });
</script>
<div class="container-fluid">
    <div class="row">
        <div class="col-md-12  pt-2">
            <form method="POST" id="anotate1" action="/anotate/${Question.id}" modelAttribute="formQuestion">

                <div class="row ">
                    <div class=" col">
                        <span class="h4">Anotate Question:</span>

                        <mark>ID: <c:out
                                value="  ${Question.questionSetId} - ${Question.getDefaultTranslation()}"></c:out></mark>

                    </div>
                    <c:if test="${error != null}" var="Message">
                        <span class="alert alert-danger"><c:out value="${error}"></c:out></span></c:if>
                    <c:if test="${success != null}" var="Message">
                        <span class="alert alert-success"><c:out value="${success}"></c:out></span></c:if>

                    <div class=" col">
                        <button type="submit" class="btn btn-success btn-sm ml-2">Save changes</button>
                        <a class="small text-muted" href="/questionslist/${Question.datasetQuestion.id}">Back to
                            Overview</a> |

                    </div>
                </div>

                <hr/>

                <div class="form-group row mt-2">
                    <div class="col-6 col-lg-3">
                        <div class="row mb-2">

                            <div class="col-5">
                                <label for="answertype">Answertype: <span class="alert-success"><c:out
                                        value="${MetadataSuggestion.answerType}"></c:out></span> </label>
                            </div>
                            <div class="col-7">
                                <select class="form-control mb-2" id="answertype" name="answertype" required>

                                    <option value="resource">resource</option>
                                    <option value="number">number</option>
                                    <option value="boolean">boolean</option>
                                    <option value="date">date</option>
                                    <option value="string">String</option>

                                </select>
                            </div>
                        </div>


                        <div class="row">
                            <div class="col-5">Out of Scope:</div>
                            <div class="col-7">
                                <label class="radio-inline">
                                    <input type="radio" class="mr-1" name="optscope" id="optscope_true" value="true">True</label>
                                <label class="radio-inline">
                                    <input type="radio" class="mr-1" name="optscope" id="optscope_false" value="false">False</label>
                                <span class="alert-success"><c:out
                                        value="${MetadataSuggestion.outOfScope}"></c:out> </span>
                            </div>

                        </div>

                        <div class="row">
                            <div class="col-5">Aggregation:</div>
                            <div class="col-7"><label class="radio-inline"><input type="radio" class="mr-1"
                                                                                  name="optaggregation"
                                                                                  id="optaggregation_true" value="true">True</label>
                                <label class="radio-inline"><input type="radio" class="mr-1" name="optaggregation"
                                                                   id="optaggregation_false" value="false">False</label>
                                <span class="alert-success"><c:out
                                        value="${MetadataSuggestion.aggregation}"></c:out> </span>
                            </div>
                        </div>

                        <div class="row">
                            <div class="col-5">Only DBPedia:</div>
                            <div class="col-7"><label class="radio-inline"><input type="radio" class="mr-1"
                                                                                  name="optdbpedia"
                                                                                  id="optdbpedia_true" value="true">True</label>
                                <label class="radio-inline"><input type="radio" class="mr-1" name="optdbpedia"
                                                                   id="optdbpedia_false"
                                                                   value="false">False</label>
                                <span class="alert-success"><c:out
                                        value="${MetadataSuggestion.onlyDbo}"></c:out> </span>
                            </div>
                        </div>

                        <div class="row">
                            <div class="col-5">Hybrid:</div>
                            <div class="col-7"><label class="radio-inline "><input type="radio" class="mr-1"
                                                                                   name="opthybrid"
                                                                                   id="opthybrid_true" value="true">True</label>
                                <label class="radio-inline "><input type="radio" class="mr-1" name="opthybrid"
                                                                    id="opthybrid_false"
                                                                    value="false">False</label>
                                <span class="alert-success"><c:out value="${MetadataSuggestion.hybrid}"></c:out> </span>
                            </div>
                        </div>

                    </div>

                    <div class="col-lg-5">
                        <div class="col">
                            <label for="sparql">SPARQL:</label>
                            <textarea rows="6" class="form-control" id="sparql" name="sparql" required
                                      onchange=" sparqlQuery(document.getElementById('sparql').value)"><c:out
                                    value="${Question.sparqlQuery}"></c:out></textarea>

                            <c:if test="${!empty Suggestion.correctedQuery}">
                                <a href="#" onclick="setSparqlSuggestion()" class="text-success">Load
                                    Suggestion: </a><br/>
                                <span class="text-secondary" id="suggestedSparql"><c:out
                                        value="${Suggestion.correctedQuery}"> </c:out></span>
                            </c:if>
                        </div>
                    </div>
                    <div class="col">
                        <div class="col">
                            <label for="file_answer">Answer from File:</label>
                            <textarea rows="4" class="form-control mb-2" id="file_answer" name="file_answer"
                                      required><c:out
                                    value="${GoldenAnswer}"></c:out></textarea>
                        </div>

                        <div class="col">
                            <label for="endpoint_answer">Answer from Endpoint: <c:out
                                    value="${Question.datasetQuestion.endpoint}"></c:out></label>
                            <textarea rows="4" class="form-control" id="endpoint_answer">${EndpointAnswer}</textarea>
                        </div>


                    </div>
                </div>

                <hr/>
                <input type="button" class="btn-sm btn-outline-info" id="button_add_row" onclick="addTranslationRow()"
                       value="Add Row">

                <a href="https://translate.google.de/#view=home&op=translate&sl=auto&tl=de&text=${Question.getDefaultTranslation()}"
                   target="_blank" rel="noopener noreferrer">Translate with google!</a>
                <div id="trans_wrapper">
                    <div class="form-group row mt-2" id="trans_wrapper1">

                        <div class="col-1">
                            Language:
                        </div>
                        <div class="col-5">
                            Question:
                        </div>
                        <div class="col-6">
                            Keywords: (comma separated)
                        </div>
                        <c:forEach items="${Language}" var="entry">
                            <c:set var="key">${entry}</c:set>

                            <div class="col-1">
                                <input type="text" class="form-control" name="trans_lang" id="${'lang_' +=entry}"
                                       value="${entry}"/>
                            </div>
                            <div class="col-5">
                                <input type="text" class="form-control" name="trans_question"
                                       id="${'questionString_' +=entry}" value="${TranslationMap[entry]}"/>
                            </div>

                            <div class="col-6">
                                <c:choose>
                                    <c:when test="${KeywordMap[entry]==''}"><c:set var="keywords"
                                                                                   value="suggestion, ${KeywordSuggestion.get(entry)}"></c:set>
                                    </c:when>
                                    <c:otherwise>
                                        <c:set var="keywords" value="${KeywordMap[entry]} "></c:set>
                                    </c:otherwise>
                                </c:choose>
                                <input type="text" class="form-control" name="trans_keywords" id="${'keyword_' +=entry}"
                                       value="${keywords}"/>
                            </div>

                        </c:forEach>

                    </div>
                </div>

            </form>
        </div>
    </div>
</div>
<%@include file="templates/footer.jsp" %>

