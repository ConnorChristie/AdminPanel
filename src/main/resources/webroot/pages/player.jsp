<div class="panel panel-default">
	<div class="panel-heading">
		<h3 class="panel-title">
			About ${player.getName()} <span class="label label-${player.getStatusLabel()}" style="float: right; text-shadow: none; padding-top: 5px; margin-top: -2px;">${player.getStatus()}</span>
		</h3>
	</div>
	<div class="panel-body" style="border-bottom: 2px solid #52C5FF;">
		<img src="https://crafatar.com/avatars/${player.getName()}?size=65&helm" style="float: left;" />
		<div style="margin-left: 12px; margin-top: -2px; float: left;">
			<span style="font-size: 14pt;"><b>${player.getName()}</b></span><br />
			<span style="font-size: 11pt;"><b>Joined: ${player.getFirstPlayed()}</b></span><br />
			<span style="font-size: 11pt;"><b>Last Played: ${player.getLastPlayed()}</b></span>
		</div>
		<br style="clear: both;" />
		<br />
		<div class="row" style="font-size: 13pt;">
			<div class="col-sm-4" style="text-align: center;">
				<b>Group</b><br />${player.getGroup()}
			</div>
			<div class="col-sm-4" style="text-align: center;">
				<b>Health</b><br />${player.getHealth()}%
			</div>
			<div class="col-sm-4" style="text-align: center;">
				<b>Food</b><br />${player.getFood()}%
			</div>
		</div>
		<br />
	</div>
</div>