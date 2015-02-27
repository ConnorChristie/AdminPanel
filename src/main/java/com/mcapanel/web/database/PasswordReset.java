package com.mcapanel.web.database;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table(name = "resetcodes")
public class PasswordReset
{
	@Id
	private int id;
	
	private String username;
	private String resetCode;
	
	public PasswordReset() {}
	
	public PasswordReset(String username, String resetCode)
	{
		this.username = username;
		this.resetCode = resetCode;
	}
	
	public int getId()
	{
		return id;
	}
	
	public void setId(int id)
	{
		this.id = id;
	}

	public String getUsername()
	{
		return username;
	}

	public void setUsername(String username)
	{
		this.username = username;
	}

	public String getResetCode()
	{
		return resetCode;
	}

	public void setResetCode(String resetCode)
	{
		this.resetCode = resetCode;
	}
}