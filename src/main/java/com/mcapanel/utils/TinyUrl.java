package com.mcapanel.utils;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.InetAddress;
import java.net.MalformedURLException;
import java.net.URL;

import com.mcapanel.panel.AdminPanelWrapper;

public class TinyUrl
{
	private static String startUrl = "http://tinyurl.com/api-create.php?url=";
	
	public TinyUrl()
	{
		tinyUrlHelper = new ErrorHandler();
	}
	
	public String shortUrl()
	{
		String url = AdminPanelWrapper.getInstance().getServerUrl();
		String tinyUrl = "";
		
		try
		{
			String tinyUrlLookup = startUrl + (!url.startsWith("http://") ? "http://" : "") + url;
			
			BufferedReader reader = new BufferedReader(new InputStreamReader(new URL(tinyUrlLookup).openStream()));
			
			tinyUrl = reader.readLine().replace("http://", "");
		} catch (MalformedURLException e)
		{
			e.printStackTrace();
		} catch (IOException e)
		{
			e.printStackTrace();
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