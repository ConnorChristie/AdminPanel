package com.mcapanel.web.controllers;

import java.io.File;
import java.io.IOException;
import java.text.NumberFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Map;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

import com.mcapanel.backup.BackupInterval;
import com.mcapanel.web.database.Backup;
import com.mcapanel.web.database.BackupSchedule;
import com.mcapanel.web.handlers.Controller;

public class BackupsController extends Controller
{
	public boolean canView()
	{
		return isLoggedIn() && user.getGroup().hasPermission("server.backups.view");
	}
	
	public boolean index()
	{
		request.setAttribute("scheduledbackups", arrayToString(getScheduledBackupsJson(true)));
		request.setAttribute("backups", arrayToString(getBackupsJson(true)));
		
		request.setAttribute("worlds", getWorlds());
		
		return renderView();
	}
	
	@SuppressWarnings("unchecked")
	public boolean schedule() throws IOException
	{
		if (isMethod("POST"))
		{
			includeIndex(false);
			mimeType("application/json");
			
			JSONObject out = new JSONObject();
			
			if (isLoggedIn() && user.getGroup().hasPermission("server.backups.schedule.issue"))
			{
				Map<String, String[]> params = request.getParameterMap();
				
				if (params.containsKey("desc"))
				{
					String desc = request.getParameter("desc");
					
					List<BackupSchedule> scheds = db.find(BackupSchedule.class).where().eq("backup_everything", true).findList();
					
					if (desc.equalsIgnoreCase("plugins"))
						scheds = db.find(BackupSchedule.class).where().eq("backup_plugins", true).findList();
					else if (desc.equalsIgnoreCase("worlds"))
						scheds = db.find(BackupSchedule.class).where().eq("backup_worlds", true).findList();
					
					if (scheds.size() > 0)
						out.put("error", "There is no need to create another backup schedule for " + desc + ".");
					else
					{
						if (params.containsKey("interval"))
						{
							if (params.containsKey("intervaltime"))
							{
								String worlds = "";
								
								if (desc.equalsIgnoreCase("worlds"))
									for (File w : bukkitServer.getWorlds())
										if (params.containsKey(w.getName()))
											worlds += w.getName() + ";";
								
								int iTime = 30;
								
								try
								{
									iTime = Integer.parseInt(request.getParameter("intervaltime"));
									
									BackupSchedule bs = new BackupSchedule(bukkitServer.getId(), desc, worlds, request.getParameter("interval"), iTime);
								
									db.save(bs);
									
									bukkitServer.getBackupHandler().backupNow(bs);
									
									out.put("good", "Successfully created a new backup schedule and it's backing up!");
								} catch (NumberFormatException e)
								{
									out.put("error", "The interval time you entered is not a number.");
								}
								
								out.put("scheduledbackups", getScheduledBackupsJson(false));
							} else
								out.put("error", "Please select an interval time to backup.");
						} else
							out.put("error", "Please select an interval to backup.");
					}
				} else
					out.put("error", "Please select something to backup.");
			} else
				out.put("error", "You do not have permission to do that.");
			
			response.getWriter().println(out.toJSONString());
			
			return true;
		}
		
		return error();
	}
	
	@SuppressWarnings("unchecked")
	public boolean processSchedule() throws IOException
	{
		if (isMethod("POST"))
		{
			includeIndex(false);
			mimeType("application/json");
			
			JSONObject out = new JSONObject();
			
			if (isLoggedIn() && (user.getGroup().hasPermission("server.backups.schedule.issue") || user.getGroup().hasPermission("server.backups.schedule.delete")))
			{
				if (arguments.size() == 1)
				{
					if (arguments.get(0).equalsIgnoreCase("backup") && user.getGroup().hasPermission("server.backups.schedule.issue"))
					{
						try
						{
							BackupSchedule bs = db.find(BackupSchedule.class, Integer.parseInt(request.getParameter("id")));
							
							if (bs != null)
							{
								bukkitServer.getBackupHandler().backupNow(bs);
								
								out.put("good", "Backing up " + bs.getDescription() + ", will be done in a few seconds.");
							} else
								out.put("error", "Could not find the selected backup schedule, try refreshing and trying again.");
						} catch (NumberFormatException e)
						{
							out.put("error", "The id supplied is not a number.");
						}
					} else if (arguments.get(0).equalsIgnoreCase("delete") && user.getGroup().hasPermission("server.backups.schedule.delete"))
					{
						try
						{
							BackupSchedule bs = db.find(BackupSchedule.class, Integer.parseInt(request.getParameter("id")));
							
							if (bs != null)
							{
								db.delete(bs);
								
								out.put("good", "Successfully deleted backup schedule " + bs.getDescription());
							} else
								out.put("error", "Could not find the selected backup schedule, try refreshing and trying again.");
						} catch (NumberFormatException e)
						{
							out.put("error", "The id supplied is not a number.");
						}
					} else
						out.put("error", "You do not have permission to do that.");
				}
				
				if (!out.containsKey("error"))
					out.put("error", "Unable to process your request.");
			} else
				out.put("error", "You do not have permission to do that.");
			
			if (isLoggedIn() && user.getGroup().hasPermission("server.backups.view"))
				out.put("scheduledbackups", getScheduledBackupsJson(false));
			
			response.getWriter().println(out.toJSONString());
			
			return true;
		}
		
		return error();
	}
	
	@SuppressWarnings("unchecked")
	public boolean processBackup() throws IOException
	{
		if (isMethod("POST"))
		{
			includeIndex(false);
			mimeType("application/json");
			
			JSONObject out = new JSONObject();
			
			if (isLoggedIn() && (user.getGroup().hasPermission("server.backups.restore") || user.getGroup().hasPermission("server.backups.delete")))
			{
				if (arguments.size() == 1)
				{
					if (arguments.get(0).equalsIgnoreCase("restore") && user.getGroup().hasPermission("server.backups.restore"))
					{
						Backup backup = db.find(Backup.class, Integer.parseInt(request.getParameter("id")));
						
						if (backup != null)
						{
							bukkitServer.getBackupHandler().restoreBackup(backup);
							
							out.put("good", "Restoring " + backup.getDescription() + ", will be done in a few seconds.");
						} else
							out.put("error", "Could not find the selected backup, try refreshing and trying again.");
					} else if (arguments.get(0).equalsIgnoreCase("delete") && user.getGroup().hasPermission("server.backups.delete"))
					{
						Backup backup = db.find(Backup.class, Integer.parseInt(request.getParameter("id")));
						
						if (backup != null)
						{
							boolean deleted = bukkitServer.getBackupHandler().deleteBackup(backup);
							
							db.delete(backup);
							
							if (deleted)
								out.put("good", "Successfully deleted backup " + backup.getDescription());
							else
								out.put("error", "Could not find the backup file, perhaps already deleted?");
						} else
							out.put("error", "Could not find the selected backup, try refreshing and trying again.");
					} else
						out.put("error", "You do not have permission to do that.");
				}
				
				if (!out.containsKey("error"))
					out.put("error", "Unable to process your request.");
			} else
				out.put("error", "You do not have permission to do that.");
			
			if (isLoggedIn() && user.getGroup().hasPermission("server.backups.view"))
				out.put("backups", getBackupsJson(false));
			
			response.getWriter().println(out.toJSONString());
			
			return true;
		}
		
		return error();
	}
	
	@SuppressWarnings("unchecked")
	private JSONArray getScheduledBackupsJson(boolean raw)
	{
		JSONArray sb = new JSONArray();
		
		List<BackupSchedule> sbs = db.find(BackupSchedule.class).findList();
		
		String b = raw ? "<td>" : "";
		String e = raw ? "</td>" : "";
		
		for (BackupSchedule bs : sbs)
		{
			JSONArray ar = new JSONArray();
			
			SimpleDateFormat sdf = new SimpleDateFormat("MMM d, yyyy h:mm a");
			
			if (raw) ar.add("<tr>");
			ar.add(b + bs.getId() + e);
			ar.add(b + bs.getDescription() + e);
			ar.add(b + BackupInterval.toText(bs.getIntervalString(), bs.getInterval()) + e);
			ar.add(b + (bs.getLastBackup() == -1 ? "Never" : sdf.format(new Date(bs.getLastBackup()))) + e);
			if (raw) ar.add("</tr>");
			
			sb.add(ar);
		}
		
		return sb;
	}
	
	@SuppressWarnings("unchecked")
	private JSONArray getBackupsJson(boolean raw)
	{
		JSONArray ba = new JSONArray();
		
		List<Backup> bl = db.find(Backup.class).findList();
		
		String b = raw ? "<td>" : "";
		String e = raw ? "</td>" : "";
		
		for (Backup back : bl)
		{
			JSONArray ar = new JSONArray();
			
			SimpleDateFormat sdf = new SimpleDateFormat("MMM d, yyyy h:mm a");
			
			if (raw) ar.add("<tr>");
			ar.add(b + back.getId() + e);
			ar.add(b + back.getDescription() + e);
			ar.add(b + NumberFormat.getInstance().format(back.getSize() / 1048576) + " MB" + e);
			ar.add(b + sdf.format(new Date(back.getDate())) + e);
			if (raw) ar.add("</tr>");
			
			ba.add(ar);
		}
		
		return ba;
	}
	
	private String getWorlds()
	{
		String worlds = "";
		
		for (File w : bukkitServer.getWorlds())
		{
			worlds += "<span style='margin-left: 40px;'><input type='checkbox' class='worldSelect' name='" + w.getName() + "' disabled />&nbsp;&nbsp;<b>" + w.getName() + "</b></span><br />";
		}
		
		return worlds;
	}
}