$(document).ready(function() {

  $.get('/getAdorAppServerInfo', function(data) {
    var serverInfo = JSON.parse(data.uooApplication[0]);
    $("#serverInfo").text("Server information: " + serverInfo.hostname);
  });

});