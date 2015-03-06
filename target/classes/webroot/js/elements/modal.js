function errorModal(body)
{
	showModalFull("Error", body, "Ok", false);
}

function showModal(title, body)
{
	showModalFull(title, body, "Ok", false);
}

function showModalFull(title, body, button, closeButton)
{
	var modal = $("#custommodal");
	
	modal.find("#custommodalLabel").text(title);
	modal.find("#custommodalBody").html(body);
	modal.find("#custommodalButton").text(button);
	
	if (closeButton)
	{
		$('<button type="button" class="btn btn-default" data-dismiss="modal">Close</button>').insertBefore("#custommodalButton");
	}
	
	modal.on("hidden.bs.modal", function() {
		modal.find(".btn-default").remove();
	});
	
	modal.modal();
}

function modalClick(modalId, handler)
{
	$(modalId + " .btn-primary").bind("click", handler);
	
	$(modalId).on("hidden.bs.modal", function () {
		$(modalId + " .btn-primary").unbind("click", handler);
	})
}