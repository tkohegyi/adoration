$(document).ready(function() {
    $("#nav-home").addClass("active");
    setupMenu();
    setupLoginPage();
});

function setupLoginPage() {
    $.get('/adoration/getLoginUrls', function(data) {
        var loginUrlInfo = JSON.parse(data.loginUrlInfo[0]).details;
        var googleLoginAnchor = loginUrlInfo.loginUrls.googleLoginAnchor;
        $("#googleLoginAnchor").attr("href", googleLoginAnchor);
        var facebookLoginAnchor = loginUrlInfo.loginUrls.facebookLoginAnchor;
        $("#facebookLoginAnchor").attr("href", facebookLoginAnchor);
    });

    $.get('/adoration/getAdorAppServerInfo', function(data) {
        var serverInfo = JSON.parse(data.adorAppApplication[0]);
        $("#serverInfo").text("Server information: " + serverInfo.hostname);
    });

}