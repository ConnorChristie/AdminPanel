package com.mcapanel.web.controllers;

import java.io.IOException;
import java.io.OutputStream;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.JSONValue;

import com.mcapanel.panel.AdminPanelWrapper;
import com.mcapanel.web.database.Group;
import com.mcapanel.web.handlers.Controller;

@SuppressWarnings("unchecked")
public class EventController extends Controller
{
	public boolean getEverything() throws IOException
	{
		includeIndex(false);
		mimeType("application/json");
		
		JSONObject out = new JSONObject();
		JSONObject data = ap.everythingEvent.getData(bukkitServer.getId());
		
		boolean isHome = false;
		
		if (arguments.size() > 0)
			isHome = arguments.get(0).equalsIgnoreCase("home");
		else
			isHome = true;
		
		out.put("time", System.currentTimeMillis());
		
		if (isLoggedIn())
		{
			if (isHome && user.getGroup().hasPermission("server.usage"))
				out.put("usage", AdminPanelWrapper.getInstance().getUsages().getUsageJson());
			
			if (isHome && (user.getGroup().hasPermission("server.controls") || user.getGroup().hasPermission("server.reload")))
				out.put("control", data.get("control"));
			
			if (user.getGroup().hasPermission("server.console.view"))
				out.put("console", data.get("console"));
			
			if (user.getGroup().hasPermission("server.whitelist.view"))
				out.put("applications", data.get("applications"));
		}
		
		if (isHome)
			out.put("playersObj", data.get("playersObj"));
		
		out.put("chats", data.get("chats"));
		
		response.getWriter().println(out.toJSONString());
		
		return true;
	}
	
	public boolean issueChat() throws IOException
	{
		if (isMethod("POST"))
		{
			includeIndex(false);
			mimeType("application/json");
			
			JSONObject out = new JSONObject();
			
			if (isLoggedIn() && user.getGroup().hasPermission("server.chat.issue"))
			{
				String chats = bukkitServer.getPluginConnector().sendMethodResponse("issueChat", user.getUsername(), request.getParameter("chatmsg"));
				
				ap.everythingEvent.fetchData();
				
				out.put("chats", chats != null ? chats : "No Chats");
				
				out.put("good", "good");
			} else
				out.put("error", "You do not have permission to do that.");
			
			response.getWriter().println(out.toJSONString());
			
			return true;
		}
		
		return error();
	}
	
	public boolean issueCommand() throws IOException
	{
		if (isMethod("POST"))
		{
			includeIndex(false);
			mimeType("application/json");
			
			JSONObject obj = new JSONObject();
			
			if (isLoggedIn() && user.getGroup().hasPermission("server.console.issue"))
			{
				OutputStream writer = bukkitServer.getWriter();
				
				if (!ap.parseCommand(request.getParameter("command")))
				{
					try
					{
						writer.write((request.getParameter("command") + "\n").getBytes());
						writer.flush();
					} catch (IOException e) { }
				}
				
				try
				{
					Thread.sleep(100);
				} catch (InterruptedException e) { }
				
				ap.everythingEvent.fetchData();
				
				obj.put("good", "good");
				obj.put("console", IndexController.getConsole(bukkitServer));
			} else
				obj.put("error", "You do not have permission to do that.");
			
			response.getWriter().println(obj.toJSONString());
			
			return true;
		}
		
		return error();
	}
	
	public boolean saveGroups() throws IOException
	{
		if (isMethod("POST"))
		{
			includeIndex(false);
			mimeType("application/json");
			
			JSONObject obj = new JSONObject();
			
			if (isLoggedIn() && user.getGroup().hasPermission("web.groups.edit"))
			{
				String data = request.getParameter("data");
				
				if (data != null)
				{
					JSONArray groups = (JSONArray) JSONValue.parse(data);
					
					for (Object g : groups)
					{
						JSONObject group = (JSONObject) g;
						
						int id = Integer.parseInt(group.get("id").toString());
						String name = group.get("name").toString();
						boolean ghost = (Boolean) group.get("ghost");
						boolean existing = (Boolean) group.get("existing");
						boolean whitelist = (Boolean) group.get("whitelist");
						
						Group gr = db.find(Group.class, id);
						
						if (gr != null)
						{
							gr.setGroupName(name);
							gr.setGhost(ghost);
							gr.setExistingDefault(existing);
							gr.setWhitelistDefault(whitelist);
							
							db.save(gr);
						}
					}
					
					obj.put("good", "Successfully saved all group settings.");
				} else
					obj.put("error", "Error parsing your request.");
			} else
				obj.put("error", "You do not have permission to do that.");
			
			response.getWriter().println(obj.toJSONString());
			
			return true;
		}
		
		return error();
	}
	
	public boolean system() throws IOException
	{
		if (isMethod("POST"))
		{
			includeIndex(false);
			mimeType("application/json");
			
			JSONObject out = new JSONObject();
			
			if (isLoggedIn() && user.getGroup().hasPermission("server.controls"))
			{
				if (arguments.size() == 1)
				{
					if (arguments.get(0).equalsIgnoreCase("startServer"))
					{
						bukkitServer.startServer();
						
						out.put("good", "Starting the server.");
					} else if (arguments.get(0).equalsIgnoreCase("stopServer"))
					{
						bukkitServer.stopServer(false);
						
						out.put("good", "Stopping the server.");
					} else if (arguments.get(0).equalsIgnoreCase("restartServer"))
					{
						bukkitServer.restartServer();
						
						out.put("good", "Restarting the server.");
					} else if (arguments.get(0).equalsIgnoreCase("reloadServer"))
					{
						bukkitServer.reloadServer();
						
						out.put("good", "Reloading the server.");
					}
				}
				
				out.put("control", HomeController.getControlsJson(bukkitServer));
				
				out.put("error", "The command you are trying to issue doesn't exist.");
			} else if (isLoggedIn() && user.getGroup().hasPermission("server.reload"))
			{
				if (arguments.size() == 1)
				{
					if (arguments.get(0).equalsIgnoreCase("reloadServer"))
					{
						bukkitServer.reloadServer();
						
						out.put("good", "Reloading the server.");
					}
				}
				
				out.put("control", HomeController.getControlsJson(bukkitServer));
				
				out.put("error", "The command you are trying to issue doesn't exist.");
			} else
				out.put("error", "You do not have permission to do that.");
			
			response.getWriter().println(out.toJSONString());
			
			return true;
		}
		
		return error();
	}
}