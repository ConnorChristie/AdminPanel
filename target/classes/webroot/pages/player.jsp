<div class="row">
	<div class="col-sm-8" style="padding-right: 0px;">
		<div class="panel panel-default">
			<div class="panel-heading">
				<h3 class="panel-title">
					About ${player.getName()} <span class="label label-${player.getStatusLabel()}" style="float: right; text-shadow: none; padding-top: 5px; margin-top: -2px;">${player.getStatus()}</span>
				</h3>
			</div>
			<div id="playerDiv" class="panel-body" style="border-bottom: 2px solid #52C5FF;">
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
				<div id="inventory" style="background: url(/images/Inventory.png) no-repeat; height: 368px; position: relative;">
					
					<script>
						$(function() {
							var inventory = $("#inventory");
							var slotTemplate = $("<div class='slot' style='width: 65px; height: 65px; background-color: blue; position: absolute;'></div>");
							
							var width = 9;
							var height = 4;
							
							for (var x = 0; x < width; x++)
							{
								for (var y = 0; y < height; y++)
								{
									var slot = slotTemplate.clone();
									
									var left = x * (65 + 8) + 33;
									var top = y * (65 + 8) + 33 + (y == height - 1 ? 17 : 0);
									
									slot.css({"left: "left + "px", "top": top + "px"});
									slot.droppable({
										accept: ".item",
										drop: function(event, ui) {
											ui.draggable.position({of: $(this), my: 'left top', at: 'left top'});
										    ui.draggable.draggable('option', 'revert', false);
										}
									});
									
									inventory.append(slot);
								}
							}
							
							var itemTemplate = $("<div class='item' style='width: 65px; height: 65px; position: absolute;'><span>4</span></div>");
							
							var item = itemTemplate.clone();
							
							item.css({"background": "url(/images/items/1-1.png) no-repeat", "background-size": "64px", "background-position": "center"});
							item.draggable({
								revert: true
							});
							
							$("#inventoryDiv").append(item);
						});
					</script>
				</div>
			</div>
		</div>
	</div>
	<div class="col-sm-4" style="padding-left: 0px;">
		<div class="panel panel-default">
			<div class="panel-heading">
				<h3 class="panel-title">
					Inventory Items
				</h3>
			</div>
			<div id="inventoryDiv" class="panel-body" style="border-bottom: 2px solid #52C5FF; background-color: lightgray;">
				Items
			</div>
		</div>
	</div>
	<script>
		$(function() {
			var height = $("#playerDiv").outerHeight();
			
			$("#inventoryDiv").css({"min-height": height + "px"});
		});
	</script>
</div>