<nav class="navbar navbar-default">
	<div class="container-fluid">
		<!-- Brand and toggle get grouped for better mobile display -->
		<div class="navbar-header">
			<button type="button" class="navbar-toggle collapsed"
				data-toggle="collapse" data-target="#bs-example-navbar-collapse-1"
				aria-expanded="false">
				<span class="sr-only">Toggle navigation</span> <span
					class="icon-bar"></span>
			</button>
			<p class="navbar-brand"></p>
		</div>

		<!-- Collect the nav links, forms, and other content for toggling -->
		<div class="collapse navbar-collapse"
			id="bs-example-navbar-collapse-1">
			<ul class="nav navbar-nav">
				<li><h3>
						<a onclick="editDoc()" type="button" class="btn btn-default" 
							id="edit">edit</a>
					</h3></li>
				<li><h3>
						<!-- href="<c:url value="/start" />"  -->
						<a onclick="leaveDoc()" href="<c:url value="/start" />" type="button" class="btn btn-default" id="overview">leave
							document</a>
					</h3></li>
				<li><h3>
						<a href="#" type="button" class="btn btn-default" id="members">members</a>
					</h3></li>
				<li><h3>
						<span class="label label-success" id="status">Status</span>
					</h3></li>
			</ul>
			<ul class="nav navbar-nav navbar-right">
				<li class="logout">
					<%@include file="6_UserLogout.jsp"%>
				</li>
			</ul>
		</div>
		<!-- /.navbar-collapse -->
	</div>
	<!-- /.container-fluid -->
</nav>