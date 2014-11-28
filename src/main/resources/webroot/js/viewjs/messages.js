$(function() {
	$("#webmessages").dataTable({
		"bLengthChange": false,
		stateSave: true
	});
	
	$("#webmessages tbody").on("click", "tr", function(e) {
		if ($("#webmessages").dataTable().fnGetData().length > 0)
			window.location = "/message/view/" + $(this).find("td:first").text();
		
		return false;
	});
});