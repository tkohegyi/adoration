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
<script src="/resources/js/external/jquery-3.4.1.js"></script>
<script src="/resources/js/external/bootstrap-4.3.1.min.js"></script>
<script src="/resources/js/common.js"></script>
<script src="/resources/js/infoGuest.js"></script>
<title>Örökimádás - Vác - Személyes Információk</title>
<link href="/resources/css/bootstrap-4.3.1.min.css" rel="stylesheet" media="screen" charset="utf-8">
<link href="/resources/css/menu.css" rel="stylesheet" media="screen" charset="utf-8">
<link id="favicon" rel="shortcut icon" type="image/png" href="/resources/img/favicon.png" />
</head>
<body class="body">
<div class="container">
    <%@include file="../include/navbar.html" %>
    <input type="hidden" name="${_csrf.parameterName}" value="${_csrf.token}" />
	<div class="centerwidediv" align="center">
	    <br/>
        <legend class="message-legend h4"d>Információk</legend>
        	<div class="centerwidediv" align="center">
                <div id="noGoogle"></div>
                <div id="yesGoogle"><b>Az Önhöz rendelt Google fiók adatai:</b>
                			<div id="nameGoogle">...</div>
                			<div id="emailGoogle">...</div>
                </div>
        	</div>
            <div class="centerwidediv" align="center">
                <div id="noFacebook"></div>
                <div id="yesFacebook"><b>Az Önhöz rendelt Facebook fiók adatai:</b>
                			<div id="nameFacebook">...</div>
                			<div id="emailFacebook">...</div>
                </div>
            </div>
            <div id="socialServiceUsed">...</div>
            <div id="status">...</div>
	</div><br/><div/>
	<div class="centerwidediv" align="center">
        <legend class="message-legend h4"d>Fő koordinátorok elérhetősége</legend>
        <div id="noLeadership">Sajnos nincs megjeleníthető adat.</div>
		<p><table id="yesLeadership"><tbody></tbody></table></p>
	</div>

	<div class="centerwidediv" align="center">
        <div class="container" style="padding:5px" align="center"><button id="message-button" type="button" class="btn btn-primary" data-toggle="modal" data-target="#sendMessageModal" onclick="msgClick()">Üzenet küldése a koordinátornak...</button></div>
	</div>

    <!-- Modal Send Message -->
    <div class="modal fade" id="sendMessageModal" tabindex="-1" role="dialog" aria-labelledby="sendMessageCenterTitle" aria-hidden="true">
      <div class="modal-dialog modal-dialog-centered modal-xl" role="document">
        <div class="modal-content">
          <div class="modal-header">
            <h5 class="modal-title" id="sendMessageCenterTitle">Üzenetküldés</h5>
            <button type="button" class="close" data-dismiss="modal" aria-label="Cancel">
              <span aria-hidden="true">&times;</span>
            </button>
          </div>
          <div class="modal-body">
                <form>
                    <input type="hidden" id="socialId" value=""/>
                    <table style="width:100%">
                        <tr class="infoTable"><td class="evenInfo" valign="top" align="right" style="width:20%">Az Ön elérhetősége (telefonszám vagy e-mail): </td><td class="oddInfo"><input type="text" id="emailOrPhone" name="emailOrPhone" style="width:100%" value=""></td></tr>
                        <tr class="infoTable"><td class="evenInfo" valign="top" align="right">Üzenet:</td><td class="oddInfo"><textarea id="messageContent" cols="55" rows="15" style="width:100%"></textarea></td></tr>
                    </table>
                </form>
          </div>
          <div class="modal-footer">
            <table width="100%"><tr>
                <td align="left">
                    <button id="cancelButton" type="button" class="btn btn-info" data-dismiss="modal">Mégsem</button>
                </td>
                <td align="right">
                    <button id="sendButton" type="button" class="btn btn-success" onclick="sendMessage()">Üzenet küldése</button>
                </td>
            </tr></table>
          </div>
        </div>
      </div>
    </div>


    <fieldset class="form-horizontal" id="downloads">
	<div class="centerwidediv" align="center">
	</div>
    </fieldset>

    <hr />

</body>
</html>
