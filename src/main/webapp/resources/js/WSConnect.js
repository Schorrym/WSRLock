//Url pointing to the STOMP Endpoint of the Server
var url = 'http://' + window.location.host + '/wsrlock/readDocument';
//New SockJS Connection is opened (Local) by using the sockjs.js Client-Library
var sock = new SockJS(url);

//Create namespace for Connection
var conData = {};
//Defining that STOMP will be used with the SockJS Protocol as fallback options
conData.client = Stomp.over(sock);
//conData.docSub;		

$(document).ready(function(){
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
		conData.client.connect(headers, function(frame){});		
})