<script src="/js/jsTree/jstree.js"></script>
<script src="/js/codemirror.js"></script>
<script src="/js/yaml.js"></script>

<script>
var saved = true;

var yData;
var myCodeMirror;

var permFile = false;

var selectedUser = null;
var selectedGroup = null;

$(function () {
	myCodeMirror = CodeMirror.fromTextArea(document.getElementById("plugin_editor"), {
	    lineNumbers: true,
	    mode: "yaml"
	});
	
	$("#plugin_jstree").on("select_node.jstree", function(e, data) {
		if (!saved)
		{
			showModalFull("Save File", "You haven't saved your file, are you sure you want to continue?", "Yes", true);
			
			$("#custommodalButton").click(function() {
				selectNode(myCodeMirror, data);
			});
		} else
		{
			selectNode(myCodeMirror, data);
		}
	}).on("after_open.jstree", function() {
		var height = $(this).height();
		
		if ($(this).height() < 500)
			height = 500;
		
		myCodeMirror.setSize(document.getElementById("plugin_editor").width, height);
	}).on("after_close.jstree", function() {
		var height = $(this).height();
		
		if ($(this).height() < 500)
			height = 500;
		
		myCodeMirror.setSize(document.getElementById("plugin_editor").width, height);
	}).jstree(JSON.parse('${files}'));
	
	myCodeMirror.on("change", function(cm, change) {
		changeEvent(0);
	});
	
	$("#savefile").click(saveFile);
});

function saveFile()
{
	if (!saved)
	{
		changeEvent(1);
		
		var path = $("#plugin_jstree").jstree().get_path('#' + $("#plugin_jstree").jstree().get_selected()[0]);
		
		if (!permFile)
		{
			$.post('/plugin/saveFile/<%= request.getPathInfo().replace("/plugin/files/", "") %>', {type:"raw", file:path.join("~"), data:myCodeMirror.getValue()}, function(data) {
				if (data.good != undefined)
				{
					changeEvent(2);
				} else if (data.error != undefined)
				{
					changeEvent(0);
					
					errorModal(data.error);
				}
			});
		} else
		{
			$.post('/plugin/saveFile/<%= request.getPathInfo().replace("/plugin/files/", "") %>', {type:"perms", file:path.join("~"), data:JSON.stringify(yData)}, function(data) {
				if (data.good != undefined)
				{
					changeEvent(2);
				} else if (data.error != undefined)
				{
					changeEvent(0);
					
					errorModal(data.error);
				}
			});
		}
	}
}

function selectNode(myCodeMirror, data)
{
	var path = data.instance.get_path('#' + data.selected[0]);
	
	if (path[path.length - 1].indexOf(".") >= 0)
	{
		var jPath = path.join("~");
		
		$.ajax({
			async: false,
			type: "POST",
			url: '/plugin/getContent/<%= request.getPathInfo().replace("/plugin/files/", "") %>',
			data: {file:jPath},
			success: function(data) {
				if (data.good != undefined)
				{
					data = data.good;
					
					if (jPath.indexOf("permissions") >= 0 && jPath.indexOf(".yml") >= 0 && "<%= request.getPathInfo() %>".indexOf("PermissionsEx") >= 0)
					{
						permFile = true;
						
						//Permissions File
						
						$("#texteditor").css({display: "none"});
						$("#permeditor").css({display: "block"});
						
						yData = YAML.parse(data);
						
						console.log(yData);
						
						var perms = [];

						updateGroups();
						
						$("#addgroup").click(function() {
							$("#groups :selected").each(function() {
								var group = $(this);
								
								$("#users :selected").each(function() {
									if (yData.users[$(this).val()].group.indexOf(group.text()) == -1)
										yData.users[$(this).val()].group.push(group.text());
								});
							});
							
							updateUser();
							changeEvent(0);
						});
						
						$("#remgroup").click(function() {
							$("#groups :selected").each(function() {
								var group = $(this);
								
								$("#users :selected").each(function() {
									var index = yData.users[$(this).val()].group.indexOf(group.text());
									
									if (index != -1)
										yData.users[$(this).val()].group.splice(index, 1);
								});
							});
							
							updateUser();
							changeEvent(0);
						});
						
						$("#groupform").submit(function() {
							if (selectedGroup != null)
							{
								var gdef = $("#gdefault").is(":checked");
								var gname = $("#gname").val();
								var gprefix = $("#gprefix").val();
								var gsuffix = $("#gsuffix").val();
									
								var group = yData.groups[selectedGroup];
								
								delete yData.groups[selectedGroup];
								
								yData.groups[gname] = group;
								
								if (yData.groups[gname]["options"] == undefined) yData.groups[gname]["options"] = {};
								
								yData.groups[gname]["options"]["default"] = gdef;
								yData.groups[gname]["options"]["prefix"] = gprefix;
								yData.groups[gname]["options"]["suffix"] = gsuffix;
								
								selectedGroup = gname;
								
								updateGroups();
								changeEvent(0);
								
								$("#groups").val(gname);
							}
							
							return false;
						});
						
						$("#makegroup").click(function() {
							var gdef = $("#gdefault").is(":checked");
							var gname = $("#gname").val();
							var gprefix = $("#gprefix").val();
							var gsuffix = $("#gsuffix").val();
							
							yData.groups[gname] = {permissions:[], options:{default:gdef, prefix:gprefix, suffix:gsuffix}};
							
							selectedGroup = gname;
							
							updateGroups();
							changeEvent(0);
							
							$("#groups").val(gname);
							
							updateSelectedGroup();
							
							return false;
						});
						
						$("#permissions").html("");
						
						for (var key in perms)
						{
							$("#permissions").html($("#permissions").html() + "<option>" + perms[key] + "</option>");
						}
						
						$("#users").html("");
						
						for (var key in yData.users)
						{
							$("#users").html($("#users").html() + "<option value='" + key + "'>" + yData.users[key].options.name + "</option>");
						}
						
						$("#addperm").click(function() {
							showModalFull("Add Permission", "<input type=\"text\" class=\"form-control\" id=\"permission\" placeholder=\"Enter Permission\" onkeydown=\"if (event.keyCode == 13) $('#custommodal .btn').click();\">", "Add Permission", true);
							
							$("#custommodal").on("shown.bs.modal", function (e) {
								$("#permission").focus();
							})
							
							$("#custommodal .btn-primary").one("click", function() {
								if (selectedGroup != null)
								{
									yData.groups[selectedGroup].permissions.push($("#permission").val());
									
									$("#permissions").html("");
									
									for (var key in yData.groups[selectedGroup].permissions)
									{
										$("#permissions").html($("#permissions").html() + "<option>" + yData.groups[selectedGroup].permissions[key] + "</option>");
									}
									
									changeEvent(0);
								}
							});
						});
						
						$("#remperm").click(function() {
							$("#permissions :selected").each(function() {
								yData.groups[selectedGroup].permissions.splice(yData.groups[selectedGroup].permissions.indexOf($(this).val(), 1));
								$(this).remove();
								
								changeEvent(0);
							});
						});
						
						$("#users").click(function() {
							selectedUser = $(this).val();
							
							updateUser();
						});
					} else
					{
						permFile = false;
						
						$("#permeditor").css({display: "none"});
						$("#texteditor").css({display: "block"});
						
						myCodeMirror.setValue(data);
						myCodeMirror.clearHistory();
						myCodeMirror.markClean();
						
						var end = path[path.length - 1].split(".");
						end = end[end.length - 1];
						
						switch (end)
						{
							case "yml":
								loadScript("/js/mode/yaml/yaml.js");
								myCodeMirror.setOption("mode", "yaml");
								
								break;
							case "js":
								loadScript("/js/mode/javascript/javascript.js");
								myCodeMirror.setOption("mode", "javascript");
								
								break;
							case "jsp":
							case "html":
								loadScript("/js/mode/xml/xml.js");
								myCodeMirror.setOption("mode", "xml");
								
								break;
							default:
								loadScript("/js/mode/" + end + "/" + end + ".js");
								myCodeMirror.setOption("mode", end);
								
								break;
						}
						
						$(window).keydown(function(event) {
							if ((event.which == 83 || event.which == 115) && event.ctrlKey)
							{
							    saveFile();
							    
							    event.preventDefault();
							    
							    return false;
							}
						    
						    return true;
						});
					}
					
					changeEvent(2);
				} else if (data.error != undefined)
				{
					errorModal(data.error);
				}
			},
			error: function() {
				changeEvent(0);
			}
		});
	} else
	{
		data.instance.toggle_node('#' + data.selected[0]);
	}
}

function updateGroups()
{
	$("#groups").html("");
	
	for (var key in yData.groups)
	{
		$("#groups").html($("#groups").html() + "<option value='" + key + "'>" + key + "</option>");
		
		/*
		for (var perm in yData.groups[key].permissions)
		{
			perm = yData.groups[key].permissions[perm];
			
			if ($.inArray(perm, perms) == -1)
			{
				perms.push(perm);
			}
		}
		*/
	}
	
	$("#groups option").click(function() {
		selectedGroup = $(this).text();
		
		updateSelectedGroup();
	});
}

function updateSelectedGroup()
{
	var group = yData.groups[selectedGroup];
	
	$("#gname").val(selectedGroup);
	
	if (group.options != undefined && group.options.default != undefined)
	{
		if (group.options.default == "true")
			$("#gdefault").prop("checked", true);
		else
			$("#gdefault").prop("checked", false);
	} else
		$("#gdefault").prop("checked", false);
	
	if (group.options != undefined && group.options.prefix != undefined)
		$("#gprefix").val(group.options.prefix);
	else
		$("#gprefix").val("");
	
	if (group.options != undefined && group.options.suffix != undefined)
			$("#gsuffix").val(group.options.suffix);
	else
		$("#gsuffix").val("");
	
	$("#permissions").html("");
	
	for (var key in group.permissions)
	{
		$("#permissions").html($("#permissions").html() + "<option>" + group.permissions[key] + "</option>");
	}
}

function updateUser()
{
	var user = yData.users[selectedUser];
	
	$("#ugroup").html("");
	
	if (user.group != undefined)
	{
		for (var key in user.group)
		{
			$("#ugroup").html($("#ugroup").html() + "<br />&nbsp;&nbsp;- " + user.group[key]);
		}
	}
	
	$("#uuid").text(selectedUser);
}

function loadScript(script)
{
	$.ajax({
	    async: false,
	    type: 'GET',
	    url: script
	});
}

function changeEvent(state)
{
	if (state == 0)
	{
		$("#savefile").removeClass("btn-success").removeClass("btn-noclick").addClass("btn-danger").text("Save File");
		
		saved = false;
		
		window.onbeforeunload = function (e) {
			if (!saved)
			{
				var message = "Noooo, you haven't saved your file!",
				
				e = e || window.event;
				
				if (e) e.returnValue = message;
				
				return message;
			}
		};
	} else if (state == 1)
	{
		$("#savefile").text("Saving File...");
	} else if (state == 2)
	{
		$("#savefile").removeClass("btn-danger").addClass("btn-noclick").addClass("btn-success").text("File Already Saved");
		
		saved = true;
	}
}
</script>

<div class="panel panel-default">
	<div class="panel-heading">
		<h3 class="panel-title" style="float: left; margin-top: 4px;">
			Plugin Files
		</h3>
		<button type="button" id="savefile" class="btn btn-xs btn-success btn-noclick" style="float: right;">File Already Saved</button>
		<br style="clear: both;" />
	</div>
	<div class="panel-body" style="padding: 0px;">
		<div class="row">
			<div class="col-sm-3" style="padding-right: 0px;">
				<div id="plugin_jstree" style="overflow: auto; padding: 5px; padding-top: 0px; width: 100%;"></div>
			</div>
			<div class="col-sm-9" style="padding-left: 0px;">
				<div id="permeditor" style="display: none;">
					<div style="float: left; padding: 10px; padding-bottom: 20px;">
						<div style="float: left;">
							<b style="font-size: 13pt;">Groups:</b>
							<select id="groups" size="4" multiple="multiple" class="form-control" style="width: 230px; min-height: 200px;"></select>
							
							<button type="button" id="addgroup" class="btn btn-xs btn-success" style="margin-top: 10px;">Add Group(s) to Selected User(s)</button>
							<br />
							<button type="button" id="remgroup" class="btn btn-xs btn-danger" style="margin-top: 10px;">Remove Group(s) from User(s)</button>
						</div>
						
						<div style="float: left; margin-left: 20px;">
							<b style="font-size: 13pt;">Permissions:</b>
							<select id="permissions" size="4" multiple="multiple" class="form-control" style="width: 230px; min-height: 200px;"></select>
							
							<button type="button" id="addperm" class="btn btn-xs btn-success" style="float: right; margin-top: 10px;">Add Permission to Selected Group</button>
							<br />
							<button type="button" id="remperm" class="btn btn-xs btn-danger" style="float: right; margin-top: 10px;">Remove Permission from Group</button>
						</div>
					</div>
					
					<div style="float: right; margin-top: 10px; margin-right: 20px; width: 330px;">
						<b style="font-size: 13pt;">Users:</b>
						<select id="users" size="4" multiple="multiple" class="form-control" style="min-height: 200px;"></select>
						
						<div style="margin-top: 10px; font-size: 13pt;">
							<b>Group: </b>
							<span id="ugroup"></span>
							<br />
							<b>UUID: </b>
							<br />
							&nbsp;&nbsp;<span id="uuid"></span>
						</div>
					</div>
					
					<br style="clear: left;" />
					
					<div style="float: left; margin-left: 10px; margin-bottom: 18px;">
						<form id="groupform" role="form" method="post" action="<%= request.getPathInfo() %>" style="width: 230px;">
							<div class="form-group">
								<label for="gname" style="float: left;">Group Name</label>
								<div style="float: right;">
									<input type="checkbox" id="gdefault" name="gdefault">
									<label for="gdefault">Is Default</label>
								</div>
								<input type="text" class="form-control" id="gname" name="gname" placeholder="Enter Group Name" required>
							</div>
							<div class="form-group">
								<label for="gprefix">Group Prefix</label>
								<input type="text" class="form-control" id="gprefix" name="gprefix" placeholder="Enter Group Prefix">
							</div>
							<div class="form-group">
								<label for="gsuffix">Group Suffix</label>
								<input type="text" class="form-control" id="gsuffix" name="gsuffix" placeholder="Enter Group Suffix">
							</div>
							<button type="submit" id="savegroup" class="btn btn-primary">Save Group</button>
							<button type="submit" id="makegroup" class="btn btn-success" style="float: right;">Create Group</button>
						</form>
					</div>
				</div>
				
				<div id="texteditor">
					<textarea id="plugin_editor" style="outline: none; width: 100%; min-height: 500px; resize: none; border-top: none; border-bottom: none; border-right: none;"></textarea>
				</div>
			</div>
		</div>
	</div>
</div>