<%@include file="1_Top.jsp" %>
<html lang="en">
  <head>
    <%@include file="2_Head.jsp" %>

	<title>Viewing document</title>
  </head>
    
  <body onload="">  
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
		<input type="hidden" id="pageName" value="readdoc">
	</body>
	<%@include file="3_HeadCon.jsp" %>
</html>