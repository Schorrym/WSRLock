<!DOCTYPE html>
<html lang="en">
  <head>
    <meta charset="utf-8">
    <meta http-equiv="X-UA-Compatible" content="IE=edge">
    <meta name="viewport" content="width=device-width, initial-scale=1">
    <!-- The above 3 meta tags *must* come first in the head; any other head content must come *after* these tags -->
    <title>Viewing document</title>

    <!-- Bootstrap -->
    <link href="./resources/css/bootstrap.css" rel="stylesheet">

    <!-- HTML5 shim and Respond.js for IE8 support of HTML5 elements and media queries -->
    <!-- WARNING: Respond.js doesn't work if you view the page via file:// -->
    <!--[if lt IE 9]>
      <script src="https://oss.maxcdn.com/html5shiv/3.7.2/html5shiv.min.js"></script>
      <script src="https://oss.maxcdn.com/respond/1.4.2/respond.min.js"></script>
    <![endif]-->
  </head>
  <body> 
<div class="container">
    <div class="row">
    	<div class="col-sm-10 col-md-10">
        	<span class="label label-default" id="docname">Doc XYZ</span>
				<form role ="form">   
			  		<div class ="form-group">
			      		<textarea class = "form-control" readonly>BlaBla</textarea>
			   		</div>				   
				</form>
        </div>
        <div class="col-sm-2 col-md-2">
        <ul id="sidenavbar" class="nav nav-pills nav-stacked">
				<li><h3 align="center"><span class="label label-success" id="status">Status</span></h3></li>
				<li><h3 align="center"><button type="button" class="btn btn-default" disabled="disabled" id="edit">Edit</button></h3></li>
				<li><h3 align="center"><button type="button" class="btn btn-default" id="overview">Leave</button></h3></li>
			</ul>
            <div class="panel-group" id="accordion">
                <div class="panel panel-default">
                    <div class="panel-heading">
                        <h4 class="panel-title">
                            <a data-toggle="collapse" data-parent="#accordion" href="#collapseUser"><span class="glyphicon glyphicon-user">
                            </span>Who is watching</a>
                        </h4>
                    </div>
                    <div id="collapseUser" class="panel-collapse collapse">
                        <div class="panel-body">
                            <table class="table" id="usertable">
                                <tr>
                                    <td>
                                        User 1
                                    </td>
                                </tr>
                            </table>
                        </div>
                    </div>
                </div>
            </div>
        </div>
        
    </div>
</div>
    <!-- jQuery (necessary for Bootstrap's JavaScript plugins) -->
    <script src="https://ajax.googleapis.com/ajax/libs/jquery/1.11.3/jquery.min.js"></script>
    <!-- Include all compiled plugins (below), or include individual files as needed -->
    <script src="../js/bootstrap.min.js"></script>
  </body>
</html>