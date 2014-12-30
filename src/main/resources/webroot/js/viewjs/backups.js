$(function() {
	var $contextMenu = $("#contextMenu");
	
	$("#scheduledbackups").dataTable({
		"bLengthChange": false,
		stateSave: true,
		"columnDefs": [
            {
               "targets": [ 0 ],
               "visible": false,
               "searchable": false
            }
        ]
	});
	
	$("#backupstable").dataTable({
		"bLengthChange": false,
		stateSave: true,
		"columnDefs": [
           {
              "targets": [ 0 ],
              "visible": false,
              "searchable": false
           }
       ]
	});
	
	$("#backuplist a").click(function(e) {
		var oldId = $("#" + $("#backuplist li.active a").attr("forid"));
		var newId = $("#" + $(this).attr("forid"));
		
		$("#backuplist li.active").removeClass("active");
		$(this).parent().addClass("active");
		
		if (oldId.attr("id") != newId.attr("id"))
		{
			oldId.fadeOut(400, function(){
				newId.fadeIn(400);
		    });
		}
		
		return false;
	});
	
	$(".worldscheck").on("click", function() {
		$(this).parent().parent().find(".worlds input").removeAttr("disabled");
	});
	
	$(".worldscheck").bind("deselect", function() {
		$(this).parent().parent().find(".worlds input").attr("disabled", "disabled");
	});
	
	$("input[name='interval']").on("click", function() {
		$(this).parent().find("input[type='text']").removeAttr("disabled");
	});
	
	$("input[name='interval']").bind("deselect", function() {
		$(this).parent().find("input[type='text']").attr("disabled", "disabled");
	});
	
	$("#backupmodal .btn-primary").on("click", function() {
		$.post("/backups/schedule", $("#schedulebackupform").serialize(), function(data) {
			if (data.good != undefined)
			{
				showModal("Created Backup Schedule", data.good);
			} else if (data.error != undefined)
			{
				errorModal(data.error);
			}
			
			if (data.scheduledbackups != undefined)
			{
				var dt = $("#scheduledbackups").dataTable();
				
				if (data.scheduledbackups.length == 0)
					dt.fnClearTable();
				else
				{
					dt.fnClearTable();
					dt.fnAddData(data.scheduledbackups);
					dt.fnDraw();
				}
			}
		});
	});
	
	$("#scheduledbackups tbody").on("click", "tr", function(e) {
		if ($("#scheduledbackups").dataTable().fnGetData().length > 0)
		{
			contextClick = true;
			
			var dt = $("#scheduledbackups").DataTable();
			
			$contextMenu.find(".dropdown-menu").html(""
				+ "<li><a action=\"backupSchedule\" href=\"/\">Backup Now</a></li>"
				+ "<li class=\"divider\"></li>"
				+ "<li><a action=\"deleteSchedule\" href=\"/\" style=\"color: red;\">Delete Schedule</a></li>");
			
			$contextMenu.css({
				display: "block",
				left: e.pageX,
				top: e.pageY
			});
			
			clickScheduleMenu(dt.row(this));
		}
	});
	
	$("#backupstable tbody").on("click", "tr", function(e) {
		if ($("#backupstable").dataTable().fnGetData().length > 0)
		{
			contextClick = true;
			
			var dt = $("#backupstable").DataTable();
			
			$contextMenu.find(".dropdown-menu").html(""
				+ "<li><a action=\"restoreBackup\" href=\"/\">Restore Backup</a></li>"
				+ "<li class=\"divider\"></li>"
				+ "<li><a action=\"deleteBackup\" href=\"/\" style=\"color: red;\">Delete Backup</a></li>");
			
			$contextMenu.css({
				display: "block",
				left: e.pageX,
				top: e.pageY
			});
			
			clickBackupMenu(dt.row(this));
		}
	});
});

function clickScheduleMenu(that)
{
	var $contextMenu = $("#contextMenu");
	
	$("#contextMenu a").click(function() {
		$contextMenu.css({
			display: "none"
		});
		
		var act = $(this).attr("action");
		
		if (act == "backupSchedule" || act == "deleteSchedule")
		{
			$.post("/backups/processSchedule/" + act.replace("Schedule", ""), { "id": that.data()[0] }, function(data) {
				if (data.good != undefined)
				{
					showModal(act == "backupSchedule" ? "Backed Up" : "Deleted Schedule", data.good);
				} else if (data.error != undefined)
				{
					errorModal(data.error);
				}
				
				if (data.scheduledbackups != undefined)
				{
					var dt = $("#scheduledbackups").dataTable();
					
					if (data.scheduledbackups.length == 0)
						dt.fnClearTable();
					else
					{
						dt.fnClearTable();
						dt.fnAddData(data.scheduledbackups);
						dt.fnDraw();
					}
				}
			});
		}
		
		return false;
	});
}

function clickBackupMenu(that)
{
	var $contextMenu = $("#contextMenu");
	
	$("#contextMenu a").click(function() {
		$contextMenu.css({
			display: "none"
		});
		
		var act = $(this).attr("action");
		
		if (act == "restoreBackup" || act == "deleteBackup")
		{
			$.post("/backups/processBackup/" + act.replace("Backup", ""), { "id": that.data()[0] }, function(data) {
				if (data.good != undefined)
				{
					showModal(act == "restoreBackup" ? "Restored Backup" : "Deleted Backup", data.good);
				} else if (data.error != undefined)
				{
					errorModal(data.error);
				}
				
				if (data.backups != undefined)
				{
					var dt = $("#backupstable").dataTable();
					
					if (data.backups.length == 0)
						dt.fnClearTable();
					else
					{
						dt.fnClearTable();
						dt.fnAddData(data.backups);
						dt.fnDraw();
					}
				}
			});
		}
		
		return false;
	});
}