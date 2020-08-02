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
        $("#adoratorId").text("Az Ön adorálói azonosítószáma: " + information.id);
        //show offered hours
        $("#yesOfferedHours").empty();
        if ((information.linkList != null) && (information.linkList.length > 0)) {
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
                    tr.addClass("evenInfo");
                } else {
                    tr.addClass("oddInfo");
                }
                var coordinatorName = "N/A";
                var phone = "N/A";
                var email = "N/A";
                if (information.leadership.length > 0) { //detect hour coordinator
                    for (var j = 0; j < information.leadership.length; j++) {
                        var coordinator = information.leadership[j];
                        if (parseInt(coordinator.coordinatorType) == hourName) {
                            coordinatorName = coordinator.personName;
                            phone = coordinator.phone;
                            email = coordinator.eMail;
                        }
                    }
                } // else N/A, already set
                tr.append($("<td class=\"infoTable\">"
                    + dayName + "</td><td class=\"infoTable\">"
                    + hourName + "</td><td class=\"infoTable\">"
                    + coordinatorName + "</td><td class=\"infoTable\">"
                    + phone + "</td><td class=\"infoTable\">"
                    + email +"</td>"));
                $("#yesOfferedHours").append(tr);
            }
        } else {
            //has no offered hours
            $("#noOfferedHours").show();
            $("#yesOfferedHours").hide();
        }
        //show leadership
        $("#yesLeadership").empty();
        if (information.leadership.length > 0) {
            //has leadership info
            $("#noLeadership").hide();
            $("#yesLeadership").show();
            var tr = $("<tr class=\"tableHead\"><th class=\"infoTable\" colspan=\"5\">Napszak és Általános Koordinátorok:</th></tr>");
            $("#yesLeadership").append(tr);
            tr = $("<tr class=\"tableHead\"><th class=\"infoTable\">Időszak:</th><th class=\"infoTable\">Név:</th><th class=\"infoTable\">Telefon:</th><th class=\"infoTable\">E-mail:</th><th class=\"infoTable\">Megjegyzés:</th></tr>");
            $("#yesLeadership").append(tr);
            for (var i = 0; i < information.leadership.length; i++) {
                var coordinator = information.leadership[i];
                if (parseInt(coordinator.coordinatorType) > 24) { //only for main coordinators
                    tr = $("<tr/>");
                    if (i % 2 == 0) {
                        tr.addClass("evenInfo");
                    } else {
                        tr.addClass("oddInfo");
                    }
                    tr.append($("<td class=\"infoTable\">" + coordinator.coordinatorTypeText
                        + "</td><td class=\"infoTable\">" + coordinator.personName
                        + "</td><td class=\"infoTable\">" + coordinator.phone
                        + "</td><td class=\"infoTable\">" + coordinator.eMail
                        + "</td><td class=\"infoTable\">" + coordinator.visibleComment));
                    $("#yesLeadership").append(tr);
                }
            }
        } else {
            //has no leadership
            $("#noLeadership").show();
            $("#yesLeadership").hide();
        }
        //show actual hour
        $("#yesAdoratorNow").empty();
        var c = getCoordinator(information.leadership, information.hourInDayNow);
        if (c != null && c.personName.length > 0) {
            tr = $("<tr class=\"tableHead\"><th class=\"infoTable\" colspan=\"3\">Órafelelős, "
                + information.hourInDayNow + " óra:</th></tr>");
            $("#yesAdoratorNow").append(tr);
            tr = $("<td class=\"infoTable\">"
                + c.personName + "</td><td class=\"infoTable\">"
                + c.phone + "</td><td class=\"infoTable\">"
                + c.eMail +"</td>");
            $("#yesAdoratorNow").append(tr);
        }
        if (information.currentHourList.length > 0) {
            //has offered hours
            $("#noAdoratorNow").hide();
            tr = $("<tr class=\"tableHead\"><th class=\"infoTable\">Név:</th><th class=\"infoTable\">Telefon:</th><th class=\"infoTable\">E-mail:</th></tr>");
            $("#yesAdoratorNow").append(tr);
            for (var i = 0; i < information.currentHourList.length; i++) {
                var offeredHour = information.currentHourList[i];
                var person = getPerson(information.relatedPersonList, offeredHour.personId);
                tr = $("<tr/>");
                if (i % 2 == 0) {
                    tr.addClass("evenInfo");
                } else {
                    tr.addClass("oddInfo");
                }
                tr.append($("<td class=\"infoTable\">"
                    + person.name + "</td><td class=\"infoTable\">"
                    + person.mobile + "</td><td class=\"infoTable\">"
                    + person.email +"</td>"));
                $("#yesAdoratorNow").append(tr);
            }
        } else {
            //has no adorator now
            $("#noAdoratorNow").show();
        }
        //show future hour
        $("#yesAdoratorNext").empty();
        c = getCoordinator(information.leadership, information.hourInDayNext);
        if (c != null && c.personName.length > 0) {
            tr = $("<tr class=\"tableHead\"><th class=\"infoTable\" colspan=\"3\">Órafelelős, "
                + information.hourInDayNext + " óra:</th></tr>");
            $("#yesAdoratorNext").append(tr);
            tr = $("<td class=\"infoTable\">"
                + c.personName + "</td><td class=\"infoTable\">"
                + c.phone + "</td><td class=\"infoTable\">"
                + c.eMail +"</td>");
            $("#yesAdoratorNext").append(tr);
        }
        if (information.futureHourList.length > 0) {
            //has offered hours
            $("#noAdoratorNext").hide();
            tr = $("<tr class=\"tableHead\"><th class=\"infoTable\">Név:</th><th class=\"infoTable\">Telefon:</th><th class=\"infoTable\">E-mail:</th></tr>");
            $("#yesAdoratorNext").append(tr);
            for (var i = 0; i < information.futureHourList.length; i++) {
                var offeredHour = information.futureHourList[i];
                var person = getPerson(information.relatedPersonList, offeredHour.personId);
                tr = $("<tr/>");
                if (i % 2 == 0) {
                    tr.addClass("evenInfo");
                } else {
                    tr.addClass("oddInfo");
                }
                tr.append($("<td class=\"infoTable\">"
                    + person.name + "</td><td class=\"infoTable\">"
                    + person.mobile + "</td><td class=\"infoTable\">"
                    + person.email +"</td>"));
                $("#yesAdoratorNext").append(tr);
            }
        } else {
            //has no adorator now
            $("#noAdoratorNext").show();
        }
    });
}
