$(function() {
	$("#playertable").dataTable({
		"bLengthChange": false,
		stateSave: true,
		"order": [[ 1, "asc" ]],
		"aoColumnDefs": [
			{ 'bSortable': false, 'aTargets': [ 0 ] }
		]
	});
	
	$("#playertable tbody").on("click", "tr", function(e) {
		if ($("#playertable").dataTable().fnGetData().length > 0)
			clickPlayer($(this), e);
		
		return false;
	});
	
	$("#playersusers a").click(function(e) {
		var oldId = $("#" + $("#playersusers li.active a").attr("forid"));
		var newId = $("#" + $(this).attr("forid"));
		
		$("#playersusers li.active").removeClass("active");
		$(this).parent().addClass("active");
		
		oldId.slideUp(function() {
			newId.slideDown(function() {
				var height = newId[0].scrollHeight;
				newId.scrollTop(height);
				
				resize();
			});
		});
		
		return false;
	});
	
});

function clickPlayer(that, e)
{
	contextClick = true;

	var $contextMenu = $("#contextMenu");
	
	clickedObject = that.find("td:nth-child(2)").text();
	
	if (canHealfeed || canKill || canKick || canBan)
	{
		$contextMenu.find(".dropdown-menu").html(""
			+ "<li><a action=\"viewPlayer\" href=\"/player/view/" + clickedObject + "\">View Player</a></li>"
			+ "<li class=\"divider\"></li>"
			+ (canHealfeed ? "<li><a action=\"healPlayer\" href=\"/\">Heal Player</a></li>"
			+ "<li><a action=\"feedPlayer\" href=\"/\">Feed Player</a></li>" : "")
			+ (canKill ? "<li><a action=\"killPlayer\" href=\"/\">Kill Player</a></li>" : "")
			+ (canHealfeed || canKill ? "<li class=\"divider\"></li>" : "")
			+ (canKick ? "<li><a action=\"kickPlayer\" href=\"/\">Kick Player</a></li>" : "")
			+ (canBan ? "<li><a action=\"banPlayer\" href=\"/\">Ban Player</a></li>" : ""));
		
		$contextMenu.css({
			display: "block",
			left: e.pageX,
			top: e.pageY
		});
		
		$("#contextMenu a").click(function() {
			$contextMenu.css({
				display: "none"
			});
			
			var act = $(this).attr("action");
			
			if (act == "viewPlayer")
			{
				document.location = $(this).attr("href");
			} else if (act == "killPlayer" || act == "healPlayer" || act == "feedPlayer")
			{
				var title = (act == "killPlayer" ? "Killed" : (act == "healPlayer" ? "Healed" : "Fed"));
				
				$.post("/player/event/" + clickedObject + "/" + act.replace("Player", ""), function(data) {
					if (data.good != undefined)
					{
						//showModal(title + " Player", data.good);
						
						var n = noty({
				            text        : "<b>Success: </b>" + data.good,
				            type        : 'success',
				            dismissQueue: true,
				            layout      : 'bottomLeft',
				            theme       : 'defaultTheme',
				            timeout     : 3000
				        });
					} else if (data.error != undefined)
					{
						//errorModal(data.error);
						
						var n = noty({
				            text        : "<b>Error: </b>" + data.error,
				            type        : 'error',
				            dismissQueue: true,
				            layout      : 'bottomLeft',
				            theme       : 'defaultTheme',
				            timeout     : 3000
				        });
					}
					
					if (data.plist != undefined)
					{
						if ($("#playertable").length != 0)
						{
							var dt = $("#playertable").dataTable();
							
							if (data.plist.length == 0)
								dt.fnClearTable();
							else
							{
								dt.fnClearTable();
								dt.fnAddData(data.plist);
								dt.fnDraw();
							}
						}
					}
				});
			} else if (act == "kickPlayer" || act == "banPlayer")
			{
				var type = act == "kickPlayer" ? "Kick" : "Ban";
				
				$("#custommodal").on("shown.bs.modal", function() { $("#" + type.toLowerCase() + "Message").focus(); });
				
				showModalFull(type + " Message", "<input type=\"text\" class=\"form-control\" id=\"" + type.toLowerCase() + "Message\" placeholder=\"Enter " + type + " Message\" onkeydown=\"if (event.keyCode == 13) $('#custommodal .btn').click();\">", type + " Player", true);
				
				var clicked;
				
				$("#custommodal .btn-primary").click(function() {
					if (!clicked)
					{
						setTimeout(function() {
							$.post("/player/event/" + clickedObject + "/" + type.toLowerCase() + "/" + $("#" + type.toLowerCase() + "Message").val(), function(data) {
								if (data.good != undefined)
								{
									//showModal((type == "Ban" ? "Bann" : "Kick") + "ed Player", data.good);
									
									var n = noty({
							            text        : "<b>Success: </b>" + data.good,
							            type        : 'success',
							            dismissQueue: true,
							            layout      : 'bottomLeft',
							            theme       : 'defaultTheme',
							            timeout     : 3000
							        });
								} else if (data.error != undefined)
								{
									//errorModal(data.error);
									
									var n = noty({
							            text        : "<b>Error: </b>" + data.error,
							            type        : 'error',
							            dismissQueue: true,
							            layout      : 'bottomLeft',
							            theme       : 'defaultTheme',
							            timeout     : 3000
							        });
								}
								
								if (data.plist != undefined)
								{
									if ($("#playertable").length != 0)
									{
										var dt = $("#playertable").dataTable();
										
										if (data.plist.length == 0)
											dt.fnClearTable();
										else
										{
											dt.fnClearTable();
											dt.fnAddData(data.plist);
											dt.fnDraw();
										}
									}
								}
							});
						}, 500);
						
						clicked = true;
					}
				});
			}
			
			return false;
		});
	} else
	{
		document.location = "/player/view/" + clickedObject;
	}
	
	contextClick = false;
}