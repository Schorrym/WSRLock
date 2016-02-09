$(document).ready(function(){

	$('#connect').click(function(){
		var url = 'http://' + window.location.host + '/wsrlock/marcopolo';
		var sock = new SockJS(url);
		
		var csrfHeader = $("meta[name='_csrf_header']").attr("content");
		var csrfToken = $("meta[name='_csrf']").attr("content");
		
		var headers = {
		      login: 'guest',
		      passcode: 'guest',
		};
		headers[csrfHeader] = csrfToken;
		
		var stomp = Stomp.over(sock);
		var payload = JSON.stringify({ 'message': 'Marcoo!'});
		
		stomp.connect(headers, function(frame){
			console.log('stomp opening');
			
			setTimeout(function(){sayMarco()}, 200);
			
			function sayMarco(){
				stomp.send("/app/marco", {}, payload);
			};			
			
			var subscription = stomp.subscribe("/topic/shout", handleShout);
			
			$('#disconnect').click(function(){
				stomp.disconnect(function(){
					console.log('stomp disconnect');
				});
			});			
		});
		
		handleShout = function handleShout(incoming){
			var message = JSON.parse(incoming.body);
			console.log('Received: ', message);
		}	
	})
})