package com.mcapanel.web.controllers;

import java.io.IOException;
import java.util.List;

import javax.persistence.OptimisticLockException;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.JSONValue;

import com.mcapanel.utils.Utils;
import com.mcapanel.web.database.Group;
import com.mcapanel.web.database.User;
import com.mcapanel.web.handlers.Controller;

public class UsersController extends Controller
{
	public boolean canView()
	{
		return isLoggedIn() && user.getGroup().hasPermission("web.users.view");
	}
	
	public boolean index()
	{
		if (isLoggedIn())
		{
			request.setAttribute("userGroups", getUserGroups());
			
			request.setAttribute("users", arrayToString(getUsersJson(true)));
		}
		return renderView();
	}
	
	@SuppressWarnings("unchecked")
	public boolean saveUsers() throws IOException
	{
		if (isMethod("POST"))
		{
			includeIndex(false);
			mimeType("application/json");
			
			JSONObject obj = new JSONObject();
			
			if (isLoggedIn() && (user.getGroup().hasPermission("web.users.group") || user.getGroup().hasPermission("web.users.whiteblack")))
			{
				String data = request.getParameter("data");
				
				if (data != null)
				{
					JSONArray users = (JSONArray) JSONValue.parse(data);
					
					for (Object o : users)
					{
						JSONObject us = (JSONObject) o;
						
						User u = db.find(User.class, Integer.parseInt(us.get("id").toString()));
						
						if (u != null)
						{
							if (user.getGroup().hasPermission("web.users.group"))
							{
								Group g = db.find(Group.class).where().ieq("group_name", (String) us.get("group")).findUnique();
								
								if (g != null)
								{
									u.setGroupId(g.getId());
								} else
									obj.put("error", "Invalid group");
							}
							
							if (user.getGroup().hasPermission("web.users.whiteblack"))
							{
								boolean whited = Boolean.parseBoolean((String) us.get("whitelisted"));
								boolean blacked = Boolean.parseBoolean((String) us.get("blacklisted"));
								
								u.setWhitelisted(whited);
								u.setBlacklisted(blacked);
							}
							
							try
							{
								db.save(u);
							} catch (OptimisticLockException e) { }
						}
					}
					
					obj.put("good", "Successfully saved all user settings.");
				} else
					obj.put("error", "Error parsing your request.");
			} else
				obj.put("error", "You do not have permission to do that.");
			
			response.getWriter().println(obj.toJSONString());
			
			return true;
		}
		
		return error();
	}
	
	@SuppressWarnings("unchecked")
	public boolean delete() throws IOException
	{
		if (isMethod("POST"))
		{
			includeIndex(false);
			mimeType("application/json");
			
			JSONObject obj = new JSONObject();
			
			if (isLoggedIn() && user.getGroup().hasPermission("web.users.delete") && arguments.size() == 1)
			{
				User us = db.find(User.class, arguments.get(0));
				
				db.delete(us);
			} else
				obj.put("error", "You do not have permission to do that.");
			
			response.getWriter().println(obj.toJSONString());
			
			return true;
		}
		
		return error();
	}
	
	@SuppressWarnings("unchecked")
	public boolean changePassword() throws IOException
	{
		if (isMethod("POST"))
		{
			includeIndex(false);
			mimeType("application/json");
			
			JSONObject obj = new JSONObject();
			
			if (isLoggedIn() && user.getGroup().hasPermission("web.users.changePassword") && arguments.size() == 1)
			{
				String pass = request.getParameter("password");
				String cpass = request.getParameter("confpassword");
				
				if (pass.equals(cpass))
				{
					User us = db.find(User.class, arguments.get(0));
					
					us.setPassHash(Utils.md5(pass + Utils.md5(us.getPassSalt())));
					
					db.save(us);
					
					obj.put("good", "Successfully changed " + us.getUsername() + "'s password!");
				} else
					obj.put("error", "The passwords you entered do not match.");
			} else
				obj.put("error", "You do not have permission to do that.");
			
			response.getWriter().println(obj.toJSONString());
			
			return true;
		}
		
		return error();
	}
	
	@SuppressWarnings("unchecked")
	private JSONArray getUsersJson(boolean raw)
	{
		JSONArray s = new JSONArray();
		
		List<User> users = db.find(User.class).findList();
		
		String b = raw ? "<td>" : "";
		String e = raw ? "</td>" : "";
		
		for (User u : users)
		{
			JSONArray ar = new JSONArray();
			
			if (raw) ar.add("<tr style='cursor: default;'>");
			ar.add(b + u.getId() + e);
			ar.add(b + u.getUsername() + e);
			ar.add(b + "<span class=\"gname\" style=\"cursor: pointer;\">" + u.getGroup().getGroupName() + "</span>" + e);
			ar.add(b + "<span uid=\"u" + u.getId() + "\" class=\"label clickLabel u" + u.getId() + " label-" + (u.isWhitelisted() ? "success\">true" : "danger\">false") + "</span>" + e);
			ar.add(b + "<span uid=\"u" + u.getId() + "\" class=\"label clickLabel u" + u.getId() + " label-" + (u.isBlacklisted() ? "success\">true" : "danger\">false") + "</span>" + e);
			ar.add(b + ((user.getGroup().hasPermission("web.users.changePassword") || user.getGroup().hasPermission("web.users.delete")) ? (user.getGroup().hasPermission("web.users.changePassword") ? ("<button type=\"button\" userid=\"" + u.getId() + "\" class=\"changePassword btn btn-xs btn-info\">Change Password</button>") : "") + (user.getGroup().hasPermission("web.users.delete") ? ("<button type=\"button\" userid=\"" + u.getId() + "\" class=\"deleteUser btn btn-xs btn-danger\" style=\"margin-left: 10px;\">Delete User</button>") : "") : "-") + e);
			if (raw) ar.add("</tr>");
			
			s.add(ar);
		}
		
		return s;
	}
	
	private String getUserGroups()
	{
		String s = "";
		
		for (Group g : db.find(Group.class).findList())
		{
			s += "<option value='" + g.getGroupName() + "'>" + g.getGroupName() + "</option>";
		}
		
		return s;
	}
}