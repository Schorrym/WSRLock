$(document).ready(function(){
//function initializeConnection(pageName){
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
		if(pageName == "start"){
			conData.delDocSub = conData.client.subscribe('/topic/delDoc', handleDelDocSubscribeIncome);
			conData.addDocSub = conData.client.subscribe('/topic/addDoc', handleAddDocSubscribeIncome);
		}else if(pageName== "readdoc"){
			var currDocId = '${currentDoc.docId}';
			conData.docSub = conData.client.subscribe('/topic/doc'+currDocId, handleSubscribeIncome);
		}
	});
//}

//When a request for Document editing is placed
function editDoc(){
	var payload = JSON.stringify({'task':'edit',
		  'docId': '${currentDoc.docId}'
		  });
	conData.client.send("/app/control", {}, payload);
};

function leaveDoc(){
	conData.docSub.unsubscribe();
	conData.client.disconnect(function(){
		console.log('stomp disconnect');
	});
};

function showDoc(){
	conData.delDocSub.unsubscribe();
	conData.addDocSub.unsubscribe();
	conData.client.disconnect(function(){
		console.log('stomp disconnect');
	});
};

function delDoc(docId){
	var delDoc = JSON.stringify({'docId': docId});
	conData.client.send("/app/delDoc", {}, delDoc);
};

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
			var message = JSON.parse(incoming.body)
			console.log('Received: ', message);
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
})