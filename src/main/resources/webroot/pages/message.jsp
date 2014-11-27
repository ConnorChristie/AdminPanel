<div class="panel panel-default">
	<div class="panel-heading">
		<h3 class="panel-title">
			Message: ${message.getId()}
		</h3>
	</div>
	<div class="panel-body">
		<div class="row" style="font-size: 13pt;">
			<div class="col-sm-12">
				<b>From</b><br />&nbsp;&nbsp;&nbsp;&nbsp;${message.getUsername()}<br /><br />
				<b>Subject</b><br />&nbsp;&nbsp;&nbsp;&nbsp;${message.getSubject()}<br /><br />
				<b>Message</b><br />&nbsp;&nbsp;&nbsp;&nbsp;${message.getMessage()}
			</div>
		</div>
		<br />
	</div>
</div>