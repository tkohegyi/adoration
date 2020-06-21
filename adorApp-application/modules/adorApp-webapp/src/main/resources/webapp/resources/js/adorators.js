$(document).ready(function() {
    $("#nav-application-log").addClass("active");
    setupMenu();
    setupLogs();
    setupPersonTable();
    loadStructure();
});

var structureInfo;
var imgSrc; //used by renderer
var hourInfo;

function loadStructure() {
    $.get("/resources/json/dataTables_peopleStructure.json", function(data) {
        structureInfo = data;
    });
}

function setupLogs() {
  $.get('/adorationSecure/logs', function(data) {
	data.files.sort();
    var firstElement = data.files.shift();
    data.files = data.files.reverse();
    data.files.unshift(firstElement);
    for (var i = 0; i < data.files.length; i++) {
      var li = $("<li>");
      var a = $("<a href='logs/" + data.files[i] + "'>").text(data.files[i]);
      var btn = $("<a target='_blank' href='logs/" + data.files[i]
          + "?source=true'>").text("[Source]");
      li.append(a);
      li.append("     ");
      li.append(btn);
      $('#div-log-files').append(li);
    }
  });
}

function setupPersonTable() {
    $('#person').DataTable( {
        "ajax": "/adorationSecure/getPersonTable",
        "language": {
                    "url": "//cdn.datatables.net/plug-ins/9dcbecd42ad/i18n/Hungarian.json"
             },
        "scrollX": true,
        "columns": [
            { "data": "id" },
            { "data": "name", "width": "200px" },
            { "data": "adorationStatus" },
            { "data": "mobile" },
            { "data": "mobileVisible" },
            { "data": "email" },
            { "data": "emailVisible" },
            { "data": "adminComment" },
            { "data": "dhcSigned" },
            { "data": "dhcSignedDate" },
            { "data": "coordinatorComment" },
            { "data": "visibleComment" },
            { "data": "isAnonymous" },
            { "data": "languageCode" }
        ],
        "columnDefs": [
            {
                "className": "text-center",
                "targets": [0,4,6,8,12,13]
            },
            {
                "render": function ( data, type, row ) {
                    var z = "<button type=\"button\" class=\"btn btn-info btn-sm\" data-toggle=\"modal\" data-target=\"#editModal\" onclick=\"changeClick(" + data + ")\">" + data + "</button>";
                    z = z + "<button type=\"button\" class=\"btn btn-warning btn-sm\" data-toggle=\"modal\" data-target=\"#timeModal\" onclick=\"changeTimeClick(" + data + ")\">Órák</button>";
                    z = z + "<button type=\"button\" class=\"btn btn-secondary btn-sm\" data-toggle=\"modal\" data-target=\"#historyModal\" onclick=\"changeHistoryClick(" + data + ")\">Log</button>";
                    return z;
                },
                "targets": 0
            },
            {
                "render": function ( data, type, row ) {
                    var z;
                    switch (data) {
                    case 1: z = 'Jelentkező-Adoráló'; break;
                    case 2: z = 'Adoráló'; break;
                    case 3: z = 'Óra-koordinátor'; break;
                    case 4: z = 'Napszak-koordinátor'; break;
                    case 5: z = 'Fő koordinátor'; break;
                    case 6: z = 'Spiritualis vezető'; break;
                    case 7: z = 'Adminisztrátor'; break;
                    case 8: z = 'Ex-Adoráló'; break;
                    case 9: z = 'Elhunyt'; break;
                    default: z = '???';
                    }
                    return z;
                },
                "targets": 2
            },
            {
                "render": function ( data, type, row ) {
                    var z;
                    switch (data) {
                    case true:
                        imgSrc = "/resources/img/dark-green-check-mark-th.png"
                        z = "<img alt=\"Igen\" src=\"" + imgSrc + "\" height=\"20\" width=\"20\" />";
                        break;
                    case false:
                        imgSrc = "/resources/img/orange-cross-th.png";
                        z = "<img alt=\"Nem\" src=\"" + imgSrc + "\" height=\"20\" width=\"20\" />";
                        break;
                    default: z = '???';
                    }
                    return z;
                },
                "targets": [4,6,8,12]
            },
            {
                "render": function ( data, type, row ) {
                    return getReadableLanguageCode(data);
                },
                "targets": 13
            }
        ]
    } );
}

function addClick() {
   $("#editId").val(0);
   $('#resetChangesButton').attr('onclick', 'reBuildAddModal()');
   if ((typeof structureInfo != "undefined") && (typeof structureInfo.new != "undefined") && (typeof structureInfo.new.title != "undefined")) {
       $('#editCenterTitle').text(structureInfo.new.title);
   } else {
       $('#editCenterTitle').text('Új felvétele');
   }
   reBuildAddModal();
}

function changeClick(e) {
   $("#editId").val(e);
   $('#resetChangesButton').attr('onclick', 'reBuildModal()');
   if ((typeof structureInfo != "undefined") && (typeof structureInfo.edit != "undefined") && (typeof structureInfo.edit.title != "undefined")) {
       $('#editCenterTitle').text(structureInfo.edit.title);
   } else {
       $('#editCenterTitle').text('Módosítás');
   }
   reBuildModal();
}

function requestComplete() {
    $('#saveChangesButton').removeAttr('disabled');
}

function beforeRequest() {
    $('#saveChangesButton').attr('disabled', 'disabled');
}

function reBuildModal() {
    requestComplete(); //ensure availability of save button when the modal is refreshed
    //reconstruct modal
    var c = $('#editContent');
    c.remove();
    var t = $('#editTable');
    var c = $("<tbody id=\"editContent\"/>");
    t.append(c);
    //get and fill modal
    var person;
    var editId = $("#editId").val(); //filled by the button's onclick method
    $.ajax({
        type: "GET",
        url: '/adorationSecure/getPerson/' + editId,
        async: false,
        success: function(response) {
            person = response.data;
        }
    });
    //identify the person
    if (typeof person == "undefined") return; //if person is not available, there is no point to rebuild
    if (typeof structureInfo != "undefined") {
        //we have structureInfo
        var info = structureInfo.info;
        for (var i = 0; i < info.length; i++) { //iterate through columns
            var row = info[i];
            //first is about visibility - if not visible, skip
            if ((typeof row.edit != "undefined") && (typeof row.edit.visible != "undefined") && (row.edit.visible == false)) continue;
            var r = $("<tr/>");
            //additional help text, based on behavior
            let addMandatory = "";
            let mandatoryFlag = row.mandatory;
            if ((typeof row.mandatory != "undefined") && mandatoryFlag) {
                addMandatory = "-m-";  // -m- is added
            }
            var td1 = $("<th>" + row.text + "</th>");
            var text = "";
            var idText = "field-" + row.id;
            var nameText = idText + "-" + row.type + addMandatory;
            let command = "person." + row.id;
            var original = eval(command);
            if (row.type == "fixText") {
                text = original;
            }
            if (row.type.split("-")[0] == "input") { //input-100, input-1000 etc
                text = "<input class=\"customField\" onchange=\"valueChanged(this,'" + row.type + "')\" type=\"text\" name=\"" + nameText + "\" id=\"" + idText + "\" value=\"" + original + "\" />";
            }
            if (row.type == "dateString-nullable") {
                text =  "<input onchange=\"valueChanged(this,'" + row.type + "')\" type=\"date\" name=\"" + nameText + "\" id=\"" + idText + "\"  value=\"" + original + "\"/>";
            }
            if (row.type == "singleSelect") {
                text = "";
                for (var j = 0; j < row.selection.length; j++) {
                    let selected = "";
                    if (original == row.selection[j].id) {
                        selected = " selected ";
                    }
                    text += "<option value=\"" + row.selection[j].id + "\"" + selected + ">" + row.selection[j].value + "</option>";
                }
                text = "<select id=\"" + idText + "\" class=\"custom-select\" onchange=\"valueChanged(this,'" + row.type + "')\">" + text + "</select>"
            }
            if (row.type == "i/n-boolean") {
                let checked = "";
                if (original == true) {
                    checked = " checked ";
                }
                text =  "<input onchange=\"valueChanged(this,'" + row.type + "')\" type=\"checkbox\" " + checked + " name=\"" + nameText + "\" id=\"" + idText + "\" />";
            }
            //preserve original value too
            var originalValue = "<input id=\"orig-" + idText + "\" type=\"hidden\" value=\"" + original + "\">";
            var td2 = $("<td id=\"td-" + idText + "\">" + text + originalValue + "</td>");
            //help text
            var td3 = $("<td>" + row.helpText + "</td>");
            r.append(td1);r.append(td2);r.append(td3);
            c.append(r);
        }
    }

}

function reBuildAddModal() {
    requestComplete(); //ensure availability of save button when the modal is refreshed
    //reconstruct modal
    var c = $('#editContent');
    c.remove();
    var t = $('#editTable');
    var c = $("<tbody id=\"editContent\"/>");
    t.append(c);
    //get and fill modal
    var editId = 0; //indicator of new person - $("#editId").val(); //filled by the button's onclick method
    if (typeof structureInfo != "undefined") {
        //we have structureInfo
        var info = structureInfo.info;
        for (var i = 0; i < info.length; i++) { //iterate through columns
            var row = info[i];
            //first is about visibility - if not visible, skip
            if ((typeof row.new != "undefined") && (typeof row.new.visible != "undefined") && (row.new.visible == false)) continue;
            var r = $("<tr/>");
            //additional help text, based on behavior
            let addMandatory = "";
            let mandatoryFlag = row.mandatory;
            if ((typeof row.mandatory != "undefined") && mandatoryFlag) {
                addMandatory = "-m-";  // -m- is added
            }
            var td1 = $("<th>" + row.text + "</th>");
            var text = "";
            var idText = "field-" + row.id;
            var nameText = idText + "-" + row.type + addMandatory;
            var original = "";
            if ((typeof row.new != "undefined") && (typeof row.new.default != "undefined")) {
                original = row.new.default;
            }
            if (row.type == "fixText") {
                text = original;
            }
            if (row.type.split("-")[0] == "input") { //input-100, input-1000 etc
                text = "<input class=\"customField\" onchange=\"valueChanged(this,'" + row.type + "')\" type=\"text\" name=\"" + nameText + "\" id=\"" + idText + "\" value=\"" + original + "\" />";
            }
            if (row.type == "dateString-nullable") {
                text =  "<input onchange=\"valueChanged(this,'" + row.type + "')\" type=\"date\" name=\"" + nameText + "\" id=\"" + idText + "\"  value=\"" + original + "\"/>";
            }
            if (row.type == "singleSelect") {
                text = "";
                for (var j = 0; j < row.selection.length; j++) {
                    let selected = "";
                    if (original == row.selection[j].id) {
                        selected = " selected ";
                    }
                    text += "<option value=\"" + row.selection[j].id + "\"" + selected + ">" + row.selection[j].value + "</option>";
                }
                text = "<select id=\"" + idText + "\" class=\"custom-select\" onchange=\"valueChanged(this,'" + row.type + "')\">" + text + "</select>"
            }
            if (row.type == "i/n-boolean") {
                let checked = "";
                if (original == true) {
                    checked = " checked ";
                }
                text =  "<input onchange=\"valueChanged(this,'" + row.type + "')\" type=\"checkbox\" " + checked + " name=\"" + nameText + "\" id=\"" + idText + "\" />";
            }
            //preserve original value too
            var originalValue = "<input id=\"orig-" + idText + "\" type=\"hidden\" value=\"" + original + "\">";
            var td2 = $("<td id=\"td-" + idText + "\">" + text + originalValue + "</td>");
            //help text
            var td3 = $("<td>" + row.helpText + "</td>");
            r.append(td1);r.append(td2);r.append(td3);
            c.append(r);
        }
    }

}

function valueChanged(theObject, type) {
	var o = $("#" + theObject.id);
	var origO = $("#" + "orig-" + theObject.id);
	var td = $("#" + "td-" + theObject.id);
	let v;
	type = type.split("-")[0]; // dateString , input etc
	switch (type) {
	    case "dateString": // val();
	        v = o.val();
	        break;
	    case "singleSelect": // select
	        v = o.find(":selected").val();
	        break;
	    case "input":
	        v = o.prop("value");
	        break;
	    case "i/n":
	        v = o.prop("checked").toString();
	}
	if (v == origO.val()) {
	    td.removeClass("table-danger");
	} else {
	    td.addClass("table-danger");
	}
}

function saveChanges() {
    var b = {}; //empty object
    var editId = $("#editId").val(); //filled by the button's onclick method
    b.id = editId;
	//validations + prepare object
	var eStr = "";
    var bad = 0;
    if (typeof structureInfo == "undefined") {
        alert("Cannot Save Person.");
    }
    //we have structureInfo
    var info = structureInfo.info;
    for (var i = 0; i < info.length; i++) { //iterate through columns
        var row = info[i];
        //first is about visibility - if not visible, skip
        if ((typeof row.edit != "undefined") && (typeof row.edit.visible != "undefined") && (row.edit.visible == false)) continue;
        if (row.type == "fixText") continue; //don't bother us with such a value
        let v;
        type = row.type.split("-")[0]; // dateString , input etc
        var idText = "field-" + row.id;
        var o = $("#" + idText);
        switch (type) {
            case "dateString": // val();
                v = o.val();
                break;
            case "singleSelect": // select
                v = o.find(":selected").val();
                break;
            default:
            case "input":
                v = o.prop("value");
                if ((typeof row.mandatory != "undefined") && (row.mandatory == true)) { // if mandatory, cannot be empty
                    if (v.length <= 0) {
                        eStr = "Value of \"" + row.name + "\" is not specified, pls specify!";
                        bad = 1;
                        }
                }
                break;
            case "i/n":
                v = o.prop("checked").toString();
                break;
        } //value in v
        let command = "b." + row.id + "=\"" + v.toString() + "\"";
        eval(command); //add object to b structure
    }
    // b is ready
    //validation done (cannot validate more at client level
    if (bad == 1) {
        alert(eStr);
        console.log("---=== ALERT ===---")
        return;
    }
    //save
    var token = $("meta[name='_csrf']").attr("content");
    var header = $("meta[name='_csrf_header']").attr("content");
    beforeRequest();
    $.ajax({
        url : '/adorationSecure/updatePerson',
        type : 'POST',
        async: false,
        contentType: 'application/json',
        data: JSON.stringify(b),
        dataType: 'json',
        success : processEntityUpdated,
        beforeSend : function(request) {
            request.setRequestHeader(header, token);
        },
        complete : requestComplete,
    }).fail( function(xhr, status) {
        var obj = JSON.parse(xhr.responseText);
        alert(obj.entityUpdate);
    });
}

function processEntityUpdated() {
    window.location.pathname = "/adorationSecure/adorators"
}

function changeHistoryClick(data) {
   reBuildHistoryModal(data);
}

function reBuildHistoryModal(personId) {
    var hc = $("<tbody id=\"historyContent\"/>");
    $.get('/adorationSecure/getPersonHistory/' + personId , function(data) {
        if ((typeof data != "undefined") && (typeof data.data != "undefined")) {
            var info = data.data;
            for (var i = 0; i < info.length; i++) {
              var r = $("<tr/>");
              var d = $("<td>" + info[i].activityType + "</td>");r.append(d);
              d = $("<td>" + info[i].atWhen + "</td>");r.append(d);
              d = $("<td>" + info[i].byWho + "</td>");r.append(d);
              d = $("<td>" + info[i].description + "</td>");r.append(d);
              if (info[i].data != null) {
                  d = $("<td>" + info[i].data + "</td>");r.append(d);
              } else {
                  d = $("<td/>");r.append(d);
              }
              hc.append(r);
            }
        } else { //logged out or other error at server side
            alert( "User Logged out, please login again." );
            window.location.pathname = "/adoration/"
        }
    });
    $('#historyContent').replaceWith(hc);
}

function changeTimeClick(data) {
   reBuildTimeModal(data);
}

function reBuildTimeModal(personId) {
    $('#editHourPersonId').val(personId); //remember about who we are talking
    $('#editHourId').val(0); //remember about who we are talking
    var hc = $("<tbody id=\"timeContent\"/>");
    $.get('/adorationSecure/getPersonCommitments/' + personId , function(data) {
        if ((typeof data != "undefined") && (typeof data.data != "undefined") && (typeof data.data.linkedHours != "undefined")) {
            hourInfo = data.data.linkedHours;;
            var info2 = data.data.others;
            var info3 = data.data.dayNames;
            for (var i = 0; i < hourInfo.length; i++) {
              var r = $("<tr onclick=\"clickHourEdit(" + hourInfo[i].hourId + ")\" />");
              //day
              var x = Math.floor(hourInfo[i].hourId / 24);
              var d = $("<td>" + info3[x] + "</td>");r.append(d);
              //hour
              var x = hourInfo[i].hourId % 24;
              var d = $("<td>" + x + "</td>");r.append(d);
              //priority
              var d = $("<td>" + hourInfo[i].priority + "</td>");r.append(d);
              //type/online
              var z;
              if (hourInfo[i].type > 0) {
                  imgSrc = "/resources/img/dark-green-check-mark-th.png"
                  z = "<td><img alt=\"Igen\" src=\"" + imgSrc + "\" height=\"20\" width=\"20\" /></td>";
              } else {
                  imgSrc = "/resources/img/orange-cross-th.png";
                  z = "<td><img alt=\"Nem\" src=\"" + imgSrc + "\" height=\"20\" width=\"20\" /></td>";
              }
              var d = $(z);r.append(d);
              //other adorators
              var d = $("<td>" + "TBD..." + "</td>");r.append(d);
              //admin comment
              var d = $("<td>" + hourInfo[i].adminComment + "</td>");r.append(d);
              //public comment
              var d = $("<td>" + hourInfo[i].publicComment + "</td>");r.append(d);
              hc.append(r);
            }
            if (hourInfo.length == 0) {
                showNewPartOfModal();
            } else {
                cancelNewPartOfModal();
            }
        } else { //logged out or other error at server side
            alert( "User Logged out, please login again." );
            window.location.pathname = "/adoration/"
        }
    });
    $('#timeContent').replaceWith(hc);
    $('#deleteHourButton').hide();
}

function cancelNewPartOfModal() {
    $('#newTimeTable').hide(500);
    $('#newButton').show(500);
}

function showNewPartOfModal() {
    $('#deleteHourButton').hide();
    $("#editHourId").val(0);
    $('#newTimeTable').show(500);
    $('#newButton').hide(500);
}

function saveNewHour() {
    var b = {}; //empty object
	//validations + prepare object
	var eStr = "";
    var bad = 0;
    //let v;
    b.id = $("#editHourId").val(); //filled by the button's onclick method, should be 0 in case of new, or the ID of the link in case of update
    b.hourId = parseInt($("#newDay").find(":selected").val()) + parseInt($("#newHour").val());
    b.personId = $("#editHourPersonId").val();
    b.priority = $("#newPriority").val();
    b.adminComment = $("#newAdminComment").val();
    b.publicComment = $("#newPublicComment").val();
    if ($("#newOnline").prop("checked").toString() == "true") {
        b.type = 1;
    } else {
        b.type = 0;
    }
    // b is ready
    //validation
    if (b.priority == "") {
        bad = 1;
        eStr = "Prioritás megadása kötelező!";
    }
    //validation done (cannot validate more at client level)
    if (bad == 1) {
        alert(eStr);
        console.log("---=== ALERT ===---")
        return;
    }
    //save
    var token = $("meta[name='_csrf']").attr("content");
    var header = $("meta[name='_csrf_header']").attr("content");
    //beforeRequest();
    $.ajax({
        url : '/adorationSecure/updatePersonCommitment',
        type : 'POST',
        async: false,
        contentType: 'application/json',
        data: JSON.stringify(b),
        dataType: 'json',
        success : processLinkEntityUpdated,
        beforeSend : function(request) {
            request.setRequestHeader(header, token);
        },
        complete : requestLinkComplete,
    }).fail( function(xhr, status) {
        var obj = JSON.parse(xhr.responseText);
        alert(obj.entityUpdate);
    });
}

function processLinkEntityUpdated() {
    var personId = $('#editHourPersonId').val();
    reBuildTimeModal(personId);
}

function requestLinkComplete() {
    processLinkEntityUpdated();
}

function clickHourEdit(hourId) {
    //identify the hour
    for (var i = 0; i < hourInfo.length; i++) {
        if (hourInfo[i].hourId == hourId) {
            break;
        }
    }
    if (hourInfo[i].hourId == hourId) { //if we really found it
        var x = Math.floor(hourInfo[i].hourId / 24) * 24;
        $("#newDay option[value=" + x + "]").prop('selected', 'selected').change();
        x = hourInfo[i].hourId % 24;
        $("#newHour option[value=" + x + "]").prop('selected', 'selected').change();
        $("#newPriority").val(hourInfo[i].priority);
        $("#newAdminComment").val(hourInfo[i].adminComment);
        $("#newPublicComment").val(hourInfo[i].publicComment);
        if (hourInfo[i].type > 0) {
            $("#newOnline").prop("checked", true);
        } else {
            $("#newOnline").removeProp("checked");
        }
        showNewPartOfModal();
        $("#editHourId").val(hourInfo[i].id);
        $('#deleteHourButton').show();
    } else {
        $('#deleteHourButton').hide();
        cancelNewPartOfModal();
        alert( "Data consistency error, reload is necessary." );
        window.location.pathname = "/adorationSecure/adorators"
    }
}

function deleteHour() {
    if (!confirm('Are you sure you want to DELETE this Committed Hour?')) {
      console.log('Thing was deleted from the database.');
      return;
    }
	console.log("---=== Delete Link Entity Clicked ===---");
    var entityId = $("#editHourId").val(); //filled during the original on-load page
    var req = {
        entityId : entityId,
    };
    var token = $("meta[name='_csrf']").attr("content");
    var header = $("meta[name='_csrf_header']").attr("content");
    $.ajax({
        url : '/adorationSecure/deletePersonCommitment',
        type : 'POST',
        async: false,
        contentType: 'application/json',
        data: JSON.stringify(req),
        dataType: 'json',
        success : processHourDeleted,
        beforeSend : function(request) {
            request.setRequestHeader(header, token);
        },
        complete : requestComplete,
    }).fail( function(xhr, status) {
        var obj = JSON.parse(xhr.responseText);
        alert(obj.entityUpdate);
    });
}

function processHourDeleted() {
    console.log("---=== Entity DELETED, going back to Entity list... ===---");
    reBuildTimeModal($("#editHourPersonId").val());
}

function deletePerson() {
    if (!confirm('Are you sure you want to DELETE this Adorator - permanently?')) {
      console.log('Thing was deleted from the database.');
      return;
    }
	console.log("---=== Delete Person Entity Clicked ===---");
    var entityId = $("#editId").val(); //filled by the button's onclick method
    var req = {
        entityId : entityId,
    };
    var token = $("meta[name='_csrf']").attr("content");
    var header = $("meta[name='_csrf_header']").attr("content");
    $.ajax({
        url : '/adorationSecure/deletePerson',
        type : 'POST',
        async: false,
        contentType: 'application/json',
        data: JSON.stringify(req),
        dataType: 'json',
        success : processPersonDeleted,
        beforeSend : function(request) {
            request.setRequestHeader(header, token);
        },
        complete : requestComplete,
    }).fail( function(xhr, status) {
        var obj = JSON.parse(xhr.responseText);
        alert(obj.entityUpdate);
    });
}

function processPersonDeleted() {
    console.log("---=== Entity DELETED, going back to Entity list... ===---");
    processEntityUpdated();
}
