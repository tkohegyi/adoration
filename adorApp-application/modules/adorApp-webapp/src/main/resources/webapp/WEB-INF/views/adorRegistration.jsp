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
    <div class="centerwidediv" align="center">
        <p>
        <table class="jelentkezes">
            <tr>
                <td class="right">Név:&nbsp;<font color="red">*</font></td>
                <td class="left"><input type="text" id="name" value="">
                <span id="nameError" class="error" style="display:none">Név megadása szükséges!</span></td>
            </tr>
            <tr>
                <td class="right">E-mail:&nbsp;<font color="red">*</font></td>
                <td class="left"><input type="text" id="email" value="">
                <span id="emailError" class="error" style="display:none">A megadott e-mail cím nem helyes!</span></td>
            </tr>
            <tr>
                <td class="right">Telefonszám:&nbsp;<font color="red">*</font></td>
                <td class="left"><input type="text" id="mobile" value="">
                <span id="mobileError" class="error" style="display:none">A megadott telefonszám nem helyes!</span></td>
            </tr>
        </table>
        </p>

        <b>Válassza ki a napot és az órát!</b> Segítségül megmutatjuk az aktuális órafedettséget az egész hétre:
        <p/>
        <fieldset class="form-horizontal">
            <div class="control-group">
                <%@include file="../include/coverageBar.html" %>
            </div>
        </fieldset>

        <br /> A zöld színnel jelölt órákat már legalább ketten vállalták, ezért amennyiben lehetséges,
        elsősorban a <font style="background-color:#fd726f">&nbsp;2 - Pirossal&nbsp;</font> jelölt órák közül,
        vagy az <font style="background-color:#fdf467">&nbsp;1 - Sárgával&nbsp;</font> jelölt órák közül válasszon,
        mert ezekre az időpontokra keresünk elsősorban szentségimádókat.<br />
        Természetesen - ha a fenti pirossal vagy sárgával jelölt órák közül egyik sem felel meg, örömmel várjuk a zölddel jelöltekben is. <br />
		A <font style="background-color:lightskyblue">&nbsp;kék&nbsp;</font> négyzetek azokat az órákat jelölik, amikor távolról, online adoráló van jelen.<br />
        <p/>
        <table class="jelentkezes">
            <tr>
                <td class="right">Választott nap:&nbsp;<font color="red">*</font></td>
                <td class="left"><select id="daySelect">
                        <option id="daySelect-1" value="1">Hétfő</option>
                        <option id="daySelect-2" value="2">Kedd</option>
                        <option id="daySelect-3" value="3">Szerda</option>
                        <option id="daySelect-4" value="4">Csütörtök</option>
                        <option id="daySelect-5" value="5">Péntek</option>
                        <option id="daySelect-6" value="6">Szombat</option>
                        <option id="daySelect-0" value="0">Vasárnap</option>
                </select></td>
            </tr>
            <tr>
                <td class="right">Választott óra:&nbsp;<font color="red">*</font></td>
                <td class="left"><select id="hourSelect">
                        <option id="hourSelect-0" value="0">0</option>
                        <option id="hourSelect-1" value="1">1</option>
                        <option id="hourSelect-2" value="2">2</option>
                        <option id="hourSelect-3" value="3">3</option>
                        <option id="hourSelect-4" value="4">4</option>
                        <option id="hourSelect-5" value="5">5</option>
                        <option id="hourSelect-6" value="6">6</option>
                        <option id="hourSelect-7" value="7">7</option>
                        <option id="hourSelect-8" value="8">8</option>
                        <option id="hourSelect-9" value="9">9</option>
                        <option id="hourSelect-10" value="10">10</option>
                        <option id="hourSelect-11" value="11">11</option>
                        <option id="hourSelect-12" value="12">12</option>
                        <option id="hourSelect-13" value="13">13</option>
                        <option id="hourSelect-14" value="14">14</option>
                        <option id="hourSelect-15" value="15">15</option>
                        <option id="hourSelect-16" value="16">16</option>
                        <option id="hourSelect-17" value="17">17</option>
                        <option id="hourSelect-18" value="18">18</option>
                        <option id="hourSelect-19" value="19">19</option>
                        <option id="hourSelect-20" value="20">20</option>
                        <option id="hourSelect-21" value="21">21</option>
                        <option id="hourSelect-22" value="22">22</option>
                        <option id="hourSelect-23" value="23">23</option>
                </select></td>
            </tr>
            <tr>
                <td class="right">Adorálás módja:&nbsp;<font color="red">*</font></td>
                <td class="left"><select id="method">
                        <option id="method-1" value="1">Heti rendszerességgel, az Vácott, az Örökimádás kápolnából</option>
                        <option id="method-2" value="2">Heti rendszerességgel, távolról, online módon</option>
                        <option id="method-3" value="3">Alkalmanként, nem rendszeresen</option>
                </select></td>
            </tr>
            <tr>
                <td class="right">Egyéb megjegyzés:&nbsp;</td>
                <td class="left"><textarea id="comment" rows="5" cols="55"></textarea></td>
            </tr>
            <tr>
                <td class="right">Szervezőként is részt szeretne venni?&nbsp;<font color="red">*</font></td>
                <td class="left"><select id="coordinate">
                        <option id="coordinate-yes" value="szervezo">Igen</option>
                        <option id="coordinate-no" value="nemszervezo" selected>Nem</option>
                </select></td>
            </tr
            <p height=5px />
            <tr>
                <td class="right">Hozzájárulás személyes adatok kezeléséhez:&nbsp;<font color="red">*</font></td>
                <td class="left">
                        <span id="dhcError" class="error" style="display:none">Adatkezelési hozzájárulás nélkül a jelentkezést nem tudjuk elfogadni!</span>
                        <br/>
                        <select class="wideselect" id="dhc">
                        <option id="consent-yes" value="consent-yes">Igen,
                            hozzájárulok, az Európai Parlament és a Tanács (EU) 2016/679 rendeletével (GDPR) összhangban.</option>
                        <option id="consent-no" value="consent-no" selected>Nem
                            járulok hozzá, és így a regisztrált szentségimádásról is lemondok.</option>
                </select><br/><a href="/resources/img/AdatkezelesiSzabalyzat.pdf" target="new">Az Adatkezelési Szabályzat megtekinthető itt.&nbsp;
                </td>
            </tr>
        </table>
        <br />
        <button type="button" class="btn btn-warning btn-sm" onclick="notRegisterClick()">Mégsem jelentkezem</button>
        &nbsp;&nbsp;&nbsp;
        <button id="registerButton" type="button" class="btn btn-success btn-sm" onclick="doRegisterClick()">Jelentkezés elküldése</button>
        <br />
        <p/>
</div>
<script src="./../resources/js/external/jquery-3.4.1.js"></script>
<script src="./../resources/js/external/bootstrap-4.3.1.min.js"></script>
<script src="./../resources/js/common.js"></script>
<script src="./../resources/js/coverage.js"></script>
<script src="./../resources/js/adorRegistration.js"></script>
</body>
</html>
