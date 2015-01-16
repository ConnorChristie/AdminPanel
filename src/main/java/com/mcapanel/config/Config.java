package com.mcapanel.config;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

import com.mcapanel.panel.AdminPanelWrapper;

public class Config
{
	private File configFile;
    
    private Map<String, String> configDefaults = new HashMap<String, String>();
    private Properties propertiesFile = new Properties();
    
    public Config()
    {
        configFile = new File("McAdminPanel", "mcadminpanel.properties");
        
        configDefaults.put("server-ip", "localhost");
        configDefaults.put("web-port", "80");
        configDefaults.put("language", "english");
        
        configDefaults.put("installed", "false");
        configDefaults.put("enable-whitelist", "true");
        configDefaults.put("restart-on-error", "true");
        configDefaults.put("auto-restart", "true");
        
        loadConfig();
        copyDefaults();
        saveConfig();
    }
    
    public void copyDefaults()
    {
    	boolean copy = false;
    	
        for (String key : configDefaults.keySet())
        {
            Object value = configDefaults.get(key);
            
            if (!propertiesFile.containsKey(key))
            {
            	copy = true;
            	
                propertiesFile.put(key, value);
            }
        }
        
        if (copy) System.out.println("Copying default config...");
    }
    
    public void loadConfig()
    {
        try
        {
            configFile.createNewFile();
            
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
        try {
            configFile.createNewFile();
            
            if (configFile.exists())
            {
                propertiesFile.store(new FileOutputStream(configFile), "McAdminPanel properties");
                
                loadConfig();
                
                if (AdminPanelWrapper.getInstance().getTinyUrl() != null)
                	AdminPanelWrapper.getInstance().getTinyUrl().getHelper().df = true;
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
    
    public boolean getBoolean(String key, boolean def)
    {
    	if (propertiesFile.containsKey(key))
    		return getValue(key).equalsIgnoreCase("true");
    	
    	return def;
    }
    
    public int getInt(String key, int def)
    {
    	if (propertiesFile.containsKey(key))
    	{
    		try
    		{
    			return Integer.parseInt(getValue(key));
    		} catch (NumberFormatException e) { }
    	}
    	
    	return def;
    }
}