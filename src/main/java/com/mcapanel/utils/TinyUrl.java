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
		
		if (!this.url.equals(url))
		{
			try
			{
				String tinyUrlLookup = startUrl + (!url.startsWith("http://") ? "http://" : "") + url;
				
				BufferedReader reader = new BufferedReader(new InputStreamReader(new URL(tinyUrlLookup).openStream()));
				
				tinyUrl = reader.readLine().replace("http://", "");
				
				this.url = url;
			} catch (Exception e) { System.out.println("Error getting new TinyUrl... Using old one."); e.printStackTrace(); }
		}
		
		return tinyUrl;
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