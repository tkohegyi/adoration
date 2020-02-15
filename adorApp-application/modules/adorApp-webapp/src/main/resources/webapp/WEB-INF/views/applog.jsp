<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ page session="true" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html lang="en">
<head>
<meta http-equiv="content-type" content="text/html; charset=utf-8">
<META HTTP-EQUIV="PRAGMA" CONTENT="NO-CACHE">
<META HTTP-EQUIV="CACHE-CONTROL" CONTENT="NO-CACHE">
<meta name="viewport" content="width=device-width, initial-scale=1.0, maximum-scale=3.0, user-scalable=yes" />
<meta name="HandheldFriendly" content="true" />
<meta name="apple-mobile-web-app-capable" content="YES" />
<meta name="author" content="Tamas Kohegyi" />
<meta name="Description" content="Perpetual adoration in Hungary, Vác / Örökimádás a váci Szent Anna Piarista Templomban" />
<meta name="Keywords" content="örökimádás,vác,perpetual,adoration" />
<title>Örökimádás - Vác - Adminisztráció</title>
<link href="/resources/css/bootstrap-4.3.1.min.css" rel="stylesheet" media="screen">
<link href="/resources/js/external/dataTables/datatables.min.css" rel="stylesheet" type="text/css"/>
<link href="/resources/css/menu.css" rel="stylesheet" media="screen">
<link id="favicon" rel="shortcut icon" type="image/png" href="/resources/img/favicon.png" />
</head>
<body class="body">
  <div class="container">
    <%@include file="../include/navbar.html" %>
    <input type="hidden" name="${_csrf.parameterName}" value="${_csrf.token}" />
    <span id="span-version" class="badge badge-info control-group"></span>
    <fieldset class="form-horizontal">
        <legend>Adorator Table</legend>
        <div class="control-group">
            <table id="person" class="table table-striped table-bordered table-hover compact cell-border" style="width:100%">
                    <thead>
                        <tr>
                            <th>ID</th>
                            <th>Név</th>
                            <th>Adoráló Státusz</th>
                            <th>Web Státusz</th>
                            <th>Telefonszám</th>
                            <th>Telefonszám látható?</th>
                            <th>e-mail</th>
                            <th>e-mail látható?</th>
                            <th>Adminisztrátor megjegyzés</th>
                            <th>Adatkezelési Hozzájárulás</th>
                            <th>Adatk. hozzájárulás dátuma</th>
                            <th>Koordinátor megjegyzés</th>
                            <th>Közös/Látható megjegyzés</th>
                            <th>Nyelvkód</th>
                        </tr>
                    </thead>
                    <tfoot>
                        <tr>
                            <th>ID</th>
                            <th>Név</th>
                            <th>Adoráló Státusz</th>
                            <th>Web Státusz</th>
                            <th>Telefonszám</th>
                            <th>Telefonszám látható?</th>
                            <th>e-mail</th>
                            <th>e-mail látható?</th>
                            <th>Adminisztrátor megjegyzés</th>
                            <th>Adatkezelési Hozzájárulás</th>
                            <th>Adatk. hozzájárulás dátuma</th>
                            <th>Koordinátor megjegyzés</th>
                            <th>Közös/Látható megjegyzés</th>
                            <th>Nyelvkód</th>
                        </tr>
                    </tfoot>
                </table>
            </div>
    </fieldset>


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
  <script src="/resources/js/external/jquery-3.4.1.js"></script>
  <script src="/resources/js/external/bootstrap-4.3.1.min.js"></script>
  <script src="/resources/js/external/dataTables/datatables.min.js"></script>
  <script src="/resources/js/common.js"></script>
  <script src="/resources/js/applog.js"></script>
</body>
</html>
