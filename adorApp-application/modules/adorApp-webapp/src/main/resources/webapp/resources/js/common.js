var loggedInUserInfo;

function setupMenu() {
    $.get('/adoration/getLoggedInUserInfo', function(data) {
        loggedInUserInfo = JSON.parse(data.loggedInUserInfo[0]);
        if (loggedInUserInfo.isLoggedIn) {
            $("#userProfile").text("Belépve: " + loggedInUserInfo.loggedInUserName);
            $("#loggedInUserLegend").show();
            $("#nav-exit").show();
        } else {
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

function getDayName(hourId, dayNames) {
    var x = getDay(hourId);
    return dayNames[x];
}

function getDay(hourId) {
    var x = Math.floor(hourId / 24);
    return x;
}

function getHourName(hourId) {
    return hourId % 24;
}

function getPerson(list, personId) {
    for (var i = 0; i < list.length; i++) {
        if (list[i].id == personId) {
            return list[i];
        }
    }
    return null;
}

function getCoordinator(list, hourInDayId) {
    for (var i = 0; i < list.length; i++) {
        if (parseInt(list[i].coordinatorType) == hourInDayId) {
            return list[i];
        }
    }
    return null;
}
