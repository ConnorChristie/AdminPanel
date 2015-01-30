var clickedObject;
var contextClick;

var unloading = false;

var cpuPlot;
var ramPlot;

var cpuSeries;
var ramSeries;

var cpuGraphData = [];
var ramGraphData = [];

function updateCpuGraph(val)
{
	if (cpuGraphData.length > 100)
	{
		cpuGraphData = cpuGraphData.slice(1);
	}
	
	cpuGraphData.push(val);
	
	var res = [];
	
	for (var i = 100; i >= 0; i--)
	{
		res.push([100 - i, cpuGraphData[cpuGraphData.length - i - 1]]);
	}

	return res;
}

function updateRamGraph(val)
{
	if (ramGraphData.length > 100)
	{
		ramGraphData = ramGraphData.slice(1);
	}
	
	ramGraphData.push(val);
	
	var res = [];
	
	for (var i = 100; i >= 0; i--)
	{
		res.push([100 - i, ramGraphData[ramGraphData.length - i - 1]]);
	}

	return res;
}

$(function() {
	var $contextMenu = $("#contextMenu");
	
	var drawn = false;
	var foundId = false;
	
	if ($("#cpuchart").length)
	{
		var container = $("#cpuchart");
		
		cpuSeries = [{
			data: [],
			lines: {
				fill: true
			}
		}];
	
		cpuPlot = $.plot(container, cpuSeries, {
			grid: {
				borderWidth: 1,
				minBorderMargin: 0,
				labelMargin: 10,
				backgroundColor: {
					colors: ["#fff", "#e4f4f4"]
				},
				margin: {
					top: 0,
					bottom: 0,
					left: 10,
					right: 20
				},
				markings: function(axes) {
					var markings = [];
					var xaxis = axes.xaxis;
					
					for (var x = Math.floor(xaxis.min); x < xaxis.max; x += xaxis.tickSize * 2)
					{
						markings.push({ xaxis: { from: x, to: x + xaxis.tickSize }, color: "rgba(232, 232, 255, 0.2)" });
					}
					
					return markings;
				}
			},
			xaxis: {
				min: 0,
				max: 100,
				tickFormatter: function(x) {
					return "-" + (100 - x) + "s";
				}
			},
			yaxis: {
				min: 0,
				max: 100,
				tickFormatter: function(x) {
					return x + "%";
				}
			}
		});
	}
	
	if ($("#ramchart").length)
	{
		var container = $("#ramchart");
		
		ramSeries = [{
			data: [],
			lines: {
				fill: true
			}
		}];
	
		ramPlot = $.plot(container, ramSeries, {
			grid: {
				borderWidth: 1,
				minBorderMargin: 0,
				labelMargin: 10,
				backgroundColor: {
					colors: ["#fff", "#e4f4f4"]
				},
				margin: {
					top: 0,
					bottom: 10,
					left: 10,
					right: 20
				},
				markings: function(axes) {
					var markings = [];
					var xaxis = axes.xaxis;
					
					for (var x = Math.floor(xaxis.min); x < xaxis.max; x += xaxis.tickSize * 2)
					{
						markings.push({ xaxis: { from: x, to: x + xaxis.tickSize }, color: "rgba(232, 232, 255, 0.2)" });
					}
					
					return markings;
				}
			},
			xaxis: {
				min: 0,
				max: 100,
				tickFormatter: function(x) {
					return "-" + (100 - x) + "s";
				}
			},
			yaxis: {
				min: 0,
				max: 100,
				tickFormatter: function(x) {
					return x + "%";
				}
			}
		});
	}
	
	$("#nav").children("li").each(function() {
		if (window.location.pathname.indexOf((this.id == "/players" ? "/player" : (this.id == "/plugins" ? "/plugin" : this.id))) >= 0)
		{
			foundId = true;
			
			$("#" + this.id).addClass("active");
		} else if (window.location.pathname == "/" || window.location.pathname == "/index.html")
		{
			$("#home").addClass("active");
		} else if (window.location.pathname.indexOf("/groups") >= 0 || window.location.pathname.indexOf("/users") >= 0 || window.location.pathname.indexOf("/messages") >= 0)
		{
			$("#webTab").addClass("active");
		} else if (window.location.pathname.indexOf("/players") >= 0 || window.location.pathname.indexOf("/plugins") >= 0 || window.location.pathname.indexOf("/backups") >= 0)
		{
			$("#serverTab").addClass("active");
		}
	});
	
	if (!foundId && window.location.pathname != "/"  && window.location.pathname != "/index.html")
	{
		$("#home").removeClass("active");
	}
	
	$("#oplayertable tbody").on("click", "tr", function(e) {
		clickPlayer($(this).attr("uuid"), e);
		
		return false;
	});
	
	resize();
	
	$(window).resize(function() {
		resize();
	});
	
	$(document).click(function () {
		if (!contextClick)
		{
			$contextMenu.css({
				display: "none"
			});
		}
		
		contextClick = false;
	});
	
	$("#loginForm").submit(function(e) {
		e.preventDefault();
		
		$.ajax({
			type: "post",
			url: "/user/login/",
			data: {"username":$("#loginForm").find("#username").val(), "password":$("#loginForm").find("#password").val()},
			success: function(data) {
				if (data.good != undefined)
				{
					document.location = $("#loginForm").attr("action");
				} else if (data.error != undefined)
				{
					errorModal(data.error);
				}
			}
		});
	});
	
	$("#chatform").submit(function(e) {
		e.preventDefault();
		
		if ($("#messages").css("display") == "block")
		{
			issueChat();
		} else if ($("#console").css("display") == "block")
		{
			issueCommand();
		}
	});
	
	var which = $.cookie("messagesconsole");
	
	if (which == undefined)
		$.cookie("messagesconsole", "messages", { expires: 7, path: '/' });
	
	if ($("#console").length == 0 || $("#console").text().trim() == "${console}")
		$.cookie("messagesconsole", "messages", { expires: 7, path: '/' });
	
	which = $.cookie("messagesconsole");
	
	if (which == "console" && $("#console").length != 0)
	{
		$("#messages").css({"display":"none"});
		$("#console").css({"display":"block"});
		
		if (!canCommand) $("#chatform").hide();
		
		$("#chatmsg").attr("placeholder", "Console Command");
		$("#chatbtn").text("Send");
		
		$("#console").parent().css({"background-color":"black"});
		
		$("#server li.active").removeClass("active");
		$("#consolebutton").addClass("active");
		
		var height = $("#console")[0].scrollHeight;
		$("#console").scrollTop(height);
	} else
		$.cookie("messagesconsole", "messages", { expires: 7, path: '/' });
	
	$("#server a").click(function(e) {
		var oldId = $("#" + $("#server li.active a").attr("forid"));
		var newId = $("#" + $(this).attr("forid"));
		
		$("#server li.active").removeClass("active");
		$(this).parent().addClass("active");
		
		oldId.slideUp(function() {
			newId.slideDown(function() {
				var height = newId[0].scrollHeight;
				newId.scrollTop(height);
				
				resize();
			});
		});
		
		if ($(this).attr("forid") == "console")
		{
			if (!canCommand) $("#chatform").hide();
			
			$("#chatmsg").attr("placeholder", "Console Command");
			$("#chatbtn").text("Send");
			
			$("#chatconsolebody").css({"background-color":"black"});
		} else if ($(this).attr("forid") == "messages")
		{
			$("#chatform").show();
			
			$("#chatmsg").attr("placeholder", "Chat Message");
			$("#chatbtn").text("Chat");
			
			$("#chatconsolebody").css({"background-color":"white"});
		}
		
		$.cookie("messagesconsole", $(this).attr("forid"), { expires: 7, path: '/' });
		
		return false;
	});
	
	if ($("#messages").text().indexOf("No Chats") >= 0)
		$("#messages").css({"text-align": "center"});
	else
		$("#messages").css({"text-align": "left"});
	
	if ($("#console").text().indexOf("No Console Data") >= 0)
		$("#console").css({"text-align": "center"});
	else
		$("#console").css({"text-align": "left"});
	
	var msgs = $("#messages");
	
	if (msgs.length > 0)
	{
		var height = msgs[0].scrollHeight;
		
		msgs.scrollTop(height);
	}
	
	$(".actButton").click(function() {
		$.post("/event/system/" + $(this).attr("act"), function(data) {
			if (data.good == undefined && data.error != undefined)
			{
				errorModal(data.error);
			}
			
			if (data.control != undefined)
			{
				var control = data.control;
				
				$("#statusTitle").html(control.statusTitle);
				
				if (control.startServer)
					$("#startServer").removeAttr("disabled");
				else
					$("#startServer").attr("disabled", "");
				
				if (control.stopServer)
					$("#stopServer").removeAttr("disabled");
				else
					$("#stopServer").attr("disabled", "");
				
				if (control.restartServer)
					$("#restartServer").removeAttr("disabled");
				else
					$("#restartServer").attr("disabled", "");
				
				if (control.reloadServer)
					$("#reloadServer").removeAttr("disabled");
				else
					$("#reloadServer").attr("disabled", "");
			}
		});
	});
	
	if (window.location.pathname.indexOf("install") < 0)
		loadEverything(true);
});

function issueChat()
{
	$.post("/event/issueChat", {chatmsg: $("#chatmsg").val()}, function(data) {
		if (data.error != undefined)
			errorModal(data.error);
		
		$("#chatmsg").val("");
		$("#messages").html(data.chats);
		
		var msgs   = $('#messages');
		var height = msgs[0].scrollHeight;
		
		msgs.scrollTop(height);
		
		if ($("#messages").text().indexOf("No Chats") >= 0)
			$("#messages").css({"text-align": "center"});
		else
			$("#messages").css({"text-align": "left"});
	});
}

function issueCommand()
{
	if (canCommand)
	{
		$.post("/event/issueCommand", {command: $("#chatmsg").val()}, function(data) {
			if (data.error != undefined)
				errorModal(data.error);
			
			$("#chatmsg").val("");
			
			if (data.console != undefined)
			{
				$("#console").html("<pre>" + data.console + "</pre>");
				
				var msgs   = $('#console');
				var height = msgs[0].scrollHeight;
				
				msgs.scrollTop(height);
				
				if ($("#console").text().indexOf("No Console Data") >= 0)
					$("#console").css({"text-align": "center"});
				else
					$("#console").css({"text-align": "left"});
			}
		});
	}
}

var prevTime = 0;
var everyOther = false;

var loggedIn = false;
var hadFirst = false;

var oldStatus = "";

function loadEverything(doCycle)
{
	var oldChats   = $("#messages").html();
	var oldConsole = $("#console").html();
	
	if (!unloading)
	{
		$.ajax("/event/getEverything/" + window.location.pathname.substring(1), {
			type: 'GET',
			timeout: 5000,
			success: function(data) {
				setBackend(true);
				
				prevTime = parseInt(data.time);
				
				if (data.time < prevTime)
				{
					return;
				}
				
				/*
				if (data.loggedIn != loggedIn && hadFirst)
				{
					window.location = "/";
				}
				
				loggedIn = data.loggedIn;
				hadFirst = true;
				
				var activeId = $("#nav").find(".active").attr("id");
				var openId = $("#nav").find(".open").attr("id");
				
				$("#nav").load("/index/tabs", function() {
					$("#nav").find("#" + activeId).addClass("active");
					$("#nav").find("#" + openId).addClass("open");
				});
				*/
				
				if (data.usage != undefined)
				{
					$("#ramtotal").html("<b>" + totalString + " " + data.usage.ramTotal + " GB</b>");
					$("#ramused").html("<b>" + usedString + " " + data.usage.ramUsed + " GB</b>");
					$("#ramfree").html("<b>" + freeString + " " + data.usage.ramFree + " GB</b>");
					
					$("#cpucores").html("<b>" + coresString + " " + data.usage.cpuCores + "</b>");
					$("#cpufreq").html("<b>" + freqString + " " + data.usage.cpuFreq + " GHz</b>");
					
					$("#ramcircle").val(data.usage.ramPercent).trigger("change");
					$("#cpucircle").val(data.usage.cpuPercent).trigger("change");
					
					//if (!everyOther)
					//{
						if ($("#cpuchart").length)
						{
							cpuSeries[0].data = updateCpuGraph(data.usage.cpuPercent);
							
							cpuPlot.setData(cpuSeries);
							cpuPlot.draw();
						}
						
						if ($("#ramchart").length)
						{
							ramSeries[0].data = updateRamGraph(data.usage.ramPercent);
							
							ramPlot.setData(ramSeries);
							ramPlot.draw();
						}
						
						//everyOther = true;
					//} else
					//{
						//everyOther = false;
					//}
					
					var disks = data.usage.disks;
					
					for (var i = 0; i < disks.length; i++)
					{
						var disk = disks[i];
						
						$("#disk" + i + "title").html("<b>" + disk.diskName + "</b>");
						$("#disk" + i + "total").html("<b>" + totalString + " " + disk.diskTotal + " GB</b>");
						$("#disk" + i + "used").html("<b>" + usedString + " " + disk.diskUsed + " GB</b>");
						$("#disk" + i + "free").html("<b>" + freeString + " " + disk.diskFree + " GB</b>");
						
						$("#disk" + i + "circle").val(disk.diskPercent).trigger("change");
					}
				}
				
				if (data.control != undefined)
				{
					var control = data.control;
					
					$("#statusTitle").html(control.statusTitle);
					
					if (oldStatus.length != 0 && oldStatus != control.statusTitle)
					{
						//Get new tabs
						
						var activeId = $("#nav").find(".active").attr("id");
						var openId = $("#nav").find(".open").attr("id");
						
						$("#nav").load("/index/tabs", function() {
							$("#nav").find("#" + activeId).addClass("active");
							$("#nav").find("#" + openId).addClass("open");
						});
					}
					
					oldStatus = control.statusTitle;
					
					if (control.startServer)
						$("#startServer").removeAttr("disabled");
					else
						$("#startServer").attr("disabled", "");
					
					if (control.stopServer)
						$("#stopServer").removeAttr("disabled");
					else
						$("#stopServer").attr("disabled", "");
					
					if (control.restartServer)
						$("#restartServer").removeAttr("disabled");
					else
						$("#restartServer").attr("disabled", "");
					
					if (control.reloadServer)
						$("#reloadServer").removeAttr("disabled");
					else
						$("#reloadServer").attr("disabled", "");
				}
				
				if (data.applications != undefined)
				{
					if (data.applications > 0)
					{
						if ($("#appBadge").length != 0)
						{
							$("#appBadge").text(data.applications);
						} else
						{
							$("#applications > a").append($("<span id='appBadge' class='badge' style='margin-left: 5px; background-color: rgb(229, 91, 91);'>" + data.applications + "</span>"));
						}
						
						var n = noty({
				            text        : "<b>New Application: </b>You have " + data.applications + " awaiting applications.",
				            type        : 'warning',
				            dismissQueue: true,
				            layout      : 'bottomLeft',
				            theme       : 'defaultTheme',
				            timeout     : 5000
				        });
					} else
					{
						$("#appBadge").remove();
					}
				}
				
				$("#messages").html(data.chats);
				
				if (data.console != undefined)
					$("#console").html("<pre>" + data.console + "</pre>");
				
				if (data.playersObj != undefined)
				{
					$("#status").html(data.playersObj.status);
					
					$("#playerson").html(data.playersObj.plist);
					$("#ponline").text(data.playersObj.online + " / " + data.playersObj.total);
				}
				
				if (oldChats != $("#messages").html())
				{
					var msgs   = $('#messages');
					var height = msgs[0].scrollHeight;
					
					msgs.scrollTop(height);
				}
				
				if (oldConsole != $("#console").html())
				{
					var msgs   = $('#console');
					var height = msgs[0].scrollHeight;
					
					msgs.scrollTop(height);
				}
				
				if ($("#messages").text().indexOf("No Chats") >= 0)
					$("#messages").css({"text-align": "center"});
				else
					$("#messages").css({"text-align": "left"});
					
				if ($("#console").text().indexOf("No Console Data") >= 0)
					$("#console").css({"text-align": "center"});
				else
					$("#console").css({"text-align": "left"});
					
				if (doCycle)
				{
					setTimeout(function() {
						loadEverything(true);
					}, 1000);
				}
			},
			error: function(xhr, ajaxOptions, thrownError) {
				if (!notLoaded)
					notLoaded = true;
				else
					setBackend(false);
				
				/*
				$("#status").html("<span style=\"color: red;\">Offline</span>");
				$("#statusTitle").html("<span style=\"color: red;\">Stopped: " + xhr.status + "</span>");
				
				$("#ponline").text("0 / 0");
				$("#playerson").html("");
				
				$("#startServer").attr("disabled", "");
				$("#stopServer").attr("disabled", "");
				$("#restartServer").attr("disabled", "");
				$("#reloadServer").attr("disabled", "");
				*/
				
				if (doCycle)
				{
					setTimeout(function() {
						loadEverything(true);
					}, 2000);
				}
			}
		});
	}
}

var notLoaded = false;
var hasBackend = true;

function setBackend(backend)
{
	notLoaded = false;
	
	if (backend != hasBackend)
	{
		hasBackend = backend;
		
		if (hasBackend)
		{
			if ($("#custommodal > .modal-title").text() == "No Backend")
			{
				$("#custommodal").modal("toggle");
			}
		} else
		{
			showModal("No Backend", "We could not connect to the McAdminPanel backend server. Is it still running?");
		}
	}
}

function resize()
{
	//$("body").css({"height": window.outerHeight});
	//$(".carousel .item").css({"height": window.outerHeight});
	
	$("#chatmsg").css({"width": $("#chatmsg").parent().width() - 68});
}

function showUpdateModal()
{
	showModal('Update McAdminPanel', 'To update McAdminPanel:<br />1. Type stop-all in the McAdminPanel console<br />2. Download the latest McAdminPanel.jar <a href=\'http://mcapanel.com/downloads/McAdminPanel.jar\'>here</a><br />3. Drag the updated jar into where the current one is and override it<br />4. Start McAdminPanel back up with the original startup script!');
}