<%@page pageEncoding="UTF-8" %>

<%@taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>

<jsp:useBean id="now" class="java.util.Date" />

<html lang="en">
	<head>
		<meta charset="utf-8">
		
		<title>McAdminPanel | ${page.substring(0, 1).toUpperCase()}${page.substring(1)}</title>
		
		<link rel="icon" type="image/x-icon" href="data:image/x-icon;base64,AAABAAEAEBAAAAEAIABoBAAAFgAAACgAAAAQAAAAIAAAAAEAIAAAAAAAAAQAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAIjNJ/xklNP8AAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAkNUz/JDVM/y5EXv8dKjv/JjdN/xgjMv8AAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAC07TvwzSmf/OVFy/yQ1TP8zSGb/FB4r/xgjMv8YIzL/Fh8t/xklNP8AAAAAAAAAAAAAAAAAAAAAJDVM/zpTdP8kNkz/JDVM/yc5Uf8cKTv/TFBU/x4rPP8gLkD/ITBC/xomNf8eLD7/GCMy/xgjMv8AAAAAAAAAAC1CXP8kNUz/NEto/zpBSP86U3T/OlN0/xwoO/8ZJTT/GCMy/xgjMv8iLTr/GiY1/xgjMv8fLT7/AAAAAAAAAAAdKj7/LkRe/yc5Uf8kNUz/LkRe/yQ1TP8mN0//GiY2/yY3Tf8fLT7/GCMy/yY3Tf8RGSb/IjFF/wAAAAAAAAAALkRe/yQ1TP8sQVv/LEFa/zpTdP8kNUz/HCg7/xklNP8fLT7/GCMy/xolNf8fLT7/JjdN/zg4OP8AAAAAAAAAACQ1TP86U3T/JDVM/xomOP8aJjj/JDVM/ydENP8ZIyf/ERkl/x8tPv8RGSX/JDNI/xgjMv8RGSX/AAAAAAAAAAAkNUz/LkRe/yxBW/8oRTT/OmZE/zpjSf8sTjP/JUEr/yE7J/8RGSX/FB8k/xEZJf8YIzL/GCMy/wAAAAAAAAAAJDVM/xomOP8eLzj/LVA2/zVePf8zWTr/TYhZ/1SUYv8mQiz/GSom/x0zJP8mQiz/GCMy/xEZJf8AAAAAAAAAABwnOP8zWjv/JT44/zlkQf9XmWT/Vphk/0FyS/9do2z/R31S/0d+U/8mRC3/JD8q/yhGMv8dMyL/AAAAAAAAAAA5ZkL/M1s8/1mcZv9Nhln/QHJL/06KW/8/bkn/TIZY/0yGWP9KhFb/TIZY/0h/U/8nRC3/HTMh/wAAAAAAAAAATYhZ/1aYZP9Cdk7/VJVh/0h/U/9TkV//UpFg/0uGWP9KhFb/Q3ZO/06KW/9Ge1D/SH9T/0FzS/8AAAAAAAAAAAAAAAAAAAAAUI1d/0N3Tv9epW3/SYBU/0V8Uf9Tk2H/SoJV/02IWf9XmmX/R35S/AAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAASoFV/0R6UP9HflL/RHlQ/0+JW/9GfVL/AAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAATYlZ/2Wydf8AAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAA/n8AAPgfAADgBwAAgAEAAIABAACAAQAAgAEAAIABAACAAQAAgAEAAIABAACAAQAAgAEAAOAHAAD4HwAA/n8AAA==" />
		
		<link rel="stylesheet" type="text/css" href="/css/bootstrap.css">
		<link rel="stylesheet" type="text/css" href="/css/carousel.css">
		<link rel="stylesheet" type="text/css" href="/css/custom.css">
		<link rel="stylesheet" type="text/css" href="/css/codemirror.css">
		<link rel="stylesheet" type="text/css" href="/css/jquery-linedtextarea.css">
		<link rel="stylesheet" type="text/css" href="/js/jsTree/themes/default/style.min.css">
		<link rel="stylesheet" type="text/css" href="/css/skins/all.css">
		
		<script src="/js/jquery.min.js"></script>
		<script src="/js/md5.js"></script>
		<script src="/js/bootstrap.js"></script>
		<script src="/js/jquery.dataTables.js"></script>
		<script src="/js/dataTables.bootstrap.js"></script>
		<script src="/js/jquery.textexpand.js"></script>
		<script src="/js/jquery.knob.js"></script>
		<script src="/js/jquery.cookie.js"></script>
		<script src="/js/noty/packaged/jquery.noty.packaged.min.js"></script>
		<script src="/js/noty/themes/bootstrap.js"></script>
		
		<script src="/js/viewjs/index.js"></script>
		<script src="/js/elements/modal.js"></script>
		
		<script>
			var canHealfeed = ${user.getGroup().hasPermission("server.players.healfeed") == true};
			var canKill = ${user.getGroup().hasPermission("server.players.kill") == true};
			var canKick = ${user.getGroup().hasPermission("server.players.kick") == true};
			var canBan = ${user.getGroup().hasPermission("server.players.ban") == true};
			
			var canCommand = ${user.getGroup().hasPermission("server.console.issue") == true};
			
			function bugAlert()
			{
				$("#custommodal").on("shown.bs.modal", function() { $("#email").focus(); });
				
				showModalFull(
					"Submit a Bug",
					'<form id="bugreport"><label for="email">Email</label><input type="text" class="form-control" id="email" name="email" placeholder="Enter Email" onkeydown="if (event.keyCode == 13) $(\'#custommodal .btn\').click();"><br><label for="description">Description of Bug</label><textarea rows="4" class="form-control" id="description" name="description" placeholder="Enter Description" onkeydown="if (event.keyCode == 13) $(\'#custommodal .btn\').click();"></textarea><br><p>Thank you for your feedback!<br />We may contact you for more information if needed.</p></form>',
					"Submit Bug",
					true
				);
				
				var clicked;
				
				$("#custommodal .btn-primary").click(function() {
					if (!clicked)
					{
						var data = {};
						$("#bugreport").serializeArray().map(function(x){data[x.name] = x.value;});
						
						$.post("http://mcapanel.com/submitbug.php", {data: JSON.stringify(data)}, function(ret) {
							if (ret == "good")
							{
								var n = noty({
						            text        : "<b>Success: </b> Successfully submitted your bug. Thanks for helping!",
						            type        : 'success',
						            dismissQueue: true,
						            layout      : 'bottomLeft',
						            theme       : 'defaultTheme',
						            timeout     : 3000
						        });
							} else
							{
								var n = noty({
						            text        : "<b>Error: </b> Could not submit your bug at this time...",
						            type        : 'error',
						            dismissQueue: true,
						            layout      : 'bottomLeft',
						            theme       : 'defaultTheme',
						            timeout     : 3000
						        });
							}
						});
						
						clicked = true;
					}
				});
			}
		</script>
		
		<script src="/js/viewjs/players.js"></script>
	</head>
	<body style="background: url(/images/diamond_upholstery.png);">
		<div class="navbar-wrapper">
			<div class="container">
				<div class="navbar navbar-default navbar-static-top"
					role="navigation">
					<div class="container">
						<div class="navbar-header">
							<button type="button" class="navbar-toggle" data-toggle="collapse"
								data-target=".navbar-collapse">
								<span class="sr-only">Toggle navigation</span>
								<span class="icon-bar"></span>
								<span class="icon-bar"></span>
								<span class="icon-bar"></span>
							</button>
							<a class="navbar-brand" href="/"><b>McAdminPanel</b></a>
						</div>
						<div class="navbar-collapse collapse">
							<ul id="nav" class="nav navbar-nav" style="float: left;">
								<c:forEach var="tab" items="${tabs}">
									<c:choose>
										<c:when test="${tab == 'home'}">
											<li id="home"><a href="/">Home</a></li>
										</c:when>
										<c:when test="${tab != 'settings'}">
											<li id="${tab}"><a href="/${tab}/">${tab.substring(0, 1).toUpperCase()}${tab.substring(1)}<c:if test="${tab.equalsIgnoreCase('applications') && applications > 0}"> <span id="appBadge" class="badge" style="margin-left: 5px; background-color: rgb(229, 91, 91);">${applications}</span></c:if></a></li>
										</c:when>
									</c:choose>
								</c:forEach>
								<c:if test="${webTabs != null}">
									<li id="serverTab" class="dropdown">
					                	<a href="#" class="dropdown-toggle" data-toggle="dropdown">Server <span class="caret"></span></a>
					               		<ul class="dropdown-menu" role="menu">
					               			<c:forEach var="serverTab" items="${serverTabs}">
					               				<li><a href="/${serverTab}/">${serverTab.substring(0, 1).toUpperCase()}${serverTab.substring(1)}</a></li>
					               			</c:forEach>
					                	</ul>
					              	</li>
								</c:if>
								<c:if test="${webTabs != null}">
									<li id="webTab" class="dropdown">
					                	<a href="#" class="dropdown-toggle" data-toggle="dropdown">Web <span class="caret"></span></a>
					               		<ul class="dropdown-menu" role="menu">
					               			<c:forEach var="webTab" items="${webTabs}">
					               				<li><a href="/${webTab}/">${webTab.substring(0, 1).toUpperCase()}${webTab.substring(1)}</a></li>
					               			</c:forEach>
					                	</ul>
					              	</li>
								</c:if>
								<c:forEach var="tab" items="${tabs}">
									<c:if test="${tab == 'settings'}">
										<li id="${tab}"><a href="/${tab}/">${tab.substring(0, 1).toUpperCase()}${tab.substring(1)}</a></li>
									</c:if>
								</c:forEach>
							</ul>
							<c:if test="${!install && loggedIn}">
								<ul id="nav" class="nav navbar-nav" style="float: right;">
									<li class="dropdown" style="float: right;">
					                	<a href="#" class="dropdown-toggle" data-toggle="dropdown" style="font-size: 18px; color: #09587B;">
					                		<b>[${user.getGroup().getGroupName()}] ${user.getUsername()} <span class="caret"></span></b>
					                	</a>
					               		<ul class="dropdown-menu" role="menu" style="width: 100%;">
					               			<li><a href="/user/logout">Logout</a></li>
					                	</ul>
					              	</li>
								</ul>
								
								<c:if test="${a}">
									<select id="serverSelect" class="form-control" style="width: 200px; height: 30px; margin-top: 10px; margin-right: 10px; float: right;">
										${servers}
									</select>
								
									<script>
										$(function() {
											var pervServer;
											
											$("#serverSelect").on("focus", function() {
												pervServer = this.value;
											}).change(function() {
												if ($(this).val() == "addServer")
												{
													$("#serverSelect").val(pervServer);
													$("#custommodal").on("shown.bs.modal", function() { $("#serverName").focus(); });
													
													showModalFull("Add Server", "<input type=\"text\" class=\"form-control\" id=\"serverName\" placeholder=\"Enter Server Name\" onkeydown=\"if (event.keyCode == 13) $('#custommodal .btn').click();\"><br /><input type=\"text\" class=\"form-control\" id=\"serverJar\" placeholder=\"Enter Server Jar\" onkeydown=\"if (event.keyCode == 13) $('#custommodal .btn').click();\">", "Add Server", true);
													
													$("#custommodal .btn-primary").click(function() {
														$.post("/server/addServer", {"serverName": $("#serverName").val(), "serverJar": $("#serverJar").val()}, function(data) {
															if (data.good != undefined)
															{
																location.reload(true);
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
														});
													});
												} else if ($(this).val().indexOf("server") != -1)
												{
													$.post("/server/selectServer", {"serverId": $(this).val().replace("server", "")}, function(data) {
														location.reload(true);
													});
												}
											});
										});
									</script>
								</c:if>
							</c:if>
							<c:if test="${!install && !loggedIn}">
								<form class="navbar-form navbar-right" role="form" id="loginForm" method="post" action="<%= request.getPathInfo() %>" onsubmit="$('#password').val(md5($('#opassword').val()))">
									<input type="hidden" name="password" id="password" />
									<div class="form-group">
										<input type="text" name="username" id="username" placeholder="Minecraft Username" class="form-control">
									</div>
									<div class="form-group">
										<input type="password" name="opassword" id="opassword" placeholder="Password" class="form-control">
									</div>
									<button type="submit" class="btn btn-success">Login</button>
								</form>
							</c:if>
						</div>
					</div>
				</div>
			</div>
		</div>
		
		<div class="container marketing" style="top: 93px;">
			<c:if test="${loggedIn && !b && user.getGroup().hasPermission('mcapanel.properties.edit')}">
				<div class="alert alert-warning" role="alert">
					<b>New Version:</b> There has been a new version of McAdminPanel released! Please <a href="javascript:void(0)" onclick="showUpdateModal();">update</a> to the latest version!
				</div>
			</c:if>
			
			<c:if test="${!install && loggedIn && user.getGroup().hasPermission('server.properties.edit') && (bukkitServer.getServerJar() == null || (bukkitServer.getServerJar() != null && !bukkitServer.getServerJar().exists()))}">
				<div class="alert alert-danger" role="alert">
					<b>Invalid Server Jar:</b> The server jar for this server could not be found, <a href="/settings/">click here</a> to change it.
				</div>
			</c:if>
			
			<c:if test="${versions.contains('1.0.0')}">
				<div class="alert alert-warning" role="alert">
					<b>Submitting Bugs:</b> If you find any bugs please report them <a href="javascript:void(0)" onclick="bugAlert();">here</a> so we can improve McAdminPanel!
				</div>
			</c:if>
			
			<c:choose>
				<c:when test="${page != null && page != '404'}">
					<div id="mainpage" class="row">
						<div class="col-sm-${(includeSidebar && !install && (!loggedIn || user.getGroup().hasPermission('server.players.view'))) ? '8' : '12'}">
							<jsp:include page="/pages/${page}.jsp" flush="true" />
						</div>
						
						<c:if test="${includeSidebar && !install && (!loggedIn || user.getGroup().hasPermission('server.players.view'))}">
							<div class="col-sm-4">
								<div class="panel panel-default">
									<div class="panel-heading">
										<h3 class="panel-title">
											Server Status: <span id="status">${status}</span>
											<span id="ponline" style="float: right;">${online} / ${total}</span>
										</h3>
									</div>
									<div class="panel-body" style="padding: 0px;">
										<table id="oplayertable" class="table" style="margin-bottom: 0px;">
											<thead>
												<tr>
													<th class="img"></th>
													<th>Username</th>
													<th>Group</th>
													<th>World</th>
												</tr>
											</thead>
											<tbody id="playerson">
												${plist}
											</tbody>
										</table>
										
										<a href="/players/">
											<div style="background-color: #E7E7E7; padding: 4px; text-align: center;">
												<b>View All Players</b>
											</div>
										</a>
									</div>
								</div>
							</div>
						</c:if>
					</div>
				</c:when>
				<c:otherwise>
					<jsp:include page="/errors/404.jsp" flush="true" />
				</c:otherwise>
			</c:choose>
			
			<c:if test="${!install && (!loggedIn || user.getGroup().hasPermission('server.chat.view'))}">
				<div class="row">
					<div class="col-sm-12">
						<div class="panel panel-default" style="margin-bottom: 0px;">
							<div class="panel-heading">
								<h3 class="panel-title">
									<c:choose>
										<c:when test="${user.getGroup().hasPermission('server.console.view')}">
											<div class="row">
												<div class="col-sm-2"><ul id="server" class="nav nav-pills"><li class="active"><a href="#" forid="messages">Server Chat</a></li></ul></div>
												<div class="col-sm-8" style="text-align: center;">
													<c:if test="${loggedIn && page != 'home' && (user.getGroup().hasPermission('server.controls') || user.getGroup().hasPermission('server.reload'))}">
														<c:choose>
															<c:when test="${user.getGroup().hasPermission('server.controls')}">
																<button type="button" id="startServer" act="startServer" class="actButton btn btn-sm btn-success" ${control.get("startServer") ? "" : "disabled"}>Start Server</button>
																<button type="button" id="stopServer" act="stopServer" class="actButton btn btn-sm btn-danger" ${control.get("stopServer") ? "" : "disabled"}>Stop Server</button>
																<button type="button" id="restartServer" act="restartServer" class="actButton btn btn-sm btn-primary" ${control.get("restartServer") ? "" : "disabled"}>Restart Server</button>
																<button type="button" id="reloadServer" act="reloadServer" class="actButton btn btn-sm btn-info" ${control.get("reloadServer") ? "" : "disabled"}>Reload Server</button>
															</c:when>
															<c:when test="${user.getGroup().hasPermission('server.reload')}">
																<button type="button" id="reloadServer" act="reloadServer" class="actButton btn btn-sm btn-info" ${control.get("reloadServer") ? "" : "disabled"}>Reload Server</button>
															</c:when>
														</c:choose>
													</c:if>
												</div>
												<div class="col-sm-2"><ul id="server" class="nav nav-pills"><li id="consolebutton" style="float: right;"><a href="#" forid="console">Server Console</a></li></ul></div>
											</div>
											
											<!--
											<ul id="server" class="nav nav-pills">
												<li class="active" style="width: 22.5%; float: left;"><a href="#" forid="messages">Server Chat</a></li>
												
												<li style="width: 55%; float: left;">
													<span style="padding: 9px;">Server Status: <span id="statusTitle">${control.get("statusTitle")}</span></span>
													<button type="button" id="startServer" act="startServer" class="actButton btn btn-sm btn-success" ${control.get("startServer") ? "" : "disabled"}>Start Server</button>
													<button type="button" id="stopServer" act="stopServer" class="actButton btn btn-sm btn-danger" ${control.get("stopServer") ? "" : "disabled"}>Stop Server</button>
													<button type="button" id="restartServer" act="restartServer" class="actButton btn btn-sm btn-primary" ${control.get("restartServer") ? "" : "disabled"}>Restart Server</button>
													<button type="button" id="reloadServer" act="reloadServer" class="actButton btn btn-sm btn-info" ${control.get("reloadServer") ? "" : "disabled"}>Reload Server</button>
												</li>
												
												<li id="consolebutton" style="width: 22.5%; float: right;"><a href="#" forid="console">Server Console</a></li>
											</ul>
											-->
										</c:when>
										<c:otherwise>
											Server Chat
										</c:otherwise>
									</c:choose>
								</h3>
							</div>
							<div id="chatconsolebody" class="panel-body" style="padding-top: 10px;">
								<div id="messages" style="line-height: 24px; font-size: 16px; max-height: 175px; overflow-y: auto;">
									${chats}
								</div>
								
								<c:if test="${user.getGroup().hasPermission('server.console.view')}">
									<div id="console" style="line-height: 24px; max-height: 188px; overflow-y: auto; display: none;">
										<pre>${console}</pre>
									</div>
								</c:if>
								
								<c:if test="${user.getGroup().hasPermission('server.chat.issue')}">
									<form style="margin-top: 10px;" id="chatform">
										<input type="text" class="form-control" name="chatmsg" id="chatmsg" placeholder="Chat Message" style="float: left;" required />
										<button type="submit" class="btn btn-primary" id="chatbtn" style="float: right;">Chat</button>
									</form>
								</c:if>
							</div>
						</div>
					</div>
				</div>
			</c:if>
			
			<div class="row" style="margin-top: 10px; margin-bottom: 40px;">
				<div class="col-sm-12">
					<span style="float: left;">Copyright &copy; <a href="http://mcapanel.com" target="_blank" style="color: #428bca;">McAdminPanel</a> <%= java.util.Calendar.getInstance().get(java.util.Calendar.YEAR) %></span>
					<span style="float: right;">McAdminPanel ${versions}</span>
				</div>
			</div>
		</div>
		
		<div class="modal fade" id="custommodal" tabindex="-1" role="dialog" aria-labelledby="custommodalLabel" aria-hidden="true">
			<div class="modal-dialog">
				<div class="modal-content">
					<div class="modal-header">
						<button type="button" class="close" data-dismiss="modal"><span aria-hidden="true">&times;</span><span class="sr-only">Close</span></button>
						<h4 class="modal-title" id="custommodalLabel">Title</h4>
					</div>
					<div id="custommodalBody" class="modal-body">
						Body
					</div>
					<div class="modal-footer">
						<button type="button" id="custommodalButton" class="btn btn-primary" data-dismiss="modal">Button</button>
					</div>
				</div>
			</div>
		</div>
		
		<div id="contextMenu" class="dropdown clearfix" style="display: none; position: absolute;">
			<ul class="dropdown-menu" role="menu" aria-labelledby="dropdownMenu" style="display: block; position: static; margin-bottom: 5px;">
				<li><a href="#">Action</a></li>
				<li><a href="#">Another action</a></li>
				<li><a href="#">Something else here</a></li>
				<li class="divider"></li>
				<li><a href="#">Separated link</a></li>
			</ul>
		</div>
	</body>
</html>