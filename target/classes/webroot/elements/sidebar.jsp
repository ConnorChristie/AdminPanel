<%@taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>

<div class="col-sm-4">
	<div class="panel panel-default">
		<div class="panel-heading">
			<h3 class="panel-title">
				Server Status: <span>${onlinePlayers.label}</span>
				<span id="ponline" style="float: right;">${onlinePlayers.online}</span>
			</h3>
		</div>
		<div class="panel-body" style="padding: 0px;">
			<table id="oplayertable" class="table" style="margin-bottom: 0px;">
				<thead>
					<tr>
						<th class="img"></th>
						<th>Username</th>
						<th>Rank</th>
						<th>World</th>
					</tr>
				</thead>
				<tbody id="playerson">
					${onlinePlayers.list}
				</tbody>
			</table>
			
			<a href="/players/">
				<div style="background-color: #E7E7E7; padding: 4px; text-align: center;">
					<b>View All Players</b>
				</div>
			</a>
		</div>
	</div>
</div>