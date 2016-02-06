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
    <title>Documents Overview</title>

	<script>
		function formSubmit() {
			document.getElementById("logoutForm").submit();
		}
	</script>

    <!-- Bootstrap -->
    <link href="resources/css/bootstrap.css" rel="stylesheet">
	<script src="resources/js/jquery-2.1.4.min.js"></script>
	<script src="resources/js/sockjs-1.0.3.min.js"></script>
	<script src="resources/js/jquery-2.1.4.min.js"></script>
	<script src="resources/js/stomp.min.js"></script>
	<script src="resources/js/WSConnect.js"></script>

  </head>
  <body>
  	<c:url value="/logout" var="logoutUrl" />
  	<form action="${logoutUrl}" method="post" id="logoutForm">
  		<sec:csrfInput/>
  	</form>
  	
  	<c:if test="${pageContext.request.userPrincipal.name != null}">
  		<h3>
  			Welcome: ${pageContext.request.userPrincipal.name} |
  				<a href="javascript:formSubmit()"> Logout</a>
  		</h3>
  	</c:if>
  	
	<sec:csrfMetaTags/>
	<button id="connect">WebSocket</button>
  	
    <div class="container">
    <section class="row">
    	<div class="col col-md-1">
    		<h2>ID</h2>
    	</div>
    	<div class="col col-md-4">
    		<h2>Document Name</h2>
    	</div>
    	<div class="col col-md-5">
    		<h2>Document Preview</h2>
    	</div>    	
    </section><!-- Ende row1 -->
    <section class="row">
    	<div class="col col-md-1">
    		<p>
    	</div>
    	<div class="col col-md-4">
    		<p>
    	</div>
    	<div class="col col-md-5">
    		<p>
    	</div>
    	<div class="col col-md-2">
    		<input type="submit" value="Show" class="btn btn-primary">
    	</div>
    </section><!-- Ende row2 -->    
    </div><!-- Ende Container -->    

  </body>
</html>