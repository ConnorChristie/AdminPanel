package com.mcapanel.web.controllers;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.JSONValue;

import com.mcapanel.utils.WebPermission;
import com.mcapanel.web.database.Group;
import com.mcapanel.web.handlers.Controller;

public class GroupsController extends Controller
{
	private List<WebPermission> perms = new ArrayList<WebPermission>();
	
	public GroupsController()
	{
		WebPermission editing = null;
		
		perms.add(editing = new WebPermission("View Server Chats", "server.chat.view", "Gives user permission to view the servers messages from players ingame."));
		editing.addPermission(new WebPermission("Issue Chats", "server.chat.issue", "Gives user permission to send messages to players ingame."));
		
		perms.add(editing = new WebPermission("View Server Console", "server.console.view", "Gives user permission to view the server console."));
		editing.addPermission(new WebPermission("Issue Commands", "server.console.issue", "Gives user permission to issue server commands (Ex. help, reload, stop, ...)."));
		
		perms.add(editing = new WebPermission("Access Server Controls", "server.controls", "Gives user permission to control the server (Start, Stop, Restart, Reload)."));
		editing.addPermission(new WebPermission("Reload Only", "server.reload", "Gives user permission to only reload the server."));
		
		perms.add(editing = new WebPermission("View Server Usage", "server.usage", "Gives user permission to view server usages."));
		
		perms.add(editing = new WebPermission("View Server Properties", "server.properties.view", "Gives user permission to view server properties (Motd, Max Players, ...)."));
		editing.addPermission(new WebPermission("Edit Properties", "server.properties.edit", "Gives user permission to edit server properties."));
		editing.addPermission(new WebPermission("Add New Server", "server.properties.add", "Gives user permission to add a new server to McAdminPanel."));
		
		perms.add(editing = new WebPermission("View Whitelist Applications", "server.whitelist.view", "Gives user permission to view whitelist applications."));
		editing.addPermission(new WebPermission("Accept/Deny Applications", "server.whitelist.edit", "Gives user permission to accept/deny whitelist applications."));
		
		perms.add(editing = new WebPermission("View Server Players", "server.players.view", "Gives user permission to view server players."));
		editing.addPermission(new WebPermission("Heal/Feed Players", "server.players.healfeed", "Gives user permission to heal/feed players."));
		editing.addPermission(new WebPermission("Kill Players", "server.players.kill", "Gives user permission to kill players."));
		editing.addPermission(new WebPermission("Kick Players", "server.players.kick", "Gives user permission to kick players."));
		editing.addPermission(new WebPermission("Ban Players", "server.players.ban", "Gives user permission to ban players."));
		
		perms.add(editing = new WebPermission("View Server Plugins", "server.plugins.view", "Gives user permission to view server plugins."));
		editing.addPermission(new WebPermission("Enable/Disable Plugins", "server.plugins.control", "Gives user permission to enable/disable plugins."));
		editing.addPermission(new WebPermission("Edit Plugin Files", "server.plugins.edit", "Gives user permission to edit plugin files."));
		editing.addPermission(new WebPermission("Install Plugins", "server.plugins.install", "Gives user permission to install plugins."));
		editing.addPermission(new WebPermission("Delete Plugins", "server.plugins.delete", "Gives user permission to delete plugins."));
		
		perms.add(editing = new WebPermission("View Server Backups", "server.backups.view", "Gives user permission to view server backups."));
		editing.addPermission(new WebPermission("Schedule Backups", "server.backups.schedule.issue", "Gives user permission to schedule backups."));
		editing.addPermission(new WebPermission("Delete Backup Schedules", "server.backups.schedule.delete", "Gives user permission to delete backup schedules."));
		editing.addPermission(new WebPermission("Restore Backups", "server.backups.restore", "Gives user permission to restore backups."));
		editing.addPermission(new WebPermission("Delete Backups", "server.backups.delete", "Gives user permission to delete backups (Caution)."));
		
		perms.add(editing = new WebPermission("View Web Users", "web.users.view", "Gives user permission to view web users."));
		editing.addPermission(new WebPermission("Change User Group", "web.users.group", "Gives user permission to change users group."));
		editing.addPermission(new WebPermission("Whitelist/Blacklist Users", "web.users.whiteblack", "Gives user permission to whitelist/blacklist users."));
		editing.addPermission(new WebPermission("Delete User", "web.users.delete", "Gives user permission to delete user."));
		
		perms.add(editing = new WebPermission("View Web Groups", "web.groups.view", "Gives user permission to view web groups."));
		editing.addPermission(new WebPermission("Edit Groups", "web.groups.edit", "Gives user permission to edit web groups."));
		editing.addPermission(new WebPermission("Edit Permissions", "web.groups.permissions", "Gives user permission to edit permissions."));
		editing.addPermission(new WebPermission("Delete Groups", "web.groups.delete", "Gives user permission to delete groups."));
		
		perms.add(editing = new WebPermission("View Web Messages", "web.messages.view", "Gives user permission to view web messages."));
		editing.addPermission(new WebPermission("Respond to Messages", "web.messages.respond", "Gives user permission to respond to messages."));
		
		perms.add(editing = new WebPermission("View McAdminPanel Settings", "mcapanel.properties.view", "Gives user permission to view McAdminPanel settings."));
		editing.addPermission(new WebPermission("Edit McAdminPanel Settings", "mcapanel.properties.edit", "Gives user permission to edit McAdminPanel settings."));
	}
	
	public boolean canView()
	{
		return isLoggedIn() && user.getGroup().hasPermission("web.groups.view");
	}
	
	public boolean index()
	{
		request.setAttribute("groups", arrayToString(getGroupsJson(true)));
		
		return renderView();
	}
	
	@SuppressWarnings("unchecked")
	private JSONArray getGroupsJson(boolean raw)
	{
		JSONArray groups = new JSONArray();
		
		List<Group> groupList = db.find(Group.class).findList();
		
		String b = raw ? "<td>" : "";
		String e = raw ? "</td>" : "";
		
		for (Group g : groupList)
		{
			JSONArray ar = new JSONArray();
			
			String permissionsHtml = "<form id='permform' method='post' groupid='" + g.getId() + "'>";
			
			for (WebPermission perm : perms)
			{
				permissionsHtml += "<div class='permgroup'><label><input type='checkbox' name='" + perm.getPermName().replace(".", "-") + "' class='permcheck' " + (g.hasPermission(perm.getPermName()) ? "checked" : "") + " /><span>" + perm.getTitle() + "</span><p>" + perm.getDescription() + "</p></label>";
				
				for (WebPermission perm1 : perm.getPermissions())
				{
					permissionsHtml += "<br /><label class='indent'><input type='checkbox' name='" + perm1.getPermName().replace(".", "-") + "' class='permcheck' " + (g.hasPermission(perm1.getPermName()) ? "checked" : "") + " /><span>" + perm1.getTitle() + "</span><p>" + perm1.getDescription() + "</p></label>";
				}
				
				permissionsHtml += "</div>";
			}
			
			boolean canEdit = isLoggedIn() && user.getGroup().hasPermission("web.groups.edit");
			
			if (raw) ar.add("<tr>");
			ar.add(b + g.getId() + e);
			ar.add(b + "<span class=\"groupname\">" + g.getGroupName() + "</span>" + e);
			ar.add(b + (canEdit ? "<input class=\"ghostcheck\" type=\"checkbox\" " + (g.isGhost() ? "checked" : "") + " />" : "") + "<span class=\"label " + (canEdit ? "changelabel" : "") + " label-" + (g.isGhost() ? "success\">true" : "danger\">false") + "</span>" + e);
			ar.add(b + (canEdit ? "<input class=\"existingradio\" type=\"radio\" " + (g.isExistingDefault() ? "checked" : "") + " />" : "") + "<span class=\"label " + (canEdit ? "changelabel" : "") + " label-" + (g.isExistingDefault() ? "success\">true" : "danger\">false") + "</span>" + e);
			ar.add(b + (canEdit ? "<input class=\"whitelistradio\" type=\"radio\" " + (g.isWhitelistDefault() ? "checked" : "") + " />" : "") + "<span class=\"label " + (canEdit ? "changelabel" : "") + " label-" + (g.isWhitelistDefault() ? "success\">true" : "danger\">false") + "</span>" + e);
			ar.add(b + (canEdit ? ((isLoggedIn() && user.getGroup().hasPermission("web.groups.permissions") ? ("<button type=\"button\" class=\"editperms btn btn-xs btn-info\" permissions=\"" + permissionsHtml + "\">Edit Permissions</button>") : "") + (isLoggedIn() && user.getGroup().hasPermission("web.groups.delete") ? ("<button type=\"button\" id=\"delgroup\" class=\"btn btn-xs btn-danger\" style=\"margin-left: 10px;\">Delete Group</button>") : "")) : "-") + e);
			if (raw) ar.add("</tr>");
			
			groups.add(ar);
		}
		
		return groups;
	}
	
	@SuppressWarnings("unchecked")
	public boolean saveGroups() throws IOException
	{
		if (isMethod("POST"))
		{
			includeIndex(false);
			mimeType("application/json");
			
			JSONObject obj = new JSONObject();
			
			if (isLoggedIn() && user.getGroup().hasPermission("web.groups.edit"))
			{
				String data = request.getParameter("data");
				
				if (data != null)
				{
					JSONArray groups = (JSONArray) JSONValue.parse(data);
					
					for (Object g : groups)
					{
						JSONObject group = (JSONObject) g;
						
						int id = Integer.parseInt(group.get("id").toString());
						String name = group.get("name").toString();
						boolean ghost = (Boolean) group.get("ghost");
						boolean existing = (Boolean) group.get("existing");
						boolean whitelist = (Boolean) group.get("whitelist");
						
						Group gr = db.find(Group.class, id);
						
						if (gr != null)
						{
							gr.setGroupName(name);
							gr.setGhost(ghost);
							gr.setExistingDefault(existing);
							gr.setWhitelistDefault(whitelist);
							
							db.save(gr);
						}
					}
					
					obj.put("good", "Successfully saved all group settings.");
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
	public boolean updatePermissions() throws IOException
	{
		if (isMethod("POST"))
		{
			includeIndex(false);
			mimeType("application/json");
			
			JSONObject obj = new JSONObject();
			
			if (canView())
			{
				String sId = request.getParameter("id");
				String data = request.getParameter("data");
				
				Group gr = db.find(Group.class, Integer.parseInt(sId));
				String[] query = data.split("&");
				
				gr.setPermissions("");
				
				for (String qData : query)
				{
					String perm = qData.split("=")[0].replace("-", ".");
					
					gr.addPermission(perm);
				}
				
				db.update(gr);
				
				obj.put("good", "Successfully saved permissions.");
				
				/*
				String data = request.getParameter("data");
				
				if (data != null)
				{
					JSONArray groups = (JSONArray) JSONValue.parse(data);
					
					for (Object g : groups)
					{
						JSONObject group = (JSONObject) g;
						
						int id = Integer.parseInt(group.get("id").toString());
						String name = group.get("name").toString();
						boolean ghost = (Boolean) group.get("ghost");
						boolean existing = (Boolean) group.get("existing");
						boolean whitelist = (Boolean) group.get("whitelist");
						String permissions = group.get("permissions").toString();
						
						Group gr = db.find(Group.class, id);
						
						if (gr != null)
						{
							gr.setGroupName(name);
							gr.setGhost(ghost);
							gr.setExistingDefault(existing);
							gr.setWhitelistDefault(whitelist);
							gr.setPermissions(permissions);
							
							db.save(gr);
						}
					}
					
					obj.put("good", "Successfully saved all group settings.");
				} else
					obj.put("error", "Error parsing your request.");
				*/
			} else
				obj.put("error", "You do not have permission to do that.");
			
			response.getWriter().println(obj.toJSONString());
			
			return true;
		}
		
		return error();
	}
	
	@SuppressWarnings("unchecked")
	public boolean addGroup() throws IOException
	{
		if (isMethod("POST"))
		{
			includeIndex(false);
			mimeType("application/json");
			
			JSONObject obj = new JSONObject();
			
			if (canView())
			{
				String groupName = request.getParameter("groupname");
				
				if (groupName != null)
				{
					Group group = new Group(groupName);
					db.save(group);
					
					obj.put("good", "Successfully added the group: " + groupName + ".");
				} else
					obj.put("error", "Error parsing your request.");
			} else
				obj.put("error", "You do not have permission to do that.");
			
			response.getWriter().println(obj.toJSONString());
			
			return true;
		}
		
		return error();
	}
}