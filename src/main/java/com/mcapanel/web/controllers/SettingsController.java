package com.mcapanel.web.controllers;

import java.io.IOException;

import org.json.simple.JSONObject;

import com.mcapanel.config.BukkitConfig;
import com.mcapanel.web.database.Server;
import com.mcapanel.web.handlers.Controller;

public class SettingsController extends Controller
{
	public boolean canView()
	{
		return isLoggedIn() && (user.getGroup().hasPermission("server.properties.view") || user.getGroup().hasPermission("server.properties.edit")
				|| user.getGroup().hasPermission("mcapanel.properties.view")|| user.getGroup().hasPermission("mcapanel.properties.edit"));
	}
	
	public boolean index()
	{
		request.setAttribute("bukkitConfig", bukkitServer.getConfig());
		
		return renderView();
	}
	
	public boolean reload() throws IOException
	{
		if (isLoggedIn() && user.getGroup().hasPermission("server.properties.view"))
		{
			bukkitServer.getConfig().loadConfig();
		}
		
		response.sendRedirect("/settings/");
		
		return true;
	}
	
	@SuppressWarnings("unchecked")
	public boolean updateLicense() throws IOException
	{
		if (isMethod("POST"))
		{
			includeIndex(false);
			mimeType("application/json");
			
			JSONObject obj = new JSONObject();
			
			if (isLoggedIn() && user.getGroup().hasPermission("mcapanel.properties.edit"))
			{
				String licemail = request.getParameter("licemail");
				String lickey = request.getParameter("lickey");
				
				config.setValue("license-email", licemail);
				config.setValue("license-key", lickey);
				
				config.saveConfig();
				
				obj.put("good", "Successfully updated your license!");
			} else
				obj.put("error", "You do not have permission to do that.");
			
			response.getWriter().println(obj.toJSONString());
			
			return true;
		}
		
		return error();
	}
	
	@SuppressWarnings("unchecked")
	public boolean saveServerSettings() throws IOException
	{
		if (isMethod("POST"))
		{
			includeIndex(false);
			mimeType("application/json");
			
			JSONObject obj = new JSONObject();
			
			if (canView() && user.getGroup().hasPermission("server.properties.edit"))
			{
				String serverjar = request.getParameter("serverjar");
				String servername = request.getParameter("servername");
				String javaargs = request.getParameter("javaargs");
				String minmemory = request.getParameter("minmemory");
				String maxmemory = request.getParameter("maxmemory");
				
				bukkitServer.setServerJar(serverjar);
				bukkitServer.setName(servername);
				bukkitServer.setJavaArgs(javaargs);
				bukkitServer.setMinMemory(minmemory);
				bukkitServer.setMaxMemory(maxmemory);
				
				Server serv = db.find(Server.class, bukkitServer.getId());
				
				if (serv != null)
				{
					serv.setServerJar(serverjar);
					serv.setName(servername);
					serv.setJavaArgs(javaargs);
					serv.setMinMemory(minmemory);
					serv.setMaxMemory(maxmemory);
					
					db.save(serv);
				}
				
				obj.put("good", "Saved all server config settings");
			} else
				obj.put("error", "You do not have permission to do that.");
			
			response.getWriter().println(obj.toJSONString());
			
			return true;
		}
		
		return error();
	}
	
	@SuppressWarnings("unchecked")
	public boolean saveServerProperties() throws IOException
	{
		if (isMethod("POST"))
		{
			includeIndex(false);
			mimeType("application/json");
			
			JSONObject obj = new JSONObject();
			
			if (canView() && user.getGroup().hasPermission("server.properties.edit"))
			{
				String seed = request.getParameter("seed");
				String serverport = request.getParameter("serverport");
				String gensettings = request.getParameter("gensettings");
				String servermotd = request.getParameter("servermotd");
				
				String maxplayers = request.getParameter("maxplayers");
				String genstructures = request.getParameter("genstructures");
				String worldtype = request.getParameter("worldtype");
				String difficulty = request.getParameter("difficulty");
				String onlinemode = request.getParameter("onlinemode");
				String enablepvp = request.getParameter("enablepvp");
				String spawnmonsters = request.getParameter("spawnmonsters");
				String spawnanimals = request.getParameter("spawnmonsters");
				String spawnnpc = request.getParameter("spawnnpc");
				String announceachiev = request.getParameter("announceachiev");
				String playertimeout = request.getParameter("playertimeout");
				String maxheight = request.getParameter("maxheight");
				String viewdistance = request.getParameter("viewdistance");
				String spawnprotection = request.getParameter("spawnprotection");
				String snooping = request.getParameter("snooping");
				
				BukkitConfig bukkitConfig = bukkitServer.getConfig();
				
				bukkitConfig.setValue("level-seed", seed);
				bukkitConfig.setValue("generator-settings", gensettings);
				bukkitConfig.setValue("motd", servermotd);
				bukkitConfig.setValue("server-port", serverport);
				
				bukkitConfig.setValue("max-players", maxplayers);
				bukkitConfig.setValue("generate-structures", genstructures);
				bukkitConfig.setValue("level-type", worldtype);
				bukkitConfig.setValue("difficulty", difficulty);
				bukkitConfig.setValue("online-mode", onlinemode);
				bukkitConfig.setValue("pvp", enablepvp);
				bukkitConfig.setValue("spawn-monsters", spawnmonsters);
				bukkitConfig.setValue("spawn-animals", spawnanimals);
				bukkitConfig.setValue("spawn-npcs", spawnnpc);
				bukkitConfig.setValue("announce-player-achievements", announceachiev);
				bukkitConfig.setValue("player-idle-timeout", playertimeout);
				bukkitConfig.setValue("max-build-height", maxheight);
				bukkitConfig.setValue("view-distance", viewdistance);
				bukkitConfig.setValue("spawn-protection", spawnprotection);
				bukkitConfig.setValue("snooper-enabled", snooping);
				
				bukkitConfig.saveConfig();
				
				obj.put("good", "Saved all server config settings");
			} else
				obj.put("error", "You do not have permission to do that.");
			
			response.getWriter().println(obj.toJSONString());
			
			return true;
		}
		
		return error();
	}
	
	@SuppressWarnings("unchecked")
	public boolean saveMcAdminSettings() throws IOException
	{
		if (isMethod("POST"))
		{
			includeIndex(false);
			mimeType("application/json");
			
			JSONObject obj = new JSONObject();
			
			if (isLoggedIn() && user.getGroup().hasPermission("mcapanel.properties.edit"))
			{
				String serverip = request.getParameter("serverip");
				String adminport = request.getParameter("adminport");
				
				String enablewhitelist = request.getParameter("enablewhitelist");
				String errorrestart = request.getParameter("errorrestart");
				String autorestart = request.getParameter("autorestart");
				
				config.setValue("server-ip", serverip);
				config.setValue("web-port", adminport);
				
				config.setValue("enable-whitelist", enablewhitelist);
				config.setValue("restart-on-error", errorrestart);
				config.setValue("auto-restart", autorestart);
				
				config.saveConfig();
				
				obj.put("good", "Saved all config settings");
			} else
				obj.put("error", "You do not have permission to do that.");
			
			response.getWriter().println(obj.toJSONString());
			
			return true;
		}
		
		return error();
	}
	
	public boolean process() throws IOException
	{
		if (isMethod("POST"))
		{
			includeIndex(false);
			mimeType("application/json");
			
			JSONObject obj = new JSONObject();
			
			if (canView())
			{
				
			}
			
			response.getWriter().println(obj.toJSONString());
			
			return true;
		}
		
		return error();
	}
}