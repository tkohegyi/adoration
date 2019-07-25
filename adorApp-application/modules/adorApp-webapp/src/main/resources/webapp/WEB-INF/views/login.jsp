<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ page session="false"%>
<!DOCTYPE html>
<html lang="en">
<head>
<meta charset="UTF-8">
<title>Perpetual Adoration Tool - Login Page</title>
<style>
.error {
	padding: 15px;
	margin-bottom: 20px;
	border: 1px solid transparent;
	border-radius: 4px;
	color: #a94442;
	background-color: #f2dede;
	border-color: #ebccd1;
}

.msg {
	padding: 15px;
	margin-bottom: 20px;
	border: 1px solid transparent;
	border-radius: 4px;
	color: #31708f;
	background-color: #d9edf7;
	border-color: #bce8f1;
}

#login-box {
	width: 800px;
	padding: 20px;
	margin: 100px auto;
	background: #fff;
	-webkit-border-radius: 2px;
	-moz-border-radius: 2px;
	border: 1px solid #000;
}
</style>
<link href="./resources/css/bootstrap-4.3.1.min.css" rel="stylesheet" media="screen">
<link href="./resources/css/menu.css" rel="stylesheet" media="screen">
<link id="favicon" rel="shortcut icon" type="image/png" href="./resources/img/favicon.png" />
</head>
<body onload='document.loginForm.username.focus();'>

  <div class="container">
    <fieldset class="form-horizontal" style="height: 0px">
    <legend class="message-legend" style="height: 64px; text-align:center; color:#39c2d7; padding: 6px; font-family: Oswald">Tool for Perpetual Adoration. <b>This is an experimental proof-of-concept.</b> Do <b>not</b> expect too much from this tool...</legend>
    <div align="middle" id="serverInfo"/>
    </fieldset>
    <fieldset class="form-horizontal" style="background-color: #373737; height: 50px">
	<div id="login-box">

		<h2>Please log in with your e-mail address...</h2>

		<c:if test="${not empty error}">
			<div class="error">${error}</div>
		</c:if>
		<c:if test="${not empty msg}">
			<div class="msg">${msg}</div>
		</c:if>

		<form name='loginForm'
		  action="<c:url value='j_spring_security_check' />" method='POST'>
		  <table width="100%">
			<tr>
				<td>Your e-mail address:</td>
				<td><input class="customField" type='text' name='username' value=''></td>
			</tr>
			<tr>
				<td>Your password (will be ignored, do not fill it):</td>
				<td><input class="customField" disabled type='password' name='password' /></td>
			</tr>
			<tr>
				<td colspan='2' style="text-align: center"><input class="btn btn-primary" name="submit" type="submit" value="Login" /></td>
			</tr>
		  </table>
		  <input type="hidden" name="${_csrf.parameterName}" value="${_csrf.token}" />
		</form>
	</div>
   </fieldset>
  </div>
  <script src="./../resources/js/external/jquery-3.4.1.js"></script>
  <script src="./../resources/js/external/bootstrap-4.3.1.min.js"></script>
  <script src="./../resources/js/login.js"></script>
</body>
</html>