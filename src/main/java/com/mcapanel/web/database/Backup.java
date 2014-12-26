package com.mcapanel.web.database;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

import com.mcapanel.web.database.base.BaseModel;

@Entity
@Table(name = "backups")
public class Backup extends BaseModel
{
	private Long schedule;
	private Long serverId;
	
	private String description;
	
	private String filename;
	
	private long date;
	private long size;
	
	public Backup() {}
	
	public Backup(Long schedId, Long serverId, String description, String filename, long size)
	{
		this.schedule = schedId;
		this.serverId = serverId;
		this.description = description;
		this.filename = filename;
		this.size = size;
		
		date = System.currentTimeMillis();
	}

	public Long getSchedule()
	{
		return schedule;
	}

	public void setSchedule(Long schedule)
	{
		this.schedule = schedule;
	}

	public Long getServerId()
	{
		return serverId;
	}

	public void setServerId(Long serverId)
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
