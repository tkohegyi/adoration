<%@ page session="true"%>
<!DOCTYPE html>
<html lang="en">
<head>
<meta charset="UTF-8">
<title>UOO Tool - Summary</title>
<link href="./../resources/css/bootstrap-4.3.1.min.css" rel="stylesheet" media="screen">
<link href="./../resources/css/menu.css" rel="stylesheet" media="screen">
<link id="favicon" rel="shortcut icon" type="image/png" href="./../resources/img/favicon.png" />
</head>
<body>
  <div class="container">
    <%@include file="../include/navbar.html" %>
    <fieldset class="form-horizontal">
    <legend class="message-legend">List of People</legend>
    </fieldset>
    <fieldset class="form-horizontal">
    <div class="control-group">
      <span class="help-block">Click on a person to edit, click on its number to see related activities.</span>
    </div>
    <div class="control-group">
              <table class="table table-hover table-bordered">
                <thead>
                  <tr>
                    <th class="span1">#</th>
                    <th>Name</th>
                    <th>E-mail</th>
                  </tr>
                </thead>
                <tbody id="searchPeopleResult">
                </tbody>
              </table>
    </div>
   </fieldset>
  </div>
  <script src="./../resources/js/external/jquery-3.4.1.js"></script>
  <script src="./../resources/js/external/bootstrap-4.3.1.min.js"></script>
  <script src="./../resources/js/external/jquery.sumoselect.min.js"></script>
  <script src="./../resources/js/peopleManagement.js"></script>
</body>
</html>
