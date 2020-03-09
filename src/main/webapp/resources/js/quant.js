var startTime, endTime;

function start() {
    startTime = new Date();

};

function end() {
    endTime = new Date();
    var timeDiff = endTime - startTime;
    //var timeDiff = timeDiff / 1000;
   // var  seconds = Math.round(timeDiff);
    document.getElementById("js_duration").value = timeDiff;

};

function validateEndpoint(endpoint,callback)
{
    valid = false;
    $.ajax({
        type: "GET", url: endpoint, async: false, timeout:1000,
        statusCode: {
            200: function() {
                valid = true;
            }
        }
    });
    if(!valid){
        var pathArray = endpoint.split('/');
        var protocol = pathArray[0];
        var host = pathArray[2];
        var url = protocol + '//' + host;
        $.ajax({
            type: "GET", url: url, async: false, timeout:1000,
            statusCode: {
                200: function() {
                    valid = true;
                }
            }
        });

    }
    return valid;
}

