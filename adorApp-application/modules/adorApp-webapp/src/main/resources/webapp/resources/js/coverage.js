var coverageAdoratorInfo;
var coverageAllHourInfo;
var coverageOnlineHourInfo;

window.addEventListener('resize', function(event){
    let intViewportWidth = window.innerWidth;
    handleSize(intViewportWidth);
});

function handleSize(width) {
    if (width < 1000) {
        if ($("#horizontalCoverage").is(":visible")) {
            $("#horizontalCoverage").hide();
        }
        if (!$("#verticalCoverage").is(":visible")) {
            $("#verticalCoverage").show();
        }
    } else {
        if (!$("#horizontalCoverage").is(":visible")) {
            $("#horizontalCoverage").show();
        }
        if ($("#verticalCoverage").is(":visible")) {
            $("#verticalCoverage").hide();
        }
    }
}

function setupCoverage() {
    $("#horizontalCoverage").hide();
    $("#verticalCoverage").hide();
    let intViewportWidth = window.innerWidth;
    handleSize(intViewportWidth);
    $.get('/adoration/getCoverageInformation', function(data) {
        var coverageInfo = JSON.parse(data.coverageInfo[0]);
        var dayNames = coverageInfo.dayNames; // eg FRIDAY: "péntek"
        var hours = coverageInfo.visibleHours; // hour is 0-167,  eg: 75: 0
        coverageAllHourInfo = coverageInfo.allHours; // hour is 0-167,  eg: 75: [327, 34, 8]
        coverageOnlineHourInfo = coverageInfo.onlineHours; // hour is 0-167,  eg: 75: [327, 34, 8]
        coverageAdoratorInfo = coverageInfo.adorators; //id - info pairs

        var days = document.getElementsByClassName('dayName');
        for (var i = 0; i < days.length; ++i) {
            var item = days[i];
            var targetDayName = item.id;
            targetDayName = targetDayName.split("-")[0];
            let command = "item.textContent = dayNames." + targetDayName;
            eval(command);
        }

        for (var i = 0; i < 168; i++) { //since we have 168 hours altogether
            var item = $("#hour-" + i);
            var item2 = $("#hour-" + i + "-2");
            var value = hours[i];
            if (value == 0) {
                if (!item.hasClass("lowPriority")) {
                    item.text("2");
                    item.removeClass("goodCoverage");
                    item.removeClass("badCoverage");
                    item.addClass("veryBadCoverage");
                    item2.text("2");
                    item2.removeClass("goodCoverage");
                    item2.removeClass("badCoverage");
                    item2.addClass("veryBadCoverage");
                } else {
                    item.text("");
                    item.addClass("lowPriorityColumn");
                    item2.text("");
                    item2.addClass("lowPriorityColumn");
                }
            }
            if (value == 1) {
                if (!item.hasClass("lowPriority")) {
                    item.text("1");
                    item.removeClass("goodCoverage");
                    item.removeClass("veryBadCoverage");
                    item.addClass("badCoverage");
                    item2.text("1");
                    item2.removeClass("goodCoverage");
                    item2.removeClass("veryBadCoverage");
                    item2.addClass("badCoverage");
                } else {
                    item.text("");
                    item.addClass("lowPriorityColumn");
                    item2.text("");
                    item2.addClass("lowPriorityColumn");
                }
            }
            if (value > 1) {
                if (!item.hasClass("lowPriority")) {
                    item.text("");
                    item.removeClass("veryBadCoverage");
                    item.removeClass("badCoverage");
                    item.addClass("goodCoverage");
                    item2.text("");
                    item2.removeClass("veryBadCoverage");
                    item2.removeClass("badCoverage");
                    item2.addClass("goodCoverage");
                } else {
                    item.text("");
                    item.addClass("lowPriorityColumn");
                    item2.text("");
                    item2.addClass("lowPriorityColumn");
                }
            }
            var onlineValue = coverageOnlineHourInfo[i];
            if (onlineValue.length > 0) {
                if (item.hasClass("lowPriority")) { //only online is possible, so emphasize it
                    item.removeClass("lowPriorityColumn");
                    item.addClass("exclusiveOnlineAdorator");
                    item2.removeClass("lowPriorityColumn");
                    item2.addClass("exclusiveOnlineAdorator");
                } else { //normal hour with online
                    item.addClass("onlineAdorator");
                    item2.addClass("onlineAdorator");
                }
            } else {
                item.removeClass("onlineAdorator");
                item2.removeClass("onlineAdorator");
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
            for (var i=0; i<personArray.length; i++,counter++) {
                var r = $("<tr/>");
                var p = personArray[i];
                var rContent = "<td><table><tbody><tr><td class=\"coverageDay goodCoverage\" align=\"center\" width=\"25px\">" + counter + "</td><td>" +
                    "<table><tbody><tr><td>ID: " + p.id + ", Név: " + p.name + "</td></tr>"
                        + "<tr><td>E-mail: " + p.email + "</td></tr>"
                        + "<tr><td>Telefon: " + p.mobile + "</td></tr>"
                        + "<tr><td>Megjegyzés: " + p.visibleComment + "</td></tr></tbody></table>"
                    + "</td></tr></tbody></table></td>";
                r.append($(rContent));
                $("#coveragePopup").append(r);
            }
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
            for (var i=0; i<personArray.length; i++,counter++) {
                var r = $("<tr/>");
                var p = personArray[i];
                var rContent = "<td><table><tr><td class=\"coverageDay onlineAdorator\" align=\"center\" width=\"25px\">" + counter + "</td><td>" +
                    "<table><tbody><tr><td>ID: " + p.id + ", Név: " + p.name + "</td></tr>"
                        + "<tr><td>E-mail: " + p.email + "</td></tr>"
                        + "<tr><td>Telefon: " + p.mobile + "</td></tr>"
                        + "<tr><td>Megjegyzés: " + p.visibleComment + "</td></tr></tbody></table>"
                    + "</td></tr></tbody></table></td>";
                r.append($(rContent));
                $("#coveragePopup").append(r);
            }
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