package com.mcapanel.web.controllers;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;

import org.apache.commons.io.IOUtils;
import org.json.simple.JSONObject;

import com.mcapanel.bukkit.BukkitServer;
import com.mcapanel.panel.AdminPanelWrapper;
import com.mcapanel.panel.ServerStatus;
import com.mcapanel.web.handlers.Controller;

@SuppressWarnings("unchecked")
public class HomeController extends Controller
{
	public boolean index() throws IOException
	{
		includeSidebar();
		
		if (isLoggedIn())
		{
			if (user.getGroup().hasPermission("server.usage"))
				request.setAttribute("usage", ap.getUsages().getUsageJson());
		}
		
		request.setAttribute("homepage", IOUtils.toString(new FileReader(new File("McAdminPanel", "webpages/homepage.html"))));
		
		return renderView();
	}
	
	public static JSONObject getControlsJson(BukkitServer server)
	{
		JSONObject obj = new JSONObject();
		
		ServerStatus status = server.getStatus();
		
		obj.put("statusTitle", "<span style='color: " + status.getColor() + ";'>" + AdminPanelWrapper.getInstance().getLanguage().localize(status.getName()) + "</span>");
		
		switch (status)
		{
			case STARTING:
			case STOPPING:
			case RESTARTING:
			case RELOADING:
				obj.put("startServer", false);
				obj.put("stopServer", false);
				obj.put("restartServer", false);
				obj.put("reloadServer", false);
				
				break;
			case STARTED:
				obj.put("startServer", false);
				obj.put("stopServer", true);
				obj.put("restartServer", true);
				obj.put("reloadServer", true);
				
				break;
			case STOPPED:
				obj.put("startServer", true);
				obj.put("stopServer", false);
				obj.put("restartServer", false);
				obj.put("reloadServer", false);
				
				break;
		}
		
		return obj;
	}
}