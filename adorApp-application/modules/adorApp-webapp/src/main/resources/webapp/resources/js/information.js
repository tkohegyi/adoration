$(document).ready(function() {
    $("#nav-information").addClass("active");
    setupMenu();
    getInformation();
});

function getDayName(hourId, dayNames) {
    var x = Math.floor(hourId / 24);
    return dayNames[x];
}

function getHourName(hourId) {
    return hourId % 24;
}

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
        $("#adoratorId").text("Az Ön adorálói azonosítószáma: " + information.id);
        //show offered hours
        $("#yesOfferedHours").empty();
        if (information.linkList.length > 0) {
            //has offered hours
            $("#noOfferedHours").hide();
            $("#yesOfferedHours").show();
            var tr = $("<tr class=\"tableHead\"><th class=\"infoTable\" colspan=\"2\">Vállalt órám/óráim:</th><th class=\"infoTable\" colspan=\"3\">Órafelelős:</th></tr>");
            $("#yesOfferedHours").append(tr);
            tr = $("<tr class=\"tableHead\"><th class=\"infoTable\">Nap:</th><th class=\"infoTable\">Óra:</th><th class=\"infoTable\">Név:</th><th class=\"infoTable\">Telefon:</th><th class=\"infoTable\">E-mail:</th></tr>");
            $("#yesOfferedHours").append(tr);
            for (var i = 0; i < information.linkList.length; i++) {
                var offeredHour = information.linkList[i];
                var dayName = getDayName(offeredHour.hourId, information.dayNames);
                var hourName = getHourName(offeredHour.hourId);
                tr = $("<tr/>");
                if (i % 2 == 0) {
                    tr.addClass("even");
                } else {
                    tr.addClass("odd");
                }
                tr.append($("<td class=\"infoTable\">" + dayName + "</td><td class=\"infoTable\">" + hourName + "<td class=\"infoTable\">?</td><td class=\"infoTable\">?</td><td class=\"infoTable\">?</td>"));
                $("#yesOfferedHours").append(tr);
            }
        } else {
            //has no offered hours
            $("#noOfferedHours").show();
            $("#yesOfferedHours").hide();
        }
    });
}
