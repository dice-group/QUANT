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
            url: "${Question.datasetQuestion.endpoint}",
            crossDomain: true,
            timeoutSeconds:5
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
            '            <input type ="text" class="form-control" name ="trans_lang" value="" required>\n' +
            '        </div>\n' +
            '        <div class="col-5">\n' +
            '            <input type ="text" class="form-control" name ="trans_question"   value="" required>\n' +
            '        </div>\n' +
            '\n' +
            '            <div class="col-6">\n' +
            '                <input type ="text" class="form-control" name ="trans_keywords"  value="" required>\n' +
            '            </div>'
        document.getElementById('trans_wrapper').appendChild(div);

    }

    function setSparqlSuggestion() {
        var newSuggestion = document.getElementById('suggestedSparql').innerText;
        document.getElementById('sparql').value = newSuggestion;
        $('#sugg_sparql').val($('#sparql').val()); // Doing it to avoid spacial characters, so that comparison in backend is easy
        sparqlQuery(newSuggestion);

        // logging
        document.getElementById('sparql_loaded').value = "true";

    }
</script>

<script>
    $(document).ready(function () {
        start();
        sparqlQuery(document.getElementById('sparql').value);
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
            <form method="POST" id="anotate1" action="/anotate/${Question.id}" onsubmit="end()" modelAttribute="formQuestion">
            <input type ="hidden" id="js_duration" name="js_duration" value="">
            <input type ="hidden" id="beginn" name="beginn" value="${beginn}">

            <!-- logging attributes start -->
            <input type ="hidden" id="sugg_answertype" name="sugg_answertype" value="${MetadataSuggestion.answerType}">
            <input type ="hidden" id="sugg_optscope" name="sugg_optscope" value="${MetadataSuggestion.outOfScope}">
            <input type ="hidden" id="sugg_optaggregation" name="sugg_optaggregation" value="${MetadataSuggestion.aggregation}">
            <input type ="hidden" id="sugg_optdbpedia" name="sugg_optdbpedia" value="${MetadataSuggestion.onlyDbo}">
            <input type ="hidden" id="sugg_opthybrid" name="sugg_opthybrid" value="${MetadataSuggestion.hybrid}">
            <c:set var="suggested_sparql" value="${Suggestion.correctedQuery == null ? '': Suggestion.correctedQuery}"/>
            <input type ="hidden" id="sugg_sparql" name="sugg_sparql" value="${suggested_sparql}">
            <input type ="hidden" id="sparql_loaded" name="sparql_loaded" value="false">
            <!-- logging attributes end -->

                <div class="row ">
                    <div class=" col">
                        <span class="h4">Anotate Question:</span>

                        <mark>ID: <c:out
                                value="  ${Question.questionSetId} - ${Question.getDefaultTranslation()}"></c:out></mark>

                    </div>

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
                                    <option value="string">string</option>
                                    <option value="number">number</option>
                                    <option value="boolean">boolean</option>
                                    <option value="date">date</option>


                                </select>
                            </div>
                        </div>


                        <div class="row">
                            <div class="col-5">Out of Scope:</div>
                            <div class="col-7">
                                <label class="radio-inline">
                                    <input type="radio" class="mr-1" name="optscope" id="optscope_true" value="true" required>True</label>
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
                                                                                  id="optaggregation_true" value="true" required>True</label>
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
                                                                                  id="optdbpedia_true" value="true" required>True</label>
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
                                                                                   id="opthybrid_true" value="true" required>True</label>
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
                                    value="${Question.sparqlQuery}" escapeXml="true"></c:out></textarea>

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
                            <label for="file_answer">Expected answer:</label>
                            <textarea rows="4" class="form-control mb-2" id="file_answer" name="file_answer"
                                      required><c:out
                                    value="${GoldenAnswer}"></c:out></textarea>
                        </div>

                        <div class="col">
                            <c:choose>
                                <c:when test="${Suggestion.endpointReachable==false}">
                                    <label for="endpoint_answer" class="alert-warning">Endpoint not reachable</label>
                                </c:when>
                                <c:otherwise>
                                    <label for="endpoint_answer">Answer from Endpoint: <c:out
                                            value="${Question.datasetQuestion.endpoint}"></c:out></label>
                                </c:otherwise>
                            </c:choose>
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
                            <input type="hidden" name="sugg_trans_keywords" value="${KeywordSuggestion.get(entry)}"required>

                            <div class="col-1">
                                <input type="text" class="form-control" name="trans_lang" id="${'lang_' +=entry}"
                                       value="${entry}" required/>
                            </div>
                            <div class="col-5">
                                <input type="text" class="form-control" name="trans_question"
                                       id="${'questionString_' +=entry}" value="<c:out value='${TranslationMap[entry]}'></c:out>" required/>
                            </div>


                            <div class="col-6">
                                <c:choose>
                                    <c:when test="${KeywordMap[entry]==''}"><c:set var="keywords"
                                                                                   value=" ${KeywordSuggestion.get(entry)}"></c:set>
                                    </c:when>
                                    <c:otherwise>
                                        <c:set var="keywords" value="${KeywordMap[entry]} "></c:set>
                                    </c:otherwise>
                                </c:choose>
                                <input type="text" class="form-control" name="trans_keywords" id="${'keyword_' +=entry} "
                                       value="${keywords}"/>
                            </div>

                        </c:forEach>
                        <input type ="hidden" name="trans_lang" value="">
                        <input type ="hidden" name="trans_question" value="">
                        <input type ="hidden" name="trans_keywords" value="">

                    </div>
                </div>

            </form>
        </div>
    </div>
</div>
<%@include file="templates/footer.jsp" %>

