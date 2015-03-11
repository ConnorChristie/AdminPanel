<%@page pageEncoding="ISO-8859-15" %>
<%@page contentType="text/html; charset=ISO-8859-15" %>
<%@taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>

<jsp:useBean id="now" class="java.util.Date" />

<html lang="en">
	<head>
		<meta http-equiv="Content-Type" content="text/html; charset=ISO-8859-15">
		
		<title>McAdminPanel | ${language.localize(page.substring(0, 1).toUpperCase().concat(page.substring(1)))}</title>
		
		<link rel="icon" type="image/x-icon" href="data:image/x-icon;base64,AAABAAEAEBAAAAEAIABoBAAAFgAAACgAAAAQAAAAIAAAAAEAIAAAAAAAAAQAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAIjNJ/xklNP8AAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAkNUz/JDVM/y5EXv8dKjv/JjdN/xgjMv8AAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAC07TvwzSmf/OVFy/yQ1TP8zSGb/FB4r/xgjMv8YIzL/Fh8t/xklNP8AAAAAAAAAAAAAAAAAAAAAJDVM/zpTdP8kNkz/JDVM/yc5Uf8cKTv/TFBU/x4rPP8gLkD/ITBC/xomNf8eLD7/GCMy/xgjMv8AAAAAAAAAAC1CXP8kNUz/NEto/zpBSP86U3T/OlN0/xwoO/8ZJTT/GCMy/xgjMv8iLTr/GiY1/xgjMv8fLT7/AAAAAAAAAAAdKj7/LkRe/yc5Uf8kNUz/LkRe/yQ1TP8mN0//GiY2/yY3Tf8fLT7/GCMy/yY3Tf8RGSb/IjFF/wAAAAAAAAAALkRe/yQ1TP8sQVv/LEFa/zpTdP8kNUz/HCg7/xklNP8fLT7/GCMy/xolNf8fLT7/JjdN/zg4OP8AAAAAAAAAACQ1TP86U3T/JDVM/xomOP8aJjj/JDVM/ydENP8ZIyf/ERkl/x8tPv8RGSX/JDNI/xgjMv8RGSX/AAAAAAAAAAAkNUz/LkRe/yxBW/8oRTT/OmZE/zpjSf8sTjP/JUEr/yE7J/8RGSX/FB8k/xEZJf8YIzL/GCMy/wAAAAAAAAAAJDVM/xomOP8eLzj/LVA2/zVePf8zWTr/TYhZ/1SUYv8mQiz/GSom/x0zJP8mQiz/GCMy/xEZJf8AAAAAAAAAABwnOP8zWjv/JT44/zlkQf9XmWT/Vphk/0FyS/9do2z/R31S/0d+U/8mRC3/JD8q/yhGMv8dMyL/AAAAAAAAAAA5ZkL/M1s8/1mcZv9Nhln/QHJL/06KW/8/bkn/TIZY/0yGWP9KhFb/TIZY/0h/U/8nRC3/HTMh/wAAAAAAAAAATYhZ/1aYZP9Cdk7/VJVh/0h/U/9TkV//UpFg/0uGWP9KhFb/Q3ZO/06KW/9Ge1D/SH9T/0FzS/8AAAAAAAAAAAAAAAAAAAAAUI1d/0N3Tv9epW3/SYBU/0V8Uf9Tk2H/SoJV/02IWf9XmmX/R35S/AAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAASoFV/0R6UP9HflL/RHlQ/0+JW/9GfVL/AAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAATYlZ/2Wydf8AAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAA/n8AAPgfAADgBwAAgAEAAIABAACAAQAAgAEAAIABAACAAQAAgAEAAIABAACAAQAAgAEAAOAHAAD4HwAA/n8AAA==" />
		
		<link rel="stylesheet" type="text/css" href="/css/bootstrap.css">
		<link rel="stylesheet" type="text/css" href="/css/carousel.css">
		<link rel="stylesheet" type="text/css" href="/css/custom.css">
		<link rel="stylesheet" type="text/css" href="/css/codemirror.css">
		<link rel="stylesheet" type="text/css" href="/css/jquery-linedtextarea.css">
		<link rel="stylesheet" type="text/css" href="/js/jsTree/themes/default/style.min.css">
		<link rel="stylesheet" type="text/css" href="/css/skins/all.css">
		<link rel="stylesheet" type="text/css" href="/css/animate.css">
		
		<script src="/js/jquery.min.js"></script>
		<script src="/js/jquery-ui.min.js"></script>
		<script src="/js/md5.js"></script>
		<script src="/js/bootstrap.js"></script>
		<script src="/js/jquery.dataTables.js"></script>
		<script src="/js/dataTables.bootstrap.js"></script>
		<script src="/js/jquery.textexpand.js"></script>
		<script src="/js/jquery.knob.js"></script>
		<script src="/js/jquery.cookie.js"></script>
		<script src="/js/noty/packaged/jquery.noty.packaged.min.js"></script>
		<script src="/js/noty/themes/relax.js"></script>
		
		<script>
			var canHealfeed = ${user.getGroup().hasPermission("server.players.healfeed") == true};
			var canKill = ${user.getGroup().hasPermission("server.players.kill") == true};
			var canKick = ${user.getGroup().hasPermission("server.players.kick") == true};
			var canBan = ${user.getGroup().hasPermission("server.players.ban") == true};
			
			var canCommand = ${user.getGroup().hasPermission("server.console.issue") == true};
			
			var totalString = "${language.localize('Total:')}";
			var usedString = "${language.localize('Used:')}";
			var freeString = "${language.localize('Free:')}";
			
			var coresString = "${language.localize('Cores:')}";
			var freqString = "${language.localize('Frequency:')}";
			
			function bugAlert()
			{
				$("#custommodal").on("shown.bs.modal", function() { $("#email").focus(); });
				
				showModalFull(
					"${language.localize('Submit a Bug')}",
					'<form id="bugreport"><label for="email">${language.localize("Email")}</label><input type="text" class="form-control" id="email" name="email" placeholder="${language.localize("Enter Email")}" onkeydown="if (event.keyCode == 13) $(\'#custommodal .btn\').click();"><br><label for="description">${language.localize("Description of Bug")}</label><textarea rows="4" class="form-control" id="description" name="description" placeholder="${language.localize("Enter Description")}" onkeydown="if (event.keyCode == 13) $(\'#custommodal .btn\').click();"></textarea><br><p>${language.localize("Thank you for your feedback!$$We may contact you for more information if needed.", "<br />")}</p></form>',
					"${language.localize('Submit Bug')}",
					true
				);
				
				modalClick("#custommodal", function() {
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
				});
			}
		</script>
		
		<script src="/js/viewjs/index.js"></script>
		<script src="/js/elements/modal.js"></script>
		<script src="/js/viewjs/players.js"></script>
		
		<style>
			.item {
		  		image-rendering: pixelated;
			}
			
			.item span {
				text-shadow: 1px 1px black;
				position: absolute;
				bottom: 0;
				right: 0;
				width: 18px;
				height: 20px;
				color: white;
				font-size: 18px;
				font-weight: bold;
			}
		</style>
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
							<a class="navbar-brand" href="/${install ? 'install/' : ''}"><b>McAdminPanel</b></a>
						</div>
						<div class="navbar-collapse collapse">
							<ul id="nav" class="nav navbar-nav" style="float: left;">
								<c:forEach var="tab" items="${tabs}">
									<c:choose>
										<c:when test="${tab == 'home'}">
											<li id="home"><a href="/">${language.localize("Home")}</a></li>
										</c:when>
										<c:when test="${tab != 'settings'}">
											<li id="${tab}"><a href="/${tab}/">${language.localize(tab.substring(0, 1).toUpperCase().concat(tab.substring(1)))}<c:if test="${tab.equalsIgnoreCase('applications') && applications > 0}"> <span id="appBadge" class="badge" style="margin-left: 5px; background-color: rgb(229, 91, 91);">${applications}</span></c:if></a></li>
										</c:when>
									</c:choose>
								</c:forEach>
								<c:if test="${serverTabs != null}">
									<li id="serverTab" class="dropdown">
					                	<a href="#" class="dropdown-toggle" data-toggle="dropdown">Server <span class="caret"></span></a>
					               		<ul class="dropdown-menu" role="menu">
					               			<c:forEach var="serverTab" items="${serverTabs}">
					               				<li><a href="/${serverTab}/">${language.localize(serverTab.substring(0, 1).toUpperCase().concat(serverTab.substring(1)))}</a></li>
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
										<li id="${tab}"><a href="/${tab}/">${language.localize(tab.substring(0, 1).toUpperCase().concat(tab.substring(1)))}</a></li>
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
													
													showModalFull("Add Server", "<input type=\"text\" class=\"form-control\" id=\"serverName\" placeholder=\"Enter Server Name\" onkeydown=\"if (event.keyCode == 13) $('#custommodal .btn').click();\" required><br /><input type=\"text\" class=\"form-control\" id=\"serverJar\" placeholder=\"Enter Server Jar\" onkeydown=\"if (event.keyCode == 13) $('#custommodal .btn').click();\" required>", "Add Server", true);
													
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
										<input type="text" name="username" id="username" placeholder="${language.localize('Minecraft Username')}" class="form-control">
									</div>
									<div class="form-group">
										<input type="password" name="opassword" id="opassword" placeholder="${language.localize('Password')}" class="form-control">
									</div>
									<button type="submit" class="btn btn-success">${language.localize("Login")}</button>
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
					${language.localize("$$New Version:$$ There has been a new version of McAdminPanel released! Please $$update$$ to the latest version!", "<b>", "</b>", "<a href='javascript:void(0)' onclick='showUpdateModal();'>", "</a>")}
				</div>
			</c:if>
			
			<c:if test="${!install && loggedIn && user.getGroup().hasPermission('server.properties.edit') && (bukkitServer.getServerJar() == null || (bukkitServer.getServerJar() != null && !bukkitServer.getServerJar().exists()))}">
				<div class="alert alert-danger" role="alert">
					${language.localize("$$Invalid Server Jar:$$ The server jar for this server could not be found, $$click here$$ to change it.", "<b>", "</b>", "<a href='/settings/'>", "</a>")}
				</div>
			</c:if>
			
			<c:choose>
				<c:when test="${page != null && page != '404'}">
					<div id="mainpage" class="row">
						<div class="col-sm-${(includeSidebar && !install && ((!loggedIn && ap.getGlobalGroup().hasPermission('server.players.view')) || user.getGroup().hasPermission('server.players.view'))) ? '8' : '12'}">
							<jsp:include page="/pages/${page}.jsp" flush="true" />
						</div>
						
						<c:if test="${includeSidebar && !install && ((!loggedIn && ap.getGlobalGroup().hasPermission('server.players.view')) || user.getGroup().hasPermission('server.players.view'))}">
							<div class="col-sm-4">
								<div class="panel panel-default" style="border-bottom: 2px solid ${status.contains('Online') ? '#42CC3D' : '#FF592B'};">
									<div class="panel-heading">
										<h3 class="panel-title">
											${language.localize("Server Status:")} <span id="status">${status}</span>
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
												<b>${language.localize("View All Players")}</b>
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
			
			<div class="alert alert-warning" role="alert" ${install ? "style='margin-bottom: 10px;'" : ""}>
				${language.localize("$$Submitting Bugs:$$ If you find any bugs please report them $$here$$ so we can improve McAdminPanel!", "<b>", "</b>", "<a href='javascript:void(0)' onclick='bugAlert();'>", "</a>")}
			</div>
			
			<c:if test="${!install && ((!loggedIn && ap.getGlobalGroup().hasPermission('server.chat.view')) || user.getGroup().hasPermission('server.chat.view'))}">
				<div class="row">
					<div class="col-sm-12">
						<div class="panel panel-default" style="margin-bottom: 0px;">
							<div class="panel-heading">
								<h3 class="panel-title">
									<c:choose>
										<c:when test="${user.getGroup().hasPermission('server.console.view')}">
											<div class="row">
												<div class="col-sm-3"><ul id="server" class="nav nav-pills"><li class="active"><a href="#" forid="messages">${language.localize("Server Chat")}</a></li></ul></div>
												<div class="col-sm-6" style="text-align: center;">
													<c:if test="${loggedIn && page != 'home' && (user.getGroup().hasPermission('server.controls') || user.getGroup().hasPermission('server.reload'))}">
														<c:choose>
															<c:when test="${user.getGroup().hasPermission('server.controls')}">
																<button type="button" id="startServer" act="startServer" class="actButton btn btn-sm btn-success" ${control.get("startServer") ? "" : "disabled"}>${language.localize("Start Server")}</button>
																<button type="button" id="stopServer" act="stopServer" class="actButton btn btn-sm btn-danger" ${control.get("stopServer") ? "" : "disabled"}>${language.localize("Stop Server")}</button>
																<button type="button" id="reloadServer" act="reloadServer" class="actButton btn btn-sm btn-info" ${control.get("reloadServer") ? "" : "disabled"}>${language.localize("Reload Server")}</button>
																<button type="button" id="restartServer" act="restartServer" class="actButton btn btn-sm btn-primary" ${control.get("restartServer") ? "" : "disabled"}>${language.localize("Restart Server")}</button>
															</c:when>
															<c:when test="${user.getGroup().hasPermission('server.reload')}">
																<button type="button" id="reloadServer" act="reloadServer" class="actButton btn btn-sm btn-info" ${control.get("reloadServer") ? "" : "disabled"}>Reload Server</button>
															</c:when>
														</c:choose>
													</c:if>
												</div>
												<div class="col-sm-3"><ul id="server" class="nav nav-pills"><li id="consolebutton" style="float: right;"><a href="#" forid="console">${language.localize("Server Console")}</a></li></ul></div>
											</div>
										</c:when>
										<c:otherwise>
											${language.localize("Server Chat")}
										</c:otherwise>
									</c:choose>
								</h3>
							</div>
							<div id="chatconsolebody" class="panel-body" style="padding-top: 10px; border-bottom: 2px solid orange;">
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
										<input type="text" class="form-control" name="chatmsg" id="chatmsg" placeholder="${language.localize('Chat Message')}" style="float: left;" required />
										<button type="submit" class="btn btn-primary" id="chatbtn" style="float: right;">${language.localize("Chat")}</button>
									</form>
									
									<script>
										$(function() {
											var width = 1075 - $("#chatbtn").width();
											
											$("#chatmsg").css({"width": width + "px"});
										});
									</script>
								</c:if>
							</div>
						</div>
					</div>
				</div>
			</c:if>
			
			<div class="row" style="margin-top: 10px; margin-bottom: 40px;">
				<div class="col-sm-12">
					<span style="float: left;">${language.localize("Copyright")} &copy; <a href="http://mcapanel.com" target="_blank" style="color: #428bca;">McAdminPanel</a> <%= java.util.Calendar.getInstance().get(java.util.Calendar.YEAR) %></span>
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