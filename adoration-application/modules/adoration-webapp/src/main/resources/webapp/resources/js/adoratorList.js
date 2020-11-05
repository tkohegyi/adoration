$(document).ready(function() {
    $("#nav-ador-list").addClass("active");
    hidePeople();
    hideLinks();
    setupMenu();
    getInfo();
    setupPersonTable();
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

function hidePeople() {
    //hide adoratorCooList and AdoratorList
    $("#adoratorList").hide();
    $("#adoratorCooList").hide();
}

function hideLinks() {
    //hide linkList
    $("#linkList").hide();
}

function adoratorListClick() {
    hideLinks();
    if (loggedInUserInfo.isPrivilegedAdorator) {
        $("#adoratorCooList").show();
        $('#personCoo').DataTable().draw();
    } else {
        $("#adoratorList").show();
        $('#person').DataTable().draw();
    }
}

function hourListClick() {
    hidePeople();
    $("#linkList").show();
    $('#link').DataTable().draw();
}

jQuery.extend( jQuery.fn.dataTableExt.oSort, {
    "adorList-pre": function ( a ) {
        var str = "commentClick(";
        var startPos = a.indexOf(str) + str.length;
        var endPos = a.indexOf(")");
        str = a.substring(startPos, endPos);
        return parseInt( str );
    },
    "adorList-asc": function ( a, b ) {
        return ((a < b) ? -1 : ((a > b) ? 1 : 0));
    },
    "adorList-desc": function ( a, b ) {
        return ((a < b) ? 1 : ((a > b) ? -1 : 0));
    },

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

function setupPersonTable() {
    //person - coordinator table
    $('#personCoo').DataTable( {
        data: peopleInfo,
        stateSave: true,
        "language": {
                    "url": "//cdn.datatables.net/plug-ins/9dcbecd42ad/i18n/Hungarian.json"
             },
        "scrollX": true,
        "lengthMenu": [[10, 50, 100, -1], [10, 50, 100, "All"]],
        "columns": [
            { "data": "id" },
            { "data": "name" },
            { "data": "mobile" },
            { "data": "email" },
            { "data": null,
                "defaultContent": "Nincs adat." }, //this is to compile committed hours
            { "data": "coordinatorComment" },
            { "data": "visibleComment" }
        ],
        "columnDefs": [
            { type: 'adorList', targets: 0 },
            {
                "className": "text-center",
                "targets": [0,1,2,3]
            },
            {
                "render": function ( data, type, row ) {
                    var z = "<button type=\"button\" class=\"btn btn-info btn-sm\" data-toggle=\"modal\" data-target=\"#editModal\" onclick=\"commentClick(" + data + ")\">" + data + "</button>";
                    return z;
                },
                "targets": 0
            },
            {
                "render": function ( data, type, row ) {
                    var z = buildHours(data.id);
                    return z;
                },
                "targets": 4
            }
        ]
    } );
    //person - adorator table
    $('#person').DataTable( {
        data: peopleInfo,
        stateSave: true,
        "language": {
                    "url": "//cdn.datatables.net/plug-ins/9dcbecd42ad/i18n/Hungarian.json"
             },
        "scrollX": true,
        "lengthMenu": [[10, 50, 100, -1], [10, 50, 100, "All"]],
        "columns": [
            { "data": "id" },
            { "data": "name" },
            { "data": "mobile" },
            { "data": "email" },
            { "data": null,
                "defaultContent": "Nincs adat." }, //this is to compile committed hours
            { "data": "visibleComment" }
        ],
        "columnDefs": [
            { type: 'adorList', targets: 0 },
            {
                "className": "text-center",
                "targets": [0,1,2,3]
            },
            {
                "render": function ( data, type, row ) {
                    var z = "<button type=\"button\" class=\"btn btn-info btn-sm\" data-toggle=\"modal\" data-target=\"#editModal\" onclick=\"commentClick(" + data + ")\">" + data + "</button>";
                    return z;
                },
                "targets": 0
            },
            {
                "render": function ( data, type, row ) {
                    var z = buildHours(data.id);
                    return z;
                },
                "targets": 4
            }
        ]
    } );
}

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

function buildHours(personId) {
    var data = "";
    if (hourInfo != "undefined" && hourInfo.length > 0) {
        for (var i = 0; i < hourInfo.length; i++) {
            if (hourInfo[i].personId == personId) {
                var hourId = hourInfo[i].hourId;
                var z = "<div>" + getDayNameLocalized(hourId, dayNames) + ", " + getHourName(hourId) + " óra";
                if (hourInfo[i].type == 0) {
                    z = z + ", Kápolnában"
                } else {
                    z = z + ", Online"
                }
                if (hourInfo[i].publicComment.length > 0) {
                    z = z + ", Megjegyzés:" + hourInfo[i].publicComment;
                }
                data = data + z + "</div><br/>";
            }
        }
        return data;
    } else {
        return "Hibás adat.";
    }
}

function commentClick() {
//coordinators may update coordinator comments
}