package com.mcapanel.web.database;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table(name = "applications")
public class Application
{
	@Id
	private int id;
	
	private int userId;
	
	private String description;
	
	private Long date;
	
	public Application() {}
	
	public Application(int userId, String description)
	{
		this.userId = userId;
		this.description = description;
		this.date = System.currentTimeMillis();
	}
	
	public int getId()
	{
		return id;
	}
	
	public void setId(int id)
	{
		this.id = id;
	}

	public int getUserId()
	{
		return userId;
	}

	public void setUserId(int userId)
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