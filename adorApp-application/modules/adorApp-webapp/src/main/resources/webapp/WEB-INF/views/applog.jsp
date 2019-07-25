<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ page session="true"%>
<!DOCTYPE html>
<html lang="en">
<head>
<meta charset="UTF-8">
<title>UOO Tool - Help</title>
<link href="./resources/css/bootstrap-4.3.1.min.css" rel="stylesheet" media="screen">
<link href="./resources/css/menu.css" rel="stylesheet" media="screen">
<link id="favicon" rel="shortcut icon" type="image/png" href="./resources/img/favicon.png" />
</head>
<body>
  <div class="container">
    <%@include file="../include/navbar.html" %>
    <span id="span-version" class="badge badge-info control-group"></span>
    <fieldset class="form-horizontal">
    <legend>Log Files</legend>
    <div class="control-group">
      <span class="help-block">Click on a log file to download its contents.</span>
    </div>
    <div class="control-group">
      <ol id="div-log-files"></ol>
    </div>
    </fieldset>
  </div>
  <script src="./resources/js/external/jquery-3.4.1.js"></script>
  <script src="./resources/js/external/bootstrap-4.3.1.min.js"></script>
  <script src="./resources/js/applog.js"></script>
</body>
</html>
