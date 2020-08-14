<%@ page session="true" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html>
<head>
<meta charset="UTF-8">
<meta name="_csrf" content="${_csrf.token}"/>
<meta name="_csrf_header" content="${_csrf.headerName}"/>
<meta http-equiv="content-type" content="text/html; charset=utf-8">
<META HTTP-EQUIV="PRAGMA" CONTENT="NO-CACHE">
<META HTTP-EQUIV="CACHE-CONTROL" CONTENT="NO-CACHE">
<meta name="viewport" content="width=device-width, initial-scale=1.0, maximum-scale=3.0, user-scalable=yes" />
<meta name="HandheldFriendly" content="true" />
<meta name="apple-mobile-web-app-capable" content="YES" />
<meta name="author" content="Tamas Kohegyi" />
<meta name="Description" content="Perpetual adoration in Hungary, Vác / Örökimádás a váci Szent Anna Piarista Templomban" />
<meta name="Keywords" content="örökimádás,vác,perpetual,adoration" />
<title>Örökimádás - Vác - Információk</title>
<link href="./../resources/css/bootstrap-4.3.1.min.css" rel="stylesheet" media="screen" charset="utf-8">
<link href="./../resources/css/menu.css" rel="stylesheet" media="screen" charset="utf-8">
<link id="favicon" rel="shortcut icon" type="image/png" href="./../resources/img/favicon.png" />
</head>
<body class="body">
<div class="container">
    <%@include file="../include/navbar.html" %>
    <input type="hidden" name="${_csrf.parameterName}" value="${_csrf.token}" />
	<div class="centerwidediv" align="center">
	    <br/>
        <legend class="message-legend h4"d>Információk</legend>
			<div id="name">...</div>
			<div id="status">...</div>
			<div id="adoratorId">...</div><br/>
	</div>
	<div class="centerwidediv" align="center">
        <legend class="message-legend h4"d>Vállalt órák</legend>
        <div id="noOfferedHours">Önnek nincs vállalt órája.</div>
		<p><table id="yesOfferedHours"><tbody></tbody></table></p>
	</div>
	<div class="centerwidediv" align="center">
        <legend class="message-legend h4"d>Szentségimádók most</legend>
        <div id="noAdoratorNow">Sajnos nincs megjeleníthető adat.</div>
		<p><table id="yesAdoratorNow"><tbody></tbody></table></p>
	</div>
	<div class="centerwidediv" align="center">
        <legend class="message-legend h4"d>Szentségimádók a következő órában</legend>
        <div id="noAdoratorNext">Sajnos nincs megjeleníthető adat.</div>
		<p><table id="yesAdoratorNext"><tbody></tbody></table></p>
	</div>
	<div class="centerwidediv" align="center">
        <legend class="message-legend h4"d>Napszak koordinátorok elérhetősége</legend>
        <div id="noLeadership">Sajnos nincs megjeleníthető adat.</div>
		<p><table id="yesLeadership"><tbody></tbody></table></p>
	</div>

	<!--
	<div class="centerwidediv" align="center">
        <legend class="message-legend h4"d>Letöltések</legend>
        <div id="noDownloads">Sajnos nincs megjeleníthető adat.</div>
		<p><table id="yesDownloads"><tbody></tbody></table></p>
	</div>
	-->

<script src="./../resources/js/external/jquery-3.4.1.js"></script>
<script src="./../resources/js/external/bootstrap-4.3.1.min.js"></script>
<script src="./../resources/js/common.js"></script>
<script src="./../resources/js/information.js"></script>
</body>
</html>
