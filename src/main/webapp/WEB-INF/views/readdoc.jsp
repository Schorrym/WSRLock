<%@include file="1_Top.jsp" %>
<html lang="en">
  <head>
    <%@include file="2_Head.jsp" %>
    <title>Viewing document</title>
  </head>
    
  <body>  
  	<%@include file="4_Navbar_Read.jsp" %>
		<div class="container">	
	    <section class="row">
			<div class="col-sm-12 col-md-12">
				<span class="label label-default" id="docname">Doc XYZ</span>
				<form role="form">
					<div class="form-group">
						<textarea class="form-control" readonly>BlaBla</textarea>
					</div>
				</form>
			</div>
		</section>
		</div>
	</body>
</html>