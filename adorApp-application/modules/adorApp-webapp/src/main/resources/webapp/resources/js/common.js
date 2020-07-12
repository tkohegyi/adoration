function setupMenu() {
    $.get('/adoration/getLoggedInUserInfo', function(data) {
        var loggedInUserInfo = JSON.parse(data.loggedInUserInfo[0]);
        if (loggedInUserInfo.isLoggedIn) {
            $("#loggedInUserLegend").text("Belépve: " + loggedInUserInfo.userName);
            $("#nav-exit").show();
        } else {
            $("#loggedInUserLegend").text("");
            $("#nav-login").show();
        }
        if (loggedInUserInfo.isRegisteredAdorator) {
            $("#nav-information").show();
            $("#nav-ador-list").show();
        } else {
            $("#nav-ador-registration").show();
        }
        if (loggedInUserInfo.isAdoratorAdmin) {
            $("#nav-application-log").show();
        }
    });
}

function getReadableLanguageCode(code) {
    var z;
    switch (code) {
        case "hu": z = 'magyar'; break;
        case "ge": z = 'német'; break;
        case "en": z = 'angol'; break;
        default: z = '???';
    }
    return z;
}

function validateEmail(email) {
    var re = /^(([^<>()\[\]\\.,;:\s@"]+(\.[^<>()\[\]\\.,;:\s@"]+)*)|(".+"))@((\[[0-9]{1,3}\.[0-9]{1,3}\.[0-9]{1,3}\.[0-9]{1,3}\])|(([a-zA-Z\-0-9]+\.)+[a-zA-Z]{2,}))$/;
    return re.test(String(email).toLowerCase());
}

function findGetParameter(parameterName) {
    var result = null,
        tmp = [];
    var items = location.search.substr(1).split("&");
    for (var index = 0; index < items.length; index++) {
        tmp = items[index].split("=");
        if (tmp[0] === parameterName) result = decodeURIComponent(tmp[1]);
    }
    return result;
}

var coverageAdoratorInfo;
var coverageAllHourInfo;
var coverageOnlineHourInfo;

function setupCoverage() {
    $.get('/adoration/getCoverageInformation', function(data) {
        var coverageInfo = JSON.parse(data.coverageInfo[0]);
        var dayNames = coverageInfo.dayNames; // eg FRIDAY: "péntek"
        var hours = coverageInfo.visibleHours; // hour is 0-167,  eg: 75: 0
        coverageAllHourInfo = coverageInfo.allHours; // hour is 0-167,  eg: 75: [327, 34, 8]
        coverageOnlineHourInfo = coverageInfo.onlineHours; // hour is 0-167,  eg: 75: [327, 34, 8]
        coverageAdoratorInfo = coverageInfo.adorators; //id - info pairs

        var days = document.getElementsByClassName('coverageDay');
        for (var i = 0; i < days.length; ++i) {
            var item = days[i];
            var targetDayName = item.id;
            let command = "item.textContent = dayNames." + targetDayName;
            eval(command);
        }

        for (var i = 0; i < 168; i++) { //since we have 168 hours altogether
            var item = $("#hour-" + i);
            var value = hours[i];
            if (value == 0) {
                item.text("2");
                item.removeClass("goodCoverage");
                item.removeClass("badCoverage");
                item.addClass("veryBadCoverage");
            }
            if (value == 1) {
                item.text("1");
                item.removeClass("goodCoverage");
                item.removeClass("veryBadCoverage");
                item.addClass("badCoverage");
            }
            if (value > 1) {
                item.text("");
                item.removeClass("veryBadCoverage");
                item.removeClass("badCoverage");
                item.addClass("goodCoverage");
            }
            var onlineValue = coverageOnlineHourInfo[i];
            if (onlineValue.length > 0) {
                item.addClass("onlineAdorator");
            } else {
                item.removeClass("onlineAdorator");
            }
        }
    });
}

function coverageClick(h) {
    if (typeof coverageAdoratorInfo != "undefined" && coverageAdoratorInfo != null) {
        $("#coveragePopup").empty();
        var personArray = []; //empty object;
        var users = coverageAllHourInfo[h];
        if ((users != null) && (users.length > 0)) {
            for (var i=0; i<users.length; i++) {
                var personId = users[i];
                var personInfo = coverageAdoratorInfo[personId];
                if (typeof personInfo != "undefined") {
                    personArray.push(personInfo);
                }
            }
        }
        var counter = 1;
        if (personArray.length > 0) {
            var r = $("<tr/>");
            for (var i=0; i<personArray.length; i++,counter++) {
                var p = personArray[i];
                var rContent = "<td><table><tbody><tr><td class=\"coverageDay goodCoverage\" align=\"center\" width=\"25px\">" + counter + "</td><td>" +
                    "<table><tbody><tr><td>ID: " + p.id + ", Név: " + p.name + "</td></tr>"
                        + "<tr><td>E-mail: " + p.email + "</td></tr>"
                        + "<tr><td>Telefon: " + p.mobile + "</td></tr>"
                        + "<tr><td>Megjegyzés: " + p.visibleComment + "</td></tr></tbody></table>"
                    + "</td></tr></tbody></table></td>";
                r.append($(rContent));
            }
            $("#coveragePopup").append(r);
        }
        //next round is about online adorators
        personArray = []; //empty object;
        users = coverageOnlineHourInfo[h];
        if ((users != null) && (users.length > 0)) {
            for (var i=0; i<users.length; i++) {
                var personId = users[i];
                var personInfo = coverageAdoratorInfo[personId];
                if (typeof personInfo != "undefined") {
                    personArray.push(personInfo);
                }
            }
        }
        if (personArray.length > 0) {
            var r = $("<tr/>");
            for (var i=0; i<personArray.length; i++,counter++) {
                var p = personArray[i];
                var rContent = "<td><table><tr><td class=\"coverageDay onlineAdorator\" align=\"center\" width=\"25px\">" + counter + "</td><td>" +
                    "<table><tbody><tr><td>ID: " + p.id + ", Név: " + p.name + "</td></tr>"
                        + "<tr><td>E-mail: " + p.email + "</td></tr>"
                        + "<tr><td>Telefon: " + p.mobile + "</td></tr>"
                        + "<tr><td>Megjegyzés: " + p.visibleComment + "</td></tr></tbody></table>"
                    + "</td></tr></tbody></table></td>";
                r.append($(rContent));
            }
            $("#coveragePopup").append(r);
        }
        if (counter > 1) { //this means we have something to show
            modal.style.display="block";
        } else {
            $(".pop").hide(500);
            $("#coveragePopup").empty();
        }
    }
}

var modal = document.getElementById("coverageModal");
var span = document.getElementsByClassName("close")[0];

// When the user clicks on <span> (x), close the modal
span.onclick = function() {
  modal.style.display = "none";
}

// When the user clicks anywhere outside of the modal, close it
window.onclick = function(event) {
  if (event.target == modal) {
    modal.style.display = "none";
  }
}