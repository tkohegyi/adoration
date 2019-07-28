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
    });
}