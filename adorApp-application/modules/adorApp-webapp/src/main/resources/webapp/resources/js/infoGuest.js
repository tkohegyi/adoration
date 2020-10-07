$(document).ready(function() {
    $("#nav-information").addClass("active");
    jQuery.ajaxSetup({async:false});
    setupMenu();
    jQuery.ajaxSetup({async:true});
    getInformation();
});

function getInformation() {
    $.get('/adorationSecure/getGuestInformation', function(data) {
        var information = data.data;
        if (information == null || information.error != null) {
            //something was wrong with either the server or with the request, let's go back
            window.location.pathname = "/adoration/";
        }
        //we have something to show
        var g;
        if (information.isGoogle) {
            $("#noGoogle").hide();
            $("#yesGoogle").show();
            $("#nameGoogle").empty();
            $("#emailGoogle").empty();
            g = $("<div><b>Név: </b>" + information.nameGoogle + "</div>");
            $("#nameGoogle").append(g);
            g = $("<div><b>Email: </b>" + information.emailGoogle + "</div>");
            $("#emailGoogle").append(g);
        } else {
            $("#noGoogle").show();
            $("#yesGoogle").hide();
        }
        if (information.isFacebook) {
            $("#noFacebook").hide();
            $("#yesFacebook").show();
            $("#nameFacebook").empty();
            $("#emailFacebook").empty();
            g = $("<div><b>Név: </b>" + information.nameFacebook + "</div>");
            $("#nameFacebook").append(g);
            g = $("<div><b>Email: </b>" + information.emailFacebook + "</div>");
            $("#emailFacebook").append(g);
        } else {
            $("#noFacebook").show();
            $("#yesFacebook").hide();
        }
        $("#socialServiceUsed").empty();
        g = $("<div><b>Belépéshez használva: </b>" + information.socialServiceUsed + "</div>");
        $("#socialServiceUsed").append(g);
        $("#status").empty();
        g = $("<div><b>Státusz: </b>" + information.status + "</div>");
        $("#status").append(g);
        $("#socialId").val(information.id);
        //show leadership
        $("#yesLeadership").empty();
        if (information.leadership.length > 0) {
            //has leadership info
            $("#noLeadership").hide();
            $("#yesLeadership").show();
            var tr = $("<tr class=\"tableHead\"><th class=\"infoTable\">Koordinátor:</th><th class=\"infoTable\">Név:</th><th class=\"infoTable\">Telefon:</th><th class=\"infoTable\">E-mail:</th></tr>");
            $("#yesLeadership").append(tr);
            for (var i = 0; i < information.leadership.length; i++) {
                var coordinator = information.leadership[i];
                if (parseInt(coordinator.coordinatorType) >= 48) { //only for general and spiritual coordinators
                    tr = $("<tr/>");
                    if (i % 2 == 0) {
                        tr.addClass("evenInfo");
                    } else {
                        tr.addClass("oddInfo");
                    }
                    tr.append($("<td class=\"infoTable\">" + coordinator.coordinatorTypeText
                        + "</td><td class=\"infoTable\">" + coordinator.personName
                        + "</td><td class=\"infoTable\">" + coordinator.phone
                        + "</td><td class=\"infoTable\">" + coordinator.eMail + "</td>"));
                    $("#yesLeadership").append(tr);
                }
            }
        } else {
            //has no leadership
            $("#noLeadership").show();
            $("#yesLeadership").hide();
        }
    });
    //show downloads - if any
    if (loggedInUserInfo.isRegisteredAdorator) {
            $("#downloads").show();
            if (loggedInUserInfo.isDailyCoordinator) {
                $("#forDc").show();
            }
            if (loggedInUserInfo.isHourlyCoordinator) {
                $("#forHc").show();
            }
            //$("#forStdA").show();
    }
}

function msgClick() {
    //cleaning up the message text
    $("#emailOrPhone").val("");
    $("messageContent").text("");
    enableButtons();
}

function sendMessage() {
    var b = {}; //empty object
    b.info = $("#emailOrPhone").val();
    b.text = $("#messageContent").val();
    //verification
    var eStr = "";
    var bad = 0;
    var patt = /^[0-9a-zA-ZöüóőúéáűíÖÜÓŐÚÉÁŰÍ\.\,\- ]*$/
    if (!patt.test(b.info)) {
        bad = 1;
        eStr = "A megadott elérhetőségben el nem fogadható karakterek is vannak, kérjük javítását!";
    }
    if (!patt.test(b.text)) {
        bad = 1;
        eStr = "Az Üzenetben el nem fogadható karakterek is vannak, kérjük fogalmazza át az üzenetet!";
    }
    if (bad > 0) {
        alert("Hiba az üzenetben!\n" + eStr);
        window.scrollTo(0, 0);
        return;
    }
    //everything is ok, send registration request
    $('#cancelButton').attr('disabled', 'disabled');
    $('#sendButton').attr('disabled', 'disabled');
    var token = $("meta[name='_csrf']").attr("content");
    var header = $("meta[name='_csrf_header']").attr("content");
    $.ajax({
        url : '/adorationSecure/messageToCoordinator',
        type : 'POST',
        async: false,
        contentType: 'application/json',
        data: JSON.stringify(b),
        dataType: 'json',
        success : dismissModal,
        beforeSend : function(request) {
            if (token.length > 0) {
            request.setRequestHeader(header, token);
            }
        },
        complete : enableButtons,
    }).fail( function(xhr, status) {
        if (status != 400) {
            window.location.pathname = "/adorationSecure/information"
        }
        alert(xhr.responseText);
    });

}

function dismissModal() {
    alert("Üzenet sikeresen elküldve!");
    window.location.pathname = "/adorationSecure/information"
}

function enableButtons() {
    $('#cancelButton').removeAttr('disabled');
    $('#sendButton').removeAttr('disabled');
}