$(document).ready(function() {
  $("#nav-application-log").addClass("active");
    $("#nav-home").addClass("active");
    setupMenu();
    setupLogs();
    setupPersonTable();
    loadStructure();
});

var structureInfo;

function loadStructure() {
    $.get("/resources/json/dataTables_peopleStructure.json", function(data) {
        structureInfo = data;
    });
}

function changeClick(e) {
   $("#editId").val(e);
   reBuildModal();
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
            { "data": "webStatus" },
            { "data": "mobile" },
            { "data": "mobileVisible" },
            { "data": "email" },
            { "data": "emailVisible" },
            { "data": "adminComment" },
            { "data": "dhcSigned" },
            { "data": "dhcSignedDate" },
            { "data": "coordinatorComment" },
            { "data": "visibleComment" },
            { "data": "languageCode" }
        ],
        "columnDefs": [
            {
                "className": "text-center",
                "targets": [0,5,7,9,13]
            },
            {
                "render": function ( data, type, row ) {
                    var z = "<button type=\"button\" class=\"btn btn-info btn-sm\" data-toggle=\"modal\" data-target=\"#editModal\" onclick=\"changeClick(" + data + ")\">" + data + "</button>";
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
                    case 0: z = 'Nem adoráló'; break;
                    case 1: z = 'Azonosításra vár'; break;
                    case 2: z = 'Adoráló Google azonosítóval'; break;
                    case 3: z = 'Adoráló Facebook azonosítással'; break;
                    case 4: z = 'Adoráló Google/Facebook azonosítással'; break;
                    default: z = '???';
                    }
                    return z;
                },
                "targets": 3
            },
            {
                "render": function ( data, type, row ) {
                    var z;
                    switch (data) {
                    case true: z = 'I'; break;
                    case false: z = 'N'; break;
                    default: z = '???';
                    }
                    return z;
                },
                "targets": [5,7,9]
            },
            {
                "render": function ( data, type, row ) {
                    return getReadableLanguageCode(data);
                },
                "targets": 13
            },
            {
                "render": function ( data, type, row ) {
                    return getReadableDateString(data);
                },
                "targets": 10
            }
        ]
    } );
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
        for (var i = 0; i < info.length; i++) {
            var row = info[i];
            //first is about visibility - if not visible, skip
            if ((typeof row.edit != "undefined") && (typeof row.edit.visible != "undefined") && (row.edit.visible == false)) break;
            var r = $("<tr/>");
            //additional help text, based on behavior
            let addMandatory = "";
            let mandatoryFlag = row.mandatory;
            if ((typeof mandatory != "undefined") && mandatory) {
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
                text = "<input class=\"customField\" onchange=\"valueChanged(this)\" type=\"text\" name=\"" + nameText + "\" id=\"" + idText + "\" value=\"" + original + "\"/>";
            }
            if (row.type == "singleSelect") {
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
