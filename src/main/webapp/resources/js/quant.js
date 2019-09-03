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

function validateEndpoint(endpoint)
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

    return valid;
}
