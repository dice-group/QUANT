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