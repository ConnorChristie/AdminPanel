<%@taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>

<%@page import="net.tanesha.recaptcha.ReCaptcha" %>
<%@page import="net.tanesha.recaptcha.ReCaptchaFactory" %>
<%@page import="java.util.Properties" %>

<script src="/js/flot/jquery.flot.min.js"></script>
<script src="http://jquery-ui.googlecode.com/svn/tags/latest/ui/jquery.effects.core.js"></script>
<script src="http://jquery-ui.googlecode.com/svn/tags/latest/ui/jquery.effects.slide.js"></script>
<script src="/js/viewjs/home.js"></script>

<div class="panel panel-default">
	<div class="panel-heading">
		<h3 class="panel-title">
			<c:choose>
				<c:when test="${loggedIn && (user.getGroup().hasPermission('server.controls') || user.getGroup().hasPermission('server.reload'))}">
					${language.localize("Server Status:")} <span id="statusTitle">${control.get("statusTitle")}</span>
					<c:if test="${loggedIn && user.getGroup().hasPermission('server.usage')}">
						<span style="float: right;"><a href="/" onclick="return showPercents();" id="apercents">${language.localize("Percents")}</a> | <a href="/" onclick="return showCharts();" id="acharts">${language.localize("Charts")}</a></span>
					</c:if>
				</c:when>
				<c:when test="${loggedIn || !config.getBoolean('enable-whitelist', true)}">
					${language.localize("Server Info")}
				</c:when>
				<c:otherwise>
					${language.localize("Whitelist Application")}
				</c:otherwise>
			</c:choose>
		</h3>
	</div>
	<div id="homepanel" class="panel-body" style="padding-top: 10px; border-bottom: 2px solid #52C5FF;">
		<c:choose>
			<c:when test="${loggedIn || !config.getBoolean('enable-whitelist', true)}">
				<c:choose>
					<c:when test="${user.getGroup().hasPermission('server.controls')}">
						<div class="row" style="padding-bottom: 12px; border-bottom: 1px solid #ddd;">
							<div class="col-sm-3"><button type="button" id="startServer" act="startServer" style="white-space: normal;" class="actButton btn btn-lg btn-success" ${control.get("startServer") ? "" : "disabled"}>${language.localize("Start Server")}</button></div>
							<div class="col-sm-3"><button type="button" id="stopServer" act="stopServer" style="white-space: normal;" class="actButton btn btn-lg btn-danger" ${control.get("stopServer") ? "" : "disabled"}>${language.localize("Stop Server")}</button></div>
							<div class="col-sm-3"><button type="button" id="reloadServer" act="reloadServer" style="white-space: normal;" class="actButton btn btn-lg btn-info" ${control.get("reloadServer") ? "" : "disabled"}>${language.localize("Reload Server")}</button></div>
							<div class="col-sm-3"><button type="button" id="restartServer" act="restartServer" style="white-space: normal;" class="actButton btn btn-lg btn-primary" ${control.get("restartServer") ? "" : "disabled"}>${language.localize("Restart Server")}</button></div>
						</div>
					</c:when>
					<c:when test="${user.getGroup().hasPermission('server.reload')}">
						<div class="row" style="padding-bottom: 12px; border-bottom: 1px solid #ddd;">
							<div class="col-sm-3"><button type="button" id="reloadServer" act="reloadServer" class="actButton btn btn-lg btn-info" ${control.get("reloadServer") ? "" : "disabled"}>Reload Server</button></div>
						</div>
					</c:when>
				</c:choose>
				<c:choose>
					<c:when test="${user.getGroup().hasPermission('server.usage')}">
						<div id="charts" style="margin-top: 10px;">
							<div style="text-align: center;">
								<span style="font-size: 13pt;"><b>${language.localize("CPU Usage")}</b></span>
							</div>
							<div id="cpuchart" style="height: 150px;"></div>
							
							<div style="text-align: center;">
								<span style="font-size: 13pt;"><b>${language.localize("RAM Usage")}</b></span>
							</div>
							<div id="ramchart" style="height: 150px;"></div>
						</div>
						
						<div id="percents" style="padding-top: 10px;">
							<div class="row">
								<div class="col-sm-6">
									<div style="float: left;">
										<span style="font-size: 20pt;"><b>${language.localize("RAM")}</b></span><br />
										<span style="font-size: 11pt;" id="ramtotal"><b>${language.localize("Total:")} ${usage.get("ramTotal")} GB</b></span><br />
										<span style="font-size: 11pt;" id="ramused"><b>${language.localize("Used:")} ${usage.get("ramUsed")} GB</b></span><br />
										<span style="font-size: 11pt;" id="ramfree"><b>${language.localize("Free:")} ${usage.get("ramFree")} GB</b></span>
									</div>
									
									<div style="float: right;">
										<input type="text" class="dial" id="ramcircle" data-width="110" data-min="0" data-max="100" data-thickness=".3" data-readOnly=true />
									</div>
									<script>
										$("#ramcircle").val(${usage.get("ramPercent")}).knob();
									</script>
									
									<br style="clear: both;" />
								</div>
								
								<div class="col-sm-6">
									<div style="float: left;">
										<span style="font-size: 20pt;"><b>${language.localize("CPU")}</b></span><br />
										<span style="font-size: 11pt;" id="cpucores"><b>${language.localize("Cores:")} ${usage.get("cpuCores")}</b></span><br />
										<span style="font-size: 11pt;" id="cpufreq"><b>${language.localize("Frequency:")} ${usage.get("cpuFreq")} GHz</b></span>
									</div>
									
									<div style="float: right;">
										<input type="text" class="dial" id="cpucircle" data-width="110" data-min="0" data-max="100" data-thickness=".3" data-readOnly=true />
									</div>
									<script>
										$("#cpucircle").val(${usage.get("cpuPercent")}).knob();
									</script>
									
								</div>
							</div>
							<c:forEach var="i" begin="1" end="${usage.get('disks').size()}">
								<c:if test="${(d = i - 1) % 2 == 0}">
									<br />
									<div class="row">
								</c:if>
								
								<div class="col-sm-6">
									<div style="float: left;">
										<span style="font-size: 16pt; display: block; width: 230px;" id="disk${d}title"><b>${usage.get('disks')[d].get("diskName")}</b></span>
										<span style="font-size: 11pt;" id="disk${d}total"><b>${language.localize("Total:")} ${usage.get('disks')[d].get("diskTotal")} GB</b></span><br />
										<span style="font-size: 11pt;" id="disk${d}used"><b>${language.localize("Used:")} ${usage.get('disks')[d].get("diskUsed")} GB</b></span><br />
										<span style="font-size: 11pt;" id="disk${d}free"><b>${language.localize("Free:")} ${usage.get('disks')[d].get("diskFree")} GB</b></span>
									</div>
									
									<div style="float: right;">
										<input type="text" class="dial" id="disk${d}circle" data-width="110" data-min="0" data-max="100" data-thickness=".3" data-readOnly=true />
									</div>
									<script>
										$("#disk${d}circle").val(${usage.get('disks')[d].get("diskPercent")}).knob();
									</script>
									
									<br style="clear: both;" />
								</div>
								
								<c:if test="${d % 2 != 0 || usage.get('disks')[d + 1] == null}">
									</div>
								</c:if>
							</c:forEach>
						</div>
					</c:when>
					<c:otherwise>
						${homepage}
					</c:otherwise>
				</c:choose>
			</c:when>
			<c:otherwise>
				<form id="whitelistform" role="form" method="post" action="/">
					<input type="hidden" id="mcpass" name="mcpass" value="" />
					<input type="hidden" id="mcpassconf" name="mcpassconf" value="" />
					<div class="form-group">
						<label for="mcname">${language.localize("Minecraft Username")}</label>
						<input type="text" class="form-control" id="mcname" name="mcname" placeholder="${language.localize('Enter Username')}" required>
					</div>
					<div class="form-group">
						<label for="tmcpass">${language.localize("Password (Don't use your Minecraft password)")}</label>
						<input type="password" class="form-control" id="tmcpass" name="tmcpass" placeholder="${language.localize('Enter Password')}" required>
					</div>
					<div class="form-group">
						<label for="tmcpassconf">${language.localize("Confirm Password")}</label>
						<input type="password" class="form-control" id="tmcpassconf" name="tmcpassconf" placeholder="${language.localize('Confirm Password')}" required>
					</div>
					<div class="form-group">
						<label for="mcdesc">${language.localize("Why should we let you in?")}</label>
						<input type="textfield" class="form-control" id="mcdesc" name="mcdesc" placeholder="${language.localize('Why do you want to join?')}" required>
					</div>
					<div class="form-group">
						<%
						ReCaptcha c = ReCaptchaFactory.newReCaptcha("6Lef1NYSAAAAAJKl-kM3Tlnw9pK6ewnsTe5krVQM", "6Lef1NYSAAAAAGuSgXGbIxyRzDffKiSn-rhEb3xL", false);
						Properties props = new Properties();
						
						props.put("theme", "white");
						%>
						
						<%= c.createRecaptchaHtml(null, props) %>
					</div>
					<button type="submit" id="submitapp" class="btn btn-primary">${language.localize("Submit Application")}</button>
				</form>
			</c:otherwise>
		</c:choose>
	</div>
</div>

