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
				
				<div class="row" style="font-size: 13pt; margin-bottom: 10px;">
					<div class="col-sm-5" style="padding-top: 15px; padding-left: 30px;">
						<b>Group</b><br />&nbsp;&nbsp;&nbsp;&nbsp;${player.getGroup()}
							<br /><br />
						<b>Health</b><br />&nbsp;&nbsp;&nbsp;&nbsp;${player.getHealth()}%
							<br /><br />
						<b>Food</b><br />&nbsp;&nbsp;&nbsp;&nbsp;${player.getFood()}%
					</div>
					
					<div class="col-sm-7">
						<b>Player Inventory</b><br />
						<div id="inventory" style="background: url(/images/Inventory.png) no-repeat; height: 187px; width: 363px; position: relative;">
							
						</div>
					</div>
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
			<div id="inventoryDiv" class="panel-body" style="border-bottom: 2px solid #52C5FF; background-color: lightgray; position: relative; overflow-y: scroll; overflow-x: hidden;">
				
			</div>
		</div>
	</div>
	<script>
		$(function() {
			var height = $("#playerDiv").outerHeight();
			
			$("#inventoryDiv").css({"min-height": height + "px"});
			
			$.getJSON("/elements/items.json", function(data) {
				var itemTemplate = $("<div class='item' data-toggle='tooltip' data-placement='top' title='Tooltip on top' style='position: absolute; cursor: pointer; width: 32px; height: 32px;'><span id='amount'>64</span></div>");
				
				var width = 8;
				var height = 75;
				
				for (var y = 0; y < height; y++)
				{
					for (var x = 0; x < width; x++)
					{
						var item = itemTemplate.clone();
						var itemJson = data[y * width + x];
						
						if (itemJson == undefined) continue;
						
						var top = y * 40 + 10;
						var left = x * 40 + 20;
						
						item.attr("title", itemJson.name);
						item.attr("type", itemJson.type);
						item.attr("meta", itemJson.meta);
						item.css({"background": "url(/images/items/" + itemJson.type + "-" + itemJson.meta + ".png) no-repeat", "background-size": "32px", "top": top + "px", "left": left + "px"});
						item.draggable({
							revert: true,
							cursor: "hand",
							stack: "body",
							appendTo: "body",
							helper: "clone",
							start: startFunction
						});
						
						$("#inventoryDiv").append(item);
					}
				}
				
				function startFunction(event, ui) {
					var clonedItem = $(this).clone();
					
					$(this).draggable("option", "start", function(event, ui) {});
					$(this).draggable("option", "helper", "");
					
					clonedItem.draggable({
						revert: true,
						cursor: "hand",
						stack: "body",
						appendTo: "body",
						helper: "clone",
						start: startFunction
					});
					
					$("#inventoryDiv").append(clonedItem);
				};
				
				var inventory = $("#inventory");
				var slotTemplate = $("<div class='slot' style='width: 33px; height: 33px; position: absolute;'></div>");
				
				var width = 9;
				var height = 4;
				
				for (var y = 0; y < height; y++)
				{
					for (var x = 0; x < width; x++)
					{
						var slot = slotTemplate.clone();
						var slotId = ((height - y) * width + x) - 9;
						
						if (slotId >= 9)
						{
							slotId = (35 - slotId) + 9;
							slotId = (slotId / 17 <= 1 ? 17 : (slotId / 26 <= 1 ? 35 : (slotId / 35 <= 1 ? 53 : 0))) - slotId + 9;
						}
						
						var left = x * (33 + 4) + 17;
						var top = y * (33 + 4) + 17 + (y == height - 1 ? 9 : 0);
						
						slot.attr("slot", slotId);
						slot.css({"left": left + "px", "top": top + "px"});
						slot.droppable({
							accept: ".item",
							drop: function(event, ui) {
								var lastSlot = ui.draggable.attr("last-slot");
								
								if ($(this).find(".item").length >= 1)
								{
									var itemDraggable = $(this).find(".item").first();
									
									if (lastSlot != undefined)
									{
										$(".slot[slot='" + lastSlot + "'").append(itemDraggable);
										itemDraggable.attr("last-slot", lastSlot);
									} else
									{
										$(".tooltip").remove();
										itemDraggable.remove();
									}
								}
								
								$(this).append(ui.draggable);
								
								ui.draggable.position({of: $(this), my: 'left top', at: 'left top'});
							    ui.draggable.draggable('option', 'revert', 'invalid');
							    ui.draggable.attr("last-slot", $(this).attr("slot"));
							    
							    saveInventory();
							}
						});
						
						inventory.append(slot);
					}
				}
				
				var invJson = $.parseJSON('${player.getInventory()}');
				
				$.each(invJson, function(i, item) {
					if (item.exists)
					{
						var itemElem = itemTemplate.clone();
						var itemJson = getObject(data, {"type": item.type, "meta": item.meta});
						
						itemElem.attr("title", itemJson.name);
						itemElem.attr("type", itemJson.type);
						itemElem.attr("meta", itemJson.meta);
						itemElem.attr("last-slot", item.slot);
						itemElem.css({"background": "url(/images/items/" + itemJson.type + "-" + itemJson.meta + ".png) no-repeat", "background-size": "32px"});
						itemElem.draggable({
							revert: true,
							cursor: "hand",
							stack: "body",
							appendTo: "body"
						});
						
						itemElem.find("#amount").first().text(item.amount != 1 ? item.amount : "");
						
						$(".slot[slot='" + item.slot + "']").append(itemElem);
					}
				});
				
				$('[data-toggle="tooltip"]').tooltip({
					container: 'body'
				});
			});
		});
		
		function saveInventory()
		{
			var inventory = [];
			
			$("#inventory .slot").each(function(slot) {
				
			});
		}
		
		function getObject(obj, keyVals) 
		{
		    var newObj = false;
		    
		    $.each(obj, function() {
		        var testObject = this;
		        var good = false;
		        
		        $.each(keyVals, function(key, value) {
	        		if (testObject[key] == value)
	        		{
	        			good = true;
	        		} else
	        		{
	        			good = false;
	        			
	        			return false;
	        		}
	        	});
		        
		        if (good) newObj = testObject;
		    });

		    return newObj;
		}
	</script>
</div>