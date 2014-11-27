package com.mcapanel.web.controllers;

import com.mcapanel.web.database.Message;
import com.mcapanel.web.handlers.Controller;

public class MessageController extends Controller
{
	public boolean canView()
	{
		return isLoggedIn() && (user.getGroup().hasPermission("web.messages.view") || user.getGroup().hasPermission("web.messages.respond"));
	}
	
	public boolean index()
	{
		return error();
	}
	
	public boolean view()
	{
		if (arguments.size() == 1)
		{
			Message message = db.find(Message.class, arguments.get(0));
			
			if (message != null)
			{
				request.setAttribute("message", message);
				
				return renderView();
			}
		}
		
		return error();
	}
}