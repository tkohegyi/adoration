$(document).ready(function() {
    $("#nav-application-log").addClass("active");
    setupMenu();
    setupLinkTable();
});

var linkInfo;

jQuery.extend( jQuery.fn.dataTableExt.oSort, {
    "hourBased-pre": function ( a ) {
        var str = " óra";
        var endPos = a.indexOf(str);
        str = a.substring(0, endPos);
        return parseInt( str );
    },
    "hourBased-desc": function ( a, b ) {
        return ((a < b) ? -1 : ((a > b) ? 1 : 0));
    },
    "hourBased-asc": function ( a, b ) {
        return ((a < b) ? 1 : ((a > b) ? -1 : 0));
    }
} );

function setupLinkTable() {
    //we must have this response before going forward
    jQuery.ajaxSetup({async:false});
    $.get("/adorationSecure/getLinkTable", function(data) {
        linkInfo = data.data;
    });
    jQuery.ajaxSetup({async:true});
    $('#link').DataTable( {
        data: linkInfo.linkList,
        stateSave: true,
        "language": {
                    "url": "//cdn.datatables.net/plug-ins/9dcbecd42ad/i18n/Hungarian.json"
             },
        "scrollX": true,
        "lengthMenu": [[5, 50, 100, -1], [5, 50, 100, "All"]],
        "columns": [
            { "data": "id" },
            { "data": "day" },
            { "data": "hour" },
            { "data": "name" },
            { "data": "phone" },
            { "data": "email" },
            { "data": "priority" },
            { "data": "type" },
            { "data": "adminComment" },
            { "data": "publicComment" }
        ],
        "columnDefs": [
            { type: 'numeric-id', targets: 0 },
            { type: 'hourBased', targets: 2 },
            {
                "className": "text-center",
                "targets": [0,1,2,3,4,5,6,7,8,9]
            },
            {
                "render": function ( data, type, row ) {
                    var z = "<button type=\"button\" class=\"btn btn-info btn-sm\" data-toggle=\"modal\" data-target=\"#editModal\" onclick=\"changeClick(" + data + ")\">" + data + "</button>";
                    z = z + "<button type=\"button\" class=\"btn btn-secondary btn-sm\" data-toggle=\"modal\" data-target=\"#historyModal\" onclick=\"changeHistoryClick(" + data + ")\">Log</button>";
                    return z;
                },
                "targets": 0
            },
            {
                "render": function ( data, type, row ) {
                    var z = getDayName(row.hourId, linkInfo.dayNames)
                    return z;
                },
                "targets": 1
            },
            {
                "render": function ( data, type, row ) {
                    var z = getHourName(row.hourId);
                    return z + " óra";
                },
                "targets": 2
            },
            {
                "render": function ( data, type, row ) {
                    var z = getPerson(linkInfo.relatedPersonList, row.personId);
                    if (z != "undefined") {
                        z = z.name;
                    } else {
                        z = "Ismeretlen";
                    }
                    return z;
                },
                "targets": 3
            },
            {
                "render": function ( data, type, row ) {
                    var z = getPerson(linkInfo.relatedPersonList, row.personId);
                    if (z != "undefined") {
                        z = z.mobile;
                    } else {
                        z = "-";
                    }
                    return z;
                },
                "targets": 4
            },
            {
                "render": function ( data, type, row ) {
                    var z = getPerson(linkInfo.relatedPersonList, row.personId);
                    if (z != "undefined") {
                        z = z.email;
                    } else {
                        z = "-";
                    }
                    return z;
                },
                "targets": 5
            },
            {
                "render": function ( data, type, row ) {
                    var z = row.type;
                    switch (z) {
                    case 0: z = 'Kápolnában'; break;
                    case 1: z = 'Online'; break;
                    default: z = '???';
                    }
                    return z;
                },
                "targets": 7
            }
        ]
    } );
    var filter = findGetParameter("filter");
    if ((filter != null) && (filter.length > 0)) {
        var table = $('#link').DataTable();
        table.search(filter).draw();
    }
}

function changeHistoryClick(data) {
   reBuildHistoryModal(data);
}

function reBuildHistoryModal(id) {
    var hc = $("<tbody id=\"historyContent\"/>");
    $.get('/adorationSecure/getLinkHistory/' + id , function(data) {
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

function changeClick(e) {
    $("#editId").val(e);
    $("#deleteButton").show();
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


function reBuildModal() {
    requestComplete(); //ensure availability of save button when the modal is refreshed
    //reconstruct modal
    var c = $('#editContent');
    c.remove();
    var t = $('#editTable');
    var c = $("<tbody id=\"editContent\"/>");
    t.append(c);
    //get and fill modal
    var retObj;
    var editId = $("#editId").val(); //filled by the button's onclick method
    $.ajax({
        type: "GET",
        url: '/adorationSecure/getLink/' + editId,
        async: false,
        success: function(response) {
            retObj = response.data;
        }
    });
    //identify the retObj
    if (typeof retObj == "undefined") return; //if link is not available, there is no point to rebuild
    var personObj = retObj.relatedPersonList[0];
    retObj = retObj.linkList[0];
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
            let command = "retObj." + row.id;
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
