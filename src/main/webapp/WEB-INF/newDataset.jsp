<%@include file="templates/layout.jsp" %>
<div class="container-fluid">
    <div class="row">
        <div class="col-md-12  pt-2">
            <h3>Add new Dataset</h3>
            <p class="mb-2">
                <mark>Add a new empty Dataset or Upload an existing!</mark>
            </p>

            <form id="formUpload" action="#" method="post" enctype="multipart/form-data">
                <div class="form-group row">
                    <label for="file" class="col-sm-2 col-form-label">Upload Dataset:</label>
                    <div class="col-sm-4">
                        <input type="file" class="form-control-file" name="file" id="file" value="" required>
                    </div>
                </div>
                <div class="form-group row">
                    <label for="datasetName" class=" col-sm-2 col-form-label">Dataset name:</label>
                    <div class="col-sm-3">
                        <input type="text" class="form-control" name="datasetName" id="datasetName" value=""
                               placeholder="Only for empty datasets!" required>

                    </div>

                </div>
                <div class="form-group row">
                    <label for="endpoint" class="col-sm-2 col-form-label">Endpoint:</label>
                    <div class="col-sm-3">
                        <input type="text" class="form-control" name="endpoint" id="endpoint"
                               placeholder="http://dbpedia.org/sparql" required>
                    </div>
                </div>
                <div class="form-group row">
                    <label for="defaultLanguage" class="col-sm-2 col-form-label">Default Language: </label>
                    <div class="col-sm-3">
                        <select class="form-control" name="defaultLanguage" id="defaultLanguage" required>
                            <option value="en">English</option>
                            <option value="de">Deutsch</option>
                            <option value="es">Espanol</option>
                            <option value="fr">Francais</option>
                        </select>

                    </div>
                </div>

                <div class="form-group row">
                    <div class="col-sm-4">
                        <span id="endpoint-error" class="alert alert-danger d-none">The endpoint you have entered is not reachable!</span>
                    </div>
                </div>

                <button type="submit" class="btn btn-primary">Submit</button>
                <c:if test="${error != null}">
                    <span class="alert alert-danger">${error}</span></c:if>
                <c:if test="${success != null}">
                    <span class="alert alert-success">${success}</span></c:if>
            </form>

        </div>
    </div>
</div>

<script>
    jQuery(function ($) {
        var $inputs = $('input[name=file],input[name=datasetName]');
        $inputs.on('input', function () {
            // Set the required property of the other input to false if this input is not empty.
            $inputs.not(this).prop('required', !$(this).val().length);
        });
    });

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