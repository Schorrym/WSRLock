<nav class="navbar navbar-default">
	<div class="container-fluid">
		<!-- Brand and toggle get grouped for better mobile display -->
		<div class="navbar-header">			
			<c:if test="${fn:contains(pageContext.request.requestURI, '/readdoc.jsp') }">			
				<a onclick="saveDoc()" type="button" class="navbar-brand" id="save" style="display: none">Save</a>
				<!-- href="<c:url value="/start" />"  -->
				<a onclick="disconnect()" href="<c:url value="/start" />" type="button" class="navbar-brand" id="exit">Exit</a>			
			</c:if>			
		</div> 
			<c:if test="${fn:contains(pageContext.request.requestURI, '/readdoc.jsp') }">
				<ul class="nav navbar-nav">
					<li><a onclick="editDoc()" id="editButton">Edit</a></li>
					<li><p class="navbar-text" id="status">reading</p></li>
				</ul>
			</c:if>
			<ul class="nav navbar-nav navbar-right">
				<li class="logout">
					<%@include file="6_UserLogout.jsp"%>
				</li>
			</ul>
		<!-- /.navbar-collapse -->
	</div>
	<!-- /.container-fluid -->
</nav>