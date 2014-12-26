package com.mcapanel.web.database;

import javax.persistence.Entity;
import javax.persistence.Table;

import com.mcapanel.web.database.base.BaseModel;

@Entity
@Table(name = "applications")
public class Application extends BaseModel
{
	private Long userId;
	
	private String description;
	
	private Long date;
	
	public Application() {}
	
	public Application(Long userId, String description)
	{
		this.userId = userId;
		this.description = description;
		this.date = System.currentTimeMillis();
	}

	public Long getUserId()
	{
		return userId;
	}

	public void setUserId(Long userId)
	{
		this.userId = userId;
	}

	public String getDescription()
	{
		return description;
	}

	public void setDescription(String description)
	{
		this.description = description;
	}

	public Long getDate()
	{
		return date;
	}

	public void setDate(Long date)
	{
		this.date = date;
	}
}