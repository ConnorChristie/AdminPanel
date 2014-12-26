package com.mcapanel.web.database;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

import com.mcapanel.web.database.base.BaseModel;

@Entity
@Table(name = "messages")
public class Message extends BaseModel
{
	private String username;
	
	private String subject;
	
	private String message;
	
	public Message() {}
	
	public Message(String username, String subject, String message)
	{
		this.username = username;
		this.subject = subject;
		this.message = message;
	}

	public String getUsername()
	{
		return username;
	}

	public void setUsername(String username)
	{
		this.username = username;
	}

	public String getSubject()
	{
		return subject;
	}

	public void setSubject(String subject)
	{
		this.subject = subject;
	}

	public String getMessage()
	{
		return message;
	}

	public void setMessage(String message)
	{
		this.message = message;
	}
}