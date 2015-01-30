<!-- <script src="/js/viewjs/install.js"></script> -->

<script>
$(function() {
	if ("${step}" == "2")
	{
		$("#step1").css({"display": "none"});
		$("#step2").css({"display": "block"});
	} else if ("${step}" == "3")
	{
		$("#step1").css({"display": "none"});
		$("#step3").css({"display": "block"});
	} else if ("${step}" == "4")
	{
		$("#step1").css({"display": "none"});
		$("#step4").css({"display": "block"});
	}
	
	$("#installform1").submit(function(e) {
		e.preventDefault();
		
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
	                text        : data.restart,
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
	                timeout     : 5000,
	                animation   : {
	                    open  : 'animated bounceInRight',
	                    close : 'animated bounceOutRight',
	                    easing: 'swing',
	                    speed : 500
	                }
	            });
			}
		});
	});
	
	$("#step2 #prevStep").click(function(e) {
		$.post("/install/stepUpdate", {"step": "1"});
		
		$("#step2").fadeOut(function() {
			$("#step1").fadeIn();
			
			$("#serverip").focus();
		});
		
		return false;
	});
	
	$("#installform2").submit(function(e) {
		e.preventDefault();
		
		$.post("/install/step", $("#installform2").serialize(), function(data) {
			if (data.good != undefined)
			{
				$("#step2").fadeOut(function() {
					$("#step3").fadeIn();
					
					//$("#cbname").focus();
				});
			} else if (data.dialog != undefined)
			{
				var n = noty({
	                text        : data.dialog,
	                type        : 'error',
	                dismissQueue: true,
	                layout      : 'topRight',
	                closeWith   : ['click'],
	                theme       : 'relax',
	                maxVisible  : 10,
	                animation   : {
	                    open  : 'animated bounceInRight',
	                    close : 'animated bounceOutRight',
	                    easing: 'swing',
	                    speed : 500
	                },
	                buttons : [
                        {addClass: 'btn btn-primary', text: '${language.localize("Yes")}', onClick: function ($noty) {
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
                        {addClass: 'btn btn-danger', text: '${language.localize("No")}', onClick: function ($noty) {
                            	$noty.close();
                            	
                            	$("#cbfile").focus();
                            	$("#cbfile").select();
                        	}
                        }
                    ]
	            });
			} else if (data.error != undefined)
			{
				var n = noty({
	                text        : data.error,
	                type        : 'error',
	                dismissQueue: true,
	                layout      : 'topRight',
	                closeWith   : ['click'],
	                theme       : 'relax',
	                maxVisible  : 10,
	                timeout     : 5000,
	                animation   : {
	                    open  : 'animated bounceInRight',
	                    close : 'animated bounceOutRight',
	                    easing: 'swing',
	                    speed : 500
	                }
	            });
			}
		});
	});
	
	$("#step3 #prevStep").click(function(e) {
		$.post("/install/stepUpdate", {"step": "2"});
		
		$("#step3").fadeOut(function() {
			$("#step2").fadeIn();
			
			$("#cbname").focus();
		});
		
		return false;
	});
	
	$("#installform3").submit(function(e) {
		e.preventDefault();
		
		$("#mcpass").val(md5($("#tmcpass").val()));
		$("#mcpassconf").val(md5($("#tmcpassconf").val()));
		
		$("#tmcpass").val("");
		$("#tmcpassconf").val("");
		
		$.post("/install/step", $("#installform3").serialize(), function(data) {
			if (data.good != undefined)
			{
				var n = noty({
	                text        : data.good,
	                type        : 'success',
	                dismissQueue: true,
	                layout      : 'topRight',
	                closeWith   : ['click'],
	                theme       : 'relax',
	                maxVisible  : 10,
	                timeout     : 7500,
	                animation   : {
	                    open  : 'animated bounceInRight',
	                    close : 'animated bounceOutRight',
	                    easing: 'swing',
	                    speed : 500
	                },
	                callback    : {
	                	afterClose: function() {
	                		window.location = "/install/";
	                	}
	                }
	            });
			} else if (data.error != undefined)
			{
				var n = noty({
	                text        : data.error,
	                type        : 'error',
	                dismissQueue: true,
	                layout      : 'topRight',
	                closeWith   : ['click'],
	                theme       : 'relax',
	                maxVisible  : 10,
	                timeout     : 5000,
	                animation   : {
	                    open  : 'animated bounceInRight',
	                    close : 'animated bounceOutRight',
	                    easing: 'swing',
	                    speed : 500
	                }
	            });
			}
		});
	});
	
	$("#step4 #prevStep").click(function(e) {
		$.post("/install/stepUpdate", {"step": "3"});
		
		$("#step4").fadeOut(function() {
			$("#step3").fadeIn();
			
			$("#mcname").focus();
		});
		
		return false;
	});
	
	$("#step4 #skipStep").click(function(e) {
		$.post("/install/skipStep", function(data) {
			window.location = "/";
		});
		
		return false;
	});
	
	$("#installform4").submit(function(e) {
		e.preventDefault();
		
		$.post("/install/step", $("#installform4").serialize(), function(data) {
			if (data.good != undefined)
			{
				window.location = "/";
			} else if (data.error != undefined)
			{
				var n = noty({
	                text        : data.error,
	                type        : 'error',
	                dismissQueue: true,
	                layout      : 'topRight',
	                closeWith   : ['click'],
	                theme       : 'relax',
	                maxVisible  : 10,
	                timeout     : 5000,
	                animation   : {
	                    open  : 'animated bounceInRight',
	                    close : 'animated bounceOutRight',
	                    easing: 'swing',
	                    speed : 500
	                }
	            });
			}
		});
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
				&raquo; ${language.localize("Here you will enter your servers IP address.")}
				<br /><br />
				&raquo; ${language.localize("Here you will enter the panels port number, port 80 is recommended.")}
			</div>
		</div>
	</div>
	<div class="col-sm-9" style="padding-left: 0px;">
		<div class="panel panel-default" style="margin-bottom: 20px;">
			<div class="panel-heading">
				<h3 class="panel-title">
					${language.localize("Step")} 1
				</h3>
			</div>
			<div class="panel-body" style="padding-top: 10px; min-height: 250px; position: relative;">
				<form id="installform1" role="form" method="post" action="/install/step">
					<input type="hidden" name="step" value="1" />
					
					<div class="form-group">
						<label for="serverip">${language.localize("Server IP (Don't include the port, ex: 99.26.137.7 or demo.mcapanel.com)")}</label>
						<input type="text" class="form-control" id="serverip" name="serverip" placeholder="${language.localize("Enter Server IP")}" required>
					</div>
					<div class="form-group">
						<label for="webport">McAdminPanel ${language.localize("Port")} (Ex: 80)</label>
						<input type="number" class="form-control" value="${config.getString('web-port', '80')}" id="webport" name="webport" placeholder="${language.localize("Enter McAdminPanel Port")}" required>
					</div>
					
					<div style="position: absolute; bottom: 15px; left: 15px;">
						<button type="submit" id="nextStep" class="btn btn-primary">${language.localize("Next Step")}</button>
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
					${language.localize("Information")}
				</h3>
			</div>
			<div class="panel-body" style="padding-top: 10px; min-height: 250px;">
				<br />
				&raquo; ${language.localize("Here you will enter a name for your server.")}
				<br /><br />
				&raquo; ${language.localize("Here you will enter the servers bukkit jar file.")}
			</div>
		</div>
	</div>
	<div class="col-sm-9" style="padding-left: 0px;">
		<div class="panel panel-default" style="margin-bottom: 20px;">
			<div class="panel-heading">
				<h3 class="panel-title">
					${language.localize("Step")} 2
				</h3>
			</div>
			<div class="panel-body" style="padding-top: 10px; min-height: 250px; position: relative;">
				<form id="installform2" role="form" method="post" action="/install/step">
					<input type="hidden" name="step" value="2" />
					
					<div class="form-group">
						<label for="cbname">${language.localize("Server Name")}</label>
						<input type="text" class="form-control" id="cbname" name="cbname" placeholder="${language.localize('Enter Server Name')}" required>
					</div>
					<div class="form-group">
						<label for="cbfile">${language.localize("Server Jar File")} (Ex: C:\Users\Connor\Desktop\McAdminPanel\craftbukkit.jar)</label>
						<input type="text" class="form-control" id="cbfile" name="cbfile" placeholder="${language.localize('Enter Server Jar File')}" required>
					</div>
					
					<div style="position: absolute; bottom: 15px; left: 15px;">
						<button type="button" id="prevStep" class="btn btn-default">${language.localize("Previous Step")}</button>
						<button type="submit" id="nextStep" class="btn btn-primary" style="margin-left: 10px;">${language.localize("Next Step")}</button>
					</div>
				</form>
			</div>
		</div>
	</div>
</div>

<div id="step3" class="row" style="display: none;">
	<div class="col-sm-3" style="padding-right: 0px;">
		<div class="panel panel-default" style="margin-bottom: 20px;">
			<div class="panel-heading">
				<h3 class="panel-title">
					${language.localize("Information")}
				</h3>
			</div>
			<div class="panel-body" style="padding-top: 10px; min-height: 250px;">
				<br />
				&raquo; ${language.localize("Here you will enter your Minecraft username.")}
				<br /><br />
				&raquo; ${language.localize("Here you will enter a password for your account.")}
			</div>
		</div>
	</div>
	<div class="col-sm-9" style="padding-left: 0px;">
		<div class="panel panel-default" style="margin-bottom: 20px;">
			<div class="panel-heading">
				<h3 class="panel-title">
					${language.localize("Step")} 3
				</h3>
			</div>
			<div class="panel-body" style="padding-top: 10px; min-height: 250px; position: relative;">
				<form id="installform3" role="form" method="post" action="/install/step">
					<input type="hidden" name="step" value="3" />
					
					<input type="hidden" id="mcpass" name="mcpass" value="" />
					<input type="hidden" id="mcpassconf" name="mcpassconf" value="" />
					
					<div class="form-group">
						<label for="mcname">${language.localize("Minecraft Username")}</label>
						<input type="text" class="form-control" id="mcname" name="mcname" placeholder="${language.localize('Enter Username')}" required>
					</div>
					
					<div class="row">
						<div class="col-sm-6 form-group">
							<label for="tmcpass">${language.localize("Password (Don't use your Minecraft password)")}</label>
							<input type="password" class="form-control" id="tmcpass" name="tmcpass" placeholder="${language.localize('Enter Password')}" required>
						</div>
						<div class="col-sm-6 form-group">
							<label for="tmcpassconf">${language.localize("Confirm Password")}</label>
							<input type="password" class="form-control" id="tmcpassconf" name="tmcpassconf" placeholder="${language.localize('Confirm Password')}" required>
						</div>
					</div>
					
					<div style="position: absolute; bottom: 15px; left: 15px;">
						<button type="button" id="prevStep" class="btn btn-default">${language.localize("Previous Step")}</button>
						<button type="submit" id="nextStep" class="btn btn-primary" style="margin-left: 10px;">${language.localize("Next Step")}</button>
					</div>
				</form>
			</div>
		</div>
	</div>
</div>

<div id="step4" class="row" style="display: none;">
	<div class="col-sm-3" style="padding-right: 0px;">
		<div class="panel panel-default" style="margin-bottom: 20px;">
			<div class="panel-heading">
				<h3 class="panel-title">
					${language.localize("Information")}
				</h3>
			</div>
			<div class="panel-body" style="padding-top: 10px; min-height: 250px;">
				<br />
				&raquo; ${language.localize("Here you will enter your McAdminPanel License Email.")}
				<br /><br />
				&raquo; ${language.localize("Here you will enter your McAdminPanel License Key.")}
			</div>
		</div>
	</div>
	<div class="col-sm-9" style="padding-left: 0px;">
		<div class="panel panel-default" style="margin-bottom: 20px;">
			<div class="panel-heading">
				<h3 class="panel-title">
					${language.localize("Step")} 4
				</h3>
			</div>
			<div class="panel-body" style="padding-top: 10px; min-height: 250px; position: relative;">
				<form id="installform4" role="form" method="post" action="/install/step">
					<input type="hidden" name="step" value="4" />
					
					<div class="form-group">
						<label for="licemail">${language.localize("License Email")}</label>
						<input type="text" class="form-control" id="licemail" name="licemail" placeholder="${language.localize('Enter License Email')}" required>
					</div>
					
					<div class="form-group">
						<label for="lickey">${language.localize("License Key")}</label>
						<input type="text" class="form-control" id="lickey" name="lickey" placeholder="${language.localize('Enter License Key')}" required>
					</div>
					
					<div style="position: absolute; bottom: 15px; left: 15px;">
						<button type="button" id="prevStep" class="btn btn-default">${language.localize("Previous Step")}</button>
						<button type="submit" id="nextStep" class="btn btn-primary" style="margin-left: 10px;">${language.localize("Next Step")}</button>
						<button type="button" id="skipStep" class="btn btn-default" style="margin-left: 10px;">${language.localize("Skip")}</button>
					</div>
				</form>
			</div>
		</div>
	</div>
</div>