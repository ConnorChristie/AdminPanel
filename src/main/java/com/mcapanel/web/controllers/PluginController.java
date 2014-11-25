package com.mcapanel.web.controllers;

import java.io.IOException;

import org.json.simple.JSONObject;
import org.json.simple.parser.ParseException;

import com.mcapanel.web.handlers.Controller;

public class PluginController extends Controller
{
	public boolean canView()
	{
		return isLoggedIn() && user.getGroup().hasPermission("plugins");
	}
	
	public boolean files() throws IOException
	{
		if (bukkitServer.getPluginConnector().connected() && arguments.size() == 1)
		{
			if (canView())
			{
				request.setAttribute("files", bukkitServer.getPluginConnector().sendMethodResponse("getPluginFiles", arguments.get(0)));
				
				return renderView();
			}
		}
		
		return error();
	}
	
	@SuppressWarnings("unchecked")
	public boolean getContent() throws IOException
	{
		if (isMethod("POST"))
		{
			includeIndex(false);
			mimeType("application/json");
			
			JSONObject out = new JSONObject();
			
			if (canView() && arguments.size() == 1 && request.getParameter("file") != null)
			{
				if (bukkitServer.getPluginConnector().connected())
				{
					String resp = bukkitServer.getPluginConnector().sendMethodResponse("getPluginContent", arguments.get(0), request.getParameter("file"));
					
					try
					{
						out = (JSONObject) jsonParser.parse(resp);
					} catch (ParseException e) { }
				} else
					out.put("error", "Unable to edit configs while the server is not running.");
			} else
				out.put("error", "You do not have permission to do that.");
			
			response.getWriter().print(out.toJSONString());
			
			return true;
		}
		
		return error();
	}
	
	@SuppressWarnings("unchecked")
	public boolean saveFile() throws IOException
	{
		if (isMethod("POST"))
		{
			includeIndex(false);
			mimeType("application/json");
			
			JSONObject out = new JSONObject();
			
			if (canView() && arguments.size() == 1 && request.getParameter("type") != null && request.getParameter("file") != null && request.getParameter("data") != null)
			{
				if (bukkitServer.getPluginConnector().connected())
				{
					String resp = bukkitServer.getPluginConnector().sendMethodResponse("savePluginContent", arguments.get(0), request.getParameter("type"), request.getParameter("file"), request.getParameter("data").replace(",", "~`~"));
					
					try
					{
						out = (JSONObject) jsonParser.parse(resp);
					} catch (ParseException e) { }
				} else
					out.put("error", "Unable to edit configs while the server is not running.");
			} else
				out.put("error", "You do not have permission to do that.");
			
			response.getWriter().println(out.toJSONString());
			
			return true;
		}
		
		return error();
	}
}