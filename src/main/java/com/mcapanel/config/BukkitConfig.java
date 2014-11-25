package com.mcapanel.config;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Properties;

public class BukkitConfig
{
	private File configFile;
	
	private Properties propertiesFile = new Properties();
	
	public BukkitConfig(File serverJar)
	{
		configFile = new File(serverJar.getParent(), "server.properties");
		
		loadConfig();
	}
	
	public void loadConfig()
	{
        try
        {
            if (configFile.exists())
            {
                propertiesFile.load(new FileInputStream(configFile));
            }
        } catch (IOException ex)
        {
            ex.printStackTrace();
        }
	}
	
	public void saveConfig()
	{
		try
		{
            if (configFile.exists())
            {
                propertiesFile.store(new FileOutputStream(configFile), "Minecraft server properties");
                
                loadConfig();
            }
        } catch (IOException ex) {
        	ex.printStackTrace();
        }
	}
	
	public String getValue(String key)
    {
        return (String) propertiesFile.get(key);
    }
    
    public String getString(String key, String def)
    {
    	if (propertiesFile.containsKey(key))
    		return getValue(key);
    	
    	return def;
    }
    
    public void setValue(String key, String value)
    {
    	value = value == null ? "" : value;
    	
        propertiesFile.put(key, value);
    }
}