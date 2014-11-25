package com.mcapanel.web.database;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

import com.avaje.ebean.validation.NotNull;

@Entity()
@Table(name = "messages")
public class Message
{
	@Id
	private int id;
	
	@NotNull
	private String username;
	
	@NotNull
	private String subject;
	
	@NotNull
	private String message;
	
	public Message() {}
	
	public Message(String username, String subject, String message)
	{
		this.username = username;
		this.subject = subject;
		this.message = message;
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