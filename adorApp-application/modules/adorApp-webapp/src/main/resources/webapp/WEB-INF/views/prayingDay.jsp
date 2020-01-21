<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ page session="true" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html>
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
<title>Örökimádás - Vác</title>
<link href="./../resources/css/bootstrap-4.3.1.min.css" rel="stylesheet" media="screen">
<link href="./../resources/css/menu.css" rel="stylesheet" media="screen">
<link href="./../resources/css/coverageBar.css" rel="stylesheet" media="screen">
<link id="favicon" rel="shortcut icon" type="image/png" href="./../resources/img/favicon.png" />
</head>
<body class="body">
    <style>
        body{
            align:center;
            text-align:center;
        }
    </style>
    <div class="container">
        <%@include file="../include/navbar.html" %>
        <input type="hidden" name="${_csrf.parameterName}" value="${_csrf.token}" />
        <br />
        <img alt="Örökimádás" src="./../resources/img/topimage3.jpg">

        <div class="centerdiv">
            <h3>Minden hónap második péntekén, imanapot tartunk,<br />amely 18:30-kor Szentmisével zárul.</h3>
            <h3>A Szentmise előtt 14:30-kor és 17:00-kor közös ima.</h3>
            <br/>
        </div>
        <img alt="Imanap" src="./../resources/img/imanap.jpg">

        <br />
        <hr />
        <a href="http://vacitemplom.piarista.hu/">Urgás vissza</a> a Szent Anna Piarista Templom oldalára.
    </div>
  <script src="./../resources/js/external/jquery-3.4.1.js"></script>
  <script src="./../resources/js/external/bootstrap-4.3.1.min.js"></script>
  <script src="./../resources/js/common.js"></script>
  <script src="./../resources/js/prayingDay.js"></script>
</body>
</html>
