$(document).ready(function() {
    $("#nav-ador-list").addClass("active");
    setupMenu();
    getInfo();
    setupLinkTable();
});

var peopleInfo;
var hourInfo;
var dayNames;

function getInfo() {
    jQuery.ajaxSetup({async:false});
    $.get("/adorationSecure/getAdoratorList", function(data) {
        peopleInfo = data.data.relatedPersonList;
        hourInfo = data.data.linkList;
        dayNames = data.data.dayNames;
    });
    jQuery.ajaxSetup({async:true});
}

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
    $('#link').DataTable( {
        data: hourInfo,
        stateSave: true,
        "language": {
                    "url": "//cdn.datatables.net/plug-ins/9dcbecd42ad/i18n/Hungarian.json"
             },
        "scrollX": true,
        "lengthMenu": [[10, 50, 100, -1], [10, 50, 100, "All"]],
        "columns": [
            { "data": "day" },
            { "data": "hour" },
            { "data": "name" },
            { "data": "phone" },
            { "data": "email" },
            { "data": "priority" },
            { "data": "type" },
            { "data": "publicComment" }
        ],
        "columnDefs": [
            { type: 'hourBased', targets: 1 },
            {
                "className": "text-center",
                "targets": [0,1,2,3,4,5,6]
            },
            {
                "render": function ( data, type, row ) {
                    var z = getDayNameLocalized(row.hourId, dayNames)
                    return z;
                },
                "targets": 0
            },
            {
                "render": function ( data, type, row ) {
                    var z = getHourName(row.hourId);
                    return z + " óra";
                },
                "targets": 1
            },
            {
                "render": function ( data, type, row ) {
                    var z = getPerson(peopleInfo, row.personId);
                    if (z != "undefined") {
                        z = z.name;
                    } else {
                        z = "Ismeretlen";
                    }
                    return z;
                },
                "targets": 2
            },
            {
                "render": function ( data, type, row ) {
                    var z = getPerson(peopleInfo, row.personId);
                    if (z != "undefined") {
                        z = z.mobile;
                    } else {
                        z = "-";
                    }
                    return z;
                },
                "targets": 3
            },
            {
                "render": function ( data, type, row ) {
                    var z = getPerson(peopleInfo, row.personId);
                    if (z != "undefined") {
                        z = z.email;
                    } else {
                        z = "-";
                    }
                    return z;
                },
                "targets": 4
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
                "targets": 6
            }
        ]
    } );
}