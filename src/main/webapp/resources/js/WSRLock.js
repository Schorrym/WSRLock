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
      currDocId: $("#docId").val(),
      sessionId: $("#sessionId").val(),
};
headers[csrfHeader] = csrfToken;

//Connect with the above given credentials
conData.client.connect(headers, function(frame){
	var pageName = $("#pageName").val();
	var httpSession = $("#sessionId").val();
	if(pageName == "start"){	
		conData.delDocSub = sub('topic', 'delDoc', handleDelDocBroadcast); 
		conData.addDocSub = sub('topic', 'addDoc', handleAddDocBroadcast);
	}else if(pageName == "readdoc"){
		var currDocId = $("#docId").val();
		conData.docSub = sub('topic', 'doc'+currDocId, handleDocBroadcast);
		conData.checkSub = sub('topic', 'checkDoc', handleCheckDoc);
		var task = JSON.stringify({'task': currDocId});
		conData.client.send("/app/checkDoc", {}, task);
	}
});

//STOMP disconnect
function disconnect(){
	conData.client.disconnect(function(){
		console.log('STOMP disconnected');
	});
};

//Client-Server -- When a request for Document editing is placed (by clicking the edit button)
function editDoc(){
	conData.docSub.unsubscribe();
	conData.lockSub = sub('user', 'lockSuccess', handleLockSuccessIncome);
	conData.saveSub = sub('user', 'saveSuccess', handleSaveSuccessIncome);
	
	var currDocId = $("#docId").val();
	var editDoc = JSON.stringify({'task': currDocId});
	conData.client.send("/app/editDoc", {}, editDoc);
};

function lockView(){
	$("#editButton").attr("onclick", "");	
	$("#status").text("writing");
	console.log('Document is now locked');
};

function freeView(doc){
	$("#editButton").attr("onclick", "editDoc()");	
	$("#status").text("reading");
	$("#docContent").val(doc.docValue);
	console.log('Document was unlocked');
};

//Server->Client -- Handles the message from the server after requesting editing the document
function handleDocBroadcast(incoming) {
	var payload = JSON.parse(incoming.body);
	var object = payload.object;
	var task = payload.task;
	if(task == "lockView"){
		lockView();
	}else if(task == "newDoc"){		
		freeView(object);
	}else if(task == "userUpdate"){
		for (var i = 0; i < object.length; i++) {
			console.log("BENUTZER: "+object[i].userName);			
		}		
	}
};

//Client->Server -- When adding a new document on start.jsp page. New document is sent to the server
function saveDoc(){
	var docId = $("#docId").val();
	var docValue = $("#docContent").val();
	var savedDoc = JSON.stringify({'docId': docId,
		  						   'docValue': docValue
		  			});
	conData.client.send("/app/saveDoc", {}, savedDoc);
};

//Server->Client -- Handles the message from the Server when saving a edited document succeeded (no broadcast, user unique)
function handleSaveSuccessIncome(incoming){	
	conData.lockSub.unsubscribe();
	conData.saveSub.unsubscribe();
	$("#docContent").prop("disabled", true);
	$("#editButton").show();
	$("#exit").show();
	$("#save").hide();
	$("#status").text("reading");
	var currDocId = $("#docId").val();
	conData.docSub = sub('topic', 'doc'+currDocId, handleDocBroadcast); 
	console.log('document was saved');
};

//Server-Client -- Handles message from the Server when locking a document has succeeded (no broadcast, user unique)
function handleLockSuccessIncome(incoming){
	var lockDoc = JSON.parse(incoming.body);
	
	$("#docContent").prop("disabled", false);
	$("#editButton").hide();
	$("#exit").hide();
	$("#save").show();
	$("#status").text("writing");	
	
	console.log('Document is locked for you: '+lockDoc.task);	
};

//When clicking the 'leav document' button on a specific document
function leaveDoc(){
	conData.docSub.unsubscribe();
	var leave = JSON.stringify({'task': $("#docId").val()});
	conData.client.send("/app/leaveDoc", {}, leave)
	disconnect();
};

//When clicking 'show' on start page to show a specific document
function showDoc(){
	conData.delDocSub.unsubscribe();
	conData.addDocSub.unsubscribe();
	disconnect();
};

//Client->Server -- When deleting a document on start.jsp page
function delDoc(docId){
	var delDoc = JSON.stringify({'docId': docId});
	conData.client.send("/app/delDoc", {}, delDoc);
};

//Server->Client -- Handles the message from server after deleting a document
function handleDelDocBroadcast(incoming) {
	var docId = JSON.parse(incoming.body);
	$("#delDocId"+docId).closest('tr').remove();
	console.log('Deleted document with ID: '+docId+' from database');
};

//Client->Server -- When adding a document on the start page
function addDoc(){	
	var docName = $("#docName").val();
	var docValue = $("#docValue").val();
	var newDoc = JSON.stringify({'docName': docName,
		  						 'docValue': docValue
		  			});
	conData.client.send("/app/addDoc", {}, newDoc);
};

//Server->Client -- Handles the message from server after adding a new document
function handleAddDocBroadcast(incoming){
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

//Subscribing events. Returns a new subscription
function sub(type, url, callback){
	if(type == 'user'){
		return conData.client.subscribe('/user/queue/'+url, callback);
	}else if(type == 'topic'){
		return conData.client.subscribe('/topic/'+url, callback);
	}else if(type == 'app'){
		return conData.client.subscribe('/app/'+url, callback);
	}else if(type == 'new'){
		return conData.client.subscribe(url, callback);
	}else{
		return null;
	}
};

//Server-Client -- Check at the beginning of the document if it is locked in database
function handleCheckDoc(incoming){
	var message = JSON.parse(incoming.body);

	if(message.task == "lockView"){
		lockView();
	}else if(message.task == "writeMode"){
		//ToDo
	}
};