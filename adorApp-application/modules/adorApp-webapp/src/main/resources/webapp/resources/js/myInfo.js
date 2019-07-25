$(document).ready(function() {
  $("#nav-home").addClass("active");

  $.get('/getLoggedInUserInfo', function(data) {
    var loggedInUserInfo = JSON.parse(data.loggedInUserInfo[0]).details;
    $("#loggedInUserLegend").text("User: " + loggedInUserInfo.userName);
    if (loggedInUserInfo.allowAccessToMenuAccountAndPeopleManagement) {
        $("#nav-summary").show();
        $("#nav-summary-people").show();
    }
    if (loggedInUserInfo.allowAccessToMenuConfiguration) {
        $("#nav-configuration").show();
    }
    if (loggedInUserInfo.allowAccessToMenuAppLog) {
        $("#nav-application-log").show();
    }
  });

});