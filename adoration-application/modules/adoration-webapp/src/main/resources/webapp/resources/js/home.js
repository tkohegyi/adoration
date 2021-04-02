$(document).ready(function() {
    $("#nav-home").addClass("active");
    setupMenu();
    setupCoverage();
    uniqueRegister();
});

function uniqueRegister() {
    if (loggedInUserInfo.isRegisteredAdorator) {
        $("#uniqueRegister").show();
    } else {
        $("#uniqueRegister").hide();
    }
}