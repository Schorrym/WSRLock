<%@ page language="java" contentType="text/html; charset=ISO-8859-1"
    pageEncoding="ISO-8859-1"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="sec" uri="http://www.springframework.org/security/tags" %>
<!DOCTYPE html>
<html lang="en">
  <head>
    <meta charset="utf-8">
    <meta http-equiv="X-UA-Compatible" content="IE=edge">
    <meta name="viewport" content="width=device-width, initial-scale=1">
    <!-- The above 3 meta tags *must* come first in the head; any other head content must come *after* these tags -->
    <title>Authenticate me!</title>

    <!-- Bootstrap -->
    <link href="resources/css/bootstrap.min.css" rel="stylesheet">
    <!-- Custom styles for this template -->
    <link href="resources/css/signin.css" rel="stylesheet">
    <!-- JavaScript templates 
    <script src="resources/js/sockjs-1.0.3.min.js"></script>
	<script src="resources/js/jquery-2.1.4.min.js"></script>
	<script src="resources/js/stomp.min.js"></script>
	<script src="resources/js/WSConnect.js"></script> -->

  </head>
  <body> 
  	
   	<div class="container">

	  <c:url value="/login" var="loginUrl" />
      <form class="form-signin" name="loginForm" 
      		action="${loginUrl}" method="POST">
      	<h4>
      		<c:if test="${param.error != null}">
				Invalid username and password.
			</c:if>
			<c:if test="${param.logout != null}">
				You have been logged out.
			</c:if>
      	</h4>
        <h2 class="form-signin-heading">Please sign in</h2>
        <label for="username" class="sr-only">Email address</label>
        <input type="text" name="username" id="username" class="form-control" placeholder="Username" required autofocus>
        <label for="password" class="sr-only">Password</label>
        <input type="password" name="password" id="inputPassword" class="form-control" placeholder="Password" required>
        <sec:csrfInput/>
        <button class="btn btn-lg btn-primary btn-block" type="submit" id="connect" name="submit">Sign in</button>
      </form>

    </div> <!-- /container -->    

  </body>
</html>