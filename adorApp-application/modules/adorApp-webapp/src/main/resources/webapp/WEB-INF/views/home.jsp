﻿<%@ page session="true" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
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
<link href="./../resources/css/bootstrap-4.3.1.min.css" rel="stylesheet" media="screen" charset="utf-8">
<link href="./../resources/css/menu.css" rel="stylesheet" media="screen" charset="utf-8">
<link href="./../resources/css/coverageBar.css" rel="stylesheet" media="screen" charset="utf-8">
<link id="favicon" rel="shortcut icon" type="image/png" href="./../resources/img/favicon.png" />
</head>
<body class="body">
  <div class="container">
    <%@include file="../include/navbar.html" %>
    <input type="hidden" name="${_csrf.parameterName}" value="${_csrf.token}" />

    <div class="centerwidediv" style="text-align: center">
        <br />
        <img alt="Örökimádás" src="./../resources/img/topimage3.jpg">
    </div>

        <fieldset class="form-horizontal">
            <legend class="message-legend h4"d>Az órák aktuális fedettsége</legend>
            <div class="control-group">
                <%@include file="../include/coverageBar.html" %>
            </div>
        </fieldset>

<div class="centerwidediv">
		<br />
		<table>
			<tr>
				<th>Jelmagyarázat:</th>
				<th></th>
			</tr>
			<tr>
				<td valign="top" align="right" class="veryBadCoverage">2 - Piros&nbsp;</td>
				<td>színűek azok az órák, amelyekben a jelentkezőkre leginkább szükség van. Ezért, ha teheted, jelentkezz a pirossal jelölt órák valamelyikére.</td>
			</tr>
			<tr>
				<td valign="middle" align="right" class="badCoverage">1 - Sárga&nbsp;</td>
				<td>színűek azok az órák, amelyekben az Örökimádás folyamatos, de nem megfelelően biztosított -
					ezekre az időpontokra is örömmel várunk még jelentkezőket.</td>
			</tr>
			<tr>
				<td valign="middle" align="right" class="goodCoverage">Zöld&nbsp;</td>
				<td>színűek azok az órák, amelyekben az Örökimádás folytonossága megfelelően biztosított. Természetesen ezekre az órákra is lehet még jelentkezni.</td>
			</tr>
			<tr>
				<td valign="middle" align="right" class="onlineAdorator">Kék keret&nbsp;&nbsp;</td>
				<td>-tel rendelkeznek azok az órák, amelyekben Online módon is biztosítva van az Örökimádás. Online adorálásra az ország bármely pontjáról lehet jelentkezni, illetve azon idősebb vagy beteg testvéreinknek is ezt a módot ajánljuk, akik személyesen nem tudják felkeresni a kápolnát.</td>
			</tr>
			<tr>
				<td valign="middle" align="right" class="lowPriorityColumn">Szürke&nbsp;&nbsp;</td>
				<td>színűek azok az órák, amelyekben a kápolnában ideiglenesen szünetel az Örökimádás. Ezeknél az óráknál csak Online adorálásra van lehetőség, így javasoljuk, hogy válassza azt.</td>
			</tr>
			<tr>
				<td valign="middle" align="right" style="background-color:#60ABF3">Kék&nbsp;&nbsp;</td>
				<td>színűek azok az órák, amelyekben a kápolnában ideiglenesen szünetel az Örökimádás, de van bejegyzett Online adoráló, aki távolról végzi az Örökimádást.</td>
			</tr>
		</table>
		<br />
	</div>

    <div class="centerwidediv">
    		<table>
    			<tr>
    				<th>Jelentkezni lehet:</th>
    				<th></th>
    			</tr>
    			<tr>
    				<td />
    				<td>- Közvetlenül, ezen az oldalon: <a href="/adoration/adorRegistration" target="_self">Jelentkezés örökimádásra</a>.</td>
    			</tr>
    			<tr>
    				<td />
    				<td>- A kápolnában elhelyezett sárga jelentkezési lapokon, kitöltés
    					után azt bedobva a piarista rendház postaládájába.</td>
    			</tr>
    			<tr>
    				<td />
    				<td>- E-mailben erre a címre írva: <a href="mailto:prhvac@gmail.com">prhvac@gmail.com</a></td>
    			</tr>
    			<tr>
    				<td />
    				<td>- A következő telefonszámok egyikén: <i>30-524-8291, 70-375-4140</i>.</td>
    			</tr>
    		</table>
    	</div>
    	<div class="centerwidediv">
    		<br />
    		<table>
    			<tr>
    				<th>Letölthető dokumentumok:</th>
    				<th></th>
    			</tr>
    			<tr>
    				<td />
    				<td>- <a href="/resources/img/BishopLetter-BeerM.pdf" target="new">dr. Beer Miklós püspök atya levele a regisztrált szentségimádókhoz</a></td>
    			</tr>
    			<tr>
    				<td />
    				<td>- <a href="/resources/img/AlapvetoSzabalyok.pdf" target="new">Alapvető szabályok szentségimádók számára</a></td>
    			</tr>
    			<tr>
    				<td />
    				<td>- <a href="/resources/img/AdatkezelesiSzabalyzat.pdf" target="new">Adatkezelési Szabályzat</a>.</td>
    			</tr>
    		</table>
    	</div>
    	<br />

    	<hr />
    	<a href="http://vacitemplom.piarista.hu/">Urgás vissza</a> a Szent Anna Piarista Templom oldalára.


  </div>
  <script src="./../resources/js/external/jquery-3.4.1.js"></script>
  <script src="./../resources/js/external/bootstrap-4.3.1.min.js"></script>
  <script src="./../resources/js/common.js"></script>
  <script src="./../resources/js/coverage.js"></script>
  <script src="./../resources/js/home.js"></script>
</body>
</html>
