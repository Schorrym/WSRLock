//Function to delete a Document in database
//function delDoc(docId){
//	$.ajax({
//		type: "GET",
//		data: {"task": "deleteDocument",
//			   "delDocId": docId},
//		url: "/wsrlock/changeDoc",
//		success: function(){
//			console.log('Deleted document with ID: '+docId+' from database');
//			$("#delDocId"+docId).closest('tr').remove();
//		},
//	   	error: function (data, status, er) {
//            console.log("error: " + JSON.stringify(data) + " \nstatus: " + status + "\ner: " + er);
//        }
//	});
//};
//Function to add new entry in database and row in table with ID "docTable"
//function addDoc(){
	
//	var docName = $("#docName").val();
//	var docValue = $("#docValue").val();
	
//	$.ajax({
//		type: "GET",
//		dataType: "json",
//		data: {"task": "addDocument",
//			   "addDocName": docName,
//			   "addDocValue": docValue},
//		url: "/wsrlock/changeDoc",
//		success: function(data) {
//			//Create Show Button			
//			var showButton = $("<a/>",{
//				href: '<c:url value="/readDoc?docId='+data.docId+'"/>',
//				type: "button",
//				id: "showDoc"
//			});
//			$(showButton).addClass("btn btn-primary").html("Show");
//			
//			//Create delete Button 
//			var delButton = $("<a/>",{
//				onclick: "delDoc("+data.docId+")",
//				type: "button",
//				id: "delDocId"+data.docId
//			});
//			$(delButton).addClass("btn btn-danger").html("x");
//			
//			//Create Row and Columns and add Button
//			var tr = $("<tr/>").insertBefore("#rowadd");
//			var td1 = $("<td/>").append(data.docId).appendTo(tr);
//			var td2 = $("<td/>").append(data.docName).appendTo(tr);
//			var td3 = $("<td/>").append(data.docValue).appendTo(tr);
//			var td4 = $("<td/>").append(showButton).append(" ").append(delButton).appendTo(tr);
//			
//			//Clear Input Values
//			$("#docName").val("");
//			$("#docValue").val("");
//		},
//	   	error: function (data, status, er) {
//            console.log("error: " + JSON.stringify(data) + " \nstatus: " + status + "\ner: " + er);
//        }
//	});
//};