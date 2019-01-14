<% request.setCharacterEncoding("utf-8"); %>
<%@include file="layout.jsp" %>
<script type="text/javascript" src="http://code.jquery.com/jquery-1.7.1.min.js"></script>
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

</script>

<p class="h4">Anotate Question: <strong>${Question.id}</strong> <button type="button" class="btn btn-success">Save changes</button>
</p>
<hr/>
<%--
<p class="h5">Details for: Question <strong>${Question.id}</strong>
    <button type="button" class="btn btn-success ml-2">Accept without changes</button>
    <button type="button" class="btn btn-primary">Save changes</button>
</p>
--%>
<form id="anotate1">
    <div class="form-group row mt-2">
        <div class="col-6"><strong><mark>Question: ${Question.translationsList[0].questionString} </mark></strong></div>
<%--
        <div class="col-6 form-inline">
            <label for="question1">Question:</label>
            <input type="text" readonly class="form-control" id="question1"
                   value="${Question.translationsList[0].questionString}">
        </div> --%>
    </div>

    <div class="form-group row mt-2">
        <%-- <label for ="sparql" class ="col-1">SPARQL:</label>--%>
        <div class="col-2">
           <%-- <label for="endpoint">Endpoint:</label>
            <input type="text" readonly class="form-control mb-2" id="endpoint" value="String Endpoint fehlt noch">
--%>
            <label for="answertype">Answertype:</label>
            <input type="text" class="form-control mb-2" id="answertype" value="${Question.answertype}">

            <div>
                Out of Scope:
                <label class="radio-inline"><input type="radio" class="mr-1" name="optscope" id="optscope_true" value ="true">True</label>
                <label class="radio-inline"><input type="radio" class="mr-1" name="optscope" id="optscope_false" value="false">False</label>
            </div>
            <script>setRadioButton('optscope', ${Question.outOfScope})</script>
            <div>
                Aggregation:
                <label class="radio-inline"><input type="radio" class="mr-1" name="optaggregation" id="optaggregation_true" value ="true">True</label>
                <label class="radio-inline"><input type="radio" class="mr-1" name="optaggregation" id="optaggregation_false" value="false">False</label>
            </div>
            <script>setRadioButton('optaggregation', ${Question.aggregation})</script>
            <div>
                Only DBPedia:
                <label class="radio-inline"><input type="radio" class="mr-1" name="optdbpedia" id="optdbpedia_true" value ="true">True</label>
                <label class="radio-inline"><input type="radio" class="mr-1" name="optdbpedia" id="optdbpedia_false" value="false">False</label>
            </div>
            <script>setRadioButton('optdbpedia', ${Question.onlydb})</script>
            <div>
                Hybrid:
                <label class="radio-inline "><input type="radio" class="mr-1" name="opthybrid" id="opthybrid_true" value ="true">True</label>
                <label class="radio-inline "><input type="radio" class="mr-1" name="opthybrid" id="opthybrid_false" value="false">False</label>
            </div>
            <script>setRadioButton('opthybrid', ${Question.hybrid})</script>


        </div>
        <div class="col">
            <div class="col">
                <label for="sparql">SPARQL:</label>
                <textarea rows="10" class="form-control" id="sparql" form="anotate1">${Question.sparqlQuery}</textarea>
            </div>
        </div>
        <div class="col">
            <div class="col">
                <label for="file_answer">Answer from File:</label>
                <textarea rows="4" class="form-control mb-2" id="file_answer" form="anotate1">dummy text</textarea>
            </div>
            <div class="col">
                <label for="endpoint_answer">Answer from Enpoint: <strong>Endpoint-URI - wie im Dataset gespeichert</strong></label>
                <textarea rows="4" class="form-control" id="endpoint_answer" form="anotate1">dummy text</textarea>
            </div>

        </div>
    </div>

    <h6> Multilingual Question and Keywords</h6>
        <hr/>

    <div class="form-group row mt-2">

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
            <input type ="text" class="form-control" id ="${'lang_' +=translation.id}" value="${translation.lang}">
        </div>
        <div class="col-5">
            <input type ="text" class="form-control" id ="${'questionString_' +=translation.id}" value="${translation.questionString}">
        </div>

            <div class="col-6">
                <input type ="text" class="form-control" id ="${'keyword_' +=translation.id}" value="${translation.keywords}">
            </div>


        </c:forEach>

    </div>




    </div>


</form>


<%@include file="footer.jsp" %>