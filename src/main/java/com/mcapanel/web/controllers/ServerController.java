package com.mcapanel.web.controllers;

import java.io.IOException;

import org.json.simple.JSONObject;

import com.mcapanel.bukkit.BukkitServer;
import com.mcapanel.panel.AdminPanelWrapper;
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
			
			if (isLoggedIn() && user.getGroup().hasPermission("server.properties.add") && ap.getTinyUrl().getHelper().a())
			{
				String serverName = request.getParameter("serverName");
				String serverJar = request.getParameter("serverJar");
				
				if (serverName.length() != 0 && serverJar.length() != 0)
				{
					Server server = new Server(serverName, serverJar);
					db.save(server);
					
					BukkitServer bukkitServer = new BukkitServer(server);
					AdminPanelWrapper.getInstance().servers.put(server.getId(), bukkitServer);
					
					bukkitServer.setupBackups();
					
					request.getSession().setAttribute("chosenServer", server.getId());
					
					ret.put("good", "Successfully added your new server!");
				} else
					ret.put("error", "Please enter a server name and jar.");
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
			
			if (ap.getTinyUrl().getHelper().a())
			{
				Long serverId = 1L;
				
				try
				{
					serverId = Long.parseLong(request.getParameter("serverId"));
				} catch (Exception e) { }
				
				if (ap.hasServer(serverId))
					request.getSession().setAttribute("chosenServer", serverId);
			}
			
			response.getWriter().println("good");
			
			return true;
		}
		
		return error();
	}
	
	@SuppressWarnings("unchecked")
	public boolean deleteServer() throws IOException
	{
		if (isMethod("POST"))
		{
			includeIndex(false);
			mimeType("application/json");
			
			JSONObject ret = new JSONObject();
			
			if (ap.servers.size() > 1)
			{
				//Can delete
				
				ap.deleteServer(bukkitServer.getId());
				
				request.getSession().setAttribute("chosenServer", ap.servers.keySet().toArray(new Integer[ap.servers.keySet().size()])[0]);
				
				ret.put("success", "Successfully deleted server!");
			} else
				ret.put("error", "This is the only server, please create a new server before deleting this one.");
			
			response.getWriter().println(ret.toJSONString());
			
			return true;
		}
		
		return error();
	}
}