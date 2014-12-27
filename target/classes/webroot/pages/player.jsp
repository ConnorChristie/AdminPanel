<div class="panel panel-default">
	<div class="panel-heading">
		<h3 class="panel-title">
			About ${player} <span class="label label-${statusLabel}" style="float: right; text-shadow: none; padding-top: 5px; margin-top: -2px;">${playerStatus}</span>
		</h3>
	</div>
	<div class="panel-body">
		<img src="https://crafatar.com/avatars/${player}?size=65&helm" style="float: left;" />
		<div style="margin-left: 12px; margin-top: -2px; float: left;">
			<span style="font-size: 14pt;"><b>${player}</b></span><br />
			<span style="font-size: 11pt;"><b>Joined: ${firstPlayed}</b></span><br />
			<span style="font-size: 11pt;"><b>Last Played: ${lastPlayed}</b></span>
		</div>
		<br style="clear: both;" />
		<br />
		<div class="row" style="font-size: 13pt;">
			<div class="col-sm-6">
				<b>Group</b><br />&nbsp;&nbsp;&nbsp;&nbsp;${group}<br /><br />
				<b>Health</b><br />&nbsp;&nbsp;&nbsp;&nbsp;${health}%<br /><br />
				<b>Food</b><br />&nbsp;&nbsp;&nbsp;&nbsp;${food}%
			</div>
			<div class="col-sm-6">
				
			</div>
		</div>
		<br />
	</div>
</div>