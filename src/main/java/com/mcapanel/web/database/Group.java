package com.mcapanel.web.database;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table(name = "groups")
public class Group
{
	@Id
	private Long id;
	
	private boolean isGhost = false;
	private boolean isExistingDefault = false;
	private boolean isWhitelistDefault = false;
	
	private String groupName;
	
	private String permissions;
	
	public Group() { }
	
	public Group(String groupName)
	{
		this.groupName = groupName;
		this.permissions = "";
	}
	
	public Long getId()
	{
		return id;
	}
	
	public void setId(Long id)
	{
		this.id = id;
	}
	
	public boolean isGhost()
	{
		return isGhost;
	}
	
	public void setGhost(boolean isGhost)
	{
		this.isGhost = isGhost;
	}
	
	public boolean isExistingDefault()
	{
		return isExistingDefault;
	}
	
	public void setExistingDefault(boolean isExistingDefault)
	{
		this.isExistingDefault = isExistingDefault;
	}
	
	public boolean isWhitelistDefault()
	{
		return isWhitelistDefault;
	}
	
	public void setWhitelistDefault(boolean isWhitelistDefault)
	{
		this.isWhitelistDefault = isWhitelistDefault;
	}
	
	public String getGroupName()
	{
		return groupName;
	}
	
	public void setGroupName(String groupName)
	{
		this.groupName = groupName;
	}
	
	public String getPermissions()
	{
		return permissions;
	}
	
	public void setPermissions(String permissions)
	{
		this.permissions = permissions;
	}
	
	public void addPermission(String perm)
	{
		if (!permissions.contains(perm))
			setPermissions(permissions + perm + ";");
	}
	
	public void removePermission(String perm)
	{
		permissions.replace(perm + ";", "");
	}
	
	public boolean hasPermission(String perm)
	{
		return permissions.contains("*") || permissions.contains(perm);
	}
}