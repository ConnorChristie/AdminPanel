$(function() {
	$("#installform").submit(function(e) {
		e.preventDefault();
		
		$("#mcpass").val(md5($("#tmcpass").val()));
		$("#mcpassconf").val(md5($("#tmcpassconf").val()));
		
		$("#tmcpass").val("");
		$("#tmcpassconf").val("");
		
		$.ajax({
			type: "post",
			url: "/install/process",
			data: $("#installform").serialize(),
			dataType : "json",
			success: function(data) {
				if (data.good != undefined)
				{
					showModal("Done Installing", data.good);
					
					$("#custommodal .btn").click(function() {
						document.location = data.redirect;
					});
				} else if (data.error != undefined)
				{
					errorModal(data.error);
				}
			}
		});
	});
});