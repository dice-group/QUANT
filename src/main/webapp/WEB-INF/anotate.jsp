<% request.setCharacterEncoding("utf-8"); %>
<%@include file="layout.jsp" %>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form"%>

<script>
    function setRadioButton(buttonname, value){
        if (value) {

            $("input[name='"+buttonname+"'][value='"+value+"']").prop('checked', true);
            return true;

        }
        else {
            $("input[name='"+buttonname+"'][value='false']").prop('checked', true);
            return true;
        }
    }


    function sparqlQuery(sparqlQuery){
        console.log(sparqlQuery)
        $.ajax({
            data: {query: sparqlQuery},
            dataType: "json",
            //url: "http://dbpedia.org/sparql",
            url: "${Question.datasetQuestion.endpoint}"
        })
            .done(function (data) {
                writeAnswers(data)
            })

            .fail(function(xhr) {
                console.log('error', xhr);
            })
    }

    function writeAnswers(result) {
        console.log(result)
        var resultList = [];
        var varsList=[];
        if (result['boolean'] !=null) {
            resultList.push(result['boolean'])
        }
        else {
            $.each(result['head']['vars'], function(index, vars){
                varsList.push(vars)
            });
            $.each( result['results']['bindings'], function (index, value){
                for (i in varsList) {
                    resultList.push(value[varsList[i]]['value'])
                }
            });
        }
        $("#endpoint_answer").val(resultList.join('\n'));

    }

    function addTranslationRow(){
        var row ='<div class="col-1">\n' +
            '            <input type ="text" class="form-control" name ="trans_lang"  value="">\n' +
            '        </div>\n' +
            '        <div class="col-5">\n' +
            '            <input type ="text" class="form-control" name ="trans_question"  value="">\n' +
            '        </div>\n' +
            '\n' +
            '            <div class="col-6">\n' +
            '                <input type ="text" class="form-control" name ="trans_keywords"  value="">\n' +
            '            </div>'
        document.getElementById('trans_wrapper').innerHTML += row;
    }

</script>

<script>
    $(document).ready( function () {
        sparqlQuery("${Question.sparqlQuery}");
        setRadioButton('optscope', ${Question.outOfScope});
        setRadioButton('optaggregation', ${Question.aggregation});
        setRadioButton('optdbpedia', ${Question.onlydb});
        setRadioButton('opthybrid', ${Question.hybrid});
    } );
</script>
<form method="POST" id="anotate1" action ="/anotate/${nextQuestion}" modelAttribute="formQuestion">

<div class="row ">
    <div class=" col">
        <span class="h4">Anotate Question:</span>
        <mark>ID: ${Question.id} - ${Question.translationsList[0].questionString}</mark>
    </div>

    <div class=" col">
            <button type="submit" class="btn btn-success btn-sm ml-2">Save changes</button>
        <a class="small text-muted" href="/questionslist/${Question.datasetQuestion.id}">Back to Overview</a> |
        <a class="small text-muted" href="/anotate/${nextQuestion}">Next Question </a>
    </div>
</div>

<hr/>

    <div class="form-group row mt-2">
        <div class="col-2">
            <label for="answertype">Answertype:</label>
            <input type="text" class="form-control mb-2" id="answertype" name="answertype" value="${Question.answertype}" />

            <div>
                Out of Scope:
                <label  class="radio-inline"><input type ="radio" class="mr-1" name="optscope" id="optscope_true" value ="true">True</label>
                <label  class="radio-inline"><input type ="radio" class="mr-1" name="optscope" id="optscope_false" value="false">False</label>
            </div>

            <div>
                Aggregation:
                <label class="radio-inline"><input type ="radio" class="mr-1" name="optaggregation" id="optaggregation_true" value ="true">True</label>
                <label class="radio-inline"><input type ="radio" class="mr-1" name="optaggregation" id="optaggregation_false" value="false">False</label>
            </div>

            <div>
                Only DBPedia:
                <label class="radio-inline"><input type ="radio" class="mr-1" name="optdbpedia" id="optdbpedia_true" value ="true">True</label>
                <label  class="radio-inline"><input type ="radio" class="mr-1" name="optdbpedia" id="optdbpedia_false" value="false" >False</label>
            </div>

            <div>
                Hybrid:
                <label  class="radio-inline "><input type ="radio" class="mr-1" name="opthybrid" id="opthybrid_true" value ="true">True</label>
                <label class="radio-inline "><input type ="radio" class="mr-1" name="opthybrid" id="opthybrid_false" value="false">False</label>
            </div>
            <script></script>


        </div>
        <div class="col">
            <div class="col">
                <label for="sparql">SPARQL:</label>
                <textarea rows="11" class="form-control" id="sparql" name ="sparql" form="anotate1">${Question.sparqlQuery}</textarea>
            </div>
        </div>
        <div class="col">
            <div class="col">
                <label for="file_answer">Answer from File:</label>
                <textarea rows="4" class="form-control mb-2" id="file_answer" name="file_answer" form="anotate1">${GoldenAnswer}</textarea>
            </div>

            <div class="col">
                <label for="endpoint_answer">Answer from Endpoint: ${Question.datasetQuestion.endpoint}</label>
                <textarea rows="4" class="form-control" id="endpoint_answer" form="anotate1"></textarea>
            </div>



        </div>
    </div>

        <hr/>
    <input type ="button" class="btn-sm btn-outline-info" id ="button_add_row" onclick="addTranslationRow()" value ="Add Row">
    <div class="form-group row mt-2" id="trans_wrapper">

        <div class="col-1">
            Language:
        </div>
        <div class="col-5">
            Question:
        </div>
        <div class="col-6">
            Keywords: (List [], comma separated)
        </div>
        <%-- Textfelder für Lang, Question, Keywords --%>

        <c:forEach items="${Question.translationsList}" var="translation">
        <div class="col-1">
            <input type ="text" class="form-control" name ="trans_lang" id ="${'lang_' +=translation.id}" value="${translation.lang}" />
        </div>
        <div class="col-5">
            <input type ="text" class="form-control" name ="trans_question" id ="${'questionString_' +=translation.id}" value="${translation.questionString}" />
        </div>

            <div class="col-6">
                <input type ="text" class="form-control" name ="trans_keywords" id ="${'keyword_' +=translation.id}" value="${translation.keywords}" />
            </div>
        </c:forEach>

    </div>

</form>


<%@include file="footer.jsp" %>

