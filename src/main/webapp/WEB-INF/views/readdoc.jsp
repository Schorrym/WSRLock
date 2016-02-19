<%@include file="1_Top.jsp" %>
<html lang="en">
  <head>
    <%@include file="2_Head.jsp" %>
    <%@include file="3_HeadCon.jsp" %>
	<script>
		function textAreaAdjust(o) {
			o.style.height = "1px";
			o.style.height = (25 + o.scrollHeight) + "px";
		};
		function leaveDoc(){
			conData.docSub.unsubscribe();
			conData.client.disconnect(function(){
				console.log('stomp disconnect');
			});
		};
		handleMessages = function (incoming){
			var message = JSON.parse(incoming.body)
			console.log('Received: ', message);
		};
		
		var currDocId = '${currentDoc.docId}'
		conData.docSub = conData.client.subscribe('/topic/doc'+currDocId, handleMessages);
		
		var payload = JSON.stringify({'task':'edit',
									  'docId': '${currentDoc.docId})'
									  });
		function editDoc(){
			conData.client.send("/app/control", {}, payload);
		}
		
// 		var payload = JSON.stringify({ 'message': 'Marcoo!'});
		
// 		function sayMarco(){
// 			stomp.send("/app/marco", {}, payload);
// 		};			
					
// 		setTimeout(function(){sayMarco()}, 200);
	</script>

	<title>Viewing document</title>
  </head>
    
  <body>  
  	<%@include file="4_Navbar_Read.jsp" %>
		<div class="container">	
	    <section class="row">
			<div class="col-sm-12 col-md-12">
				<span class="label label-default" id="docname">Document: ${currentDoc.docName}</span>
				<form role="form">
					<div class="form-group">
						<textarea  onkeyup="textAreaAdjust(this)" style="overflow:visible" class="form-control" readonly>
						${currentDoc.docValue}
						</textarea>
					</div>
				</form>
			</div>
		</section>
		</div>
	</body>
</html>