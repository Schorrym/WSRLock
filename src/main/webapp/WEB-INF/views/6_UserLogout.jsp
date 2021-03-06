<!-- This will show the Users name if available and the logout function -->
<c:url value="/logout" var="logoutUrl" />
<!-- This will redirect to springs security config "logout" url config see "WebSocketSecurityConfig.class" -->
<form action="${logoutUrl}" method="post" id="logoutForm">
	<!-- Spring Security Cross-Site-Request Schutz -->
	<sec:csrfInput />
</form>
<c:if test="${pageContext.request.userPrincipal.name != null}">
	<h3>
		Welcome: ${pageContext.request.userPrincipal.name} | 
		<a onclick="disconnect()" href="javascript:formSubmit()"> Logout</a>
	</h3>
</c:if>

<!-- This will add Meta Tags for Spring Security CSRF-Protection -->
<sec:csrfMetaTags/>