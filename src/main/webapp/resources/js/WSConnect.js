$(document).ready(function(){

	$('#connect').click(function(){
		var url = 'http://' + window.location.host + '/wsrlock/marco';
		var sock = new SockJS(url);
		
		sock.onopen = function() {
			console.log('opening');
			sayMarco();
		};
	
		sock.onmessage = function(e) {
			console.log('Received message: ', e.data);
			setTimeout(function(){sayMarco()}, 200);
		};
	
		sock.onclose = function() {
			console.log('Closing');
		};
	
		function sayMarco() {
			console.log('Sending Marco');
			sock.send("Marco");
		};
		
		$('#disconnect').click(function(){
			sock.close();
		});
	})

})