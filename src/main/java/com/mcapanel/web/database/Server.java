package com.mcapanel.web.database;

import javax.persistence.Entity;
import javax.persistence.Table;

import com.mcapanel.web.database.base.BaseModel;

@Entity
@Table(name = "servers")
public class Server extends BaseModel
{
	private String name;
	
	private String serverJar;
	
	private String minMemory;
	
	private String maxMemory;
	
	public Server() { }
	
	public Server(String name, String serverJar)
	{
		this.name = name;
		this.serverJar = serverJar;
		
		this.minMemory = "1024m";
		this.maxMemory = "2048m";
	}

	public String getName()
	{
		return name;
	}

	public void setName(String name)
	{
		this.name = name;
	}

	public String getServerJar()
	{
		return serverJar;
	}

	public void setServerJar(String serverJar)
	{
		this.serverJar = serverJar;
	}

	public String getMinMemory()
	{
		return minMemory;
	}

	public void setMinMemory(String minMemory)
	{
		this.minMemory = minMemory;
	}

	public String getMaxMemory()
	{
		return maxMemory;
	}

	public void setMaxMemory(String maxMemory)
	{
		this.maxMemory = maxMemory;
	}
}