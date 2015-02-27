package com.mcapanel.language;

import java.io.IOException;
import java.io.InputStream;
import java.util.regex.Pattern;

import org.apache.commons.io.IOUtils;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

public class Language
{
	private String language = "english";
	private JSONObject jsonFile;
	
	public Language(String language)
	{
		try
		{
			InputStream is = getClass().getResourceAsStream("/languages.yml");
			String jsonTxt = IOUtils.toString(is, "iso-8859-1");
			
			jsonFile = (JSONObject) new JSONParser().parse(jsonTxt);
		} catch (IOException e)
		{
			e.printStackTrace();
		} catch (ParseException e)
		{
			e.printStackTrace();
		}
		
		setLanguage(language);
	}
	
	public String localize(String base)
	{
		if (language.equalsIgnoreCase("english")) return base;
		
		Object oString = getTranslated(base);
		
		if (oString == null) return base;
		
		return oString.toString();
	}
	
	public String localize(String base, String... args)
	{
		if (language.equalsIgnoreCase("english"))
		{
			if (base.contains("$$"))
			{
				for (String arg : args)
				{
					base = base.replaceFirst(Pattern.quote("$$"), arg);
				}
			}
			
			return base;
		}
		
		Object oString = getTranslated(base);
		
		if (oString == null) return base;
		
		String retStr = oString.toString();
		
		if (retStr.contains("$$"))
		{
			for (String arg : args)
			{
				retStr = retStr.replaceFirst(Pattern.quote("$$"), arg);
			}
		}
		
		return retStr;
	}
	
	private Object getTranslated(String str)
	{
		try
		{
			InputStream is = getClass().getResourceAsStream("/languages.yml");
			String jsonTxt = IOUtils.toString(is, "iso-8859-1");
			
			jsonFile = (JSONObject) new JSONParser().parse(jsonTxt);
		} catch (IOException e)
		{
			e.printStackTrace();
		} catch (ParseException e)
		{
			e.printStackTrace();
		}
		
		return ((JSONObject) jsonFile.get(language)).get(str);
	}
	
	public void setLanguage(String lang)
	{
		language = lang.toLowerCase();
	}
	
	public String getLanguage()
	{
		return language;
	}
}