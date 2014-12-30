package com.mcapanel.web.database;

import java.math.BigInteger;
import java.nio.charset.Charset;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.UUID;

import javax.persistence.Entity;
import javax.persistence.Table;
import javax.persistence.Transient;

import com.mcapanel.bukkit.utils.UUIDFetcher;
import com.mcapanel.panel.AdminPanelWrapper;
import com.mcapanel.web.database.base.BaseModel;

@Entity
@Table(name = "users")
public class User extends BaseModel
{
	private Long groupId;
	
	private String uuid;
	
	private String username;
	
	private String passHash;
	
	private String passSalt;
	
	private String ipAddress;
	
	private boolean whitelisted = false;
	private boolean blacklisted = false;
	
	@Transient
	private Group group = null;
	
	public User() { }
	
	public User(String usernameLocal, String passHash, String passSalt, String ipAddress)
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
		
		this.uuid      = "";
		this.username  = usernameLocal;
		this.passHash  = passHash;
		this.passSalt  = passSalt;
		this.ipAddress = ipAddress;
		
		new Thread(new Runnable() {
			public void run()
			{
				try
				{
					final UUID uuidLocal = UUIDFetcher.getUUIDOf(username);
					
					AdminPanelWrapper.executeMain(new Runnable() {
						public void run()
						{
							uuid = uuidLocal.toString() + "," + UUID.nameUUIDFromBytes(("OfflinePlayer:" + username).getBytes(Charset.forName("UTF-8"))).toString();
							
							saveUser();
						}
					});
				} catch (Exception e) { e.printStackTrace(); }
			}
		}).start();
	}
	
	private void saveUser()
	{
		AdminPanelWrapper.getInstance().getDatabase().save(this);
	}

	public Long getGroupId()
	{
		return groupId;
	}
	
	public void setGroupId(Long groupId)
	{
		this.groupId = groupId;
	}
	
	public Group getGroup()
	{
		if (group == null) group = AdminPanelWrapper.getInstance().getDatabase().find(Group.class, groupId);
		
		return group;
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