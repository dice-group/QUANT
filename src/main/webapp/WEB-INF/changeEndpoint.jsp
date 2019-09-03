<%@include file="templates/layout.jsp" %>
<div class="container-fluid">
    <div class="row">
        <div class="col-md-12  pt-2">
            <h3>Change Endpoint</h3>

            <form id="formUpload" action="#" method="post" enctype="multipart/form-data">

                <div class="form-group row">
                    <label for="endpoint" class="col-sm-2 col-form-label">Endpoint:</label>
                    <div class="col-sm-3">
                        <input type="text" class="form-control" name="endpoint" id="endpoint" value="${Dataset.endpoint}" required>
                    </div>
                </div>
                <div class="form-group row">
                    <div class="col-sm-7">
                        <span id="endpoint-error" class="alert alert-danger d-none col-sm-7">The endpoint you have entered is not reachable!</span>
                    </div>
                </div>

                <button type="submit" class="btn btn-primary">Submit</button>

            </form>

        </div>
    </div>
</div>

<script>
    jQuery('#formUpload').submit(function(event){
        var endpointUrl = $('#endpoint').val();
        var valid = validateEndpoint(endpointUrl);

        if(!valid){
            event.preventDefault();
            $('#endpoint-error').removeClass('d-none');
            $('#endpoint-error').addClass('d-block');
        }
    });

</script>

<%@include file="templates/footer.jsp" %>