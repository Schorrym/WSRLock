$(document).ready(function(){

	$('#connect').click(function(){
		var url = 'http://' + window.location.host + '/wsrlock/marcopolo';
		var sock = new SockJS(url);
		
		var stomp = Stomp.over(sock);
		var payload = JSON.stringify({ 'message': 'Marcoo!'});
		
		stomp.connect('guest', 'guest', function(frame){
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