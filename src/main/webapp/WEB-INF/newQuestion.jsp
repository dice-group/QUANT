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
            url: "${Dataset.endpoint}"
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
        var div = document.createElement('div');
        div.className ='form-group row mt-2';
        div.innerHTML ='<div class="col-1">\n' +
            '            <input type ="text" class="form-control" name ="trans_lang"  value=""required >\n' +
            '        </div>\n' +
            '        <div class="col-5">\n' +
            '            <input type ="text" class="form-control" name ="trans_question"  value="" required >\n' +
            '        </div>\n' +
            '\n' +
            '            <div class="col-6">\n' +
            '                <input type ="text" class="form-control" name ="trans_keywords"  value="" required >\n' +
            '            </div>'
        document.getElementById('trans_wrapper').appendChild(div);
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
<form method="POST" id="saveNewQuestion" action ="/newQuestion/${Dataset.id}" modelAttribute="formQuestion">
<input type ="hidden" id ="user" name="user" value="${User.id}">
    <div class="row ">
        <div class=" col-6">
            <span class="h4">Add new Question:</span>

            <button type="submit" class="btn btn-success btn-sm ml-2">Save Question</button>
            <a class="small text-muted" href="/manageDataset/${Dataset.id}">Back to Overview</a>
        </div>
        <div class="col">
            <c:if test="${error != null}">
                <span class="alert alert-danger">${error}</span></c:if>
            <c:if test="${success != null}">
                <span class="alert alert-success">${success}</span></c:if>
        </div>
    </div>

    <hr/>

    <div class="form-group row mt-2">
        <div class="col-2">
            <label for="answertype">Answertype:</label>
            <select class="form-control mb-2" id="answertype" name="answertype" required>
                <option value=""></option>
                <option value ="resource">resource</option>
                <option value ="number">number</option>
                <option value ="boolean">boolean</option>
                <option value ="date">date</option>

            </select>

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
                <textarea rows="11" class="form-control" id="sparql" name ="sparql" onchange=" sparqlQuery(document.getElementById('sparql').value)" required></textarea>
            </div>
        </div>
        <div class="col">
            <div class="col">
                <label for="file_answer">Answer from File:</label>
                <textarea rows="4" class="form-control mb-2" id="file_answer" name="file_answer" required></textarea>
            </div>

            <div class="col">
                <label for="endpoint_answer">Answer from Endpoint: ${Dataset.endpoint}</label>
                <textarea rows="4" class="form-control" id="endpoint_answer"></textarea>
            </div>



        </div>
    </div>

    <hr/>
    <input type ="button" class="btn-sm btn-outline-info" id ="button_add_row" onclick="addTranslationRow()" value ="Add Row">
    <div id ="trans_wrapper">
        <div class="form-group row mt-2" id="trans_1">

            <div class="col-1">
                Language:
            </div>
            <div class="col-5">
                Question:
            </div>
            <div class="col-6">
                Keywords: (comma separated)
            </div>
            <%-- Textfelder für Lang, Question, Keywords --%>

                <div class="col-1">
                    <input type ="text" class="form-control" name ="trans_lang" value="en" required />
                </div>
                <div class="col-5">
                    <input type ="text" class="form-control" name ="trans_question" required />
                </div>

                <div class="col-6">
                    <input type ="text" class="form-control" name ="trans_keywords" />
                </div>

        </div>
    </div>


</form>


<%@include file="footer.jsp" %>

