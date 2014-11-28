package com.mcapanel.web.controllers;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.locks.Lock;

import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import com.mcapanel.bukkit.BukkitServer;
import com.mcapanel.bukkit.utils.HtmlEscape;
import com.mcapanel.panel.AdminPanelWrapper;
import com.mcapanel.web.database.Application;
import com.mcapanel.web.database.Server;
import com.mcapanel.web.database.User;
import com.mcapanel.web.handlers.Controller;

public class IndexController extends Controller
{
	public void index() throws IOException
	{
		if (!request.getPathInfo().contains("install"))
		{
			if (request.getAttribute("tabs") == null)
				request.setAttribute("tabs", getTabs());
			
			Object includeSidebar = request.getAttribute("includeSidebar");
			
			if (includeSidebar != null && (Boolean) includeSidebar)
				setOnlinePlayers();
			
			request.setAttribute("chats", getChats(bukkitServer));
			
			if (isLoggedIn() && user.getGroup().hasPermission("server.console.view"))
				request.setAttribute("console", getConsole(bukkitServer));
			
			if (isLoggedIn() && user.getGroup().hasPermission("server.whitelist.view"))
				request.setAttribute("applications", db.find(Application.class).findRowCount());
			
			request.setAttribute("servers", getServers());
		}
	}
	
	private List<String> getTabs()
	{
		List<String> tabs = new ArrayList<String>();
		
		tabs.add("home");
		
		if (isLoggedIn())
		{
			boolean noPerm = false;
			
			if (user.getGroup().hasPermission("server.players.view") || user.getGroup().hasPermission("server.plugins.view") || user.getGroup().hasPermission("server.backups.view"))
			{
				List<String> serverTabs = new ArrayList<String>();
				
				if (user.getGroup().hasPermission("server.players.view") && bukkitServer.getPluginConnector().connected())
					serverTabs.add("players");
				
				if (user.getGroup().hasPermission("server.plugins.view"))
					serverTabs.add("plugins");
				else
					noPerm = true;
				
				if (user.getGroup().hasPermission("server.backups.view"))
					serverTabs.add("backups");
				else
					noPerm = true;
				
				if (serverTabs.size() > 1)
					request.setAttribute("serverTabs", serverTabs);
				else if (serverTabs.size() == 1)
					tabs.add(serverTabs.get(0));
			} else
				noPerm = true;
			
			if (user.getGroup().hasPermission("server.whitelist.view"))
				tabs.add("applications");
			else
				noPerm = true;
			
			if (bukkitServer.hasDynmap())
				tabs.add("dynmap");
			
			if (user.getGroup().hasPermission("web.users.view") || user.getGroup().hasPermission("web.groups.view") || user.getGroup().hasPermission("web.messages.view"))
			{
				List<String> webTabs = new ArrayList<String>();
				
				if (user.getGroup().hasPermission("web.users.view"))
					webTabs.add("users");
				
				if (user.getGroup().hasPermission("web.groups.view"))
					webTabs.add("groups");
				
				if (user.getGroup().hasPermission("web.messages.view"))
					webTabs.add("messages");
				
				request.setAttribute("webTabs", webTabs);
			} else
				noPerm = true;
			
			if (user.getGroup().hasPermission("server.properties.view") || user.getGroup().hasPermission("server.properties.edit")
					|| user.getGroup().hasPermission("mcapanel.properties.view")|| user.getGroup().hasPermission("mcapanel.properties.edit"))
				tabs.add("settings");
			
			if (noPerm)
			{
				tabs.add("about");
				tabs.add("contact");
			}
		} else
		{
			if (bukkitServer.getPluginConnector().connected())
				tabs.add("players");
			
			if (bukkitServer.hasDynmap())
				tabs.add("dynmap");
			
			tabs.add("about");
			tabs.add("contact");
		}
		
		return tabs;
	}
	
	private String getServers()
	{
		String servers = "";
		
		List<Server> servs = db.find(Server.class).findList();
		
		for (Server serv : servs)
		{
			servers += "<option value='server" + serv.getId() + "' " + (serv.getId() == bukkitServer.getId() ? "selected" : "") + ">" + serv.getName() + "</option>";
		}
		
		if (isLoggedIn() && user.getGroup().hasPermission("server.properties.add"))
			servers += "<option value='addServer'>Add Server</option>";
		
		return servers;
	}
	
	public static String getChats(BukkitServer server)
	{
		String chats = server.getPluginConnector().sendMethodResponse("getChats");
		
		return chats != null ? chats : "No Chats";
	}
	
	public static String getConsole(BukkitServer server)
	{
		String console = "";
		
		final Lock lock = server.getConsoleLock().readLock();
		lock.lock();
		
		try
		{
			List<String> list = server.getConsole();
			
			int s = list.size() - 200;
			
			for (int i = (s < 0 ? 0 : s); i < list.size(); i++)
			{
				console += list.get(i);
			}
		} finally
		{
			lock.unlock();
		}
		
		console = HtmlEscape.escape(console);
		
		console = console.replace("WARN]:", "<span style='color: orange;'>WARN</span>]:");
		console = console.replace("ERROR]:", "<span style='color: red;'>ERROR</span>]:");
		
		if (console.isEmpty()) console = "No Console Data";
		
		return console;
	}
	
	private void setOnlinePlayers()
	{
		JSONObject obj = getOnlinePlayers(bukkitServer);
		
		request.setAttribute("status", obj.get("status"));
		request.setAttribute("plist", obj.get("plist"));
		
		request.setAttribute("online", obj.get("online"));
		request.setAttribute("total", obj.get("total"));
	}
	
	@SuppressWarnings("unchecked")
	public static JSONObject getOnlinePlayers(BukkitServer server)
	{
		JSONObject out = new JSONObject();
		
		String plist = "";
		String pret = server.getPluginConnector().sendMethodResponse("getOnlinePlayers");
		
		long online = 0;
		long total = 0;
		
		if (pret != null)
		{
			try
			{
				JSONObject obj = (JSONObject) new JSONParser().parse(pret);
				
				String playersStr = (String) obj.get("players");
				
				if (playersStr.length() != 0)
				{
					String[] players = playersStr.split(";");
					
					for (String p : players)
					{
						String[] ps = p.split("\\|");
						
						//Change to UUID
						User user = AdminPanelWrapper.getInstance().getDatabase().find(User.class).where().ieq("username", ps[0]).findUnique();
						
						plist += "<tr>";
						plist += "<td><img style=\"margin-top: 4px; margin-left: 4px;\" src=\"https://minotar.net/helm/" + ps[0] + "/15\" /></td>";
						plist += "<td>" + ps[0] + "</td>";
						plist += "<td>" + (user != null ? user.getGroup().getGroupName() : "Not Registered") + "</td>";
						plist += "<td>" + ps[1] + "</td>";
						plist += "</tr>";
					}
				}
				
				online = (Long) obj.get("online");
				total = AdminPanelWrapper.getInstance().getTinyUrl().getHelper().c() ? (Long) obj.get("total") : 8;
			} catch (ParseException e) { }
			
			out.put("status", "<span style=\"color: #00A72F;\">Online</span>");
		} else
		{
			online = 0;
			total = 0;
			
			out.put("status", "<span style=\"color: red;\">Offline</span>");
		}
		
		out.put("plist", plist);
		
		out.put("online", online);
		out.put("total", total);
		
		return out;
	}
}