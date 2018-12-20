<% request.setCharacterEncoding("utf-8"); %>
<%@include file="layout.jsp" %>

<h4>Anotate Question</h4>
<hr/>

<span style="font-size: larger">Questions Details: Question <strong>${Question.id}</strong><span/>
<button type="button" class="btn btn-success ml-2">Accept without changes</button>
<button type="button" class="btn btn-primary">Save changes</button>

<form>
    <div class = "form-group row mt-2">
        <label for="question1" class ="col-1">Question</label>
        <div class="col-5">
            <input type="text" readonly class="form-control" id="question1" value="${Question.translationsList[0].questionString}">
        </div>
    </div>

</form>

    <p>



${Question.answertype}

</p>


<%@include file="footer.jsp"%>