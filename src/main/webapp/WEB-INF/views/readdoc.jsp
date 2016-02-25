<%@include file="1_Top.jsp" %>
<html>
  <head>
    <%@include file="2_Head.jsp" %>	
	<title>Editing document</title>
  </head>
    
  <body>  
  	<%@include file="4_Navbar.jsp" %>
		<div class="container">	
	    <section class="row">
			<div class="col-sm-12 col-md-12">
				<label for="docContent" class="label label-default" id="doc${currentDoc.docId}">Document: ${currentDoc.docName}</label>				
			</div>
		</section>
		<section class="row">
			<div class="col-sm-10 col-md-10">
				<div class="form-group">					
					<textarea  class="form-control" id="docContent" disabled>${currentDoc.docValue}</textarea>					
				</div>
			</div>
			<div class="col-sm-2 col-md-2">
				<div class="panel-group">
					<div  class="panel panel-primary">
				      <div id="userList" class="panel-heading">members</div>
				    </div>
				</div>				
			</div>
		</section>
		</div>
		<!-- Params to parse to javascript -->
		<input type="hidden" id="pageName" value="readdoc">
		<input type="hidden" id="docId" value="${currentDoc.docId}">
		<input type="hidden" id="sessionId" value="${pageContext.session.id}">
	</body>
	<%@include file="3_HeadCon.jsp" %>
</html>