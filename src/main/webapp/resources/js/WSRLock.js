//En- Decoding
if (!window.btoa) window.btoa = $.base64.btoa;
if (!window.atob) window.atob = $.base64.atob;
//Url pointing to the STOMP Endpoint of the Server
var url = 'http://' + window.location.host + '/wsrlock/readDocument';
//New SockJS Connection is opened (Local) by using the sockjs.js Client-Library
var sock = new SockJS(url);

//Create namespace for Connection
var conData = {};

//Gets the geoLocation from the Browsers Navigator Object
function getGeoLocation(){
	if (navigator.geolocation) { 
		function success(pos) {
		  var crd = pos.coords;
		  conData.position = crd.latitude + crd.longitude;
		};

		function error(err) {
		  console.warn('ERROR(' + err.code + '): ' + err.message);
		};

		navigator.geolocation.getCurrentPosition(success, error);
	}
};

//Collects all information within the Navigator Object to identify the Client-side
conData.getUserObjects = function(){
	var useObj = JSON.stringify({'userAgent': navigator.userAgent,
									'appVersion': navigator.appVersion,
									'platform': navigator.platform,
									'appName': navigator.appName,
									'jSession': window.localStorage.getItem("jSession")});
	return useObj;
};

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

var pageName = $("#pageName").val();
var currDocId = $("#docId").val();
var interval;
//Connect with the above given credentials
conData.client.connect(headers, function(frame){
	//No STOMP debugging is shown in the browser console
	conData.client.debug = null;
	conData.hashCode = sub('user', 'getChallenge', handleChallenge);
	conData.client.send("/app/tokenCreate", {userObj: conData.getUserObjects()});
	if(pageName == "start"){
		conData.delDocSub = sub('topic', 'delDoc', handleDelDocBroadcast); 
		conData.addDocSub = sub('topic', 'addDoc', handleAddDocBroadcast);
	}else if(pageName == "readdoc"){
		conData.docSub = sub('topic', 'doc'+currDocId, handleDocBroadcast);
		conData.checkSub = sub('topic', 'checkDoc', handleCheckDoc);
		var docId = JSON.stringify({'docId': currDocId});
		
		conData.client.send("/app/checkDoc", {challenge: window.localStorage.getItem("pChallenge")}, docId);		
		setInterval(function(){
			conData.client.send("/app/broadcastUser", {challenge: window.localStorage.getItem("pChallenge")}, docId);
			}, 5000);
	}else{
		conData.client.disconnect();
	}
});

//Set the challenge given by the server to a CRAM MD5
function setHash(challenge){
	var chlng = $.base64.atob(challenge);
	var chlngClient = chlng.concat(conData.getUserObjects());
	var md5 = $.md5(chlngClient);
	var base64 = $.base64.btoa(md5);
	window.localStorage.setItem("pChallenge", base64);
};

//Get the generated Hash (Base64) from local Storage
function getHash(){
	return window.localStorage.getItem("pChallenge");
};

//Server-Client -- Challenge comes from server
function handleChallenge(incoming){
	var payload = JSON.parse(incoming.body);
	var challenge = payload.challenge;
	setHash(challenge);
};

//Client-Server -- When a request for Document editing is placed (by clicking the edit button)
function editDoc(){
	conData.docSub.unsubscribe();
	conData.lockSub = sub('user', 'editMode', handleLockIncome);
	var editDoc = JSON.stringify({'docId': currDocId});
	conData.client.send("/app/editDoc", {challenge: getHash()}, editDoc);
};

//This is for locking the view when someone else are editing the document
function lockView(){
	$("#editButton").attr("onclick", "");
	$("#status").text("writing");
};

//Dialog pops up before content gets refreshed after saving new document
function contentRefreshDialog(doc){
	if (confirm('Do you want to refresh the content?')) {
		$("#docContent").val(doc.docValue);
	} else {
	    alert('Document value has changed. Be carefull with the current data!');
	}
};

//Gives the view free again after a document was successfully saved by another editor
function freeView(doc){
	$("#editButton").attr("onclick", "editDoc()");	
	$("#status").text("reading");
	
	contentRefreshDialog(doc);
};

//STOMP disconnect
function disconnect(){
	if(pageName == "readdoc"){
		leaveDoc();
	}	
	conData.client.disconnect(function(){
		console.log('STOMP disconnected');
	});
};

//Server->Client -- Handles the message from the server after requesting editing the document
function handleDocBroadcast(incoming) {
	var payload = JSON.parse(incoming.body);
	var object = payload.object;
	var task = payload.task;
	
	if(task == "writeMode"){
		lockView();
	}else if(task == "lockDoc"){
		lockView();
	}else if(task == "lockView"){
		lockView();
	}else if(task == "newDoc" || task == "timeOver"){		
		freeView(object);
	}else if(task == "userUpdate"){
		userUpdate(object);
	}
};

//Add userName to the members list of specified doc
function userUpdate(userList){
	if(userList != null){
		$(".panel-body").remove();
		for (var i = 0; i < userList.length; i++) {
			var div = $("<div/>");
			div.addClass("panel-body text-right").html(userList[i]);
			div.insertAfter("#userList");
		}
	}
}

//Client->Server -- When adding a new document on start.jsp page. New document is sent to the server
function saveDoc(){
	var docVersion = window.localStorage.getItem("docVersion");
	window.localStorage.removeItem("docVersion");
	var docId = $("#docId").val();
	var docValue = $("#docContent").val();
	var savedDoc = JSON.stringify({'docId': docId,
									'docValue': docValue,
									'docVersion': docVersion,
		  			});
	conData.client.send("/app/saveDoc", {challenge: getHash()}, savedDoc);
};

//Client->Server -- Auto save the value of textarea while in writemode of a document
function autoSave(){
	var autoValue = JSON.stringify({'docId': $("#docId").val(),
									'docValue': $("#docContent").val()});
	conData.client.send("/app/autoSave", {challenge: getHash()}, autoValue);
	return;
};

//Server-Client -- Handles messages from the Server to the user who locked a document
function handleLockIncome(incoming){
	var payload = JSON.parse(incoming.body);
	var object = payload.object;
	var task = payload.task;
	
	//Handles message from the Server when locking a document has succeeded (no broadcast, user unique)
	//lockDoc = document locked after requesting (click edit)
	//writeMode = document reentered after system crash for example
	if(task == "lockDoc" || task == "writeMode"){
		var docVersion = object['docUsers']['doc']['docVersion'];
		window.localStorage.setItem("docVersion", docVersion);
		if(task == "writeMode"){
			$("#docContent").val(object['tempDocValue']);
		}
		conData.interval = setInterval( autoSave, 5000 );		
		$("#docContent").prop("disabled", false);
		$("#editButton").hide();
		$("#exit").hide();
		$("#save").show();
		$("#status").text("writing");
		console.log('Document is locked for you: '+object['docUsers']['user']['userName']);
	//Handles the message from the Server when saving a edited document succeeded (no broadcast, user unique)
	}else if(task == "docSaved" || task == "timeOver"){
		conData.lockSub.unsubscribe();
		$("#docContent").prop("disabled", true);
		$("#editButton").show();
		$("#exit").show();
		$("#save").hide();
		$("#status").text("reading");
		var currDocId = $("#docId").val();
		conData.docSub = sub('topic', 'doc'+currDocId, handleDocBroadcast);
		clearInterval(conData.interval);
	}else if(task == "userUpdate"){
		userUpdate(object);
	}
};

//Client->Server -- When clicking the 'leave document' button on a specific document
function leaveDoc(){
	conData.docSub.unsubscribe();
	var leave = JSON.stringify({'task': $("#docId").val()});
	conData.client.send("/app/leaveDoc", {challenge: getHash()}, leave)
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
	conData.client.send("/app/delDoc", {challenge: getHash()}, delDoc);
};

//Server->Client -- Handles the message from server after deleting a document
function handleDelDocBroadcast(incoming) {
	var docId = JSON.parse(incoming.body);
	$("#delDocId"+docId).closest('tr').remove();
};

//Client->Server -- When adding a document on the start page
function addDoc(){	
	var docName = $("#docName").val();
	var docValue = $("#docValue").val();
	var newDoc = JSON.stringify({'docName': docName,
		  						 'docValue': docValue
		  			});
	conData.client.send("/app/addDoc", {challenge: getHash()}, newDoc);
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
		editDoc();
	}
};