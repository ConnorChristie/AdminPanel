package com.mcapanel.utils;

import java.io.File;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import java.util.TimerTask;

import javax.swing.filechooser.FileSystemView;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

import com.jezhumble.javasysmon.CpuTimes;
import com.jezhumble.javasysmon.JavaSysMon;

public class UsageMonitor extends TimerTask
{
	private JavaSysMon monitor = null;
	
	private CpuTimes now = null;
	private CpuTimes previous = null;
	
	private JSONObject usageJson = new JSONObject();
	
	public UsageMonitor()
	{
		monitor = new JavaSysMon();
		
		if (!monitor.supportedPlatform())
		{
			System.out.println("Performance monitoring not supported!");
			
			monitor = null;
		} else
			now = monitor.cpuTimes();
	}
	
	public long getCpuFrequency()
	{
		if (monitor != null)
			return monitor.cpuFrequencyInHz();
		
		return 0L;
	}
	
	public float getCpuUsage()
	{
		if (monitor != null && previous != null && now != null)
			return now.getCpuUsage(previous);
		
		return 0F;
	}
	
	public int getNumCpus()
	{
		if (monitor != null)
			return monitor.numCpus();
		
		return 0;
	}
	
	public String getOsName()
	{
		if (monitor != null)
			return monitor.osName();
		
		return "";
	}
	
	public long getPhysicalMemoryFree()
	{
		if (monitor != null)
			return monitor.physical().getFreeBytes();
		
		return 0L;
	}
	
	public long getPhysicalMemoryTotal()
	{
		if (monitor != null)
			return monitor.physical().getTotalBytes();
		
		return 0L;
	}
	
	public int getPid()
	{
		if (monitor != null)
			return monitor.currentPid();
		
		return 0;
	}
	
	public long getUptime()
	{
		if (monitor != null)
			return monitor.uptimeInSeconds();
		
		return 0L;
	}
	
	public void infanticide()
	{
		if (monitor != null)
			monitor.infanticide();
	}
	
	@Override
	public void run()
	{
		if (monitor != null)
		{
			previous = now;
			now = monitor.cpuTimes();
			
			updateUsage();
		}
	}
	
	public JSONObject getUsageJson()
	{
		return usageJson;
	}
	
	@SuppressWarnings("unchecked")
	private void updateUsage()
	{
		DecimalFormat df = new DecimalFormat("#.0");
		
		usageJson.put("ramTotal", Double.parseDouble(df.format(getPhysicalMemoryTotal() / 1024.0 / 1024.0 / 1024.0)));
		usageJson.put("ramFree", Double.parseDouble(df.format(getPhysicalMemoryFree() / 1024.0 / 1024.0 / 1024.0)));
		usageJson.put("ramUsed", Double.parseDouble(df.format(((Double) usageJson.get("ramTotal")) - ((Double) usageJson.get("ramFree")))));
		
		usageJson.put("ramPercent", (int) (((Double) usageJson.get("ramUsed")) * 100 / ((Double) usageJson.get("ramTotal"))));
		
		usageJson.put("cpuPercent", (int) (getCpuUsage() * 100));
		usageJson.put("cpuCores", getNumCpus());
		usageJson.put("cpuFreq", Double.parseDouble(df.format(getCpuFrequency() / 1000000000.0)));
		
		List<File> fs = new ArrayList<File>(Arrays.asList(File.listRoots()));
	    FileSystemView fsv = FileSystemView.getFileSystemView();
	    
	    Iterator<File> fsit = fs.iterator();
	    
	    while (fsit.hasNext())
	    {
	    	try
	    	{
		    	File f = fsit.next();
		    	
		    	if (f == null
		    			|| !fsv.isFileSystemRoot(f)
		    			|| fsv.getSystemDisplayName(f).isEmpty()
		    			|| fsv.getSystemDisplayName(f).toLowerCase().contains("reserve"))
		    		fsit.remove();
	    	} catch (Exception e) { }
	    }
	    
	    usageJson.put("disksCount", fs.size());
	    
	    JSONArray arr = new JSONArray();
	    
	    for (File f : fs)
	    {
	    	JSONObject obj2 = new JSONObject();
	    	
	    	obj2.put("diskUsed", Double.parseDouble(df.format((f.getTotalSpace() - f.getFreeSpace()) / 1024.0 / 1024.0 / 1024.0)));
	    	obj2.put("diskTotal", Double.parseDouble(df.format(f.getTotalSpace() / 1024.0 / 1024.0 / 1024.0)));
	    	obj2.put("diskFree", Double.parseDouble(df.format(((Double) obj2.get("diskTotal")) - ((Double) obj2.get("diskUsed")))));
	    	
	    	obj2.put("diskPercent", (int) (((Double) obj2.get("diskUsed")) * 100 / ((Double) obj2.get("diskTotal"))));
	    	
	    	arr.add(obj2);
	    }
	    
	    usageJson.put("disks", arr);
	}
}