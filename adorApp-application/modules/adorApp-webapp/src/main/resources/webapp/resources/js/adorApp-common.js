
//this method is to setup the menu item visibility correctly
//input parameter is a loggedInUserInfo JSON
function setupMenuItemVisibility(loggedInUserInfo) {
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
}

//escape a text
function escapeHTML(unsafeText) {
    let div = document.createElement('div');
    div.innerText = unsafeText;
    return div.innerHTML;
}