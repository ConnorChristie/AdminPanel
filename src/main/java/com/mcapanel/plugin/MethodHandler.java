package com.mcapanel.plugin;

import org.json.simple.JSONObject;

import com.mcapanel.panel.AdminPanelWrapper;
import com.mcapanel.web.database.Group;
import com.mcapanel.web.database.PasswordReset;
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
	
	public String getInitial()
	{
		return ap.initialEvent.getData();
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
	
	public void passwordCode(String name, String code)
	{
		PasswordReset pwr = new PasswordReset(name, code);
		ap.getDatabase().save(pwr);
	}
}