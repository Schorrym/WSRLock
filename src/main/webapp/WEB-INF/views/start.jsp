<%@include file="1_Top.jsp"%>
<html lang="en">
<head>
<%@include file="2_Head.jsp"%>

<script src="resources/js/custom_ajax.js"></script>

<title>Documents Overview</title>

</head>

<body onload="">
	
	<div class="container">
		<%@include file="5_Navbar_Start.jsp"%>
		<section class="row">
			<div class="col-sm-12 col-md-12">
				<div class="table-responsive">
				<c:url value="/addDoc" var="addDocUrl" />
				<!-- <form class="form-add-doc" name="addDocForm" action="${addDocUrl}" method="POST"> -->
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
							<c:forEach var="doc" items="${documents}" varStatus="counter">
								<tr id="row-${doc.docId}">
									<td>${doc.docId}</td>
									<td>${doc.docName}</td>
									<td>${doc.docValue}</td>
									<td>
										<a onclick="showDoc()" href="<c:url value="/readDoc?docId=${doc.docId}"/>" type="button" class="btn btn-primary" id="showDoc">Show</a>
										<a onclick="delDoc(${doc.docId})" type="button" class="btn btn-danger" id="delDocId${doc.docId}">x</a>
									</td>
								</tr>
							</c:forEach>
								<tr id="rowadd">
									<td></td>
									<td>
										<input type="text" name="docName" id="docName" class="form-control" placeholder="New document name">
									</td>
									<td>
										<input type="text" name="docValue" id="docValue" class="form-control" placeholder="Put a value here..">
									</td>
									<td>
										<button onclick="addDoc()" class="btn btn-primary" type="button" id="addDoc1" name="addDoc">+</button>
									</td>
								</tr>							
						</tbody>
					</table>					
				</div>
			</div>
		</section>
		<!-- Ende row2 -->
	</div>
	<!-- Ende Container -->
	<input type="hidden" id="pageName" value="start">
</body>
<%@include file="3_HeadCon.jsp" %>
</html>