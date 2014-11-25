<script>
$(function() {
	$("#usertable").dataTable({
		"bLengthChange": false,
		stateSave: true
	});
	
	gnameClick();
});

function gnameClick()
{
	$(".gname").click(function() {
		var group = $(this).text();
		
		$(this).parent().html("<select class='form-control' id='groupname' style='height: 24px; padding: 2px; width: 140px;'>${userGroups}</select>");
		
		$("#groupname").val(group);
		$("#groupname").focus();
		
		$("#groupname").blur(function() {
			$(this).parent().html("<span class=\"gname\" style=\"cursor: pointer;\">" + $(this).val() + "</span>");
			
			var rows = $("#usertable").dataTable().fnGetNodes();
			
			var data = [];
			
			for (var i = 0; i < rows.length; i++)
			{
				data[i] = {};
				
				data[i].id = $(rows[i]).find("td:eq(0)").text();
				data[i].group = $(rows[i]).find("td:eq(2)").text();
			}
			
			$.post("/users/saveUsers", {"data":JSON.stringify(data)}, function(ret) {
				if (ret.good != undefined)
				{
					var n = noty({
			            text        : "<b>Success: </b>" + ret.good,
			            type        : 'success',
			            dismissQueue: true,
			            layout      : 'bottomLeft',
			            theme       : 'defaultTheme',
			            timeout     : 2000
			        });
				} else if (ret.error != undefined)
				{
					var n = noty({
			            text        : "<b>Error: </b>" + ret.error,
			            type        : 'error',
			            dismissQueue: true,
			            layout      : 'bottomLeft',
			            theme       : 'defaultTheme',
			            timeout     : 2000
			        });
				}
			});
			
			gnameClick();
		});
	});
}
</script>

<div class="panel panel-default">
	<div class="panel-heading">
		<h3 class="panel-title">
			Web Users
		</h3>
	</div>
	<div class="panel-body" style="padding: 0px;">
		<table id="usertable" class="table table-striped" style="margin-bottom: 0px; border-bottom: 1px solid lightgray;">
			<thead>
				<tr>
					<th>ID</th>
					<th>Minecraft Username</th>
					<th>Group</th>
					<th>Whitelisted</th>
					<th>Blacklisted</th>
				</tr>
			</thead>
			<tbody>
				${users}
			</tbody>
		</table>
	</div>
</div>