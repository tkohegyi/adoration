$(document).ready(function() {
    $("#nav-application-log").addClass("active");
    setupMenu();
    setupPersonTable();
    loadStructure();
});

var structureInfo;
var imgSrc; //used by renderer
var hourInfo;

function loadStructure() {
    $.get("/resources/json/dataTables_socialStructure.json", function(data) {
        structureInfo = data;
    });
}

function setupPersonTable() {
    $('#social').DataTable( {
        "ajax": "/adorationSecure/getSocialTable",
        "language": {
                    "url": "//cdn.datatables.net/plug-ins/9dcbecd42ad/i18n/Hungarian.json"
             },
        "scrollX": true,
        "columns": [
            { "data": "id" },
            { "data": "personId", "width": "200px" },
            { "data": "socialStatus" },
            { "data": "facebookUserName" },
            { "data": "facebookFirstName" },
            { "data": "facebookEmail" },
            { "data": "facebookUserId" },
            { "data": "googleUserName" },
            { "data": "googleUserPicture" },
            { "data": "googleEmail" },
            { "data": "googleUserId" }
        ],
        "columnDefs": [
            {
                "className": "text-center",
                "targets": [0,1,2,3,4,5,6,7,8,9,10]
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
                    var z;
                    if (data != null) {
                        z = data;
                    } else {
                        z = "Nem tudjuk ki...";
                    }
                    return z;
                },
                "targets": 1
            },
            {
                "render": function ( data, type, row ) {
                    var z;
                    switch (data) {
                    case 1: z = 'Azonosításra vár'; break;
                    case 2: z = 'Azonosított adoráló'; break;
                    case 3: z = 'Vendég/érdeklődő'; break;
                    default: z = '???';
                    }
                    return z;
                },
                "targets": 2
            }
        ]
    } );
}

function processEntityUpdated() {
    window.location.pathname = "/adorationSecure/social"
}
