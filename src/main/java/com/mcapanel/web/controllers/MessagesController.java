package com.mcapanel.web.controllers;

import java.util.List;

import org.json.simple.JSONArray;

import com.mcapanel.web.database.Message;
import com.mcapanel.web.handlers.Controller;

public class MessagesController extends Controller
{
	public boolean canView()
	{
		return isLoggedIn() && (user.getGroup().hasPermission("web.messages.view") || user.getGroup().hasPermission("web.messages.respond"));
	}
	
	public boolean index()
	{
		request.setAttribute("webmessages", arrayToString(getMessagesJson(true)));
		
		return renderView();
	}
	
	@SuppressWarnings("unchecked")
	private JSONArray getMessagesJson(boolean raw)
	{
		JSONArray groups = new JSONArray();
		
		List<Message> groupList = db.find(Message.class).findList();
		
		String b = raw ? "<td>" : "";
		String e = raw ? "</td>" : "";
		
		for (Message g : groupList)
		{
			JSONArray ar = new JSONArray();
			
			if (raw) ar.add("<tr>");
			ar.add(b + g.getId() + e);
			ar.add(b + g.getUsername() + e);
			ar.add(b + g.getSubject() + e);
			if (raw) ar.add("</tr>");
			
			groups.add(ar);
		}
		
		return groups;
	}
}