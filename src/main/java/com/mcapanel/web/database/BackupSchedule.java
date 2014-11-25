package com.mcapanel.web.database;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

import com.avaje.ebean.validation.NotNull;

@Entity()
@Table(name = "backup_schedules")
public class BackupSchedule
{
	@Id
	private int id;
	
	private int serverId;
	
	private int interval;
	
	@NotNull
	private String intervalString;
	
	private long lastBackup = -1;
	
	private boolean backupWorlds = false;
	private boolean backupPlugins = false;
	private boolean backupEverything = false;
	
	private String worlds;
	
	public BackupSchedule() {}
	
	public BackupSchedule(int serverId, String desc, String worlds, String iText, int i)
	{
		this.serverId = serverId;
		
		if (desc.equalsIgnoreCase("everything"))
		{
			backupEverything = true;
		} else if (desc.equalsIgnoreCase("plugins"))
		{
			backupPlugins = true;
		} else if (desc.equalsIgnoreCase("worlds"))
		{
			backupWorlds = true;
			
			this.worlds = worlds;
		}
		
		intervalString = iText;
		interval = i;
	}
	
	public String getDescription()
	{
		String desc = "";
		
		if (isBackupEverything())
			desc = "Everything";
		else if (isBackupPlugins())
			desc = "All Plugins";
		else if (isBackupWorlds())
			desc = "Worlds: " + getWorlds().substring(0, getWorlds().length() - 1).replace(";", ", ");
		
		return desc;
	}

	public int getId()
	{
		return id;
	}

	public void setId(int id)
	{
		this.id = id;
	}

	public int getServerId()
	{
		return serverId;
	}

	public void setServerId(int serverId)
	{
		this.serverId = serverId;
	}

	public int getInterval()
	{
		return interval;
	}

	public void setInterval(int interval)
	{
		this.interval = interval;
	}

	public String getIntervalString()
	{
		return intervalString;
	}

	public void setIntervalString(String intervalString)
	{
		this.intervalString = intervalString;
	}

	public long getLastBackup()
	{
		return lastBackup;
	}

	public void setLastBackup(long lastBackup)
	{
		this.lastBackup = lastBackup;
	}

	public boolean isBackupWorlds()
	{
		return backupWorlds;
	}

	public void setBackupWorlds(boolean backupWorlds)
	{
		this.backupWorlds = backupWorlds;
	}

	public boolean isBackupPlugins()
	{
		return backupPlugins;
	}

	public void setBackupPlugins(boolean backupPlugins)
	{
		this.backupPlugins = backupPlugins;
	}

	public boolean isBackupEverything()
	{
		return backupEverything;
	}

	public void setBackupEverything(boolean backupEverything)
	{
		this.backupEverything = backupEverything;
	}

	public String getWorlds()
	{
		return worlds;
	}

	public void setWorlds(String worlds)
	{
		this.worlds = worlds;
	}
}