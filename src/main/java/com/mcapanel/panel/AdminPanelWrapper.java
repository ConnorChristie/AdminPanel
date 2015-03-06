package com.mcapanel.panel;

import java.awt.Desktop;
import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.PrintStream;
import java.net.BindException;
import java.net.URI;
import java.net.URL;
import java.net.URLClassLoader;
import java.nio.charset.Charset;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Timer;
import java.util.UUID;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.logging.ConsoleHandler;
import java.util.logging.Formatter;
import java.util.logging.Handler;
import java.util.logging.Level;
import java.util.logging.LogRecord;
import java.util.logging.Logger;

import javax.persistence.Entity;
import javax.persistence.PersistenceException;

import org.apache.jasper.servlet.JspServlet;
import org.apache.tomcat.InstanceManager;
import org.apache.tomcat.SimpleInstanceManager;
import org.eclipse.jetty.annotations.ServletContainerInitializersStarter;
import org.eclipse.jetty.apache.jsp.JettyJasperInitializer;
import org.eclipse.jetty.plus.annotation.ContainerInitializer;
import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.server.handler.HandlerCollection;
import org.eclipse.jetty.server.session.HashSessionManager;
import org.eclipse.jetty.server.session.SessionHandler;
import org.eclipse.jetty.servlet.DefaultServlet;
import org.eclipse.jetty.servlet.ServletHolder;
import org.eclipse.jetty.webapp.WebAppContext;
import org.reflections.Reflections;

import com.avaje.ebean.EbeanServer;
import com.avaje.ebean.EbeanServerFactory;
import com.avaje.ebean.ExpressionList;
import com.avaje.ebean.config.DataSourceConfig;
import com.avaje.ebean.config.ServerConfig;
import com.avaje.ebean.config.dbplatform.SQLitePlatform;
import com.avaje.ebeaninternal.api.SpiEbeanServer;
import com.avaje.ebeaninternal.server.ddl.CreateTableVisitor;
import com.avaje.ebeaninternal.server.ddl.DdlGenerator;
import com.avaje.ebeaninternal.server.deploy.BeanDescriptorManager;
import com.avaje.ebeaninternal.server.deploy.parse.DeployCreateProperties;
import com.avaje.ebeaninternal.server.lib.sql.DataSourcePool;
import com.avaje.ebeaninternal.server.lib.sql.PooledConnection;
import com.avaje.ebeaninternal.server.lib.sql.PooledConnectionQueue;
import com.avaje.ebeaninternal.server.lib.sql.TransactionIsolation;
import com.avaje.ebeaninternal.server.subclass.SubClassManager;
import com.mcapanel.bukkit.BukkitServer;
import com.mcapanel.bukkit.utils.UUIDFetcher;
import com.mcapanel.config.Config;
import com.mcapanel.language.Language;
import com.mcapanel.log.NoLogging;
import com.mcapanel.utils.TinyUrl;
import com.mcapanel.utils.UsageMonitor;
import com.mcapanel.utils.Utils;
import com.mcapanel.web.database.Group;
import com.mcapanel.web.database.User;
import com.mcapanel.web.handlers.ControllerHandler;
import com.mcapanel.web.handlers.MultipartConfigInjectionHandler;
import com.mcapanel.web.servlets.AppServlet;

public class AdminPanelWrapper
{
	public static final String VERSION = "v1.0.8";
	public static String VERSION_SUB = "";
	
	private Config config;
	private UsageMonitor usageMonitor;
	
	private Thread shutdownHook;
	private TinyUrl tinyUrl;
	
	public Map<Integer, BukkitServer> servers = new HashMap<Integer, BukkitServer>();
	
	private Language language;
	private EbeanServer db;
	
	private Server webServer;
	private static AdminPanelWrapper instance;
	
	private final Timer timer = new Timer("Usage Thread", false);
	
	public InitialEvent initialEvent;
	public EverythingEvent everythingEvent;
	
	public static final BlockingQueue<Runnable> queue = new LinkedBlockingQueue<Runnable>();
	
	public static void main(String[] args) throws Exception
	{
		new AdminPanelWrapper();
	}
	
	public AdminPanelWrapper() throws Exception
	{
		instance = this;
		
		new File("McAdminPanel").mkdirs();
		
		setupLogger();
		
		config = new Config();
		language = new Language(config.getString("language", "english"));
		
		System.out.println(language.localize("Starting McAdminPanel") + " " + VERSION + "...");
		
		setupConfig();
		setupDatabases();
		
		startUsages();
		
		System.out.println(language.localize("Loading servers") + "...");
		System.out.println(language.localize("Loading backups") + "...");
		
		List<com.mcapanel.web.database.Server> servs = db.find(com.mcapanel.web.database.Server.class).findList();
		
		for (com.mcapanel.web.database.Server serv : servs)
		{
			BukkitServer bukkitServer = new BukkitServer(serv);
			
			servers.put(serv.getId(), bukkitServer);
			
			bukkitServer.setupBackups();
		}
		
		everythingEvent = new EverythingEvent();
		everythingEvent.start();
		
		initialEvent = new InitialEvent();
		initialEvent.start();
		
		if (!config.getBoolean("installed", false))
		{
			/*
			Scanner in = new Scanner(System.in);
			
	        Logger.getLogger(getClass().getName()).info(language.localize("Enter Port") + ": ");
	        
	        String s = in.next();
	        
	        config.setValue("web-port", s);
	        config.saveConfig();
	        */
			
			config.setValue("web-port", "80");
	        config.saveConfig();
		}
		
		startWebPanel();
		setShutdownHook();
		
		new Thread(new InputReader()).start();
		
		while (true)
		{
			queue.take().run();
		}
	}
	
	private void setupLogger()
	{
		final Logger logger = Logger.getLogger(getClass().getName());
		
		Handler handler = new ConsoleHandler();
		
		handler.setFormatter(new Formatter() {
			public String format(LogRecord record)
			{
				SimpleDateFormat sdf = new SimpleDateFormat("HH:mm:ss");
				
				return "[" + sdf.format(new Date(record.getMillis())) + " " + record.getLevel() + "]: " + record.getMessage();
			}
		});
		
		logger.setUseParentHandlers(false);
		logger.addHandler(handler);
		
		System.setOut(new PrintStream(new OutputStream()
		{
			private StringBuffer buffer = new StringBuffer();
			
			public void write(int b) throws IOException
			{
				buffer.append((char) b);
				
				if ((char) b == '\n')
				{
					logger.info(buffer.toString());
					buffer.setLength(0);
				}
			}
		}));
	}
	
	public static void executeMain(Runnable run)
	{
		queue.add(run);
	}
	
	public void restartWebServer()
	{
		new Thread(new Runnable() {
			public void run()
			{
				try
				{
					webServer.stop();
					
					startWebPanel();
				} catch (Exception e)
				{
					e.printStackTrace();
				}
			}
		}).start();
	}
	
	public BukkitServer getServer(int id)
	{
		return servers.get(id);
	}
	
	public Collection<BukkitServer> getServers()
	{
		return servers.values();
	}
	
	public boolean hasServer(int id)
	{
		return servers.containsKey(id);
	}
	
	public void deleteServer(int id)
	{
		if (hasServer(id))
		{
			final BukkitServer serv = getServer(id);
			
			servers.remove(id);
			
			serv.stopServer(false);
			
			db.delete(com.mcapanel.web.database.Server.class, id);
		}
	}
	
	private void setupConfig()
	{
		System.out.println(language.localize("Loading config file") + "...");
		
		tinyUrl = new TinyUrl();
		
		File webPages = new File("McAdminPanel", "webpages/");
		webPages.mkdirs();
		
		if (webPages.isDirectory() && webPages.list().length == 0)
		{
			try
			{
				Utils.copyResourcesToDirectory(Utils.jarForClass(getClass(), null), "webpages", "McAdminPanel/webpages");
			} catch (Exception e)
			{
				System.out.println("Could not copy the webpages folder over...");
			}
		}
		
		//updateServerJar();
	}
	
	public Config getConfig()
	{
		return config;
	}
	
	public TinyUrl getTinyUrl()
	{
		return tinyUrl;
	}
	
	public String getServerUrl()
	{
		return config.getString("server-ip", "localhost") + ":" + config.getString("web-port", "80");
	}
	
	private void startUsages()
	{
		usageMonitor = new UsageMonitor();
		
		timer.scheduleAtFixedRate(usageMonitor, 0L, 750L);
	}
	
	public UsageMonitor getUsages()
	{
		return usageMonitor;
	}
	
	private void setupDatabases()
	{
		System.out.println(language.localize("Loading databases") + "...");
		
		//Disable annoying log messages...
		Logger.getLogger(DdlGenerator.class.getName()).setLevel(Level.WARNING);
		Logger.getLogger(DataSourcePool.class.getName()).setLevel(Level.WARNING);
		Logger.getLogger(SubClassManager.class.getName()).setLevel(Level.WARNING);
		Logger.getLogger(PooledConnection.class.getName()).setLevel(Level.WARNING);
		Logger.getLogger(CreateTableVisitor.class.getName()).setLevel(Level.SEVERE);
		Logger.getLogger(BeanDescriptorManager.class.getName()).setLevel(Level.WARNING);
		Logger.getLogger(PooledConnectionQueue.class.getName()).setLevel(Level.WARNING);
		Logger.getLogger(DeployCreateProperties.class.getName()).setLevel(Level.WARNING);
		Logger.getLogger("com.avaje.ebean.config.PropertyMapLoader").setLevel(Level.WARNING);
		
		ServerConfig serverConfig = new ServerConfig();
		
		serverConfig.setDefaultServer(false);
		serverConfig.setRegister(false);
		serverConfig.setClasses(getDatabaseClasses());
		serverConfig.setName("McAdminPanel");
		
		DataSourceConfig sourceConfig = new DataSourceConfig();
		
		sourceConfig.setDriver("org.sqlite.JDBC");
		sourceConfig.setUrl("jdbc:sqlite:McAdminPanel/McAdminPanel.db");
		sourceConfig.setUsername("adminpanel");
		sourceConfig.setPassword("SeaFishRawr");
		sourceConfig.setMaxInactiveTimeSecs(10);
		sourceConfig.setMaxConnections(100);
		sourceConfig.setIsolationLevel(TransactionIsolation.getLevel("SERIALIZABLE"));
		
		if (sourceConfig.getDriver().contains("sqlite"))
		{
			serverConfig.setDatabasePlatform(new SQLitePlatform());
			serverConfig.getDatabasePlatform().getDbDdlSyntax().setIdentity("");
		}
		
		serverConfig.setDataSourceConfig(sourceConfig);
		
		PrintStream out = System.out;
		System.setOut(new PrintStream(new OutputStream() { public void write(int b) throws IOException { } }));
		
		db = EbeanServerFactory.create(serverConfig);
		
		System.setOut(out);
		
		int errorCount = 0;
		
		for (Class<?> c : getDatabaseClasses())
		{
			try
			{
				getDatabase().find(c).findRowCount();
			} catch (PersistenceException e)
			{
				errorCount++;
			}
		}
		
		if (errorCount == 1)
		{
			SpiEbeanServer serv = (SpiEbeanServer) getDatabase();
			DdlGenerator gen = serv.getDdlGenerator();
			
			String resetCodes = "CREATE TABLE resetcodes (id integer primary key, username varchar(255), reset_code varchar(255));";
			
			gen.runScript(true, resetCodes);
		} else if (errorCount > 1)
		{
			System.out.println("Initializing database...");
			
			installDDL();
			
			Group g = new Group("Global");
			g.setPermissions("server.chat.view;server.players.view;");
			getDatabase().save(g);
			
			g = new Group("Ghost");
			g.setGhost(true);
			g.setWhitelistDefault(true);
			g.setPermissions("server.chat.view;server.players.view;");
			getDatabase().save(g);
			
			g = new Group("Player");
			g.setExistingDefault(true);
			g.setPermissions("server.chat.view;server.chat.issue;server.players.view;");
			getDatabase().save(g);
			
			g = new Group("Moderator");
			g.setPermissions("server.chat.view;server.chat.issue;server.console.view;server.reload;server.properties.view;server.whitelist.view;server.whitelist.edit;server.players.view;server.players.healfeed;server.players.kill;server.players.kick;server.players.ban;server.plugins.view;server.plugins.edit;server.backups.view;server.backups.schedule.issue;web.users.view;web.users.whiteblack;web.groups.view;web.messages.view;web.messages.respond;mcapanel.properties.view;");
			getDatabase().save(g);
			
			g = new Group("Admin");
			g.setPermissions("server.chat.view;server.chat.issue;server.console.view;server.console.issue;server.controls;server.reload;server.usage;server.properties.view;server.properties.edit;server.properties.add;server.whitelist.view;server.whitelist.edit;server.players.view;server.players.healfeed;server.players.kill;server.players.kick;server.plugins.view;server.plugins.control;server.plugins.edit;server.plugins.install;server.plugins.delete;server.backups.view;server.backups.schedule.issue;server.backups.schedule.delete;server.backups.restore;server.backups.delete;web.users.view;web.users.group;web.users.whiteblack;web.users.delete;web.groups.view;web.groups.edit;web.groups.permissions;web.groups.delete;web.messages.view;web.messages.respond;mcapanel.properties.view;mcapanel.properties.edit;");
			getDatabase().save(g);
		}
		
		if (getDatabase().find(Group.class).where().ieq("group_name", "Global").findRowCount() == 0)
		{
			Group g = new Group("Global");
			g.setPermissions("server.chat.view;server.players.view;");
			getDatabase().save(g);
		}
		
		ExpressionList<User> users = getDatabase().find(User.class).where().eq("uuid", "");
		
		if (users.findRowCount() > 0)
		{
			final List<User> userList = users.findList();
			
			new Thread(new Runnable() {
				public void run()
				{
					try
					{
						List<String> userNames = new ArrayList<String>();
						
						for (User u : userList)
						{
							userNames.add(u.getUsername());
						}
						
						final Map<String, UUID> uuids = new UUIDFetcher(userNames).call();
						
						for (final User u : userList)
						{
							AdminPanelWrapper.executeMain(new Runnable() {
								public void run()
								{
									if (uuids.containsKey(u.getUsername()))
									{
										u.setUuid(uuids.get(u.getUsername()).toString() + "," + UUID.nameUUIDFromBytes(("OfflinePlayer:" + u.getUsername()).getBytes(Charset.forName("UTF-8"))).toString());
										
										getDatabase().save(u);
									}
								}
							});
						}
					} catch (Exception e) { e.printStackTrace(); }
				}
			}).start();
		}
	}
	
	private void installDDL()
	{
		SpiEbeanServer serv = (SpiEbeanServer) getDatabase();
		DdlGenerator gen = serv.getDdlGenerator();
		
		gen.runScript(true, gen.generateCreateDdl());
	}
	
	public List<Class<?>> getDatabaseClasses()
	{
		List<Class<?>> list = new ArrayList<Class<?>>();
		Reflections r = new Reflections("com.mcapanel.web.database");
		
		list.addAll(r.getTypesAnnotatedWith(Entity.class));
		
		return list;
	}
	
	public EbeanServer getDatabase()
	{
		return db;
	}
	
	public Group getGlobalGroup()
	{
		return getDatabase().find(Group.class).where().ieq("group_name", "Global").findUnique();
	}
	
	public User getUserFromPlayer(Object uuid)
	{
		return instance.getDatabase().find(User.class).where().contains("uuid", uuid.toString()).findUnique();
	}
	
	private void startWebPanel() throws Exception
	{
		System.out.println(language.localize("Starting web server on port $$", config.getString("web-port", "80")) + "...");
		
		ControllerHandler.loadControllers();
		
		org.eclipse.jetty.util.log.Log.setLog(new NoLogging());
		
		webServer = new Server(config.getInt("web-port", 80));
		
		File tmpDir = new File("McAdminPanel", "webtemp");
		
		if (!tmpDir.exists())
			tmpDir.mkdirs();
		
		tmpDir.deleteOnExit();
		
		URI baseUri = getClass().getResource("/webroot").toURI();
		
		System.setProperty("org.apache.jasper.compiler.disablejsr199", "false");
		
		WebAppContext context = new WebAppContext();
		
		context.setContextPath("/");
		context.setAttribute("javax.servlet.context.tempdir", tmpDir);
		context.setResourceBase(baseUri.toASCIIString());
		context.setAttribute(InstanceManager.class.getName(), new SimpleInstanceManager());
		
		HashSessionManager hsm = new HashSessionManager();
		
		hsm.getSessionCookieConfig().setName("session");
		hsm.setStoreDirectory(new File("McAdminPanel", "websessions"));
		hsm.setIdleSavePeriod(1);
		
		SessionHandler sh = new SessionHandler(hsm);
		context.setSessionHandler(sh);
		
		MultipartConfigInjectionHandler multipartConfigInjectionHandler = new MultipartConfigInjectionHandler();

		HandlerCollection collection = new HandlerCollection();
		collection.addHandler(context);

		multipartConfigInjectionHandler.setHandler(collection);
		
		webServer.setHandler(multipartConfigInjectionHandler);
		
		JettyJasperInitializer sci = new JettyJasperInitializer();
		ServletContainerInitializersStarter sciStarter = new ServletContainerInitializersStarter(context);
		
		ContainerInitializer initializer = new ContainerInitializer(sci, null);
		List<ContainerInitializer> initializers = new ArrayList<ContainerInitializer>();
		
		initializers.add(initializer);
		
		context.setAttribute("org.eclipse.jetty.containerInitializers", initializers);
		context.addBean(sciStarter, true);
		
		ClassLoader jspClassLoader = new URLClassLoader(new URL[0], this.getClass().getClassLoader());
		context.setClassLoader(jspClassLoader);
		
		ServletHolder holderJsp = new ServletHolder("jsp", JspServlet.class);
		
		holderJsp.setInitOrder(0);
		holderJsp.setInitParameter("fork", "false");
		holderJsp.setInitParameter("xpoweredBy", "false");
		holderJsp.setInitParameter("keepgenerated", "false");
		holderJsp.setInitParameter("compilerTargetVM", "1.6");
		holderJsp.setInitParameter("compilerSourceVM", "1.6");
		
		context.addServlet(holderJsp, "/index.jsp");
		context.addServlet(holderJsp, "/elements/*");
		context.addServlet(holderJsp, "/errors/*");
		context.addServlet(holderJsp, "/pages/*");
		
		ServletHolder holderDefault = new ServletHolder("default", DefaultServlet.class);
		
		holderDefault.setInitParameter("resourceBase", baseUri.toASCIIString());
		context.setInitParameter("org.eclipse.jetty.servlet.Default.dirAllowed", "false");
		
		context.addServlet(holderDefault, "/css/*");
		context.addServlet(holderDefault, "/js/*");
		context.addServlet(holderDefault, "/images/*");
		
		ServletHolder holderAppMapping = new ServletHolder("app", AppServlet.class);
		
		context.addServlet(holderAppMapping, "/*");
		
		try
		{
			webServer.start();
			
			if (!config.getBoolean("installed", false))
			{
				System.out.println(language.localize("Goto $$ in a browser to start the setup.", "http://localhost:" + config.getString("web-port", "80")));
				
				try
				{
					Desktop.getDesktop().browse(URI.create("http://localhost:" + config.getString("web-port", "80") + "/install/"));
				} catch (Exception e) { }
			}
		} catch (BindException e)
		{
			System.out.println(language.localize("McAdminPanel failed to bind to port $$", config.getString("web-port", "80")) + "...");
			
			System.exit(-1);
		}
	}
	
	public Language getLanguage()
	{
		return language;
	}
	
	private void setShutdownHook()
	{
		if (shutdownHook != null)
		{
			Runtime.getRuntime().removeShutdownHook(shutdownHook);
		}
		
		shutdownHook = new Thread(new Runnable()
		{
			@Override
			public void run()
			{
				//stopBukkitServerRaw(false);
				
				for (BukkitServer serv : servers.values())
				{
					serv.stopServer(false);
				}
				
				try
				{
					webServer.stop();
				} catch (Exception e) { }
			}
		});
		
		Runtime.getRuntime().addShutdownHook(shutdownHook);
	}
	
	private class InputReader implements Runnable
	{
		@Override
		public void run()
		{
			try
			{
				BufferedReader console = new BufferedReader(new InputStreamReader(System.in));
				
				String line;
				
				while ((line = console.readLine()) != null)
				{
					try
					{
						//Check if it's a command for us
						
						if (parseCommand(line))
							continue;
						
						for (BukkitServer serv : servers.values())
						{
							System.out.println("Server: " + serv.getName() + ", " + serv.hasConsoleFocus());
							
							if (serv.hasConsoleFocus() && serv.getWriter() != null)
							{
								serv.getWriter().write((line + "\n").getBytes());
								serv.getWriter().flush();
							}
						}
					} catch (Exception ex)
					{
						ex.printStackTrace();
					}
				}
			} catch (Exception ex)
			{
				ex.printStackTrace();
			}
		}
	}
	
	public boolean parseCommand(String cmd)
	{
		if (cmd.equalsIgnoreCase("start"))
		{
			getFocusedServer().startServer();
			
			return true;
		} else if (cmd.equalsIgnoreCase("restart"))
		{
			getFocusedServer().restartServer();
			
			return true;
		} else if (cmd.equalsIgnoreCase("stop-all"))
		{
			System.exit(0);
			
			return true;
		} else if (cmd.startsWith("server"))
		{
			String[] p = cmd.split(" ");
			String serv = "";
			
			for (int i = 1; i < p.length; i++)
				serv += p[i];
			
			for (BukkitServer s : servers.values())
			{
				if (s.getName().toLowerCase().contains(serv.toLowerCase()))
				{
					for (BukkitServer se : servers.values())
					{
						se.setConsoleFocus(false);
					}
					
					s.setConsoleFocus(true);
					
					System.out.println("Server: " + s.getName() + " is now outputting to this console.");
					
					break;
				}
			}
			
			return true;
		}
		
		return false;
	}
	
	private BukkitServer getFocusedServer()
	{
		for (BukkitServer serv : servers.values())
		{
			if (serv.hasConsoleFocus())
			{
				return serv;
			}
		}
		
		return null;
	}
	
	public static AdminPanelWrapper getInstance()
	{
		return instance;
	}
	
	public boolean inDev()
	{
	    if (System.getenv("eclipse42") == null)
	    {
	       return false;
	    }
	    
	    return true;
	}
	
	public String getVersion()
	{
		return VERSION + " " + VERSION_SUB;
	}
}