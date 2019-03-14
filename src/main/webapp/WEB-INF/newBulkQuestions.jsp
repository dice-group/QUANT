<%@include file="templates/layout.jsp" %>


<script>


    function addTranslationRow() {
        var div = document.createElement('div');
        div.className = 'form-group row mt-2';
        div.innerHTML = '<div class="col-6">\n' +
            '            <input type ="text" class="form-control" name ="trans_question"  value="" placeholder="Your Question" >\n' +
            '        </div>'
        document.getElementById('trans_wrapper').appendChild(div);
    }

</script>

<div class="container-fluid">
    <div class="row">
        <div class="col-md-12  pt-2">
            <form method="POST" id="saveNewQuestion" action="/newBulkQuestions/${Dataset.id}"
                  modelAttribute="formQuestion">
                <input type="hidden" id="user" name="user" value="${User.id}">
                <div class="row ">
                    <div class=" col-6">
                        <span class="h4">Add bulk of Questions</span>

                        <button type="submit" class="btn btn-success btn-sm ml-2">Save Questions</button>
                        <a class="small text-muted" href="/manageDataset/${Dataset.id}">Back to Overview</a>
                    </div>

                </div>
                <hr/>
                <input type="button" class="btn-sm btn-outline-info" id="button_add_row"
                       onclick="addTranslationRow()"
                       value="Add Row">
                <div id="trans_wrapper">
                    <div class="form-group row mt-2">

                        <div class="col-6">
                            Questions in <strong>default Language</strong>:
                        </div>
                    </div>

                    <div class="form-group row mt-2">
                        <div class="col-6">
                            <input type="text" class="form-control" name="trans_question" placeholder="Your Question"
                                   required/>
                        </div>

                    </div>
                </div>


        </form>
    </div>
</div>
</div>

<%@include file="templates/footer.jsp" %>

