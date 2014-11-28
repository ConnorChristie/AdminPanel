<%@taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>

<script src="/js/viewjs/backups.js"></script>

<script>
$(function() {
	if (${user.getGroup().hasPermission("server.backups.schedule.issue")})
	{
		$("#scheduledbackups_wrapper .col-xs-6:first").html('<button type="button" id="newbackupschedule" class="btn btn-xs btn-success">Schedule Backup</button>');
	}
});
</script>

<div class="panel panel-default">
	<div class="panel-heading">
		<h3 class="panel-title">
			<ul id="backuplist" class="nav nav-pills">
				<li class="active"><a href="#" forid="scheduledbackupsdiv">Scheduled Backups</a></li>
				<li style="float: right;"><a href="#" forid="backupsdiv">Archived Backups</a></li>
			</ul>
		</h3>
	</div>
	<div class="panel-body" style="padding: 0px;">
		<div id="scheduledbackupsdiv">
			<table id="scheduledbackups" class="table table-striped" style="margin-bottom: 0px; border-bottom: 1px solid lightgray;">
				<thead>
					<tr>
						<th>ID</th>
						<th>Description</th>
						<th>Interval</th>
						<th>Last Backup</th>
					</tr>
				</thead>
				<tbody>
					${scheduledbackups}
				</tbody>
			</table>
		</div>
		
		<div id="backupsdiv" style="display: none;">
			<table id="backupstable" class="table table-striped" style="margin-bottom: 0px; border-bottom: 1px solid lightgray;">
				<thead>
					<tr>
						<th>ID</th>
						<th>Description</th>
						<th>Size</th>
						<th>Backed Up</th>
					</tr>
				</thead>
				<tbody>
					${backups}
				</tbody>
			</table>
		</div>
	</div>
</div>

<div class="modal fade" id="backupmodal" tabindex="-1" role="dialog" aria-labelledby="backupmodalLabel" aria-hidden="true">
	<div class="modal-dialog">
		<div class="modal-content">
			<div class="modal-header">
				<button type="button" class="close" data-dismiss="modal"><span aria-hidden="true">&times;</span><span class="sr-only">Close</span></button>
				<h4 class="modal-title" id="backupmodalLabel">New Backup Schedule</h4>
			</div>
			<div class="modal-body">
				<form id="schedulebackupform" role="form" method="post" action="/backups/">
					<h3>What to Backup</h3>
					<span style="margin-left: 10px;"><input type="radio" name="desc" value="everything" checked />&nbsp;&nbsp;<b>Everything</b></span>
					<br />
					<span style="margin-left: 10px;"><input type="radio" name="desc" value="plugins" />&nbsp;&nbsp;<b>All Plugins</b></span>
					<c:if test="${true}"><!-- Tested if server is installed yet -->
						<br />
						<span style="margin-left: 10px;"><input type="radio" name="desc" value="worlds" class="worldscheck" />&nbsp;&nbsp;<b>Worlds: </b></span>
						<br />
						<div class="worlds">
							${worlds}
						</div>
					</c:if>
					<h3>Backup Interval</h3>
					<div class="interval input-group-sm">
						<span style="margin-left: 10px;"><input type="radio" name="interval" value="minutes" checked />&nbsp;&nbsp;<b>Every <input type="text" class="form-control" name="intervaltime" value="30" /> Minute(s)</b></span>
						<br />
						<span style="margin-left: 10px;"><input type="radio" name="interval" value="hours" />&nbsp;&nbsp;<b>Every <input type="text" class="form-control" name="intervaltime" disabled /> Hour(s)</b></span>
						<br />
						<span style="margin-left: 10px;"><input type="radio" name="interval" value="days" />&nbsp;&nbsp;<b>Every <input type="text" class="form-control" name="intervaltime" disabled /> Day(s)</b></span>
						<br />
					</div>
				</form>
			</div>
			<div class="modal-footer">
				<button type="button" class="btn btn-default" data-dismiss="modal">Cancel</button>
				<button type="button" class="btn btn-primary" data-dismiss="modal">Schedule Backup</button>
			</div>
		</div>
	</div>
</div>