<%@include file="1_Top.jsp"%>
<html lang="en">
<head>
<%@include file="2_Head.jsp"%>
<title>Documents Overview</title>

<script type="text/javascript">

$(document).ready(function(){	
	//Function to delete a row in table with ID "docTable"
	$("[id^=delDoc]").click(function(){
		$(this).closest('tr').remove();
	});
})
//Function to delete a Document in database
function delDoc(docId){
	$.ajax({
		type: "GET",
		data: {"task": "deleteDocument",
			   "delDocId": docId},
		url: "/wsrlock/changeDoc",
		success: function(){
			console.log('Deleted document with ID: '+docId+' from database');
		},
	   	error: function (data, status, er) {
            console.log("error: " + JSON.stringify(data) + " \nstatus: " + status + "\ner: " + er);
        }
	});
};
//Function to add new entry in database and row in table with ID "docTable"
function addDoc(){	
	var docName = $("#docName").val();
	var docValue = $("#docValue").val();
	
	$.ajax({
		type: "GET",		
		data: {"task": "addDocument",
			   "addDocName": docName,
			   "addDocValue": docValue},
		url: "/wsrlock/changeDoc",
		success: function() {
			var newRowId = $('#docTable tr:last').prev().attr('');
			var html = "<tr><td>"+(newRowId+1)+"</td><td>"+docName+"</td><td>"+docValue+"</td><td>BUTTONS</td></tr>";
			$(html).insertBefore("#rowadd");
		},
	   	error: function (data, status, er) {
            console.log("error: " + JSON.stringify(data) + " \nstatus: " + status + "\ner: " + er);
        }
	});
};
</script>

</head>

<body>
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
									<td>${counter.count}</td>
									<td>${doc.docName}</td>
									<td>${doc.docValue}</td>
									<td>
										<a href="/docEdit?docId=${doc.docId}" type="button" value="Show" class="btn btn-primary" id="showDoc">Show</a> 
										<button onclick="delDoc(${doc.docId})" type="button" value="x" class="btn btn-danger" id="delDoc${doc.docId}">x</button>
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
					<sec:csrfInput/>
					
				</div>
			</div>
		</section>
		<!-- Ende row2 -->
	</div>
	<!-- Ende Container -->
	
</body>
</html>