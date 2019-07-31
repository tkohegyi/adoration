$(document).ready(function() {
    $("#nav-home").addClass("active");
    setupMenu();
    setupCoverage();
});

function setupCoverage() {
    $.get('/getCoverageInformation', function(data) {
        var coverageInfo = JSON.parse(data.coverageInfo[0]).details;
        var dayNames = coverageInfo.dayNames; // eg FRIDAY: "péntek"
        var hours = coverageInfo.hours; // hour is 0-167,  eg: 75: 0

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
        }
    });
}