<%@taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>

<script src="/js/viewjs/plugins.js"></script>

<script>
$(function() {
	$("#fileupload").submit(function(e) {
		e.preventDefault();
		
		var formData = new FormData($('#fileupload')[0]);
        $.ajax({
            url: '/plugins/upload',
            type: 'POST',
            xhr: function() {
                myXhr = $.ajaxSettings.xhr();
                
                if (myXhr.upload)
                {
                	showModalFull("Upload File", '<div class="progress"><div id="uploaded" class="progress-bar progress-bar-success progress-bar-striped" role="progressbar" aria-valuenow="0" aria-valuemin="0" aria-valuemax="100" style="width: 0%"><span class="sr-only">0% Complete</span></div></div>', "Ok", null);
                	
                    myXhr.upload.addEventListener('progress', function(e) {
                    	var perc = Math.ceil(e.loaded * 100 / e.total);
                    	
                    	console.log("Progress: " + e.loaded + " / " + e.total + " (" + perc + "%)");
                    	
                    	$("#uploaded").css('width', perc + '%').attr('aria-valuenow', perc);
                    }, false);
                }
                return myXhr;
            },
            success: completeHandler = function(data) {
            	setTimeout(function() { showModal("Uploaded File", "Successfully uploaded your files and also reloaded the server! Plugins should be installed!") }, 1000);
            },
            error: errorHandler = function() {
                showModal("Error Uploading File", "There was an error uploading the requested files, please refresh the page and try again.");
            },
            data: formData,
            cache: false,
            contentType: false,
            processData: false
        }, 'json');
	});
});
</script>

<div class="panel panel-default">
	<div class="panel-heading">
		<h3 class="panel-title">
			<c:choose>
				<c:when test="${connected}">
					<ul id="pluginlist" class="nav nav-pills">
						<li class="active"><a href="#" forid="installedpluginsdiv">Installed Plugins</a></li>
						<li style="float: right;"><a href="#" forid="browsepluginsdiv">Browse Plugins</a></li>
					</ul>
				</c:when>
				<c:otherwise>
					Browse Plugins
				</c:otherwise>
			</c:choose>
		</h3>
	</div>
	<div class="panel-body" style="padding: 0px;">
		<c:if test="${connected}">
			<div id="installedpluginsdiv">
				<table id="installedplugins" class="table table-striped"
					style="margin-bottom: 0px; border-bottom: 1px solid lightgray;">
					<thead>
						<tr>
							<th>Plugin Name</th>
							<th>Status</th>
							<th>Description</th>
						</tr>
					</thead>
					<tbody>${plugins}
					</tbody>
				</table>
			</div>
		</c:if>

		<style>
.pluginview {
	border-left: none;
	border-right: none;
	border-bottom-right-radius: 0px !important;
	border-bottom-left-radius: 0px !important;
}

.pluginview:first-child {
	border: none;
}

.pluginview:last-child {
	border-bottom: none;
}

#pluginsholder {
	border-radius: 4px;
	margin-bottom: 0px;
	height: 739px;
	overflow-y: scroll;
	border: 1px solid #ddd;
}
</style>

		<div id="browsepluginsdiv"
			style="padding: 15px; ${connected ? 'display: none;' : ''}">
			<div class="row">
				<div class="col-sm-6">
					<input type="text" id="pluginsearch" class="form-control" style="margin-bottom: 15px;" placeholder="Search for plugin" />
				</div>
				<div class="col-sm-6">
					<form id="fileupload" method="POST" action="/plugins/upload" enctype="multipart/form-data">
						<input type="file" name="pluginfile" id="pluginfile" class="form-control" style="width: 78%; float: left;" multiple />
						<input type="submit" name="uploadfile" class="btn btn-primary" style="width: 20%; float: right;" value="Upload File" />
					</form>
				</div>
			</div>
			<div class="panel panel-default"
				style="box-shadow: none; border: 1px solid #DADADA; margin-bottom: 15px;">
				<div class="panel-heading" style="height: 38px;">
					<div class="col-sm-4" style="padding-left: 0px;">
						<h3 class="panel-title">Categories</h3>
					</div>
					<div class="col-sm-8" style="border-left: 1px solid #DADADA;">
						<h3 class="panel-title">Plugins</h3>
					</div>
				</div>
				<div class="panel-body">
					<div class="col-sm-4" style="padding-left: 0px;">
						<div id="pluginscats" class="list-group"
							style="margin-bottom: 0px;"></div>
					</div>
					<div class="col-sm-8" style="border-left: 1px solid #DADADA;">
						<div id="pluginsholder">
							<div id="pluginsview" class="list-group"
								style="margin-bottom: 0px;"></div>
						</div>
					</div>
				</div>
			</div>
		</div>
	</div>
</div>