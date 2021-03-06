<%@taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>

<script src="/js/icheck.min.js"></script>

<style>
	label > span {
		font-size: 11pt;
		font-family: "Lucida Sans Unicode", "Lucida Grande", sans-serif;
		margin-left: 6px;
	}
	
	label > p {
		font-weight: normal;
		margin-top: 3px;
	}
	
	.indent {
		margin-left: 50px;
	}
	
	.permgroup {
		border-bottom: 1px solid #e5e5e5;
		margin-bottom: 20px;
		padding-bottom: 5px;
	}
	
	.permgroup:last-child {
		border-bottom: none;
		margin-bottom: 0px;
		padding-bottom: 0px;
	}
</style>

<script>
var canEditGroups = ${user.getGroup().hasPermission("web.groups.edit") == true};

$(function() {
	var clicked;
	
	$(".editperms").on("click", function() {
		showModalFull("Edit Permissions", $(this).attr("permissions"), "Save", true);
		
		$("#custommodalButton").click(function() {
			if (!clicked)
			{
				savePermissions();
				
				clicked = true;
			}
		});
		
		$('.permcheck').iCheck({
		    checkboxClass: 'icheckbox_square-blue',
		    radioClass: 'iradio_square-blue',
		    increaseArea: '10%'
	  	});
	});
	
	$(".delgroup").on("click", function() {
		showModalFull("Delete Group", "<form><label>Move everybody to this group:</label><select id='delgroupSelect' class='form-control'>${groupsStr}</select></form>", "Delete Group", true);
		
		var cled = false;
		var bttn = $(this);
		
		$("#custommodalButton").click(function() {
			if (!cled)
			{
				$.post("/groups/deleteGroup", {"groupid": bttn.attr("groupid"), "moveto": $("#delgroupSelect").val()}, function(data) {
					if (data.good != undefined)
					{
						var n = noty({
				            text        : "<b>Success: </b>" + data.good,
				            type        : 'success',
				            dismissQueue: true,
				            layout      : 'bottomLeft',
				            theme       : 'defaultTheme',
				            timeout     : 2000
				        });
						
						clicked = false;
					} else if (data.error != undefined)
					{
						var n = noty({
				            text        : "<b>Error: </b>" + data.error,
				            type        : 'error',
				            dismissQueue: true,
				            layout      : 'bottomLeft',
				            theme       : 'defaultTheme',
				            timeout     : 2000
				        });
						
						clicked = false;
					}
				});
				
				cled = true;
			}
		});
	});
});

function savePermissions()
{
	$.post("/groups/updatePermissions", {"id": $("#permform").attr("groupid"), "data": $("#permform").serialize()}, function(data) {
		if (data.good != undefined)
		{
			var n = noty({
	            text        : "<b>Success: </b>" + data.good,
	            type        : 'success',
	            dismissQueue: true,
	            layout      : 'bottomLeft',
	            theme       : 'defaultTheme',
	            timeout     : 2000
	        });
			
			clicked = false;
		} else if (data.error != undefined)
		{
			var n = noty({
	            text        : "<b>Error: </b>" + data.error,
	            type        : 'error',
	            dismissQueue: true,
	            layout      : 'bottomLeft',
	            theme       : 'defaultTheme',
	            timeout     : 2000
	        });
			
			clicked = false;
		}
	});
}
</script>

<script src="/js/viewjs/groups.js"></script>

<div class="panel panel-default">
	<div class="panel-heading">
		<h3 class="panel-title">
			Web Groups
		</h3>
	</div>
	<div class="panel-body" style="padding: 0px;">
		<table id="grouptable" class="table table-striped" style="margin-bottom: 0px; border-bottom: 1px solid lightgray;">
			<thead>
				<tr>
					<th>ID</th>
					<th>Group Name</th>
					<th>Ghost</th>
					<th>Existing Default</th>
					<th>Whitelist Default</th>
					<th>Actions</th>
				</tr>
			</thead>
			<tbody>
				${groups}
			</tbody>
		</table>
	</div>
</div>