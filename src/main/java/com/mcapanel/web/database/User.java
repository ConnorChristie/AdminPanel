package com.mcapanel.web.database;

import java.math.BigInteger;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.Transient;

import com.avaje.ebean.validation.NotNull;
import com.mcapanel.panel.AdminPanelWrapper;

@Entity()
@Table(name = "users")
public class User
{
	@Id
	private int id;
	
	private int groupId;
	
	@NotNull
	private String uuid;
	
	@NotNull
	private String username;
	
	@NotNull
	private String passHash;
	
	@NotNull
	private String passSalt;
	
	@NotNull
	private String ipAddress;
	
	private boolean whitelisted = false;
	private boolean blacklisted = false;
	
	@Transient
	private Group group = null;
	
	public User() { }
	
	public User(String username, String passHash, String passSalt, String ipAddress)
	{
		try
		{
			MessageDigest messageDigest = MessageDigest.getInstance("MD5");
			
			messageDigest.reset();
			messageDigest.update(passSalt.getBytes());
			
			String salt = new BigInteger(1, messageDigest.digest()).toString(16);
			
			messageDigest.reset();
			messageDigest.update((passHash + salt).getBytes());
			
			passHash = new BigInteger(1, messageDigest.digest()).toString(16);
		} catch (NoSuchAlgorithmException e)
		{
			e.printStackTrace();
		}
		
		this.uuid      = "";//AdminPanelWrapper.getInstance().getUUIDFetcher().getUUIDOf(username).toString();
		this.username  = username;
		this.passHash  = passHash;
		this.passSalt  = passSalt;
		this.ipAddress = ipAddress;
	}

	public int getId()
	{
		return id;
	}

	public void setId(int id)
	{
		this.id = id;
	}

	public int getGroupId()
	{
		return groupId;
	}
	
	public Group getGroup()
	{
		if (group == null) group = AdminPanelWrapper.getInstance().getDatabase().find(Group.class, groupId);
		
		return group;
	}

	public void setGroupId(int groupId)
	{
		this.groupId = groupId;
	}

	public String getUuid()
	{
		return uuid;
	}

	public void setUuid(String uuid)
	{
		this.uuid = uuid;
	}
	
	public String getUsername()
	{
		return username;
	}
	
	public void setUsername(String username)
	{
		this.username = username;
	}

	public String getPassHash()
	{
		return passHash;
	}

	public void setPassHash(String passHash)
	{
		this.passHash = passHash;
	}

	public String getPassSalt()
	{
		return passSalt;
	}

	public void setPassSalt(String passSalt)
	{
		this.passSalt = passSalt;
	}

	public String getIpAddress()
	{
		return ipAddress;
	}

	public void setIpAddress(String ipAddress)
	{
		this.ipAddress = ipAddress;
	}

	public boolean isWhitelisted()
	{
		return whitelisted;
	}

	public void setWhitelisted(boolean whitelisted)
	{
		this.whitelisted = whitelisted;
	}

	public boolean isBlacklisted()
	{
		return blacklisted;
	}

	public void setBlacklisted(boolean blacklisted)
	{
		this.blacklisted = blacklisted;
	}
}