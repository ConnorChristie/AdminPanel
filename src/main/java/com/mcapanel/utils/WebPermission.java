package com.mcapanel.utils;

import java.util.ArrayList;
import java.util.List;

public class WebPermission
{
	private String perm;
	private String permName;
	private String description;
	
	private List<WebPermission> perms = new ArrayList<WebPermission>();
	
	public WebPermission(String perm, String permName, String description)
	{
		this.perm = perm;
		this.permName = permName;
		this.description = description;
	}
	
	public void addPermission(WebPermission perm)
	{
		perms.add(perm);
	}
	
	public List<WebPermission> getPermissions()
	{
		return perms;
	}
	
	public String getTitle()
	{
		return perm;
	}

	public String getPermName()
	{
		return permName;
	}

	public String getDescription()
	{
		return description;
	}
}