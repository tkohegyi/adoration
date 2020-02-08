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
        "scrollX": true,
        "columns": [
            { "data": "id" },
            { "data": "name" },
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
        ]
    } );
}

