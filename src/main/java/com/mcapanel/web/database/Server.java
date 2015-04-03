package com.mcapanel.web.database;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table(name = "servers")
public class Server
{
	@Id
	private int id;
	
	private String name;
	private String serverJar;
	private String javaArgs;
	private String minMemory;
	private String maxMemory;
	
	public Server() { }
	
	public Server(String name, String serverJar)
	{
		this.name = name;
		this.serverJar = serverJar;
		this.javaArgs = "";
		
		this.minMemory = "1024m";
		this.maxMemory = "2048m";
	}
	
	public int getId()
	{
		return id;
	}
	
	public void setId(int id)
	{
		this.id = id;
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
	
	public String getJavaArgs()
	{
		return javaArgs;
	}

	public void setJavaArgs(String javaArgs)
	{
		this.javaArgs = javaArgs;
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