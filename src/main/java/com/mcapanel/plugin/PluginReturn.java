package com.mcapanel.plugin;

public class PluginReturn
{
	private long time;
	
	private String method;
	
	private String data;
	
	public PluginReturn(long time, String method, String data)
	{
		this.time = time;
		this.method = method;
		this.data = data;
	}
	
	public long getTime()
	{
		return time;
	}
	
	public String getMethod()
	{
		return method;
	}
	
	public String getData()
	{
		return data;
	}
}