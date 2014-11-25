package com.mcapanel.web.database;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

import com.avaje.ebean.validation.NotNull;

@Entity()
@Table(name = "backups")
public class Backup
{
	@Id
	private int id;
	
	private int schedule;
	private int serverId;
	
	@NotNull
	private String description;
	
	@NotNull
	private String filename;
	
	private long date;
	private long size;
	
	public Backup() {}
	
	public Backup(int schedId, int serverId, String description, String filename, long size)
	{
		this.schedule = schedId;
		this.serverId = serverId;
		this.description = description;
		this.filename = filename;
		this.size = size;
		
		date = System.currentTimeMillis();
	}

	public int getId()
	{
		return id;
	}

	public void setId(int id)
	{
		this.id = id;
	}

	public int getSchedule()
	{
		return schedule;
	}

	public void setSchedule(int schedule)
	{
		this.schedule = schedule;
	}

	public int getServerId()
	{
		return serverId;
	}

	public void setServerId(int serverId)
	{
		this.serverId = serverId;
	}

	public String getDescription()
	{
		return description;
	}

	public void setDescription(String description)
	{
		this.description = description;
	}

	public String getFilename()
	{
		return filename;
	}

	public void setFilename(String filename)
	{
		this.filename = filename;
	}

	public long getDate()
	{
		return date;
	}

	public void setDate(long date)
	{
		this.date = date;
	}

	public long getSize()
	{
		return size;
	}

	public void setSize(long size)
	{
		this.size = size;
	}
}
