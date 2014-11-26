$(function() {
	$("#webmessages").dataTable({
		"bLengthChange": false,
		stateSave: true
	});
	
	$("#webmessages tbody").on("click", "tr", function(e) {
		window.location = "/message/view/" + $(this).find("td:first").text();
		
		return false;
	});
});