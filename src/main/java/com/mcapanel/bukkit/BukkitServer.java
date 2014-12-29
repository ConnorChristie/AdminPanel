package com.mcapanel.bukkit;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.PrintStream;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

import org.apache.commons.codec.digest.DigestUtils;
import org.apache.commons.io.FileUtils;

import com.mcapanel.backup.BackupHandler;
import com.mcapanel.config.BukkitConfig;
import com.mcapanel.panel.AdminPanelWrapper;
import com.mcapanel.panel.ServerStatus;
import com.mcapanel.plugin.PluginConnector;
import com.mcapanel.web.database.Server;

public class BukkitServer
{
	private AdminPanelWrapper ap;
	
	private Long serverId;
	private boolean consoleFocus;
	
	private File serverJar;
	private String serverName;
	
	private String minMemory = "1024m";
	private String maxMemory = "4096m";
	
	private BukkitConfig bukkitConfig;
	private ServerStatus serverStatus;
	
	private Process process;
	private ProcessBuilder processBuilder;
	
	private BackupHandler backupHandler;
	private PluginConnector pluginConnector;
	
	private OutputStream writer;
	private BufferedReader outReader;
	private BufferedReader errReader;
	
	private List<String> console = new ArrayList<String>();
	private ReadWriteLock consoleLock = new ReentrantReadWriteLock();
	
	private boolean hasDynmap;
	
	public BukkitServer(Server server)
	{
		this(server.getId(), server.getName(), new File(server.getServerJar()), server.getMinMemory(), server.getMaxMemory());
	}
	
	private BukkitServer(Long id, String serverName, File serverJar, String minMemory, String maxMemory)
	{
		this.ap = AdminPanelWrapper.getInstance();
		
		this.serverId = id;
		this.consoleFocus = false;
		
		this.serverJar = serverJar;
		this.serverName = serverName;
		
		this.minMemory = minMemory;
		this.maxMemory = maxMemory;
		
		this.bukkitConfig = new BukkitConfig(serverJar);
		this.serverStatus = ServerStatus.STOPPED;
		
		pluginConnector = new PluginConnector(this);
		
		processBuilder = new ProcessBuilder(new String[] {
			"java",
			"-Djline.terminal=jline.UnsupportedTerminal",
			"-Xms" + minMemory,
			"-Xmx" + maxMemory,
			"-jar", serverJar.getAbsolutePath()
		});
		
		processBuilder.directory(serverJar.getParentFile());
		
		copyPlugin();
	}
	
	public Long getId()
	{
		return serverId;
	}
	
	public void installServer()
	{
		if (serverJar != null && serverJar.exists())
		{
			copyPlugin();
			
			setupBackups();
		}
	}
	
	public void startServer()
	{
		if (serverStatus == ServerStatus.STOPPED || serverStatus == ServerStatus.RESTARTING)
		{
			System.out.println("Starting " + serverName + " server...");
			
			if (serverStatus == ServerStatus.STOPPED && serverStatus != ServerStatus.RESTARTING)
				serverStatus = ServerStatus.STARTING;
			
			try
			{
				process = processBuilder.start();
				
				writer = new PrintStream(process.getOutputStream());
				
				outReader = new BufferedReader(new InputStreamReader(process.getInputStream()));
				errReader = new BufferedReader(new InputStreamReader(process.getErrorStream()));
				
				new Thread(new BukkitConsoleReader(false)).start();
				new Thread(new BukkitConsoleReader(true)).start();
			} catch (IOException ex)
			{
				System.out.println("Error starting server... Please try again.");
				
				serverStatus = ServerStatus.STOPPED;
			}
		}
	}
	
	public void stopServer(final boolean restart)
	{
		new Thread(new Runnable()
		{
			public void run()
			{
				stopServerRaw(restart);
			}
		}).start();
	}
	
	public void stopServerRaw(boolean restart)
	{
		if (serverStatus == ServerStatus.STARTED || serverStatus == ServerStatus.RESTARTING)
		{
			if (!restart)
				serverStatus = ServerStatus.STOPPING;
			
			pluginConnector.setConnected(false);
			
			try
			{
				writer.write("stop\n".getBytes());
				writer.flush();
				
				process.waitFor();
				
				writer.close();
				
				outReader.close();
				errReader.close();
				
				if (restart)
				{
					startServer();
				} else
				{
					serverStatus = ServerStatus.STOPPED;
				}
			} catch (Exception e)
			{
				e.printStackTrace();
				
				serverStatus = ServerStatus.STOPPED;
			}
		}
	}
	
	public void reloadServer()
	{
		if (serverStatus == ServerStatus.STARTED)
		{
			serverStatus = ServerStatus.RELOADING;
			
			try
			{
				writer.write("reload\n".getBytes());
				writer.flush();
			} catch (IOException e)
			{
				e.printStackTrace();
				
				serverStatus = ServerStatus.STOPPED;
			}
		}
	}
	
	public void restartServer()
	{
		serverStatus = ServerStatus.RESTARTING;
		
		stopServerRaw(true);
	}
	
	public void setConsoleFocus(boolean consoleFocus)
	{
		this.consoleFocus = consoleFocus;
	}
	
	public boolean hasConsoleFocus()
	{
		return consoleFocus;
	}
	
	public ServerStatus getStatus()
	{
		return serverStatus;
	}
	
	public void setStatus(ServerStatus serverStatus)
	{
		this.serverStatus = serverStatus;
	}
	
	public PluginConnector getPluginConnector()
	{
		return pluginConnector;
	}
	
	public OutputStream getWriter()
	{
		return writer;
	}
	
	public void setName(String name)
	{
		this.serverName = name;
	}
	
	public String getName()
	{
		return serverName;
	}
	
	public void setMinMemory(String minMemory)
	{
		this.minMemory = minMemory;
	}
	
	public String getMinMemory()
	{
		return minMemory;
	}
	
	public void setMaxMemory(String maxMemory)
	{
		this.maxMemory = maxMemory;
	}
	
	public String getMaxMemory()
	{
		return maxMemory;
	}
	
	public void setServerJar(String jar)
	{
		serverJar = new File(jar);
		
		processBuilder = new ProcessBuilder(new String[] {
			"java",
			"-Djline.terminal=jline.UnsupportedTerminal",
			"-Xms" + minMemory,
			"-Xmx" + maxMemory,
			"-jar", serverJar.getAbsolutePath()
		});
		
		processBuilder.directory(serverJar.getParentFile());
		
		bukkitConfig = new BukkitConfig(serverJar);
	}
	
	public File getServerJar()
	{
		return serverJar;
	}
	
	public List<String> getConsole()
	{
		return console;
	}
	
	public ReadWriteLock getConsoleLock()
	{
		return consoleLock;
	}
	
	public BackupHandler getBackupHandler()
	{
		return backupHandler;
	}
	
	public BukkitConfig getConfig()
	{
		return bukkitConfig;
	}
	
	private void println(String line)
	{
		if (consoleFocus || !consoleFocus)
		{
			System.err.print("[" + getName() + "] " + line);
			System.err.flush();
		}
	}
	
	private class BukkitConsoleReader implements Runnable
	{
		private boolean isErr = false;
		
		public BukkitConsoleReader(boolean isErr)
		{
			this.isErr = isErr;
		}
		
		@Override
		public void run()
		{
			try
			{
				BufferedReader reader;
				
				if (isErr)
					reader = errReader;
				else
					reader = outReader;
				
				StringBuffer sb = new StringBuffer();
				
				int read;
				
				while ((read = reader.read()) != -1)
				{
					char c = (char) read;
					
					if (c == '\n')
					{
						sb.append(c);
						
						String line = sb.toString();
						
						if (!pluginConnector.listen(line))
						{
							line = line.replaceAll("[ ]{5,}", "");
							
							println(line);
							
							final Lock wLock = consoleLock.writeLock();
							wLock.lock();
							
							try
							{
								console.add(line);
							} finally
							{
								wLock.unlock();
							}
						}
						
						if (line.contains("SEVERE]") && ap.getConfig().getBoolean("restart-on-error", true))
						{
							new Thread(new Runnable() {
								public void run()
								{
									try
									{
										process.waitFor();
										
										startServer();
									} catch (InterruptedException e) { }
								}
							}).start();
							
							new Thread(new Runnable() {
								public void run()
								{
									try
									{
										wait(5000);
									} catch (InterruptedException e)
									{
										e.printStackTrace();
									}
									
									try
									{
										process.exitValue();
									} catch (IllegalThreadStateException e)
									{
										process.destroy();
									}
								}
							}).start();
						}
						
						if ((line.contains("Done") && line.contains("For help, type \"help\" or \"?\"")) || line.contains("CONSOLE: Reload complete."))
						{
							serverStatus = ServerStatus.STARTED;
							
							if (!pluginConnector.connected())
							{
								System.out.println("The McAdminPanel Plugin did not respond, try restarting McAdminPanel");
							}
						} else if (line.contains("Stopping server") && serverStatus != ServerStatus.STOPPING && serverStatus != ServerStatus.RESTARTING)
						{
							serverStatus = ServerStatus.STOPPED;
						} else if (line.contains("**** FAILED TO BIND TO PORT!"))
						{
							serverStatus = ServerStatus.STOPPED;
						} else if (line.contains("Unable to access jarfile"))
						{
							serverStatus = ServerStatus.STOPPED;
						}
						
						if (line.contains("[dynmap] Enabled"))
						{
							hasDynmap = true;
						}
						
						sb.setLength(0);
					} else if (read != 8)
					{
						sb.append(c);
					}
				}
			} catch (Exception ex)
			{
				ex.printStackTrace();
			}
		}
	}
	
	private void copyPlugin()
	{
		copyPlugin(false);
	}
	
	private void copyPlugin(boolean force)
	{
		File pluginFile = new File(serverJar.getParentFile(), "plugins/McAdminPanelPlugin.jar");
		
		if (pluginFile == null || (pluginFile != null && !pluginFile.exists()) || force)
		{
			try
			{
				FileUtils.copyInputStreamToFile(getClass().getResourceAsStream("/plugin/McAdminPanelPlugin.jar"), pluginFile);
			} catch (Exception e)
			{
				System.out.println("Could not copy the McAdminPanel Plugin to the plugins folder...");
			}
		} else if (pluginFile != null && pluginFile.exists())
		{
			try
			{
				InputStream fis = getClass().getResourceAsStream("/plugin/McAdminPanelPlugin.jar");
				String md5 = DigestUtils.md5Hex(fis);
				
				fis.close();
				
				FileInputStream ifis = new FileInputStream(pluginFile);
				String imd5 = DigestUtils.md5Hex(ifis);
				
				ifis.close();
				
				if (!md5.equals(imd5))
				{
					copyPlugin(true);
					
					System.out.println("Copying updated plugin...");
				}
			} catch (IOException e)
			{
				e.printStackTrace();
			}
		}
	}
	
	public void setupBackups()
	{
		if (serverJar != null && serverJar.exists())
		{
			backupHandler = new BackupHandler(this);
			backupHandler.runSchedules();
		}
	}
	
	public List<File> getWorlds()
	{
		List<File> worlds = new ArrayList<File>();
		
		if (serverJar != null && serverJar.exists())
		{
			for (File f : serverJar.getParentFile().listFiles())
			{
				if (f.isDirectory())
				{
					File file = new File(f, "level.dat");
					
					if (file.exists())
					{
						worlds.add(f);
					}
				}
			}
		}
		
		return worlds;
	}
	
	public boolean hasDynmap()
	{
		return hasDynmap;
	}
}