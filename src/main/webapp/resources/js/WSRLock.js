//Url pointing to the STOMP Endpoint of the Server
var url = 'http://' + window.location.host + '/wsrlock/readDocument';
//New SockJS Connection is opened (Local) by using the sockjs.js Client-Library
var sock = new SockJS(url);

//Create namespace for Connection
var conData = {};
//Defining that STOMP will be used with the SockJS Protocol as fallback options
conData.client = Stomp.over(sock);

//Get the CSRF attributes from the initial JSP page
var csrfHeader = $("meta[name='_csrf_header']").attr("content");
var csrfToken = $("meta[name='_csrf']").attr("content");

//Setting the Header for connecting to the STOMP MessageBroker
var headers = {
      login: 'guest',
      passcode: 'guest',
};
headers[csrfHeader] = csrfToken;

//Connect with the above given credentials
conData.client.connect(headers, function(frame){
	var pageName = $("#pageName").val();
	var httpSession = $("#sessionId").val();
	if(pageName == "start"){		
		conData.delDocSub = conData.client.subscribe('/topic/delDoc', handleDelDocSubscribeIncome, { id: httpSession});
		conData.addDocSub = conData.client.subscribe('/topic/addDoc', handleAddDocSubscribeIncome, { id: httpSession});
	}else if(pageName == "readdoc"){
		var currDocId = $("#docId").val();
		conData.docSub = conData.client.subscribe('/topic/doc'+currDocId, handleEditDocSubscribeIncome, { id: httpSession});
	}
});

//STOMP disconnect
function disconnect(){
	conData.client.disconnect(function(){
		console.log('STOMP disconnected');
	});
};

function saveDoc(){
	var docId = $("#docId").val();
	var docValue = $("#docContent").val();
	var savedDoc = JSON.stringify({'docId': docId,
		  						   'docValue': docValue
		  			});
	conData.client.send("/app/saveDoc", {}, savedDoc);
};

//When a request for Document editing is placed (by clicking the edit button)
function editDoc(){
	conData.docSub.unsubscribe();
	conData.lockSub = conData.client.subscribe('/user/queue/lockSuccess', handleLockSuccessSubscribeIncome)
	conData.saveSub = conData.client.subscribe('/user/queue/saveSuccess', handleSaveSuccessSubscribeIncome)
	
	var currDocId = $("#docId").val();
	var editDoc = JSON.stringify({'docId': currDocId});
	conData.client.send("/app/editDoc", {}, editDoc);
};

function handleSaveSuccessSubscribeIncome(incoming){
	conData.lockSub.unsubscribe();
	conData.saveSub.unsubscribe();
	$("#docContent").prop("disabled", true);
	$("#editButton").show();
	$("#exit").show();
	$("#save").hide();
	$("#status").text("reading");
	var lockDoc = JSON.parse(incoming.body);
	var httpSession = $("#sessionId").val();
	var currDocId = $("#docId").val();
	conData.docSub = conData.client.subscribe('/topic/doc'+currDocId, handleEditDocSubscribeIncome, { id: httpSession});
	console.log('document was saved');
};

//Handles the message from the server after requesting editing the document
function handleEditDocSubscribeIncome(incoming) {
	var lockDoc = JSON.parse(incoming.body);
	
	//Zum abriegeln der view
	if(lockDoc.docId == null){
		$("#editButton").attr("onclick", "");	
		$("#status").text("writing");
		console.log('Document is now locked');
	}else{
		$("#editButton").attr("onclick", "editDoc()");	
		$("#status").text("reading");
		$("#docContent").val(lockDoc.docValue);
		console.log('Document was unlocked');
	}	
}

function handleLockSuccessSubscribeIncome(incoming){
	$("#docContent").prop("disabled", false);
	$("#editButton").hide();
	$("#exit").hide();
	$("#save").show();
	$("#status").text("writing");
	var lockDoc = JSON.parse(incoming.body);
	
	console.log('Document is locked for you: '+lockDoc);	
}


//When clicking the 'leav document' button on a specific document
function leaveDoc(){
	conData.docSub.unsubscribe();
	disconnect();
};

//When clicking 'show' on start page to show a specific document
function showDoc(){
	conData.delDocSub.unsubscribe();
	conData.addDocSub.unsubscribe();
	disconnect();
};

//When deleting adocument on start page
function delDoc(docId){
	var delDoc = JSON.stringify({'docId': docId});
	conData.client.send("/app/delDoc", {}, delDoc);
};

//When adding a document on the start page
function addDoc(){	
	var docName = $("#docName").val();
	var docValue = $("#docValue").val();
	var newDoc = JSON.stringify({'docName': docName,
		  						 'docValue': docValue
		  			});
	conData.client.send("/app/addDoc", {}, newDoc);
};		



//Handles the message from server after deleting a document
function handleDelDocSubscribeIncome(incoming) {
	var docId = JSON.parse(incoming.body);
	$("#delDocId"+docId).closest('tr').remove();
	console.log('Deleted document with ID: '+docId+' from database');
};

//Handles the message from server after adding a new document
function handleAddDocSubscribeIncome(incoming){
	var message = JSON.parse(incoming.body);

	//Create Show Button			
	var showButton = $("<a/>",{
		href: '<c:url value="/readDoc?docId='+message.docId+'"/>',
		type: "button",
		id: "showDoc"
	});
	$(showButton).addClass("btn btn-primary").html("Show");
	
	//Create delete Button 
	var delButton = $("<a/>",{
		onclick: "delDoc("+message.docId+")",
		type: "button",
		id: "delDocId"+message.docId
	});
	$(delButton).addClass("btn btn-danger").html("x");
	
	//Create Row and Columns and add Button
	var tr = $("<tr/>").insertBefore("#rowadd");
	var td1 = $("<td/>").append(message.docId).appendTo(tr);
	var td2 = $("<td/>").append(message.docName).appendTo(tr);
	var td3 = $("<td/>").append(message.docValue).appendTo(tr);
	var td4 = $("<td/>").append(showButton).append(" ").append(delButton).appendTo(tr);
	
	//Clear Input Values
	$("#docName").val("");
	$("#docValue").val("");
};

//Adjusting the size of the textarea
$(function(){
	$('#docContent').autosize({append:"\n"});
});

//Will perfom Logout with corresponding Form with name "logoutForm"
function formSubmit() {
	document.getElementById("logoutForm").submit();
};