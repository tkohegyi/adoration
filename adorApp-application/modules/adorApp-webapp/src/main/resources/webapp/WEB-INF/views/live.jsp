<%@ page session="true" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html lang="en">
<head>
<meta charset="UTF-8">
<meta name="_csrf" content="${_csrf.token}"/>
<meta name="_csrf_header" content="${_csrf.headerName}"/>
<meta http-equiv="content-type" content="text/html; charset=utf-8">
<META HTTP-EQUIV="PRAGMA" CONTENT="NO-CACHE">
<META HTTP-EQUIV="CACHE-CONTROL" CONTENT="NO-CACHE">
<meta name="viewport" content="width=device-width, initial-scale=1.0, maximum-scale=3.0, user-scalable=yes" />
<META http-equiv="refresh" content="3;URL=https://orok-imadas-vac-szent-anna-piarista-templom.click2stream.com/">
<meta name="HandheldFriendly" content="true" />
<meta name="apple-mobile-web-app-capable" content="YES" />
<meta name="author" content="Tamas Kohegyi" />
<meta name="Description" content="Perpetual adoration in Hungary, Vác / Örökimádás a váci Szent Anna Piarista Templomban" />
<meta name="Keywords" content="örökimádás,vác,perpetual,adoration" />
<script src="/resources/js/external/jquery-3.4.1.js"></script>
<script src="/resources/js/external/bootstrap-4.3.1.min.js"></script>
<script src="/resources/js/common.js"></script>
<script src="/resources/js/live.js" nonce></script>
<title>Örökimádás - Vác - Élő Közvetítés</title>
<link href="/resources/css/bootstrap-4.3.1.min.css" rel="stylesheet" media="screen">
<link href="/resources/css/menu.css" rel="stylesheet" media="screen">
<link id="favicon" rel="shortcut icon" type="image/png" href="/resources/img/favicon.png" />
</head>
<body class="body">
  <div class="container">
    <%@include file="../include/navbar.html" %>
    <input type="hidden" name="${_csrf.parameterName}" value="${_csrf.token}" />

    <fieldset class="form-horizontal">
        <legend>Élő közvetítés:</legend>
        <h3><a href="https://orok-imadas-vac-szent-anna-piarista-templom.click2stream.com/">Urgás az Örökimádás Webkamerájához</a></h3>
        <!-- iframe width="560" height="315" src="https://www.youtube.com/embed/Iy5nd-jqIzs" frameborder="0" allow="accelerometer; autoplay; encrypted-media; gyroscope; picture-in-picture" allowfullscreen></iframe -->
    </fieldset>

  </div>
</body>
</html>
