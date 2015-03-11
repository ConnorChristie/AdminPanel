package com.mcapanel.bukkit;

public class BukkitPlayer
{
	private String name;
	private String group;
	
	private String status;
	private String statusLabel;
	
	private String firstPlayed;
	private String lastPlayed;
	
	private String health;
	private String food;
	
	public BukkitPlayer(String name)
	{
		this.name = name;
	}

	public String getName()
	{
		return name;
	}

	public String getGroup()
	{
		return group;
	}

	public void setGroup(String group)
	{
		this.group = group;
	}

	public String getStatus()
	{
		return status;
	}

	public void setStatus(String status)
	{
		this.status = status;
	}

	public String getStatusLabel()
	{
		return statusLabel;
	}

	public void setStatusLabel(String statLabel)
	{
		this.statusLabel = statLabel;
	}

	public String getFirstPlayed()
	{
		return firstPlayed;
	}

	public void setFirstPlayed(String firstPlayed)
	{
		this.firstPlayed = firstPlayed;
	}

	public String getLastPlayed()
	{
		return lastPlayed;
	}

	public void setLastPlayed(String lastPlayed)
	{
		this.lastPlayed = lastPlayed;
	}

	public String getHealth()
	{
		return health;
	}

	public void setHealth(String health)
	{
		this.health = health;
	}

	public String getFood()
	{
		return food;
	}

	public void setFood(String food)
	{
		this.food = food;
	}
	
	
}