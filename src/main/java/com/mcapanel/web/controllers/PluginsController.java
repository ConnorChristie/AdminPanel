package com.mcapanel.web.controllers;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.Part;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;
import org.eclipse.jetty.util.MultiPartInputStreamParser.MultiPart;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import com.mcapanel.web.handlers.Controller;

public class PluginsController extends Controller
{
	public boolean canView()
	{
		return isLoggedIn() && user.getGroup().hasPermission("plugins");
	}
	
	public boolean index()
	{
		if (bukkitServer.getPluginConnector().connected())
			request.setAttribute("plugins", arrayToString(getPluginsJson(true)));
		
		return renderView();
	}
	
	public boolean upload() throws IOException, ServletException
	{
		if (isMethod("POST") && request.getParameter("uploadfile") != null)
		{
			for (Part p : request.getParts())
			{
				if (p.getName().equalsIgnoreCase("pluginfile"))
				{
					MultiPart partFile = (MultiPart) p;
					
					File file = partFile.getFile();
					
					if (file != null)
					{
						File f = new File(new File(bukkitServer.getServerJar().getParent(), "plugins"), partFile.getContentDispositionFilename());
						
						FileUtils.copyFile(file, f);
					}
				}
			}
			
			bukkitServer.reloadServer();
		}
		
		response.setStatus(HttpServletResponse.SC_NO_CONTENT);
		
		return true;
	}
	
	@SuppressWarnings("unchecked")
	protected JSONArray getPluginsJson(boolean raw)
	{
		JSONArray out = new JSONArray();
		
		String pluginStr = bukkitServer.getPluginConnector().sendMethodResponse("getPluginsJson");
		
		if (pluginStr != null)
		{
			try
			{
				JSONArray s = (JSONArray) new JSONParser().parse(pluginStr);
				
				String b = raw ? "<td>" : "";
				String e = raw ? "</td>" : "";
				
				for (Object a : s)
				{
					JSONObject obj = (JSONObject) a;
					
					JSONArray ar = new JSONArray();
					
					if (raw) ar.add("<tr>");
					ar.add(b + obj.get("name") + e);
					ar.add(b + "<span class=\"label label-" + (((Boolean) obj.get("enabled")) ? "success\">Enabled" : "danger\">Disabled") + "</span>" + e);
					ar.add(b + obj.get("description") + e);
					if (raw) ar.add("</tr>");
					
					out.add(ar);
				}
			} catch (ParseException e) { }
		}
		
		return out;
	}
	
	@SuppressWarnings("unchecked")
	public boolean process() throws IOException
	{
		if (isMethod("POST"))
		{
			includeIndex(false);
			mimeType("application/json");
			
			JSONObject out = new JSONObject();
			
			if (canView())
			{
				String act = arguments.get(0);
				String slug = request.getParameter("slug");
				String link = request.getParameter("link") == null ? "-" : request.getParameter("link");
				String pluginName = request.getParameter("pluginName");
				
				if (arguments.size() == 1)
				{
					if (bukkitServer.getPluginConnector().connected())
					{
						String resp = bukkitServer.getPluginConnector().sendMethodResponse("doPluginEvent", act, pluginName, slug, link == null ? "-" : link);
						
						try
						{
							out = (JSONObject) jsonParser.parse(resp);
						} catch (ParseException e) { }
						
						out.put("plugins", getPluginsJson(false));
					} else
					{
						if (act.equalsIgnoreCase("install"))
						{
							if (bukkitServer.getServerJar() != null && bukkitServer.getServerJar().exists())
							{
								if (link.equals("-"))
									link = "http://api.bukget.org/3/plugins/bukkit/" + slug + "/latest/download";
								
								InputStream in = new URL(link).openStream();
								
								File outFile = new File(bukkitServer.getServerJar().getParentFile() + File.separator + "plugins", pluginName + ".jar");
								outFile.getParentFile().mkdirs();
								
								FileUtils.copyInputStreamToFile(in, outFile);
								IOUtils.closeQuietly(in);
								
								out.put("good", "Successfully installed the plugin!");
							} else
								out.put("error", "Please specify your server jar in the <a href='/settings/'>settings</a> first.");
						} else if (!pluginName.equalsIgnoreCase("McAdminPanelPlugin"))
						{
							if (act.equalsIgnoreCase("enable") || act.equalsIgnoreCase("disable") || act.equalsIgnoreCase("delete"))
							{
								out.put("error", "Unable to " + act + " while the server is not running.");
							}
						}
					}
				}
				
				if (!out.containsKey("error"))
					out.put("error", "Unable to process your request.");
			} else
				out.put("error", "You do not have permission to do that.");
			
			response.getWriter().println(out.toJSONString());
			
			return true;
		}
		
		return error();
	}
}