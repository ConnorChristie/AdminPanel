$(function() {
	$("#whitelistform").submit(function(e) {
		e.preventDefault();
		
		$("#mcpass").val(md5($("#tmcpass").val()));
		$("#mcpassconf").val(md5($("#tmcpassconf").val()));
		
		$("#tmcpass").val("");
		$("#tmcpassconf").val("");
		
		$.ajax({
			type: "post",
			url: "/user/whitelist",
			data: $("#whitelistform").serialize(),
			dataType : "json",
			success: function(data) {
				if (data.good != undefined)
				{
					showModal("Thanks for Applying!", data.good);
					
					$("#custommodal .btn").click(function() {
						document.location = "/";
					});
				} else if (data.error != undefined)
				{
					Recaptcha.reload();
					
					errorModal(data.error);
				}
			}
		});
	});
	
	$("#charts").hide();
	
	showPercents();
});

function showCharts()
{
	$("#apercents").css({"color": "#428bca"});
	$("#acharts").css({"color": "#5E5E5E"});
	
	$("#percents").fadeOut(400, function(){
        $("#charts").fadeIn(400);
    });
	
	return false;
}

function showPercents()
{
	$("#acharts").css({"color": "#428bca"});
	$("#apercents").css({"color": "#5E5E5E"});
	
	$("#charts").fadeOut(400, function(){
        $("#percents").fadeIn(400);
    });
	
	return false;
}