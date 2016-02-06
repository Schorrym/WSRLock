<%@ page language="java" contentType="text/html; charset=ISO-8859-1"
    pageEncoding="ISO-8859-1"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
	<title>Hello Spring WebSocket</title>
	<script src="resources/js/sockjs-1.0.3.js"></script>
	<script src="resources/js/jquery-2.1.4.min.js"></script>
	<script src="resources/js/stomp.js"></script>
	<script src="resources/js/WSConnect.js"></script>
</head>
<body>
<noscript><h2 style="color: #ff0000">Seems your browser doesn't support Javascript! Websocket relies on Javascript being enabled. Please enable
    Javascript and reload this page!</h2></noscript>
<div>
   <button id="connect" >Connect to WebSocket</button>
   <button id="disconnect" >Disconnect WebSocket</button>
</div>
</body>
</html>