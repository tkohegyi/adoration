$(document).ready(function() {
  $("#nav-application-log").addClass("active");
    $("#nav-home").addClass("active");
    setupMenu();
    setupVersion();
    setupLogs();
    setupPersonTable();
});

function setupVersion() {
  //TODO
  //$.get('/adorationSecure/version', function(data) {
  //    $("#span-version").text(data.uooVersion);
  //});
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
                "render": function ( data, type, row ) {
                    return '<a href=\"/adorationSecure/editPerson?id=' + data + '\">' + data +'</a>';
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
            }
        ]
    } );
}

