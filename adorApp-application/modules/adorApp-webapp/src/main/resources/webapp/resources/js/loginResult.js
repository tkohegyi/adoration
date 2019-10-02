$(document).ready(function() {
    $("#nav-home").addClass("active");
    setupMenu();
    setupLoginResultPage();
});

function setupLoginResultPage() {
    $.get('/adoration/getLoginUrls', function(data) {
        var loginUrlInfo = JSON.parse(data.loginUrlInfo[0]).details;
        for (var i = 0; i < loginUrlInfo.length; ++i) {
            //todo
        }
    });

}