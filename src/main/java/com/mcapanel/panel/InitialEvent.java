package com.mcapanel.panel;

import java.util.List;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

import com.mcapanel.web.database.Application;
import com.mcapanel.web.database.Group;
import com.mcapanel.web.database.User;

public class InitialEvent extends Thread
{
	private AdminPanelWrapper ap = AdminPanelWrapper.getInstance();
	
	private String data = "";
	
	private boolean isRunning = false;
	
	public void run()
	{
		isRunning = true;
		
		while (isRunning)
		{
			AdminPanelWrapper.executeMain(new Runnable() {
				public void run()
				{
					fetchData();
				}
			});
			
			try
			{
				sleep(5000);
			} catch (InterruptedException e) { }
		}
		
		try
		{
			join();
		} catch (InterruptedException e) { }
	}
	
	@SuppressWarnings("unchecked")
	public void fetchData()
	{
		JSONObject objOut = new JSONObject();
		JSONArray out = new JSONArray();
		
		List<User> us = ap.getDatabase().find(User.class).findList();
		
		for (User u : us)
		{
			JSONObject obj = new JSONObject();
			
			Group group = ap.getDatabase().find(Group.class, u.getGroupId());
			
			if (group != null)
			{
				JSONObject gro = new JSONObject();
				
				gro.put("name", group.getGroupName());
				gro.put("ghost", group.isGhost());
				gro.put("permissions", group.getPermissions());
				
				obj.put("group", gro);
			}
			
			obj.put("uuid", u.getUuid());
			obj.put("username", u.getUsername());
			obj.put("ipAddress", u.getIpAddress());
			
			obj.put("whitelisted", u.isWhitelisted());
			obj.put("blacklisted", u.isBlacklisted());
			
			out.add(obj);
		}
		
		objOut.put("whitelist", ap.getConfig().getBoolean("enable-whitelist", true));
		objOut.put("appCount", ap.getDatabase().find(Application.class).findIds().size());
		objOut.put("hasTinyUrl", !ap.getTinyUrl().getHelper().c());
		objOut.put("tinyUrl", ap.getTinyUrl().shortUrl());
		objOut.put("users", out);
		
		data = objOut.toJSONString();
	}
	
	public String getData()
	{
		return data;
	}
}