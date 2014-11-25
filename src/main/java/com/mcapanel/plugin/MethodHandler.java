package com.mcapanel.plugin;

import java.util.List;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

import com.mcapanel.panel.AdminPanelWrapper;
import com.mcapanel.web.database.Application;
import com.mcapanel.web.database.Group;
import com.mcapanel.web.database.User;

public class MethodHandler
{
	private AdminPanelWrapper ap;
	
	public MethodHandler()
	{
		this.ap = AdminPanelWrapper.getInstance();
	}
	
	@SuppressWarnings("unchecked")
	public String getUser(String uuid)
	{
		JSONObject out = new JSONObject();
		
		User u = ap.getDatabase().find(User.class).where().ieq("uuid", uuid).findUnique();
		
		if (u != null)
		{
			Group group = ap.getDatabase().find(Group.class, u.getGroupId());
			
			if (group != null)
			{
				JSONObject gro = new JSONObject();
				
				gro.put("ghost", group.isGhost());
				gro.put("permissions", group.getPermissions());
				
				out.put("group", gro);
			}
			
			out.put("whitelisted", u.isWhitelisted());
			out.put("blacklisted", u.isBlacklisted());
		}
		
		out.put("exists", u != null);
		
		return out.toJSONString();
	}
	
	@SuppressWarnings("unchecked")
	public String getInitial()
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
		
		objOut.put("appCount", ap.getDatabase().find(Application.class).findIds().size());
		objOut.put("hasTinyUrl", !ap.getTinyUrl().getHelper().c());
		objOut.put("tinyUrl", ap.getTinyUrl().shortUrl());
		objOut.put("users", out);
		
		return objOut.toJSONString();
	}
	
	//Change to uuid
	public void setUserIp(String name, String ip)
	{
		User u = ap.getDatabase().find(User.class).where().ieq("username", name).findUnique();
		
		if (u != null)
		{
			u.setIpAddress(ip);
			
			ap.getDatabase().save(u);
		}
	}
}