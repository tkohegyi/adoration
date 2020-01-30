$(document).ready(function() {
    $("#nav-home").addClass("active");
    setupMenu();
    setupLoginPage();
});

function setupLoginPage() {
    $.get('/adoration/getAdorAppServerInfo', function(data) {
        var serverInfo = JSON.parse(data.adorAppApplication[0]);
        $("#serverInfo").text("Server information: " + serverInfo.hostname);
    });

}