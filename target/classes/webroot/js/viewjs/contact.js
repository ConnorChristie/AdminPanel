$(function() {
	$("#contactform").submit(function(e) {
		e.preventDefault();
		
		$.ajax("/contact/send", {
			data: $("#contactform").serialize(),
			type: "POST",
			success: function(data) {
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
				}
			}
		});
	});
});