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
				<div class="form-group">
					<label for="docContent" class="label label-default" id="doc${currentDoc.docId}">Document: ${currentDoc.docName}</label>
					<textarea  class="form-control" id="docContent" disabled>${currentDoc.docValue}</textarea>					
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