package com.mcapanel.web.controllers;

import java.io.IOException;

import org.json.simple.JSONObject;

import com.mcapanel.web.database.Server;
import com.mcapanel.web.handlers.Controller;

public class ServerController extends Controller
{
	public boolean canView()
	{
		return true;
	}
	
	public boolean index()
	{
		return error();
	}
	
	@SuppressWarnings("unchecked")
	public boolean addServer() throws IOException
	{
		if (isMethod("POST"))
		{
			includeIndex(false);
			mimeType("application/json");
			
			JSONObject ret = new JSONObject();
			
			if (isLoggedIn() && user.getGroup().hasPermission("server.properties.add"))
			{
				String serverName = request.getParameter("serverName");
				String serverJar = request.getParameter("serverJar");
				
				Server server = new Server(serverName, serverJar);
				db.save(server);
				
				ret.put("good", "Successfully added your new server!");
			} else
				ret.put("error", "You do not have permission to do that.");
			
			response.getWriter().println(ret.toJSONString());
			
			return true;
		}
		
		return error();
	}
	
	public boolean selectServer() throws IOException
	{
		if (isMethod("POST"))
		{
			includeIndex(false);
			
			int serverId = 1;
			
			try
			{
				serverId = Integer.parseInt(request.getParameter("serverId"));
			} catch (Exception e) { }
			
			if (ap.hasServer(serverId))
				request.getSession().setAttribute("chosenServer", serverId);
			
			response.getWriter().println("good");
			
			return true;
		}
		
		return error();
	}
}