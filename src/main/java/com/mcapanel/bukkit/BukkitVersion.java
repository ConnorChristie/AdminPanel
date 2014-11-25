package com.mcapanel.bukkit;

public enum BukkitVersion
{
	RECOMMENDED("rb", "http://dl.bukkit.org/latest-rb/craftbukkit.jar"),
	BETA("beta", "http://dl.bukkit.org/latest-beta/craftbukkit.jar"),
	DEVELOPMENT("dev", "http://dl.bukkit.org/latest-dev/craftbukkit.jar");
	
	private String mod = "rb";
	private String url = "http://dl.bukkit.org/latest-rb/craftbukkit.jar";
	
	private BukkitVersion(String mod, String url)
	{
		this.mod = mod;
		this.url = url;
	}
	
	public String getUrl()
	{
		return url;
	}
	
	public static BukkitVersion getVersion(String mod)
	{
		for (BukkitVersion ver : BukkitVersion.values())
		{
			if (ver.mod.equals(mod)) return ver;
		}
		
		return null;
	}
}