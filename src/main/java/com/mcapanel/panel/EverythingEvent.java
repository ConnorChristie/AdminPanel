package com.mcapanel.panel;

import java.util.HashMap;
import java.util.Map;

import org.json.simple.JSONObject;

import com.mcapanel.bukkit.BukkitServer;
import com.mcapanel.web.controllers.HomeController;
import com.mcapanel.web.controllers.IndexController;
import com.mcapanel.web.database.Application;

public class EverythingEvent extends Thread
{
	private Map<Integer, JSONObject> data = new HashMap<Integer, JSONObject>();
	
	private boolean isRunning = false;
	
	private int unreadApps = 0;
	private int apps = 0;
	
	public void run()
	{
		isRunning = true;
		
		AdminPanelWrapper.executeMain(new Runnable() {
			public void run()
			{
				unreadApps = AdminPanelWrapper.getInstance().getDatabase().find(Application.class).findRowCount();
			}
		});
		
		while (isRunning)
		{
			AdminPanelWrapper.executeMain(new Runnable() {
				public void run()
				{
					fetchData();
				}
			});
			
			try
			{
				sleep(1000);
			} catch (InterruptedException e) { }
		}
		
		try
		{
			join();
		} catch (InterruptedException e) { }
	}
	
	@SuppressWarnings("unchecked")
	public void fetchData()
	{
		apps = AdminPanelWrapper.getInstance().getDatabase().find(Application.class).findRowCount();
		
		for (BukkitServer bukkitServer : AdminPanelWrapper.getInstance().getServers())
		{
			JSONObject datas = new JSONObject();
			
			datas.put("control",    HomeController.getControlsJson(bukkitServer));
			datas.put("playersObj", IndexController.getOnlinePlayers(bukkitServer));
			datas.put("chats",      IndexController.getChats(bukkitServer));
			datas.put("console",    IndexController.getConsole(bukkitServer));
			if (apps != unreadApps) datas.put("applications", apps);
			
			data.put(bukkitServer.getId(), datas);
		}
		
		unreadApps = apps;
	}
	
	public JSONObject getData(int serverId)
	{
		return data.get(serverId);
	}
	
	public void stopRunning()
	{
		isRunning = false;
	}
}