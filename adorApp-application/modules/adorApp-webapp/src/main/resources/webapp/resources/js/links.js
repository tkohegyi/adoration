$(document).ready(function() {
    $("#nav-application-log").addClass("active");
    setupMenu();
    setupLinkTable();
    loadStructure();
});

var structureInfo;
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

function loadStructure() {
    $.get("/resources/json/dataTables_linkStructure.json", function(data) {
        structureInfo = data;
    });
}

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
