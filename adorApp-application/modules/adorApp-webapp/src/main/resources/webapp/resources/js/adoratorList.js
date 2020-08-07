$(document).ready(function() {
    $("#nav-application-log").addClass("active");
    hidePeople();
    hideLinks();
    setupMenu();
    setupPersonTable();
    setupLinkTable();
});

var peopleInfo;
var hourInfo;

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
    } else {
        $("#adoratorList").show();
    }
}

function hourListClick() {
    hidePeople();
    $("#linkList").show();
}

jQuery.extend( jQuery.fn.dataTableExt.oSort, {
    "adorList-pre": function ( a ) {
        var str = "commitmentClick(";
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
    }
} );

function setupPersonTable() {
    jQuery.ajaxSetup({async:false});
    $.get("/adorationSecure/getAdoratorList", function(data) {
        peopleInfo = data.data;
    });
    jQuery.ajaxSetup({async:true});
    //person - coordinator table
    $('#personCoo').DataTable( {
        data: peopleInfo,
        stateSave: true,
        "language": {
                    "url": "//cdn.datatables.net/plug-ins/9dcbecd42ad/i18n/Hungarian.json"
             },
        "scrollX": true,
        "lengthMenu": [[5, 50, 100, -1], [5, 50, 100, "All"]],
        "columns": [
            { "data": "id" },
            { "data": "name", "width": "200px" },
            { "data": "mobile" },
            { "data": "email" },
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
                    var z = "<button type=\"button\" class=\"btn btn-info btn-sm\" data-toggle=\"modal\" data-target=\"#editModal\" onclick=\"commitmentClick(" + data + ")\">" + data + "</button>";
                    return z;
                },
                "targets": 0
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
        "lengthMenu": [[5, 50, 100, -1], [5, 50, 100, "All"]],
        "columns": [
            { "data": "id" },
            { "data": "name", "width": "200px" },
            { "data": "mobile" },
            { "data": "email" },
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
                    var z = "<button type=\"button\" class=\"btn btn-info btn-sm\" data-toggle=\"modal\" data-target=\"#editModal\" onclick=\"commitmentClick(" + data + ")\">" + data + "</button>";
                    return z;
                },
                "targets": 0
            }
        ]
    } );
    var filter = findGetParameter("filter");
    if ((filter != null) && (filter.length > 0)) {
        var table = $('#personCoo').DataTable();
        table.search(filter).draw();
        table = $('#person').DataTable();
        table.search(filter).draw();
    }
}

function setupLinkTable() {
}

