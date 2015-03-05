<%@taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>

<script>
$(function() {
	$("#getcode").click(function() {
		$("#custommodal").on("shown.bs.modal", function() { $("#resetcode").focus(); });
		
		showModalFull("Obtain Reset Code", "How to obtain password reset code:<br />1. Login to the server<br />2. Type the command <code>/mcapanel resetpassword</code><br />3. It will display a code, enter it below!<br /><br /><label>Reset Code</label><input type='text' id='resetcode' class='form-control' placeholder='Enter Reset Code' onkeydown=\"if (event.keyCode == 13) $('#custommodal .btn').click();\" />", "Done", true);
		
		modalClick("#custommodal", function() {
			$("#seccode").val($("#resetcode").val());
		});
		
		return false;
	});
	
	$("#contactform").submit(function(e) {
		e.preventDefault();
		
		$.post("/password/reset", {"mcname":$("#mcname").val(), "seccode":$("#seccode").val(), "password":md5($(this).find("#password").val()), "confpassword":md5($("#confpassword").val())}, function(data) {
			if (data.error != undefined)
			{
				errorModal(data.error);
			} else if (data.success != undefined)
			{
				showModal("Changed Password", data.success);
				
				modalClick("#custommodal", function() {
					window.location = "/";
				});
			}
		});
	});
});
</script>

<div class="panel panel-default">
	<div class="panel-heading">
		<h3 class="panel-title">
			Reset Password
		</h3>
	</div>
	<div class="panel-body">
		<form id="contactform" method="post" action="/password/reset">
			<div class="row">
				<div class="col-sm-6 form-group">
					<label for="mcname">Minecraft Username</label>
					<input type="text" class="form-control" id="mcname" name="mcname" placeholder="Enter Username" required>
				</div>
				
				<div class="col-sm-6 form-group">
					<label for="seccode">Reset Code</label> | <a href="/password/code" id="getcode" style="color: #428bca !important;">Obtain Code</a>
					<input type="text" class="form-control" id="seccode" name="seccode" placeholder="Enter Reset Code" required>
				</div>
			</div>
			
			<div class="row">
				<div class="col-sm-6 form-group">
					<label for="password">New Password</label>
					<input type="password" class="form-control" id="password" name="password" placeholder="Enter Password" required>
				</div>
				
				<div class="col-sm-6 form-group">
					<label for="confpassword">Confirm Password</label>
					<input type="password" class="form-control" id="confpassword" name="confpassword" placeholder="Enter Password" required>
				</div>
			</div>
			
			<button type="submit" id="resetpassword" class="btn btn-primary">Reset Password</button>
		</form>
	</div>
</div>