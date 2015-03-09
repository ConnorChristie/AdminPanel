<%@taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>

<script>var canWhitelist = ${user.getGroup().hasPermission("server.whitelist.edit") == true};</script>

<script src="/js/viewjs/applications.js"></script>

<div class="panel panel-default">
	<div class="panel-heading">
		<h3 class="panel-title">
			Whitelist Applications
		</h3>
	</div>
	<div class="panel-body" style="padding: 0px; border-bottom: 2px solid #52C5FF;">
		<table id="whitelistapps" class="table table-striped" style="margin-bottom: 0px; border-bottom: 1px solid lightgray;">
			<thead>
				<tr>
					<th>ID</th>
					<th>Minecraft Username</th>
					<th>Description</th>
					<th>Date</th>
				</tr>
			</thead>
			<tbody>
				${whitelistapps}
			</tbody>
		</table>
	</div>
</div>