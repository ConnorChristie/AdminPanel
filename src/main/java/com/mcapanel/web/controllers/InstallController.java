package com.mcapanel.web.controllers;

import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.RandomStringUtils;
import org.json.simple.JSONObject;

import com.mcapanel.bukkit.BukkitServer;
import com.mcapanel.bukkit.BukkitVersion;
import com.mcapanel.panel.AdminPanelWrapper;
import com.mcapanel.web.database.Group;
import com.mcapanel.web.database.Server;
import com.mcapanel.web.database.User;
import com.mcapanel.web.handlers.Controller;

public class InstallController extends Controller
{
	public boolean canView()
	{
		return !ap.getConfig().getBoolean("installed", false);
	}
	
	public boolean index()
	{
		request.setAttribute("tabs", getTabs());
		request.setAttribute("install", true);
		
		return renderView();
	}
	
	@SuppressWarnings("unchecked")
	public boolean step() throws IOException
	{
		if (isMethod("POST"))
		{
			includeIndex(false);
			mimeType("application/json");
			
			boolean samePort = true;
			JSONObject out = new JSONObject();
			
			if (!config.getBoolean("installed", false))
			{
				String step = request.getParameter("step");
				
				if (step != null)
				{
					if (step.equals("1"))
					{
						String serverIp = request.getParameter("serverip");
						String webPort = request.getParameter("webport");
						
						samePort = (config.getString("web-port", "80").equals(webPort));
						
						config.setValue("server-ip", serverIp);
						config.setValue("web-port", webPort);
						config.saveConfig();
						
						request.getSession().setAttribute("step", "2");
						
						if (!samePort)
						{
							new Thread(new Runnable() {
								public void run()
								{
									try
									{
										Thread.sleep(1000);
										
										ap.restartWebServer();
									} catch (InterruptedException e) {}
								}
							}).start();
							
							out.put("restart", language.localize("Since the port you entered is different, we have to restart the webserver. A new page will open automatically, if not check the console."));
						} else
						{
							out.put("good", "good");
						}
					} else if (step.equals("2"))
					{
						String cbName = request.getParameter("cbname");
						String cbFile = request.getParameter("cbfile");
						String cbInstall = request.getParameter("cbinstall");
						String override = request.getParameter("override");
						
						final BukkitVersion bv = BukkitVersion.getVersion(cbInstall);
						
						if (bv != null)
						{
							new Thread(new Runnable() {
								public void run()
								{
									try
									{
										System.out.println("Downloading CraftBukkit...");
										
										File cbFile = new File("craftbukkit.jar");
										
										FileUtils.copyURLToFile(new URL(bv.getUrl()), cbFile);
										
										config.setValue("server-jar", cbFile.getAbsolutePath());
										config.saveConfig();
										
										System.out.println("Done downloading CraftBukkit!");
									} catch (MalformedURLException e)
									{
										e.printStackTrace();
									} catch (IOException e)
									{
										e.printStackTrace();
									}
								}
							}).start();
						}
						
						File f = new File(cbFile);
						
						if (bv == null && (f == null || (f != null && !f.exists())) && (override == null || !override.equals("true")))
						{
							out.put("dialog", language.localize("It seems the server jar file you entered does not exist, do you want to proceed anyway?"));
						} else
						{
							Server server = new Server(cbName, cbFile);
							db.save(server);
							
							BukkitServer bukkitServer = new BukkitServer(server);
							AdminPanelWrapper.getInstance().servers.put(server.getId(), bukkitServer);
							
							request.getSession().setAttribute("chosenServer", server.getId());
							bukkitServer.setupBackups();
							
							request.getSession().setAttribute("step", "3");
							
							out.put("good", "good");
						}
					} else if (step.equals("3"))
					{
						String mcname = request.getParameter("mcname");
						String mcpass = request.getParameter("mcpass");
						String mcpassconf = request.getParameter("mcpassconf");
						
						if (mcpass.equals(mcpassconf))
						{
							User u = new User(mcname, mcpass, RandomStringUtils.randomAlphanumeric(8), request.getRemoteAddr().equals("0:0:0:0:0:0:0:1") ? "127.0.0.1" : request.getRemoteAddr());
							
							u.setGroupId(db.find(Group.class).where().ieq("group_name", "Admin").findUnique().getId());
							u.setWhitelisted(true);
							
							db.save(u);
							
							loginUser(u);
							
							request.getSession().setAttribute("step", "4");
							
							out.put("good", language.localize("Successfully saved all installation settings.%sIt is recommended to create another user so you can see what they see.", "<br />"));
						} else
							out.put("error", language.localize("The passwords that you entered do not match."));
					} else if (step.equals("4"))
					{
						String licemail = request.getParameter("licemail");
						String lickey = request.getParameter("lickey");
						
						config.setValue("installed", "true");
						config.setValue("license-email", licemail);
						config.setValue("license-key", lickey);
						config.saveConfig();
						
						out.put("good", "good");
					}
				}
			} else
				out.put("error", language.localize("McAdminPanel is already installed."));
			
			response.getWriter().println(out.toJSONString());
			
			return true;
		}
		
		return error();
	}
	
	public boolean stepUpdate()
	{
		if (isMethod("POST"))
		{
			includeIndex(false);
			mimeType("application/json");
			
			String step = request.getParameter("step");
			
			if (step != null && !step.isEmpty())
			{
				request.getSession().setAttribute("step", step);
			}
			
			return true;
		}
		
		return error();
	}
	
	public boolean skipStep() throws IOException
	{
		if (isMethod("POST"))
		{
			includeIndex(false);
			mimeType("text/plain");
			
			config.setValue("installed", "true");
			config.setValue("license-email", "");
			config.setValue("license-key", "");
			config.saveConfig();
			
			response.getWriter().println("good");
			
			return true;
		}
		
		return error();
	}
	
	private List<String> getTabs()
	{
		List<String> tabs = new ArrayList<String>();
		
		tabs.add("install");
		
		return tabs;
	}
	
	@SuppressWarnings("unchecked")
	public boolean process() throws IOException
	{
		if (isMethod("POST"))
		{
			includeIndex(false);
			mimeType("application/json");
			
			boolean samePort = true;
			
			JSONObject out = new JSONObject();
			
			if (!config.getBoolean("installed", false))
			{
				String serverIp = request.getParameter("serverip");
				String webPort = request.getParameter("webport");
				
				String cbName = request.getParameter("cbname");
				String cbFile = request.getParameter("cbfile");
				String cbInstall = request.getParameter("cbinstall");
				
				String mcname = request.getParameter("mcname");
				String mcpass = request.getParameter("mcpass");
				String mcpassconf = request.getParameter("mcpassconf");
				
				String licemail = request.getParameter("licemail");
				String lickey = request.getParameter("lickey");
				
				if (mcpass.equals(mcpassconf))
				{
					User u = new User(mcname, mcpass, RandomStringUtils.randomAlphanumeric(8), request.getRemoteAddr().equals("0:0:0:0:0:0:0:1") ? "127.0.0.1" : request.getRemoteAddr());
					
					u.setGroupId(db.find(Group.class).where().ieq("group_name", "Admin").findUnique().getId());
					u.setWhitelisted(true);
					
					db.save(u);
					
					loginUser(u);
					
					Server server = new Server(cbName, cbFile);
					db.save(server);
					
					BukkitServer bukkitServer = new BukkitServer(server);
					AdminPanelWrapper.getInstance().servers.put(server.getId(), bukkitServer);
					
					request.getSession().setAttribute("chosenServer", server.getId());
					bukkitServer.setupBackups();
					
					samePort = (config.getString("web-port", "80").equals(webPort));
					
					config.setValue("installed", "true");
					config.setValue("server-ip", serverIp);
					config.setValue("web-port", webPort);
					
					config.setValue("license-email", licemail);
					config.setValue("license-key", lickey);
					
					config.saveConfig();
					
					final BukkitVersion bv = BukkitVersion.getVersion(cbInstall);
					
					if (bv != null)
					{
						new Thread(new Runnable() {
							public void run()
							{
								try
								{
									System.out.println("Downloading CraftBukkit...");
									
									File cbFile = new File("craftbukkit.jar");
									
									FileUtils.copyURLToFile(new URL(bv.getUrl()), cbFile);
									
									config.setValue("server-jar", cbFile.getAbsolutePath());
									config.saveConfig();
									
									System.out.println("Done downloading CraftBukkit!");
								} catch (MalformedURLException e)
								{
									e.printStackTrace();
								} catch (IOException e)
								{
									e.printStackTrace();
								}
							}
						}).start();
					}
					
					out.put("good", "Successfully saved all installation settings.<br />It is recommended to create another user so you can see what they see." + (!samePort ? "<br />Since you changed the web port, we are going to restart the panel." : ""));
					out.put("redirect", "http://localhost:" + webPort);
				} else
					out.put("error", "The passwords that you entered do not appear to match.");
			} else
				out.put("error", "You are not allowed to do that.");
			
			response.getWriter().println(out.toJSONString());
			
			if (!samePort)
			{
				ap.restartWebServer();
			}
			
			return true;
		}
		
		return error();
	}
}