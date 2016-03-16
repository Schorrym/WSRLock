<%@include file="1_Top.jsp" %>
<html lang="en">
  <head>
    <%@include file="2_Head.jsp" %>
    <title>Authenticate me!</title>
    
    <!-- Custom styles for this template -->
    <link href="resources/css/signin.css" rel="stylesheet">    
	
  </head>
  <body> 
  	
   	<div class="container">

	  <c:url value="/login" var="loginUrl" />
      <form class="form-signin" name="loginForm" action="${loginUrl}" method="POST">
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