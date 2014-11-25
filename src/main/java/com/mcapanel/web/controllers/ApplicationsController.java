package com.mcapanel.web.controllers;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

import com.mcapanel.web.database.Application;
import com.mcapanel.web.database.Group;
import com.mcapanel.web.database.User;
import com.mcapanel.web.handlers.Controller;

public class ApplicationsController extends Controller
{
	public boolean canView()
	{
		return isLoggedIn() && user.getGroup().hasPermission("server.whitelist.view");
	}
	
	public boolean index()
	{
		request.setAttribute("whitelistapps", arrayToString(getWhitelistAppsJson(true)));
		
		return renderView();
	}
	
	@SuppressWarnings("unchecked")
	private JSONArray getWhitelistAppsJson(boolean raw)
	{
		JSONArray s = new JSONArray();
		
		List<Application> as = db.find(Application.class).findList();
		
		String b = raw ? "<td>" : "";
		String e = raw ? "</td>" : "";
		
		for (Application a : as)
		{
			User user = db.find(User.class, a.getUserId());
			
			if (user != null)
			{
				JSONArray ar = new JSONArray();
				
				if (raw) ar.add("<tr>");
				ar.add(b + a.getId() + e);
				ar.add(b + user.getUsername() + e);
				ar.add(b + a.getDescription() + e);
				ar.add(b + new SimpleDateFormat("M-d-y 'at' h:mm a").format(new Date(a.getDate())) + e);
				if (raw) ar.add("</tr>");
				
				s.add(ar);
			} else
				db.delete(a);
		}
		
		return s;
	}
	
	@SuppressWarnings("unchecked")
	public boolean process() throws IOException
	{
		if (isMethod("POST"))
		{
			includeIndex(false);
			mimeType("application/json");
			
			JSONObject r = new JSONObject();
			
			if (isLoggedIn() && user.getGroup().hasPermission("server.whitelist.edit"))
			{
				if (arguments.size() == 2)
				{
					Application a = db.find(Application.class, arguments.get(1));
					
					if (a != null)
					{
						User u = db.find(User.class, a.getUserId());
						//Player p = Bukkit.getPlayer(u.getUsername()); //Change to UUID
						
						db.delete(a);
						
						if (arguments.get(0).equalsIgnoreCase("accept"))
						{
							u.setWhitelisted(true);
							u.setGroupId(db.find(Group.class).where().eq("is_existing_default", true).findUnique().getId());
							
							db.save(u);
							
							//Change to UUID
							bukkitServer.getPluginConnector().sendMethod("doPlayerAppNotice", u.getUsername(), "accept");
							
							r.put("good", "Successfully accepted " + u.getUsername() + "'s application");
						} else if (arguments.get(0).equalsIgnoreCase("deny"))
						{
							u.setBlacklisted(true);
							
							db.save(u);
							
							//Change to UUID
							bukkitServer.getPluginConnector().sendMethod("doPlayerAppNotice", u.getUsername(), "deny");
							
							r.put("good", "Successfully denied " + u.getUsername() + "'s application");
						}
						
						r.put("whitelistapps", getWhitelistAppsJson(false));
					} else
						r.put("error", "Please select an application.");
				} else
					r.put("error", "Please select an application.");
			} else
				r.put("error", "You do not have permission to do that.");
			
			response.getWriter().print(r.toJSONString());
			
			return true;
		}
		
		return error();
	}
}