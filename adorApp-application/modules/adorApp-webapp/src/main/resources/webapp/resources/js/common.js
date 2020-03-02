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
            $("#nav-ador-status").show();
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

function getReadableDateString(data) {
    var z = '';
    if (typeof data != "undefined") {
        var dateTime = new Date(data);
        z = dateTime.toISOString().substring(0, 10);
    }
    return z;
}