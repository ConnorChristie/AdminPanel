$(function() {
	$("#whitelistapps").dataTable({
		"bLengthChange": false,
		stateSave: true
	});
	
	if (canWhitelist)
	{
		$("#whitelistapps tbody").on("click", "tr", function(e) {
			if ($("#whitelistapps").dataTable().fnGetData().length > 0)
				clickApplication($(this), e);
			
			return false;
		});
	} else
	{
		$("tbody tr").css({"cursor": "default"});
	}
});

var clickedObject;

function clickApplication(that, e)
{
	var $contextMenu = $("#contextMenu");
	
	$contextMenu.find(".dropdown-menu").html("<li><a action=\"acceptApplication\" href=\"/\">Accept Application</a></li><li><a action=\"denyApplication\" href=\"/\">Deny Application</a></li>");
	
	$contextMenu.css({
		display: "block",
		left: e.pageX,
		top: e.pageY
	});
	
	clickedObject = that.find("td:nth-child(1)").text();
	
	$("#contextMenu a").click(function() {
		$contextMenu.css({
			display: "none"
		});
		
		var act = $(this).attr("action");
		
		if (act == "acceptApplication" || act == "denyApplication")
		{
			var type = act == "acceptApplication" ? "Accept" : "Deny";
			
			$.post("/applications/process/" + type.toLowerCase() + "/" + clickedObject, function(data) {
				if (data.good != undefined)
				{
					showModal((type == "Deny" ? "Denied" : "Accepted") + " Application", data.good);
				} else if (data.error != undefined)
				{
					errorModal(data.error);
				}
				
				if (data.whitelistapps != undefined)
				{
					var dt = $("#whitelistapps").dataTable();
					
					if (data.whitelistapps.length == 0)
						dt.fnClearTable();
					else
					{
						dt.fnClearTable();
						dt.fnAddData(data.whitelistapps);
						dt.fnDraw();
					}
				}
			});
		}
		
		return false;
	});
}