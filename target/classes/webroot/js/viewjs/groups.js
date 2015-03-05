var edited = false;

$(function() {
	$("#grouptable").dataTable({
		"bLengthChange": false,
		stateSave: true
	});
	
	if (canEditGroups)
	{
		$("#grouptable_wrapper .col-xs-6:first").html('<button type="button" id="savegroups" class="btn btn-xs btn-success btn-noclick">Groups Already Saved</button>');
		$("#grouptable_wrapper .col-xs-6:first").html($("#grouptable_wrapper .col-xs-6:first").html() + '<button type="button" id="addgroup" class="btn btn-xs btn-info" style="margin-top: 12px; margin-left: 12px;">Add Group</button>');
		
		$("#savegroups").click(function() {
			if (!$(this).hasClass("btn-noclick"))
			{
				var rows = $("#grouptable").dataTable().fnGetNodes();
				
				var data = [];
				
				for (var i = 0; i < rows.length; i++)
				{
					data[i] = {};
					
					data[i].id = $(rows[i]).find("td:eq(0)").text();
					data[i].name = $(rows[i]).find("td:eq(1)").text();
					data[i].ghost = $(rows[i]).find("td:eq(2)").find("input").is(":checked");
					data[i].existing = $(rows[i]).find("td:eq(3)").find("input").is(":checked");
					data[i].whitelist = $(rows[i]).find("td:eq(4)").find("input").is(":checked");
					data[i].permissions = $(rows[i]).find("td:eq(5)").text();
				}
				
				$.post("/groups/saveGroups", {"data":JSON.stringify(data)}, function(ret) {
					if (ret.good != undefined)
					{
						var n = noty({
				            text        : "<b>Success: </b>" + ret.good,
				            type        : 'success',
				            dismissQueue: true,
				            layout      : 'bottomLeft',
				            theme       : 'defaultTheme',
				            timeout     : 2000
				        });
					} else if (ret.error != undefined)
					{
						var n = noty({
				            text        : "<b>Error: </b>" + ret.error,
				            type        : 'error',
				            dismissQueue: true,
				            layout      : 'bottomLeft',
				            theme       : 'defaultTheme',
				            timeout     : 2000
				        });
					}
				});
				
				setEdited(false);
			}
		});
		
		gnameClick();
		permsClick();
		
		$("#grouptable tbody .ghostcheck").on("click", function() {
			var checked = $(this).is(":checked");
			
			if (checked)
				$(this).parent().find(".label").removeClass("label-danger").addClass("label-success").text("true");
			else
				$(this).parent().find(".label").removeClass("label-success").addClass("label-danger").text("false");
			
			setEdited(true);
		});
		
		$("#grouptable tbody .changelabel").on("click", function() {
			var checked = $(this).parent().find(".ghostcheck").is(":checked");
			
			$(this).parent().find(".ghostcheck").prop("checked", !checked);
			
			if (!checked)
				$(this).removeClass("label-danger").addClass("label-success").text("true");
			else
				$(this).removeClass("label-success").addClass("label-danger").text("false");
			
			setEdited(true);
		});
		
		$("#grouptable tbody .existingradio").parent().on("click", function() {
			$("#grouptable tbody tr").each(function() {
				$(this).find("td:nth-child(4)").find("input").prop("checked", false);
				$(this).find("td:nth-child(4)").find(".label").removeClass("label-success").addClass("label-danger").text("false");
			});
			
			$(this).find(".existingradio").prop("checked", true);
			$(this).find(".label").removeClass("label-danger").addClass("label-success").text("true");
			
			setEdited(true);
		});
		
		$("#grouptable tbody .whitelistradio").parent().on("click", function() {
			$("#grouptable tbody tr").each(function() {
				$(this).find("td:nth-child(5)").find("input").prop("checked", false);
				$(this).find("td:nth-child(5)").find(".label").removeClass("label-success").addClass("label-danger").text("false");
			});
			
			$(this).find(".whitelistradio").prop("checked", true);
			$(this).find(".label").removeClass("label-danger").addClass("label-success").text("true");
			
			setEdited(true);
		});
		
		$("#addgroup").click(function() {
			$("#custommodal").on("shown.bs.modal", function() { $("#groupname").focus(); });
			
			showModalFull("Add Group", "<label for='groupname'>Group Name</label><input type=\"text\" class=\"form-control\" id=\"groupname\" placeholder=\"Enter Group Name\" onkeydown=\"if (event.keyCode == 13) $('#custommodal .btn').click();\">", "Save Group", true);
			
			modalClick("#custommodal", function() {
				var gname = $("#groupname").val();
				
				$.post("/groups/addGroup", {"groupname": gname}, function(data) {
					if (data.good != undefined)
					{
						location.reload();
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
		});
	}
});

function setEdited(edit)
{
	edited = edit;
	
	if (edited)
		$("#savegroups").removeClass("btn-noclick").removeClass("btn-success").addClass("btn-danger").text("Save Groups");
	else
		$("#savegroups").addClass("btn-noclick").removeClass("btn-danger").addClass("btn-success").text("Groups Already Saved");
	
	window.onbeforeunload = function (e) {
		if (edited)
		{
			var message = "Noooo, you haven't saved your new group settings!",
			
			e = e || window.event;
			
			if (e)
				e.returnValue = message;
			
			return message;
		}
	};
}

function gnameClick()
{
	$("#grouptable tbody .groupname").on("click", function() {
		$(this).parent().css({ "padding-bottom":"7px" }).html("<input type=\"text\" id=\"groupname\" value=\"" + $(this).text() + "\" style=\"margin-top: 0px; height: 24px; width: " + ($(this).width() + 10) + "px;\">");
		
		$("#groupname").autoGrowInput({comfortZone: 7, minWidth: 50, maxWidth: 200}).trigger('keyup').focus();
		
		$("#groupname").blur(function() {
			$(this).parent().css({ "padding-bottom":"11px" }).html("<span class=\"groupname\">" + $(this).val() + "</span>");
			
			gnameClick();
		});
		
		$("#groupname").keypress(function(event){
			if ((event.keyCode ? event.keyCode : event.which) == "13")
			{
				$(this).parent().css({ "padding-bottom":"11px" }).html("<span class=\"groupname\">" + $(this).val() + "</span>");
				
				gnameClick();
			}
		});
		
		setEdited(true);
	});
}

function permsClick()
{
	$("#grouptable tbody .groupperms").on("click", function() {
		$(this).parent().css({ "padding-bottom":"7px" }).html("<input type=\"text\" id=\"groupperms\" value=\"" + $(this).text() + "\" style=\"margin-top: 0px; height: 24px; width: " + ($(this).width() + 10) + "px;\">");
		
		$("#groupperms").autoGrowInput({comfortZone: 7, minWidth: 50, maxWidth: 300}).trigger('keyup').focus();
		
		$("#groupperms").blur(function() {
			$(this).parent().css({ "padding-bottom":"11px" }).html("<span class=\"groupperms\">" + $(this).val() + "</span>");
			
			permsClick();
		});
		
		$("#groupperms").keypress(function(event){
			if ((event.keyCode ? event.keyCode : event.which) == "13")
			{
				$(this).parent().css({ "padding-bottom":"11px" }).html("<span class=\"groupperms\">" + $(this).val() + "</span>");
				
				permsClick();
			}
		});
		
		setEdited(true);
	});
}