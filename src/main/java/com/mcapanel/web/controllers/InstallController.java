package com.mcapanel.web.controllers;

import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.RandomStringUtils;
import org.json.simple.JSONObject;

import com.mcapanel.bukkit.BukkitVersion;
import com.mcapanel.web.database.Group;
import com.mcapanel.web.database.User;
import com.mcapanel.web.handlers.Controller;

public class InstallController extends Controller
{
	public boolean canView()
	{
		return !ap.getConfig().getBoolean("installed", false);
	}
	
	public boolean index()
	{
		request.setAttribute("tabs", getTabs());
		request.setAttribute("install", true);
		
		return renderView();
	}
	
	private List<String> getTabs()
	{
		List<String> tabs = new ArrayList<String>();
		
		tabs.add("install");
		
		return tabs;
	}
	
	@SuppressWarnings("unchecked")
	public boolean process() throws IOException
	{
		if (isMethod("POST"))
		{
			includeIndex(false);
			mimeType("application/json");
			
			JSONObject out = new JSONObject();
			
			if (!config.getBoolean("installed", false))
			{
				String serverIp = request.getParameter("serverip");
				String webPort = request.getParameter("webport");
				String cbFile = request.getParameter("cbfile");
				String cbInstall = request.getParameter("cbinstall");
				
				String mcname = request.getParameter("mcname");
				String mcpass = request.getParameter("mcpass");
				String mcpassconf = request.getParameter("mcpassconf");
				
				String licemail = request.getParameter("licemail");
				String lickey = request.getParameter("lickey");
				
				if (mcpass.equals(mcpassconf))
				{
					User u = new User(mcname, mcpass, RandomStringUtils.randomAlphanumeric(8), request.getRemoteAddr());
					
					u.setGroupId(db.find(Group.class).where().ieq("group_name", "Admin").findUnique().getId());
					u.setWhitelisted(true);
					
					db.save(u);
					
					loginUser(u);
					
					config.setValue("installed", "true");
					config.setValue("server-jar", cbFile);
					
					config.setValue("server-ip", serverIp);
					config.setValue("web-port", webPort);
					
					config.setValue("license-email", licemail);
					config.setValue("license-key", lickey);
					
					config.saveConfig();
					
					//ap.install();
					
					final BukkitVersion bv = BukkitVersion.getVersion(cbInstall);
					
					if (bv != null)
					{
						new Thread(new Runnable() {
							public void run()
							{
								try
								{
									System.out.println("Downloading CraftBukkit...");
									
									File cbFile = new File("craftbukkit.jar");
									
									FileUtils.copyURLToFile(new URL(bv.getUrl()), cbFile);
									
									config.setValue("server-jar", cbFile.getAbsolutePath());
									config.saveConfig();
									
									//ap.install();
									
									System.out.println("Done downloading CraftBukkit!");
								} catch (MalformedURLException e)
								{
									e.printStackTrace();
								} catch (IOException e)
								{
									e.printStackTrace();
								}
							}
						}).start();
					}
					
					out.put("good", "Successfully saved all installation settings.");
				} else
					out.put("error", "The passwords that you entered do not appear to match.");
			} else
				out.put("error", "You are not allowed to do that.");
			
			response.getWriter().println(out.toJSONString());
			
			return true;
		}
		
		return error();
	}
}