<%@taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>

<div class="panel panel-default">
	<div class="panel-heading">
		<h3 class="panel-title">
			Bukkit Players
		</h3>
	</div>
	<div class="panel-body" style="padding: 0px;">
		<table id="playertable" class="table table-striped" style="margin-bottom: 0px; border-bottom: 1px solid lightgray;">
			<thead>
				<tr>
					<th class="img" style="min-width: 0px;"></th>
					<th>Minecraft Username</th>
					<th>Status</th>
					<th>Group</th>
					<th>World</th>
					<th>Health</th>
					<th>Food</th>
				</tr>
			</thead>
			<tbody>
				${playerlist}
			</tbody>
		</table>
	</div>
</div>