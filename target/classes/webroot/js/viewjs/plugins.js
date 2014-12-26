$(function() {
	var $contextMenu = $("#contextMenu");
	
	$("#installedplugins").dataTable({
		"bLengthChange": false,
		stateSave: true
	});
	
	$("#browseplugins").dataTable({
		"bLengthChange": false,
		"stateSave": true,
		"aoColumns": [
			{ "data": "plugin_name" },
			{ "data": "description" }
		],
		"oLanguage": {"sZeroRecords": "Loading plugins...", "sEmptyTable": "Loading plugins..."}
	});
	
	var arr = [];
	
	$.get("http://api.bukget.org/3/categories", function(data) {
		for (var cate in data)
		{
			var name = data[cate].name;
			
			$("#pluginscats").html($("#pluginscats").html() + "<a href='/plugins/" + name.replace(/ /g, "-").toLowerCase() + "' class='list-group-item plugincat'>" + name + "</a>");
		}
		
		$(".plugincat").click(function() {
			$("#pluginsholder").scrollTop(0);
			
			$(".plugincat").each(function() {
				$(this).removeClass("active");
				this.style.setProperty("color", "#5a5a5a", "important");
			});
			
			$(this).addClass("active");
			this.style.setProperty("color", "white", "important");
			
			var category = $(this).text();
			
			$.get("http://api.bukget.org/3/categories/" + category + "?start=0&size=40&fields=slug,plugin_name,description,authors", function(data) {
				$("#pluginsview").html("");
				
				setPlugins(data);
				
				loadMore(null, category);
				
				if ($("#pluginsview").height() < $("#pluginsholder").height())
				{
					$(".pluginview:last-child").css({"border-bottom": "1px solid #ddd"});
				}
			});
			
			return false;
		});
		
		$("a[href='/plugins/admin-tools']").trigger("click");
		
		$("#pluginsearch").keydown(function(e) {
			var search = $(this).val();
			
		    if (e.keyCode == 13 && search != "")
		    {
		    	$(".plugincat").each(function() {
					$(this).removeClass("active");
					this.style.setProperty("color", "#5a5a5a", "important");
				});
		    	
				$.get("http://api.bukget.org/3/search/plugin_name/like/" + search + "?start=0&size=40&fields=slug,plugin_name,description,authors", function(data) {
					$("#pluginsview").html("");
					
					setPlugins(data);
					
					loadMore(search, null);
					
					if ($("#pluginsview").height() < $("#pluginsholder").height())
					{
						$(".pluginview:last-child").css({"border-bottom": "1px solid #ddd"});
					}
				});
		    }
		});
	});
	
	/*
	$.get("http://api.bukget.org/3/plugins?sort=plugin_name", function(data) {
		var dt = $("#browseplugins").dataTable();
		
		for (var i = 0; i < data.length; i++)
		{
			if (data[i].plugin_name && data[i].description && data[i].plugin_name.indexOf("{project.name}") == -1)
			{
				arr.push(data[i]);
			}
		}
		
		dt.fnAddData(arr);
		dt.fnDraw();
	});
	*/
	
	$("#installedplugins tbody").on("click", "tr", function(e) {
		if ($("#installedplugins").dataTable().fnGetData().length > 0)
		{
			contextClick = true;
			
			dt = $("#installedplugins").DataTable();
			
			var i = dt.row(this).index();
			
			var d = dt.row(this).data();
			
			var enabled = d[1].indexOf("Enabled") >= 0 ? true : false;
			
			$contextMenu.find(".dropdown-menu").html(""
				+ "<li><a action=\"configPlugin\" href=\"/plugin/files/" + d[0] + "\">Edit Files</a></li>"
				+ "<li><a action=\"" + (enabled ? "disable" : "enable") + "Plugin\" href=\"/\">" + (enabled ? "Disable" : "Enable") + " Plugin</a></li>"
				+ "<li class=\"divider\"></li>"
				+ "<li><a action=\"deletePlugin\" href=\"/\" style=\"color: red;\">Delete Plugin</a></li>");
			
			$contextMenu.css({
				display: "block",
				left: e.pageX,
				top: e.pageY
			});
			
			clickPluginMenuF(d);
		}
	});
	
	$("#pluginlist a").click(function(e) {
		var oldId = $("#" + $("#pluginlist li.active a").attr("forid"));
		var newId = $("#" + $(this).attr("forid"));
		
		$("#pluginlist li.active").removeClass("active");
		$(this).parent().addClass("active");
		
		if (oldId.attr("id") != newId.attr("id"))
		{
			oldId.fadeOut(400, function(){
				newId.fadeIn(400);
		    });
		}
		
		return false;
	});
});

function loadMore(search, category)
{
	if ($("#pluginsview").children().length >= 40)
	{
		$("#pluginsview").html($("#pluginsview").html() + "<a href='#' id='moreresults' class='list-group-item pluginview'>More Results...</a>");
		
		var url = "http://api.bukget.org/3/search/plugin_name/like/" + search;
		
		if (search == null) url = "http://api.bukget.org/3/categories/" + category;
		
		$("#moreresults").click(function() {
			$.get(url + "?start=" + $("#pluginsview").children().length + "&size=40&fields=slug,plugin_name,description,authors", function(data) {
				$("#moreresults").remove();
				
				setPlugins(data);
				
				loadMore(search, category);
			});
			
			return false;
		});
	}
}

function setPlugins(data)
{
	for (var plugin in data)
	{
		var slug = data[plugin].slug;
		var name = data[plugin].plugin_name;
		var desc = data[plugin].description;
		var authors = data[plugin].authors;
		
		if (name.length == 0) continue;
		
		var authorStr = "";
		
		for (var a = 0; a < authors.length; a++)
		{
			authorStr += authors[a] + (a != authors.length - 1 ? ", " : "");
		}
		
		$("#pluginsview").append("<a href='/plugins/' onclick='return clickPlugin($(this), event);' class='list-group-item pluginview' name='" + name + "' slug='" + slug + "'>" + name + " " + (authors.length != 0 ? ("<span style='font-size: 9pt; color: gray;'> - " + authorStr) + "</span>" : "") + "<br /><span style='font-size: 8pt;'>" + desc + "</span></a>");
	}
}

function clickPlugin(me, e)
{
	var $contextMenu = $("#contextMenu");
	
	contextClick = true;
	
	$contextMenu.find(".dropdown-menu").html(""
		+ "<li><a action=\"installPlugin\" href=\"/\">Install Plugin</a></li>"
		+ "<li class=\"divider\"></li>"
		+ "<li><a action=\"infoPlugin\" href=\"/plugin/view/" + me.attr("slug") + "\">More Information</a></li>");
	
	$contextMenu.css({
		display: "block",
		left: e.pageX,
		top: e.pageY
	});
	
	clickPluginMenu(me);
	
	return false;
}

function clickCategory(category)
{
	var c = $("a[href='" + category + "']");
	
	$("#custommodal").modal("hide");
	
	var once = false;
	
	$("#custommodal").on('hidden.bs.modal', function () {
		if (!once)
		{
			c.trigger('click');
			
			once = true;
		}
	});
	
	return false;
}

function clickPluginMenuF(data)
{
	var $contextMenu = $("#contextMenu");
	
	$("#contextMenu a").click(function() {
		$contextMenu.css({
			display: "none"
		});
		
		var act = $(this).attr("action");
		
		if (act == "configPlugin")
		{
			document.location = $(this).attr("href");
		} else if (act == "enablePlugin" || act == "disablePlugin" || act == "deletePlugin")
		{
			$.post("/plugins/process/" + act.replace("Plugin", ""), { "pluginName": data[0] }, function(data) {
				if (data.good != undefined)
				{
					showModal(act == "deletePlugin" ? "Deleted Plugin" : "Changed Plugin Status", data.good);
				} else if (data.error != undefined)
				{
					errorModal(data.error);
				}
				
				if (data.plugins != undefined)
				{
					if ($("#installedplugins").length != 0)
					{
						var dt = $("#installedplugins").dataTable();
						
						if (data.plugins.length == 0)
							dt.fnClearTable();
						else
						{
							dt.fnClearTable();
							dt.fnAddData(data.plugins);
							dt.fnDraw();
						}
					}
				}
			});
		}
		
		return false;
	});
}

function clickPluginMenu(item)
{
	var $contextMenu = $("#contextMenu");
	
	$("#contextMenu a").click(function() {
		$contextMenu.css({
			display: "none"
		});
		
		var act = $(this).attr("action");
		
		if (act == "installPlugin")
		{
			var n = noty({
	            text        : "Currently installing " + item.attr("name"),
	            type        : 'success',
	            dismissQueue: true,
	            layout      : 'bottomLeft',
	            theme       : 'defaultTheme',
	            timeout     : 3000
	        });
			
			$.post("/plugins/process/install", { "slug": item.attr("slug"), "pluginName": item.attr("name") }, function(data) {
				if (data.good != undefined)
				{
					showModal("Installed Plugin", data.good);
				} else if (data.error != undefined)
				{
					errorModal(data.error);
				}
				
				if (data.plugins != undefined)
				{
					if ($("#installedplugins").length != 0)
					{
						var dt = $("#installedplugins").dataTable();
						
						if (data.plugins.length == 0)
							dt.fnClearTable();
						else
						{
							dt.fnClearTable();
							dt.fnAddData(data.plugins);
							dt.fnDraw();
						}
					}
				}
			});
		} else if (act == "infoPlugin")
		{
			$.get("http://api.bukget.org/3/plugins/bukkit/" + item.attr("slug"), function(data) {
				var body = "";
				
				body = body + "<b class=\"pluginver\">BukkitDev</b><br />";
				body = body + "&nbsp;&nbsp;&nbsp;&nbsp;<a href=\"" + data.dbo_page + "\" target=\"_blank\">" + data.dbo_page + "</a><br /><br />";
				
				var auths = data.authors;
				
				body = body + "<b class=\"pluginver\">Authors</b><br />";
				
				for (var a = 0; a < auths.length; a++)
				{
					body = body + "&nbsp;&nbsp;&nbsp;&nbsp;<a href=\"http://dev.bukkit.org/profiles/" + auths[a] + "\" target=\"_blank\">" + auths[a] + "</a><br />";
				}
				
				var cats = data.categories;
				
				var images = {
					"admin-tools": "http://dev.bukkit.org/thumbman/category-icons/0/165/32x32/admin_tools.png.-m1.png",
					"anti-griefing-tools": "http://dev.bukkit.org/thumbman/category-icons/0/171/32x32/anti_grief.png.-m1.png",
					"chat-related": "http://dev.bukkit.org/thumbman/category-icons/0/175/32x32/chat.png.-m1.png",
					"developer-tools": "http://dev.bukkit.org/thumbman/category-icons/0/166/32x32/dev_tools.png.-m1.png",
					"economy": "http://dev.bukkit.org/thumbman/category-icons/0/172/32x32/economy.png.-m1.png",
					"fixes": "http://dev.bukkit.org/thumbman/category-icons/0/176/32x32/fixes.png.-m1.png",
					"fun": "http://dev.bukkit.org/thumbman/category-icons/0/167/32x32/fun.png.-m1.png",
					"general": "http://dev.bukkit.org/thumbman/category-icons/0/173/32x32/general.png.-m1.png",
					"informational": "http://dev.bukkit.org/thumbman/category-icons/0/177/32x32/informational.png.-m1.png",
					"mechanics": "http://dev.bukkit.org/thumbman/category-icons/0/168/32x32/mechanics.png.-m1.png",
					"miscellaneous": "http://dev.bukkit.org/thumbman/category-icons/0/174/32x32/misc.png.-m1.png",
					"role-playing": "http://dev.bukkit.org/thumbman/category-icons/0/178/32x32/role_playing.png.-m1.png",
					"teleportation": "http://dev.bukkit.org/thumbman/category-icons/0/169/32x32/teleportation.png.-m1.png",
					"website-administration": "http://dev.bukkit.org/thumbman/category-icons/0/180/32x32/web.png.-m1.png",
					"world-editing-and-management": "http://dev.bukkit.org/thumbman/category-icons/0/179/32x32/world_management.png.-m1.png",
					"world-generators": "http://dev.bukkit.org/thumbman/category-icons/0/272/32x32/worldgenerators_icon.png.-m1.png"
				};
				
				body = body + "<br /><b class=\"pluginver\">Categories</b><br />&nbsp;&nbsp;&nbsp;&nbsp;";
				
				for (var c = 0; c < cats.length; c++)
				{
					var cat = cats[c];
					
					var h = cat.replace(/ /g, "-").toLowerCase();
					
					body = body + '<a title="' + cat + '" class="category" style="height:32px;width:32px;background:url(' + images[h] + ')" href="/plugins/' + h + '" onclick="return clickCategory(\'/plugins/' + h + '\');">' + cat + '</a>';
				}
				
				body = body + "<hr />";
				
				var vers = data.versions;
				
				for (var v = 0; v < vers.length; v++)
				{
					var ver = vers[v];
					
					body = body + "<div><h2 style=\"float: left;\"><b>" + ver.version + (v == 0 ? " - Latest" : "") + "</b></h2><button id=\"installversion" + v + "\" link=\"" + ver.download + "\" version=\"" + ver.version + "\" type=\"button\" style=\"float: right;\" class=\"btn btn-sm btn-success\">Install Version</button><br style=\"clear: both;\" /></div>";
					body = body + window.atob(ver.changelog);
					
					if (v != vers.length - 1)
						body = body + "<hr />";
				}
				
				showModal(item.attr("name") + " Plugin", body, "Close", false);
				
				//$("#custommodal .modal-body").html($("#custommodal .modal-body").html() + "");
				
				/*
				$("#custommodal .modal-title").html(arr[i].plugin_name + " Plugin");
				$("#custommodal .modal-body").html(data.good);
				$("#custommodal .btn-primary").html("Got It");
				
				$("#custommodal").modal();
				
				showModal(arr[i].plugin_name + " Plugin", data.good);
				*/
				
				for (var v = 0; v < vers.length; v++)
				{
					$("#installversion" + v).click(function() {
						$("#custommodal").modal("hide");
						
						var version = $(this);
						var once = false;
						
						$("#custommodal").on('hidden.bs.modal', function () {
							if (!once)
							{
								var n = noty({
						            text        : "Currently installing " + item.attr("name") + " v" + version.attr("version"),
						            type        : 'success',
						            dismissQueue: true,
						            layout      : 'bottomLeft',
						            theme       : 'defaultTheme',
						            timeout     : 3000
						        });
								
								$.post("/plugins/process/install", { "link": version.attr("link"), "pluginName": item.attr("name") }, function(data) {
									if (data.good != undefined)
									{
										showModal("Installed Plugin", data.good);
									} else if (data.error != undefined)
									{
										errorModal(data.error);
									}
									
									if (data.plugins != undefined)
									{
										var dt = $("#installedplugins").dataTable();
										
										if (data.plugins.length == 0)
											dt.fnClearTable();
										else
										{
											dt.fnClearTable();
											dt.fnAddData(data.plugins);
											dt.fnDraw();
										}
									}
								});
								
								once = true;
							}
						});
					});
				}
			});
		}
		
		return false;
	});
}