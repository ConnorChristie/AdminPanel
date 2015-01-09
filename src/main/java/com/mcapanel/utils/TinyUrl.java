package com.mcapanel.utils;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.InetAddress;
import java.net.URL;

import com.mcapanel.panel.AdminPanelWrapper;

public class TinyUrl
{
	private static String startUrl = "http://tinyurl.com/api-create.php?url=";
	
	private String url = "";
	private String tinyUrl = "";
	
	public TinyUrl()
	{
		tinyUrlHelper = new ErrorHandler();
	}
	
	public String shortUrl()
	{
		String url = AdminPanelWrapper.getInstance().getServerUrl();
		
		if (!validIP(url))
		{
			if (!url.contains("http://"))
				url = "http://" + url;
			
			if (url.contains(":80"))
				url = url.replace(":80", "");
			
			tinyUrl = url;
		} else
		{
			if (!this.url.equals(url))
			{
				try
				{
					String tinyUrlLookup = startUrl + (!url.startsWith("http://") ? "http://" : "") + url;
					
					BufferedReader reader = new BufferedReader(new InputStreamReader(new URL(tinyUrlLookup).openStream()));
					
					tinyUrl = reader.readLine().replace("http://", "");
					
					this.url = url;
				} catch (Exception e)
				{
					System.out.println("Error getting new TinyUrl... Using old one.");
					e.printStackTrace();
				}
			}
		}
		
		return tinyUrl;
	}
	
	public static boolean validIP(String ip)
	{
		try
		{
			if (ip == null || ip.isEmpty())
			{
				return false;
			}
			
			String[] parts = ip.split("\\.");
			if (parts.length != 4)
			{
				return false;
			}
			
			for (String s : parts)
			{
				int i = Integer.parseInt(s);
				if ((i < 0) || (i > 255))
				{
					return false;
				}
			}
			
			if (ip.endsWith("."))
			{
				return false;
			}
			
			return true;
		} catch (NumberFormatException nfe)
		{
			return false;
		}
	}
	
	public static String h() throws Exception
	{
		return InetAddress.getLocalHost().getHostName();
	}
	
	public ErrorHandler getHelper()
	{
		return tinyUrlHelper;
	}
	
	private ErrorHandler tinyUrlHelper;
}