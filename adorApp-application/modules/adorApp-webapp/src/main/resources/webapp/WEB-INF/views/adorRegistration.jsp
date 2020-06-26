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
<title>Örökimádás - Vác - Jelentkezés Örökimádásra</title>
<link href="./../resources/css/bootstrap-4.3.1.min.css" rel="stylesheet" media="screen" charset="utf-8">
<link href="./../resources/css/menu.css" rel="stylesheet" media="screen" charset="utf-8">
<link href="./../resources/css/coverageBar.css" rel="stylesheet" media="screen" charset="utf-8">
<link id="favicon" rel="shortcut icon" type="image/png" href="./../resources/img/favicon.png" />
</head>
<body class="body">
  <div class="container">
    <%@include file="../include/navbar.html" %>
    <input type="hidden" name="${_csrf.parameterName}" value="${_csrf.token}" />

	<div class="centerwidediv" align="center">
        <legend class="message-legend h4"d>Jelentkezés Örökimádásra</legend>
		<p>
			Igen, válaszolok a hívásra! <br /> Jézussal akarok lenni, aki
			valóságosan jelen van a Legszentebb Oltáriszentségben, <br /> és
			minden héten egy órán keresztül imádni akarom Őt<br /> a váci Szent Anna
			Piarista Templomban.<br>
		</p>
	</div>
	<br />
		<div class="centerwidediv">
			<table class="jelentkezes">
				<tr>
					<td class="right">Név:&nbsp;</td>
					<td class="left"><input type="text" name="nev" value="">
					<span class="error"></span></td>
				</tr>
				<tr>
					<td class="right">E-mail:&nbsp;</td>
					<td class="left"><input type="text" name="email" value="">
					<span class="error"></span></td>
				</tr>
				<tr>
					<td class="right">Telefonszám:&nbsp;</td>
					<td class="left"><input type="text" name="telefon" value="">
					<span class="error"></span></td>
				</tr>
			</table>
			Válassza ki a napot és az órát! Segítségül megmutatjuk az aktuális
			órafedettséget az egész hétre:
			<p>

        <fieldset class="form-horizontal">
            <div class="control-group">
                <%@include file="../include/coverageBar.html" %>
            </div>
        </fieldset>

<br /> A zöld színnel jelölt órákat már legalább ketten vállalták,
			ezért amennyiben lehetséges, <br /> elsősorban a <font color=#ff1919>2
				- Pirossal </font>jelölt órák közül, vagy a <font color=#ffff19>1 -
				Sárgával </font>jelölt órák közül válasszon, <br />mert ezekre az
			időpontokra keresünk elsősorban szentségimádókat.<br /> Természetesen
			- ha a fenti pirossal vagy sárgával jelölt órák közül egyik sem felel
			meg, örömmel várjuk a zölddel jelöltekben is. <br /> <br />
			<table class="jelentkezes">
				<tr>
					<td class="right">Választott nap:&nbsp;</td>
					<td class="left"><select name="nap">
							<option name="nap" value="hetfo">Hétfő</option>
							<option name="nap" value="kedd">Kedd</option>
							<option name="nap" value="szerda">Szerda</option>
							<option name="nap" value="csutortok">Csütörtök</option>
							<option name="nap" value="pentek">Péntek</option>
							<option name="nap" value="szombat">Szombat</option>
							<option name="nap" value="vasarnap">Vasárnap</option>
					</select></td>
				</tr>
				<tr>
					<td class="right">Választott óra:&nbsp;</td>
					<td class="left"><select name="ora">
							<option name="ora" value="0">0</option>
							<option name="ora" value="1">1</option>
							<option name="ora" value="2">2</option>
							<option name="ora" value="3">3</option>
							<option name="ora" value="4">4</option>
							<option name="ora" value="5">5</option>
							<option name="ora" value="6">6</option>
							<option name="ora" value="7">7</option>
							<option name="ora" value="8">8</option>
							<option name="ora" value="9">9</option>
							<option name="ora" value="10">10</option>
							<option name="ora" value="11">11</option>
							<option name="ora" value="12">12</option>
							<option name="ora" value="13">13</option>
							<option name="ora" value="14">14</option>
							<option name="ora" value="15">15</option>
							<option name="ora" value="16">16</option>
							<option name="ora" value="17">17</option>
							<option name="ora" value="18">18</option>
							<option name="ora" value="19">19</option>
							<option name="ora" value="20">20</option>
							<option name="ora" value="21">21</option>
							<option name="ora" value="22">22</option>
							<option name="ora" value="23">23</option>
					</select></td>
				</tr>
				<tr>
					<td class="right">Egyéb megjegyzés:&nbsp;</td>
					<td class="left"><textarea name="comment" rows="5" cols="55"></textarea></td>
				</tr>
				<tr>
					<td class="right">Szervezőként is részt szeretne venni?&nbsp;</td>
					<td class="left"><select name="coordinate">
							<option name="coordinate" value="szervezo">Igen</option>
							<option name="coordinate" value="nemszervezo">Nem</option>
					</select></td>
				</tr>
				<tr>
					<td class="right">Hozzájárulás személyes adatok kezeléséhez:&nbsp;<br/><a href="./resources/AdatkezelesiSzabalyzat.pdf" target="new">Az Adatkezelési Szabályzat megtekinthető itt.&nbsp;</td>
					<td class="left"><select class="wideselect" name="accept">
							<option name="accept" value="hozzajarulok">Igen,
								hozzájárulok, összhangban az Európai Parlament és a Tanács (EU) 2016/679 rendeletével (GDPR) összhangban.</option>
							<option name="accept" value="nemjarulokhozza">Nem
								járulok hozzá, és így a regisztrált szentségimádásról is
								lemondok.</option>
					</select><span class="error"></td>
				</tr>
			</table>
			<br /> <input type="submit" name="nosubmit"	value=" Mégsem jelentkezem ">
			&nbsp;&nbsp;&nbsp;<input type="submit" name="submit" value=" Jelentkezés elküldése ">

  </div>
  <script src="./../resources/js/external/jquery-3.4.1.js"></script>
  <script src="./../resources/js/external/bootstrap-4.3.1.min.js"></script>
  <script src="./../resources/js/common.js"></script>
  <script src="./../resources/js/adorRegistration.js"></script>
</body>
</html>
