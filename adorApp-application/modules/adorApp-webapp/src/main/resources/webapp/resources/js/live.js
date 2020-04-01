$(document).ready(function() {
    $("#nav-ador-live").addClass("active");
    setupMenu();
    setupLiveCommunication();
    timer = window.setInterval("heartBeat()", 15000); //15 sec
});

var timer;
var hashCode;
function setupLiveCommunication() {
    $.ajax({
        type: "GET",
        url: "/adoration/registerLiveAdorator/",
        async: false,
        success: function(data) {
            hashCode = data.hash[0];
        }
    });
}

function heartBeat() {
    var legend = document.querySelector('legend');
    if ($(legend).is(':visible')) {
        console.log('HeartBeat: ', hashCode);
        $.get("/adoration/liveAdorator/" + hashCode, function(data) {});
    } else {
        console.log('Not visible');
    }
}

function isInViewport(elem) {
    var bounding = elem.getBoundingClientRect();
    return (
        bounding.top >= 0 &&
        bounding.left >= 0 &&
        bounding.bottom <= (window.innerHeight || document.documentElement.clientHeight) &&
        bounding.right <= (window.innerWidth || document.documentElement.clientWidth)
    );
};