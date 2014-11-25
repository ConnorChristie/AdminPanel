package com.mcapanel.panel;

public enum ServerStatus
{
	STARTING("Starting...", "#00A72F"),
	STARTED("Started", "#00A72F"),
	STOPPING("Stopping...", "red"),
	STOPPED("Stopped", "red"),
	RESTARTING("Restarting...", "#0DBBCA"),
	RELOADING("Reloading...", "#0DBBCA");
	
	private String name;
	private String color;
	
	private ServerStatus(String name, String color)
	{
		this.name = name;
		this.color = color;
	}
	
	public String getName()
	{
		return name;
	}
	
	public String getColor()
	{
		return color;
	}
}