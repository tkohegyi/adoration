$(document).ready(function() {
    $("#nav-information").addClass("active");
    setupMenu();
    getInformation();
});

function getInformation() {
    $.get('/adorationSecure/getInformation', function(data) {
        var information = data.data;
        if (information == null || information.error != null) {
            //something was wrong with either the server or with the request, let's go back
            window.location.pathname = "/adoration/";
        }
        //we have something to show
        $("#name").text("Az Ön neve: " + information.name);
        $("#status").text("Státusza: " + information.status);
    });
}
