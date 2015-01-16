<!-- <script src="/js/viewjs/install.js"></script> -->

<script>
$(function() {
	if ("${step}" == "2")
	{
		$("#step1").css({"display": "none"});
		$("#step2").css({"display": "block"});
	}
	
	$("#step1 #nextStep").click(function(e) {
		$.post("/install/step", $("#installform1").serialize(), function(data) {
			if (data.good != undefined)
			{
				$("#step1").fadeOut(function() {
					$("#step2").fadeIn();
					
					$("#cbname").focus();
				});
			} else if (data.restart != undefined)
			{
				var n = noty({
	                text        : "<b>Info: </b>" + data.restart,
	                type        : 'warning',
	                dismissQueue: true,
	                layout      : 'topRight',
	                closeWith   : 'click',
	                theme       : 'relax',
	                maxVisible  : 10,
	                animation   : {
	                    open  : 'animated bounceInRight',
	                    close : 'animated bounceOutRight',
	                    easing: 'swing',
	                    speed : 500
	                }
	            });
			}
		});
		
		return false;
	});
	
	$("#step2 #prevStep").click(function(e) {
		$.post("/install/stepUpdate", {"step": "1"});
		
		$("#step2").fadeOut(function() {
			$("#step1").fadeIn();
			
			$("#serverip").focus();
		});
		
		return false;
	});
	
	$("#step2 #nextStep").click(function(e) {
		$.post("/install/step", $("#installform2").serialize(), function(data) {
			if (data.good != undefined)
			{
				$("#step2").fadeOut(function() {
					$("#step3").fadeIn();
					
					//$("#cbname").focus();
				});
			} else if (data.error != undefined)
			{
				var n = noty({
	                text        : data.error,
	                type        : 'error',
	                dismissQueue: true,
	                layout      : 'topRight',
	                closeWith   : 'click',
	                theme       : 'relax',
	                maxVisible  : 10,
	                animation   : {
	                    open  : 'animated bounceInRight',
	                    close : 'animated bounceOutRight',
	                    easing: 'swing',
	                    speed : 500
	                },
	                buttons : [
                        {addClass: 'btn btn-primary', text: 'Yes', onClick: function ($noty) {
                            	$noty.close();
                            	
                            	$.post("/install/step", $("#installform2").serialize() + "&override=true", function(data) {
                        			if (data.good != undefined)
                        			{
                        				$("#step2").fadeOut(function() {
                        					$("#step3").fadeIn();
                        					
                        					//$("#cbname").focus();
                        				});
                        			}
                            	});
                        	}
                        },
                        {addClass: 'btn btn-danger', text: 'No', onClick: function ($noty) {
                            	$noty.close();
                            	
                            	$("#cbfile").focus();
                            	$("#cbfile").select();
                        	}
                        }
                    ]
	            });
			}
		});
		
		return false;
	});
});
</script>

<div id="step1" class="row">
	<div class="col-sm-3" style="padding-right: 0px;">
		<div class="panel panel-default" style="margin-bottom: 20px;">
			<div class="panel-heading">
				<h3 class="panel-title">
					${language.localize("Information")}
				</h3>
			</div>
			<div class="panel-body" style="padding-top: 10px; min-height: 250px;">
				<br />
				&raquo; Here you will enter your servers IP address.
				<br /><br />
				&raquo; Here you will enter the panels port number, port 80 is recommended.
			</div>
		</div>
	</div>
	<div class="col-sm-9" style="padding-left: 0px;">
		<div class="panel panel-default" style="margin-bottom: 20px;">
			<div class="panel-heading">
				<h3 class="panel-title">
					Step 1
				</h3>
			</div>
			<div class="panel-body" style="padding-top: 10px; min-height: 250px; position: relative;">
				<form id="installform1" role="form" method="post" action="/install/step">
					<input type="hidden" name="step" value="1" />
					
					<div class="form-group">
						<label for="serverip">Server IP (Don't include the port, ex: 99.26.137.7 or demo.mcapanel.com)</label>
						<input type="text" class="form-control" id="serverip" name="serverip" placeholder="Enter Server IP" required>
					</div>
					<div class="form-group">
						<label for="webport">McAdminPanel Port (Ex: 80)</label>
						<input type="number" class="form-control" value="${config.getString('web-port', '80')}" id="webport" name="webport" placeholder="Enter McAdminPanel Port" required>
					</div>
					
					<div style="position: absolute; bottom: 15px; left: 15px;">
						<button type="submit" id="nextStep" class="btn btn-primary">Next Step</button>
					</div>
				</form>
			</div>
		</div>
	</div>
</div>

<div id="step2" class="row" style="display: none;">
	<div class="col-sm-3" style="padding-right: 0px;">
		<div class="panel panel-default" style="margin-bottom: 20px;">
			<div class="panel-heading">
				<h3 class="panel-title">
					Information
				</h3>
			</div>
			<div class="panel-body" style="padding-top: 10px; min-height: 250px;">
				<br />
				&raquo; Here you will enter a name for your server.
				<br /><br />
				&raquo; Here you will enter the servers bukkit jar file.
			</div>
		</div>
	</div>
	<div class="col-sm-9" style="padding-left: 0px;">
		<div class="panel panel-default" style="margin-bottom: 20px;">
			<div class="panel-heading">
				<h3 class="panel-title">
					Step 2
				</h3>
			</div>
			<div class="panel-body" style="padding-top: 10px; min-height: 250px; position: relative;">
				<form id="installform2" role="form" method="post" action="/install/step">
					<input type="hidden" name="step" value="2" />
					
					<div class="form-group">
						<label for="cbname">Server Name</label>
						<input type="text" class="form-control" id="cbname" name="cbname" placeholder="Enter Server Name" required>
					</div>
					<div class="form-group">
						<label for="cbfile">Server Jar File (Ex: C:\Users\Connor\Desktop\McAdminPanel\craftbukkit.jar)</label>
						<input type="text" class="form-control" id="cbfile" name="cbfile" placeholder="Enter Server Jar File" required>
					</div>
					
					<div style="position: absolute; bottom: 15px; left: 15px;">
						<button type="button" id="prevStep" class="btn btn-default">Previous Step</button>
						<button type="submit" id="nextStep" class="btn btn-primary" style="margin-left: 10px;">Next Step</button>
					</div>
				</form>
			</div>
		</div>
	</div>
</div>