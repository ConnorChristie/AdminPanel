package com.mcapanel.web.handlers;

import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.json.simple.JSONArray;
import org.json.simple.parser.JSONParser;

import com.avaje.ebean.EbeanServer;
import com.mcapanel.bukkit.BukkitServer;
import com.mcapanel.config.Config;
import com.mcapanel.panel.AdminPanelWrapper;
import com.mcapanel.utils.Utils;
import com.mcapanel.web.database.User;

public abstract class Controller
{
	public List<String> arguments;
	
	public AdminPanelWrapper ap;
	public JSONParser jsonParser;
	
	public HttpServletRequest request;
	public HttpServletResponse response;
	
	public User user;
	public Config config;
	public EbeanServer db;
	public BukkitServer bukkitServer;
	
	public boolean includeIndex = true;
	
	public Controller()
	{
		jsonParser = new JSONParser();
	}
	
	public boolean canView()
	{
		return true;
	}
	
	public boolean isLoggedIn()
	{
		return user != null;
	}
	
	public boolean isMethod(String type)
	{
		return request.getMethod().equals(type);
	}
	
	public void includeIndex(boolean includeIndex)
	{
		this.includeIndex = includeIndex;
	}
	
	public void mimeType(String mime)
	{
		response.setContentType(mime);
	}
	
	public boolean error()
	{
		return renderView("404");
	}
	
	public boolean renderView()
	{
		mimeType("text/html");
		
		return renderView(getClass().getSimpleName().toLowerCase().replace("controller", ""));
	}
	
	public boolean renderView(String view)
	{
		request.setAttribute("page", view);
		
		return true;
	}
	
	public void includeSidebar()
	{
		request.setAttribute("includeSidebar", true);
	}
	
	public String arrayToString(JSONArray a)
	{
		String ret = "";
		
		for (Object oo : a)
		{
			for (Object o : (JSONArray) oo)
			{
				String s = (String) o;
				
				ret += s;
			}
		}
		
		return ret;
	}
	
	public void loginUser(User u)
	{
		request.getSession().setAttribute("userId", u.getId());
		request.getSession().setAttribute("userAddr", u.getIpAddress());
		request.getSession().setAttribute("userHash", Utils.md5(u.getId() + u.getPassSalt()));
	}
}