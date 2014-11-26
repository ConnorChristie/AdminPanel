<script src="/js/viewjs/install.js"></script>

<div class="panel panel-default" style="margin-bottom: 0px;">
	<div class="panel-heading">
		<h3 class="panel-title">
			Install McAdminPanel
		</h3>
	</div>
	<div class="panel-body" style="padding-top: 10px;">
		<form id="installform" role="form" method="post" action="/install/">
			<input type="hidden" id="mcpass" name="mcpass" value="" />
			<input type="hidden" id="mcpassconf" name="mcpassconf" value="" />
			<div class="row">
				<div class="col-sm-8 form-group">
					<label for="serverip">Server IP (Don't include the port, ex: 99.26.137.7 or demo.mcapanel.com)</label>
					<input type="text" class="form-control" id="serverip" name="serverip" placeholder="Enter Server IP" required>
				</div>
				<div class="col-sm-4 form-group">
					<label for="webport">McAdminPanel Port (Ex: 80)</label>
					<input type="number" class="form-control" id="webport" name="webport" placeholder="Enter McAdminPanel Port" required>
				</div>
			</div>
			<div class="row">
				<div class="col-sm-6 form-group">
					<label for="cbname">Server Name</label>
					<input type="text" class="form-control" id="cbname" name="cbname" placeholder="Enter Server Name">
				</div>
				<div class="col-sm-6 form-group">
					<label for="cbfile">Server Jar File (Ex: C:\Users\Connor\Desktop\McAdminPanel\craftbukkit.jar)</label>
					<input type="text" class="form-control" id="cbfile" name="cbfile" placeholder="Enter Server Jar File">
				</div>
				<!--
				<div class="col-sm-4 form-group">
					<label for="cbinstall">Install CraftBukkit for me</label>
					<select class="form-control" id="cbinstall" name="cbinstall">
						<option value="false">No, don't download and install CraftBukkit</option>
						<option value="rb">Yes, latest CraftBukkit Recommended</option>
						<option value="beta">Yes, latest CraftBukkit Beta</option>
						<option value="dev">Yes, latest CraftBukkit Development</option>
					</select>
				</div>
				 -->
			</div>
			<br />
			<div class="row">
				<div class="col-sm-6 form-group">
					<label for="mcname">Minecraft Username</label>
					<input type="text" class="form-control" id="mcname" name="mcname" placeholder="Enter Username" required>
				</div>
				<div class="col-sm-6 form-group">
					<label for="tmcpass">Password (Don't use your Minecraft password)</label>
					<input type="password" class="form-control" id="tmcpass" name="tmcpass" placeholder="Enter Password" required>
				</div>
			</div>
			<div class="form-group">
				<label for="tmcpassconf">Confirm Password</label>
				<input type="password" class="form-control" id="tmcpassconf" name="tmcpassconf" placeholder="Confirm Password" required>
			</div>
			<br />
			<div class="row">
				<div class="col-sm-6 form-group">
					<label for="licemail">License Email (Optional)</label>
					<input type="text" class="form-control" id="licemail" name="licemail" placeholder="Enter License Email">
				</div>
				<div class="col-sm-6 form-group">
					<label for="lickey">License Key (Optional)</label>
					<input type="text" class="form-control" id="lickey" name="lickey" placeholder="Enter License Key">
				</div>
			</div>
			<button type="submit" id="finishinstall" class="btn btn-primary">Save Settings</button>
		</form>
	</div>
</div>