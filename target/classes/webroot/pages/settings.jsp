<%@taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>

<script>
$(function() {
	$("#editlicense").click(function(e) {
		e.preventDefault();
		
		$("#custommodal").on("shown.bs.modal", function() { $("#licemail").focus(); });
		
		showModalFull("Edit License", "<label for='licemail'>License Email</label><input type=\"text\" class=\"form-control\" id=\"licemail\" placeholder=\"Enter License Email\" onkeydown=\"if (event.keyCode == 13) $('#custommodal .btn').click();\"><br /><label for='lickey'>License Key</label><input type=\"text\" class=\"form-control\" id=\"lickey\" placeholder=\"Enter License Key\" onkeydown=\"if (event.keyCode == 13) $('#custommodal .btn').click();\"><br /><p>It may take a few seconds to update your license</p>", "Save", true);
		
		var clicked;
		
		$("#custommodal .btn-primary").click(function() {
			if (!clicked)
			{
				$("#licenemail").text($("#licemail").val());
				$("#licenkey").text($("#lickey").val());
				
				$.post("/settings/updateLicense", {"licemail":$("#licemail").val(), "lickey":$("#lickey").val()}, function(data) {
					window.location = '/settings/';
				});
				
				clicked = true;
			}
		});
	});
	
	$("#settingsswitch a").click(function(e) {
		var oldId = $("#" + $("#settingsswitch li.active a").attr("forid"));
		var newId = $("#" + $(this).attr("forid"));
		
		$("#settingsswitch li.active").removeClass("active");
		$(this).parent().addClass("active");
		
		if (oldId.attr("id") != newId.attr("id"))
		{
			oldId.fadeOut(400, function(){
				newId.fadeIn(400);
		    });
		}
		
		return false;
	});
	
	$("#settingsform input").each(function() {
		$(this).blur(function() {
			saveServerSettings();
		});
		
		$(this).keydown(function(e) {
		    if (e.keyCode == 13)
		    {
		    	saveServerSettings();
		    }
		});
	});
	
	$("#settingsform select").each(function() {
		$(this).blur(function() {
			saveServerSettings();
		});
	});
	
	$("#propertiesform input").each(function() {
		$(this).blur(function() {
			saveServerProperties();
		});
		
		$(this).keydown(function(e) {
		    if (e.keyCode == 13)
		    {
		    	saveServerProperties();
		    }
		});
	});
	
	$("#propertiesform select").each(function() {
		$(this).blur(function() {
			saveServerProperties();
		});
	});
	
	$("#mcadminform input").each(function() {
		$(this).blur(function() {
			saveMcAdminSettings();
		});
		
		$(this).keydown(function(e) {
		    if (e.keyCode == 13)
		    {
		    	saveMcAdminSettings();
		    }
		});
	});
	
	$("#mcadminform select").each(function() {
		$(this).blur(function() {
			saveMcAdminSettings();
		});
	});
});

function saveServerSettings()
{
	$.ajax("/settings/saveServerSettings", {
		data: $("#settingsform").serialize(),
		type: "POST",
		success: function(data) {
			if (data.good != undefined)
			{
				var n = noty({
		            text        : "<b>Success: </b>" + data.good,
		            type        : 'success',
		            dismissQueue: true,
		            layout      : 'bottomLeft',
		            theme       : 'defaultTheme',
		            timeout     : 2000
		        });
			} else if (data.error != undefined)
			{
				var n = noty({
		            text        : "<b>Error: </b>" + data.error,
		            type        : 'error',
		            dismissQueue: true,
		            layout      : 'bottomLeft',
		            theme       : 'defaultTheme',
		            timeout     : 2000
		        });
			}
		},
		error:  function(data) {
			var n = noty({
	            text        : "<b>Error: </b>Could not save config settings",
	            type        : 'error',
	            dismissQueue: true,
	            layout      : 'bottomLeft',
	            theme       : 'defaultTheme',
	            timeout     : 2000
	        });
		}
	});
}

function saveServerProperties()
{
	$.ajax("/settings/saveServerProperties", {
		data: $("#propertiesform").serialize(),
		type: "POST",
		success: function(data) {
			if (data.good != undefined)
			{
				var n = noty({
		            text        : "<b>Success: </b>" + data.good,
		            type        : 'success',
		            dismissQueue: true,
		            layout      : 'bottomLeft',
		            theme       : 'defaultTheme',
		            timeout     : 2000
		        });
			} else if (data.error != undefined)
			{
				var n = noty({
		            text        : "<b>Error: </b>" + data.error,
		            type        : 'error',
		            dismissQueue: true,
		            layout      : 'bottomLeft',
		            theme       : 'defaultTheme',
		            timeout     : 2000
		        });
			}
		},
		error:  function(data) {
			var n = noty({
	            text        : "<b>Error: </b>Could not save config settings",
	            type        : 'error',
	            dismissQueue: true,
	            layout      : 'bottomLeft',
	            theme       : 'defaultTheme',
	            timeout     : 2000
	        });
		}
	});
}

function saveMcAdminSettings()
{
	$.ajax("/settings/saveMcAdminSettings", {
		data: $("#mcadminform").serialize(),
		type: "POST",
		success: function(data) {
			if (data.good != undefined)
			{
				var n = noty({
		            text        : "<b>Success: </b>" + data.good,
		            type        : 'success',
		            dismissQueue: true,
		            layout      : 'bottomLeft',
		            theme       : 'defaultTheme',
		            timeout     : 2000
		        });
			} else if (data.error != undefined)
			{
				var n = noty({
		            text        : "<b>Error: </b>" + data.error,
		            type        : 'error',
		            dismissQueue: true,
		            layout      : 'bottomLeft',
		            theme       : 'defaultTheme',
		            timeout     : 2000
		        });
			}
		},
		error:  function(data) {
			var n = noty({
	            text        : "<b>Error: </b>Could not save config settings",
	            type        : 'error',
	            dismissQueue: true,
	            layout      : 'bottomLeft',
	            theme       : 'defaultTheme',
	            timeout     : 2000
	        });
		}
	});
}

function deleteServer(e)
{
	e.preventDefault();
	
	showModalFull("Delete Server", "Are you sure you want to delete this server? This cannot be undone.", "Delete", "Close");
	
	var clicked = false;
	
	$("#custommodalButton").click(function() {
		if (!clicked)
		{
			$.post("/server/deleteServer", function(data) {
				if (data.error != undefined)
				{
					var n = noty({
			            text        : "<b>Error: </b>" + data.error,
			            type        : 'error',
			            dismissQueue: true,
			            layout      : 'bottomLeft',
			            theme       : 'defaultTheme',
			            timeout     : 5000
			        });
				} else
				{
					window.location = "/";
				}
			});
			
			clicked = true;
		}
	});
}
</script>

<div class="panel panel-default">
	<div class="panel-heading">
		<h3 class="panel-title">
			<ul id="settingsswitch" class="nav nav-pills">
				<li class="active"><a href="#" forid="serversettings">Server Settings</a></li>
				<li style="float: right;"><a href="#" forid="mcadminsettings">McAdminPanel Settings</a></li>
			</ul>
		</h3>
	</div>
	<div class="panel-body">
		<div id="serversettings" style="padding-left: 0px; padding-right: 0px;">
			<div class="panel panel-default" style="box-shadow: none; border: 1px solid #DADADA; margin-bottom: 15px;">
            	<div class="panel-heading" style="height: 38px;">
              		<h3 class="panel-title" style="float: left;">Server Settings</h3>
              		<span style="float: right;"><a href="/server/deleteServer" onclick="deleteServer(event);" style="color: red !important;">Delete Server</a></span>
            	</div>
            	<div class="panel-body">
					<form id="settingsform" role="form" method="post" action="/">
						<div class="form-group">
							<div class="col-sm-4">
								<label for="servername">Server Name</label><br />
								The name of this server, this is what is displayed in the dropdown list.
							</div>
							<div class="col-sm-8">
								<br />
								<input type="text" class="form-control"
									id="servername" name="servername"
									value="${bukkitServer.getName()}"
									${user.getGroup().hasPermission("server.properties.edit") ? "" : "disabled"}
								>
							</div>
							<br style="clear: both;" />
						</div>
						<div class="form-group">
							<div class="col-sm-4">
								<label for="serverjar">Server Jar</label><br />
								The absolute location of the server's jar.
							</div>
							<div class="col-sm-8">
								<br />
								<input type="text" class="form-control"
									id="serverjar" name="serverjar"
									value="${bukkitServer.getServerJar().getAbsoluteFile()}"
									${user.getGroup().hasPermission("server.properties.edit") ? "" : "disabled"}
								>
							</div>
							<br style="clear: both;" />
						</div>
						<div class="form-group">
							<div class="col-sm-4">
								<label for="minmemory">Minimum Memory</label><br />
								The minimum amount of memory that the server will run on. (Ex. 1024m)
							</div>
							<div class="col-sm-8">
								<br />
								<input type="text" class="form-control"
									id="minmemory" name="minmemory"
									value="${bukkitServer.getMinMemory()}"
									${user.getGroup().hasPermission("server.properties.edit") ? "" : "disabled"}
								>
							</div>
							<br style="clear: both;" />
						</div>
						<div class="form-group">
							<div class="col-sm-4">
								<label for="maxmemory">Maximum Memory</label><br />
								The maximum amount of memory that the server will run with. (Ex. 4096m)
							</div>
							<div class="col-sm-8">
								<br />
								<input type="text" class="form-control"
									id="maxmemory" name="maxmemory"
									value="${bukkitServer.getMaxMemory()}"
									${user.getGroup().hasPermission("server.properties.edit") ? "" : "disabled"}
								>
							</div>
						</div>
					</form>
				</div>
			</div>
			
          	<div class="panel panel-default" style="box-shadow: none; border: 1px solid #DADADA; margin-bottom: 15px;">
            	<div class="panel-heading" style="height: 38px;">
              		<h3 class="panel-title" style="float: left;">Minecraft Properties</h3>
              		<span style="float: right;"><a href="/settings/reload" style="color: #428bca !important;">Reload Properties</a></span>
            	</div>
            	<div class="panel-body">
					<form id="propertiesform" role="form" method="post" action="/">
						<div class="form-group">
							<div class="col-sm-4">
								<label for="serverport">Server Port</label><br />
								The port the server is hosted (listening) on. Make sure you port forward this port.
							</div>
							<div class="col-sm-8">
								<br />
								<input type="number" class="form-control"
									id="serverport" name="serverport"
									value="${bukkitConfig.getString('server-port', '25565')}"
									${user.getGroup().hasPermission("server.properties.edit") ? "" : "disabled"}
								>
							</div>
							<br style="clear: both;" />
						</div>
						<div class="form-group">
							<div class="col-sm-4">
								<label for="servermotd">Server MOTD</label><br />
								Headline to show in the in-game server list.
							</div>
							<div class="col-sm-8">
								<br />
								<input type="text" class="form-control"
									id="servermotd" name="servermotd"
									value="${bukkitConfig.getString('motd', '')}"
									${user.getGroup().hasPermission("server.properties.edit") ? "" : "disabled"}
								>
							</div>
							<br style="clear: both;" />
						</div>
						<div class="form-group">
							<div class="col-sm-4">
								<label for="maxplayers">Maximum Players</label><br />
								How many players to allow on the server.
							</div>
							<div class="col-sm-8">
								<br />
								<input type="number" class="form-control"
									id="maxplayers" name="maxplayers"
									value="${bukkitConfig.getString('max-players', '')}"
									${user.getGroup().hasPermission("server.properties.edit") ? "" : "disabled"}
								>
							</div>
							<br style="clear: both;" />
						</div>
						<div class="form-group">
							<div class="col-sm-4">
								<label for="difficulty">Difficulty</label><br />
								The difficlty for the server, what everybody starts off as.
							</div>
							<div class="col-sm-8">
								<br />
								<select class="form-control" id="difficulty" name="difficulty" ${user.getGroup().hasPermission("server.properties.edit") ? "" : "disabled"}>
									<option value="0">Peaceful</option>
									<option value="1">Easy</option>
									<option value="2">Normal</option>
									<option value="3">Hard</option>
								</select>
								<script>$("#difficulty").val("${bukkitConfig.getString('difficulty', '1')}");</script>
							</div>
							<br style="clear: both;" />
						</div>
						<div class="form-group">
							<div class="col-sm-4">
								<label for="onlinemode">Online Mode</label><br />
								Disable if not connected to internet.
							</div>
							<div class="col-sm-8">
								<br />
								<select class="form-control" id="onlinemode" name="onlinemode" ${user.getGroup().hasPermission("server.properties.edit") ? "" : "disabled"}>
									<option value="true">Yes</option>
									<option value="false">No</option>
								</select>
								<script>$("#onlinemode").val("${bukkitConfig.getString('online-mode', '')}");</script>
							</div>
							<br style="clear: both;" />
						</div>
						<div class="form-group">
							<div class="col-sm-4">
								<label for="enablepvp">Enable PvP</label><br />
								Whether or not Player vs Player is enabled.
							</div>
							<div class="col-sm-8">
								<br />
								<select class="form-control" id="enablepvp" name="enablepvp" ${user.getGroup().hasPermission("server.properties.edit") ? "" : "disabled"}>
									<option value="true">Yes</option>
									<option value="false">No</option>
								</select>
								<script>$("#enablepvp").val("${bukkitConfig.getString('pvp', '')}");</script>
							</div>
							<br style="clear: both;" />
						</div>
						<div class="form-group">
							<div class="col-sm-4">
								<label for="worldtype">World Type</label><br />
								What type of world should be generated by the Minecraft server.
							</div>
							<div class="col-sm-8">
								<br />
								<select class="form-control" id="worldtype" name="worldtype" ${user.getGroup().hasPermission("server.properties.edit") ? "" : "disabled"}>
									<option value="DEFAULT">Default</option>
									<option value="FLAT">Flat</option>
									<option value="LARGEBIOMES">Large Biomes</option>
									<option value="AMPLIFIED">Amplified (1.7 and later)</option>
								</select>
								<script>$("#worldtype").val("${bukkitConfig.getString('level-type', '')}");</script>
							</div>
							<br style="clear: both;" />
						</div>
						<div class="form-group">
							<div class="col-sm-4">
								<label for="seed">World Seed</label><br />
								Seed value to use for the world generator.
							</div>
							<div class="col-sm-8">
								<br />
								<input type="text" class="form-control"
									id="seed" name="seed"
									value="${bukkitConfig.getString('level-seed', '')}"
									${user.getGroup().hasPermission("server.properties.edit") ? "" : "disabled"}
								>
							</div>
							<br style="clear: both;" />
						</div>
						<div class="form-group">
							<div class="col-sm-4">
								<label for="gensettings">World Generator Settings</label><br />
								Controls how the world generator will build the world.
							</div>
							<div class="col-sm-8">
								<br />
								<input type="text" class="form-control"
									id="gensettings" name="gensettings"
									value="${bukkitConfig.getString('generator-settings', '')}"
									${user.getGroup().hasPermission("server.properties.edit") ? "" : "disabled"}
								>
							</div>
							<br style="clear: both;" />
						</div>
						<div class="form-group">
							<div class="col-sm-4">
								<label for="genstructures">Generate Structures</label><br />
								Whether or not NPC villages and other structures should be generated.
							</div>
							<div class="col-sm-8">
								<br />
								<select class="form-control" id="genstructures" name="genstructures" ${user.getGroup().hasPermission("server.properties.edit") ? "" : "disabled"}>
									<option value="true">Yes</option>
									<option value="false">No</option>
								</select>
								<script>$("#genstructures").val("${bukkitConfig.getString('generate-structures', '')}");</script>
							</div>
							<br style="clear: both;" />
						</div>
						<div class="form-group">
							<div class="col-sm-4">
								<label for="spawnmonsters">Spawn Monsters</label><br />
								Whether or not the world will spawn monsters.
							</div>
							<div class="col-sm-8">
								<br />
								<select class="form-control" id="spawnmonsters" name="spawnmonsters" ${user.getGroup().hasPermission("server.properties.edit") ? "" : "disabled"}>
									<option value="true">Yes</option>
									<option value="false">No</option>
								</select>
								<script>$("#spawnmonsters").val("${bukkitConfig.getString('spawn-monsters', '')}");</script>
							</div>
							<br style="clear: both;" />
						</div>
						<div class="form-group">
							<div class="col-sm-4">
								<label for="spawnanimals">Spawn Animals</label><br />
								Whether or not the world will spawn animals.
							</div>
							<div class="col-sm-8">
								<br />
								<select class="form-control" id="spawnanimals" name="spawnanimals" ${user.getGroup().hasPermission("server.properties.edit") ? "" : "disabled"}>
									<option value="true">Yes</option>
									<option value="false">No</option>
								</select>
								<script>$("#spawnmonsters").val("${bukkitConfig.getString('spawn-animals', '')}");</script>
							</div>
							<br style="clear: both;" />
						</div>
						<div class="form-group">
							<div class="col-sm-4">
								<label for="spawnnpc">Spawn NPC's</label><br />
								Whether or not the world will spawn NPC's.
							</div>
							<div class="col-sm-8">
								<br />
								<select class="form-control" id="spawnnpc" name="spawnnpc" ${user.getGroup().hasPermission("server.properties.edit") ? "" : "disabled"}>
									<option value="true">Yes</option>
									<option value="false">No</option>
								</select>
								<script>$("#spawnnpc").val("${bukkitConfig.getString('spawn-npcs', '')}");</script>
							</div>
							<br style="clear: both;" />
						</div>
						<div class="form-group">
							<div class="col-sm-4">
								<label for="announceachiev">Announce Achivements</label><br />
								Whether or not to announce player achivements to the world.
							</div>
							<div class="col-sm-8">
								<br />
								<select class="form-control" id="announceachiev" name="announceachiev" ${user.getGroup().hasPermission("server.properties.edit") ? "" : "disabled"}>
									<option value="true">Yes</option>
									<option value="false">No</option>
								</select>
								<script>$("#announceachiev").val("${bukkitConfig.getString('announce-player-achievements', '')}");</script>
							</div>
							<br style="clear: both;" />
						</div>
						<div class="form-group">
							<div class="col-sm-4">
								<label for="playertimeout">Player Idle Timeout</label><br />
								If not zero, players are kicked from the server if they are idle for more than that many minutes.
							</div>
							<div class="col-sm-8">
								<br />
								<input type="number" class="form-control"
									id="playertimeout" name="playertimeout"
									value="${bukkitConfig.getString('player-idle-timeout', '0')}"
									${user.getGroup().hasPermission("server.properties.edit") ? "" : "disabled"}
								>
							</div>
							<br style="clear: both;" />
						</div>
						<div class="form-group">
							<div class="col-sm-4">
								<label for="maxheight">Max Build Height</label><br />
								How high into the sky players can build.
							</div>
							<div class="col-sm-8">
								<br />
								<input type="number" class="form-control"
									id="maxheight" name="maxheight"
									value="${bukkitConfig.getString('max-build-height', '256')}"
									${user.getGroup().hasPermission("server.properties.edit") ? "" : "disabled"}
								>
							</div>
							<br style="clear: both;" />
						</div>
						<div class="form-group">
							<div class="col-sm-4">
								<label for="viewdistance">View Distance</label><br />
								How many chunks a player is able to view at once. Reduce this value to help improve in-game lag issues.
							</div>
							<div class="col-sm-8">
								<br />
								<select class="form-control" id="viewdistance" name="viewdistance" ${user.getGroup().hasPermission("server.properties.edit") ? "" : "disabled"}>
									<option value="3">3</option>
									<option value="4">4</option>
									<option value="5">5</option>
									<option value="6">6</option>
									<option value="7">7</option>
									<option value="8">8</option>
									<option value="9">9</option>
									<option value="10">10 (Default)</option>
									<option value="11">11</option>
									<option value="12">12</option>
									<option value="13">13</option>
									<option value="14">14</option>
									<option value="15">15</option>
								</select>
								<script>$("#viewdistance").val("${bukkitConfig.getString('view-distance', '')}");</script>
							</div>
							<br style="clear: both;" />
						</div>
						<div class="form-group">
							<div class="col-sm-4">
								<label for="spawnprotection">Spawn Protection</label><br />
								The radius of the area to protect from the spawn.
							</div>
							<div class="col-sm-8">
								<br />
								<input type="number" class="form-control"
									id="spawnprotection" name="spawnprotection"
									value="${bukkitConfig.getString('spawn-protection', '16')}"
									${user.getGroup().hasPermission("server.properties.edit") ? "" : "disabled"}
								>
							</div>
							<br style="clear: both;" />
						</div>
						<div class="form-group">
							<div class="col-sm-4">
								<label for="snooping">Allow Snooping</label><br />
								Allow Mojang to collect information and statistics about your server.
							</div>
							<div class="col-sm-8">
								<br />
								<select class="form-control" id="snooping" name="snooping" ${user.getGroup().hasPermission("server.properties.edit") ? "" : "disabled"}>
									<option value="true">Yes</option>
									<option value="false">No</option>
								</select>
								<script>$("#snooping").val("${bukkitConfig.getString('snooper-enabled', '')}");</script>
							</div>
						</div>
					</form>
				</div>
          	</div>
        </div>
        <div id="mcadminsettings" style="display: none;">
	        <div class="col-sm-7" style="padding-left: 0px; padding-right: 0px;">
	          	<div class="panel panel-default" style="box-shadow: none; border: 1px solid #DADADA; margin-bottom: 15px;">
	            	<div class="panel-heading" style="height: 38px;">
	              		<h3 class="panel-title">McAdminPanel Settings</h3>
	            	</div>
	            	<div class="panel-body">
						<form id="mcadminform" role="form" method="post" action="/">
							<div class="form-group">
								<label for="serverip">Server IP</label>
								<input type="text" class="form-control" id="serverip" name="serverip" placeholder="Enter Server IP" value="${config.getString('server-ip', '')}" required="" ${user.getGroup().hasPermission("mcapanel.properties.edit") ? "" : "disabled"}>
							</div>
							<div class="form-group">
								<label for="adminport">McAdminPanel Port</label>
								<input type="text" class="form-control" id="adminport" name="adminport" placeholder="Enter McAdminPanel Port" value="${config.getString('web-port', '')}" required="" ${user.getGroup().hasPermission("mcapanel.properties.edit") ? "" : "disabled"}>
							</div>
							<div class="form-group">
								<label for=enablewhitelist>Enable Whitelist</label>
								<select class="form-control" id="enablewhitelist" name="enablewhitelist" ${user.getGroup().hasPermission("mcapanel.properties.edit") ? "" : "disabled"}>
									<option value="true">Yes</option>
									<option value="false">No</option>
								</select>
								<script>$("#enablewhitelist").val("${config.getString('enable-whitelist', 'true')}");</script>
							</div>
							<div class="form-group">
								<label for="errorrestart">Restart on Error</label>
								<select class="form-control" id="errorrestart" name="errorrestart" ${user.getGroup().hasPermission("mcapanel.properties.edit") ? "" : "disabled"}>
									<option value="true">Yes</option>
									<option value="false">No</option>
								</select>
								<script>$("#errorrestart").val("${config.getString('restart-on-error', 'true')}");</script>
							</div>
							<div class="form-group">
								<label for="autorestart">Auto Restart (Restarts on stops)</label>
								<select class="form-control" id="autorestart" name="autorestart" ${user.getGroup().hasPermission("mcapanel.properties.edit") ? "" : "disabled"}>
									<option value="true">Yes</option>
									<option value="false">No</option>
								</select>
								<script>$("#autorestart").val("${config.getString('auto-restart', 'true')}");</script>
							</div>
						</form>
					</div>
	          	</div>
	        </div>
	        <div class="col-sm-5" style="padding-right: 0px;">
	          	<div class="panel panel-default" style="box-shadow: none; border: 1px solid #DADADA; margin-bottom: 15px;">
	            	<div class="panel-heading" style="height: 38px;">
	              		<h3 class="panel-title" style="float: left;">License Details</h3>
	              		<a href="#" id="editlicense" style="float: right; color: #428bca !important;">Edit</a>
	              		<br style="clear: both;" />
	            	</div>
	            	<div class="panel-body">
						<c:choose>
							<c:when test="${config.getValue('license-email') == null || config.getValue('license-key') == null}">
								<div class="form-group">
									<label for="email">Email:</label>
									<input type="text" class="form-control" id="email" name="email" placeholder="Enter Email Address" required>
								</div>
								<div class="form-group">
									<label for="licensekey">License Key:</label>
									<input type="text" class="form-control" id="licensekey" name="licensekey" placeholder="Enter License Key" required>
								</div>
							</c:when>
							<c:otherwise>
								<label>Email:</label> <span id="licenemail">${config.getValue('license-email')}</span>
								<br />
								<label>License Key:</label> <span id="licenkey">${config.getValue('license-key')}</span>
								<br />
								<label>License Status:</label> <span style="color: ${a ? 'green;">Active' : 'red;">Not Active'}</span>
								<br />
								<label>Version:</label> ${versions} | <span style="color: ${b ? 'green;">Latest' : 'red;">Outdated'}</span>${!b ? (' | <a href="javascript:void" onclick="showUpdateModal();" style="color: #428bca !important;">Update</a>') : ''}
							</c:otherwise>
						</c:choose>
					</div>
	          	</div>
	        </div>
    	</div>
	</div>
</div>