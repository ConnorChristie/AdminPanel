<!-- <script src="/js/viewjs/messages.js"></script> -->

<script>
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
</script>

<div class="panel panel-default">
	<div class="panel-heading">
		<h3 class="panel-title">
			Messages
		</h3>
	</div>
	<div class="panel-body" style="padding: 0px;">
		<table id="webmessages" class="table table-striped" style="margin-bottom: 0px; border-bottom: 1px solid lightgray;">
			<thead>
				<tr>
					<th>ID</th>
					<th>Minecraft Username</th>
					<th>Subject</th>
				</tr>
			</thead>
			<tbody>
				${webmessages}
			</tbody>
		</table>
	</div>
</div>