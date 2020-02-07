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
  <div class="container">
    <%@include file="../include/navbar.html" %>
    <input type="hidden" name="${_csrf.parameterName}" value="${_csrf.token}" />

    <fieldset class="form-horizontal">
        <legend class="message-legend"d>Az órák aktuális fedettsége</legend>
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
				<td valign="top" align="right"><font color=#ff1919>2 - Piros&nbsp;</font></td>
				<td>színűek azok az órák, amelyekben a jelentkezőkre leginkább szükség van. Ezért, ha teheted, jelentkezz a pirossal jelölt órák valamelyikére.</td>
			</tr>
			<tr>
				<td valign="top" align="right"><font color=#ffff19>1 - Sárga&nbsp;</font></td>
				<td>színűek azok az órák, amelyekben az Örökimádás folyamatos, de nem megfelelően biztosított -
					ezekre az időpontokra is örömmel várunk még jelentkezőket.</td>
			</tr>
			<tr>
				<td valign="top" align="right"><font color=#19ff19>Zöld&nbsp;</font></td>
				<td>színűek azok az órák, amelyekben az Örökimádás folytonossága megfelelően biztosított. Természetesen ezekre az órákra is lehet még jelentkezni.</td>
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
    				<td>- Közvetlenül, ezen az oldalon: <a href="./orok-jelentkezes.php" target="_self">Jelentkezés örökimádásra</a>.</td>
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
    				<td>- <a href="./resources/PüspökiLevél.pdf" target="new">dr. Beer Miklós püspök atya levele a regisztrált szentségimádókhoz</a></td>
    			</tr>
    			<tr>
    				<td />
    				<td>- <a href="./resources/AlapvetőSzabályokSzentségimádókSzámára-Web.pdf" target="new">Alapvető szabályok szentségimádók számára</a></td>
    			</tr>
    			<tr>
    				<td />
    				<td>- <a href="./resources/AdatkezelésiSzabályzat.pdf" target="new">Adatkezelési Szabályzat</a>.</td>
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
  <script src="./../resources/js/home.js"></script>
</body>
</html>
