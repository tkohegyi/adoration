$(document).ready(function() {
  $("#nav-summary-people").addClass("active");

  $.get('/adoration/getLoggedInUserInfo', function(data) {
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

    $.get('/adoration/personListSimple', function(data) {
  	data.person.sort();
      for (var i = 0; i < data.person.length; i++) {
        var m = data.person[i].split("\u00b8");
        var a = $("<tr><td><a href='personActivityDetail/" + m[1] + "'>" + (i+1) + "</a></td><td><a href='personEdit/" + m[1] + "'>" + m[0] + "</a></td><td>" + m[4] + "</td><td>" + m[2] + "</td><td>" + m[3] + "</td></tr>");
        $('#searchPeopleResult').append(a);
      };
    });


});