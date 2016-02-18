<%@include file="1_Top.jsp"%>
<html lang="en">
<head>
<%@include file="2_Head.jsp"%>
<title>Documents Overview</title>

<script type="text/javascript">
$(document).ready(function(){
	$("[id^=delDoc]").click(function(){
		$(this).closest('tr').remove();
	});
	
})
</script>

</head>

<body>
	<div class="container">
		<%@include file="5_Navbar_Start.jsp"%>
		<section class="row">
			<div class="col-sm-12 col-md-12">
				<div class="table-responsive">
				<c:url value="/addDoc" var="addDocUrl" />
				<form class="form-add-doc" name="addDocForm" action="${addDocUrl}" method="POST">
					<table class="table table-striped" id="docTable">
						<thead>
							<tr>
								<th>#</th>
								<th>Document name</th>
								<th>Document preview</th>
								<th></th>
							</tr>
						</thead>
						<tbody>
							<c:forEach var="doc" items="${documents}">
								<tr id="row-${doc.docId}">
									<td>${doc.docId}</td>
									<td>${doc.docName}</td>
									<td>${doc.docValue}</td>
									<td>
										<a href="/docEdit?docId=${doc.docId}" type="button" value="Show" class="btn btn-primary" id="showDoc">Show</a> 
										<button type="button" value="x" class="btn btn-danger" id="delDoc${doc.docId}">x</button>
									</td>
								</tr>
							</c:forEach>
								<tr>
									<td></td>
									<td>
										<input type="text" name="docName" id="docName" class="form-control" placeholder="New document name">
									</td>
									<td>
										<input type="text" name="docValue" id="docValue" class="form-control" placeholder="Put a value here..">
									</td>
									<td>
										<button class="btn btn-primary" type="submit" id="addDoc" name="addDoc">+</button>
									</td>
								</tr>							
						</tbody>
					</table>
					<sec:csrfInput/>
					</form>
				</div>
			</div>
		</section>
		<!-- Ende row2 -->
	</div>
	<!-- Ende Container -->
	
</body>
</html>