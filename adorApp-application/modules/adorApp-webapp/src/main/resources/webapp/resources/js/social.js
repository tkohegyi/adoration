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
    $('#person').DataTable( {
        "ajax": "/adorationSecure/getSocialTable",
        "language": {
                    "url": "//cdn.datatables.net/plug-ins/9dcbecd42ad/i18n/Hungarian.json"
             },
        "scrollX": true,
        "columns": [
            { "data": "id" },
            { "data": "name", "width": "200px" },
            { "data": "facebookusername" },
            { "data": "facebookfirstname" },
            { "data": "facebookemail" },
            { "data": "facebookuserid" },
            { "data": "googleusername" },
            { "data": "googlepicture" },
            { "data": "googleemail" },
            { "data": "googleuserid" }
        ],
        "columnDefs": [
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
                "targets": [5,7,9]
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

function processEntityUpdated() {
    window.location.pathname = "/adorationSecure/social"
}
