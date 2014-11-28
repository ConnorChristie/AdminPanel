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
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Timer;
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
import org.eclipse.jetty.server.session.HashSessionManager;
import org.eclipse.jetty.server.session.SessionHandler;
import org.eclipse.jetty.servlet.DefaultServlet;
import org.eclipse.jetty.servlet.ServletHolder;
import org.eclipse.jetty.webapp.WebAppContext;
import org.reflections.Reflections;

import com.avaje.ebean.EbeanServer;
import com.avaje.ebean.EbeanServerFactory;
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
import com.avaje.ebeaninternal.server.lib.sql.TransactionIsolation;
import com.avaje.ebeaninternal.server.subclass.SubClassManager;
import com.mcapanel.bukkit.BukkitServer;
import com.mcapanel.config.Config;
import com.mcapanel.log.NoLogging;
import com.mcapanel.utils.TinyUrl;
import com.mcapanel.utils.UsageMonitor;
import com.mcapanel.web.database.Group;
import com.mcapanel.web.database.User;
import com.mcapanel.web.handlers.ControllerHandler;
import com.mcapanel.web.servlets.AppServlet;

public class AdminPanelWrapper
{
	public static final String VERSION = "v1.0.1";
	public static String VERSION_SUB = "";
	
	private Config config;
	private UsageMonitor usageMonitor;
	
	//private Process bukkitProcess;
	private Thread shutdownHook;
	
	private TinyUrl tinyUrl;
	//private BackupHandler backupHandler;
	
	public Map<Integer, BukkitServer> servers = new HashMap<Integer, BukkitServer>();
	
	/*
	private File serverJar;
	private ServerStatus status = ServerStatus.STOPPED;
	*/
	
	private EbeanServer ebean;
	//private PluginConnector pluginConnector;
	
	//private OutputStream writer;
	//private List<String> console = new ArrayList<String>();
	
	//private final ReadWriteLock consoleLock = new ReentrantReadWriteLock();
	
	//private BufferedReader outReader;
	//private BufferedReader errReader;
	
	private Server webServer;
	private static AdminPanelWrapper instance;
	
	//private boolean hasDynmap;
	
	//private UUIDFetcher uuidFetcher;
	private final Timer timer = new Timer("Usage Thread", false);
	
	public EverythingEvent everythingEvent;
	
	public static void main(String[] args) throws Exception
	{
		new AdminPanelWrapper();
	}
	
	public AdminPanelWrapper() throws Exception
	{
		instance = this;
		
		setupLogger();
		
		System.out.println("Starting McAdminPanel " + VERSION + "...");
		
		new File("McAdminPanel").mkdirs();
		
		setupConfig();
		setupDatabases();
		
		//checkPlugin();
		
		//setupBackups();
		startUsages();
		
		//pluginConnector = new PluginConnector(this);
		//uuidFetcher = new UUIDFetcher();
		
		new Thread(new InputReader()).start();
		
		//startWebPanel();
		//startBukkitServer();
		
		System.out.println("Loading servers...");
		System.out.println("Loading backups...");
		
		List<com.mcapanel.web.database.Server> servs = ebean.find(com.mcapanel.web.database.Server.class).findList();
		
		for (com.mcapanel.web.database.Server serv : servs)
		{
			BukkitServer bukkitServer = new BukkitServer(serv);
			
			servers.put(serv.getId(), bukkitServer);
			
			bukkitServer.setupBackups();
		}
		
		everythingEvent = new EverythingEvent();
		everythingEvent.start();
		
		startWebPanel();
		setShutdownHook();
		
		//servers.get(1).startServer();
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
	
	/*
	public void install()
	{
		updateServerJar();
		
		if (serverJar != null && serverJar.exists())
		{
			copyPlugin();
			
			setupBackups();
		}
	}
	
	private void checkPlugin()
	{
		//System.out.println(Arrays.toString(ObfuscatedString.array("c8ae18a3707cc266f29553c132f44c82")));
		
		try
		{
			File pl = new File(serverJar.getParentFile(), "plugins/McAdminPanelPlugin.jar");
			
			if (pl != null && pl.exists())
			{
				String md5 = new ObfuscatedString(new long[] {-6239580717245095042L, 7772693983827735681L, 149453379078971602L, 3225733154312424749L, 3085561093364578028L}).toString();
				String md52 = DigestUtils.md5Hex(new FileInputStream(pl));
				
				if (md5.equals(md52))
				{
					System.out.println("Equals!");
				} else
				{
					copyPlugin();
				}
				
				//System.out.println("MD5: " + md5);
				//System.out.println("MD52: " + md52);
			} else
			{
				copyPlugin();
			}
		} catch (IOException e)
		{
			e.printStackTrace();
		}
	}
	
	private void copyPlugin()
	{
		try
		{
			if (inDev())
			{
				//FileUtils.copyInputStreamToFile(getClass().getResourceAsStream("/plugin/McAdminPanelPlugin.jar"), new File(serverJar.getParentFile(), "plugins/McAdminPanelPlugin.jar"));
			} else
			{
				FileUtils.copyInputStreamToFile(getClass().getResourceAsStream("/resources/plugin/McAdminPanelPlugin.jar"), new File(serverJar.getParentFile(), "plugins/McAdminPanelPlugin.jar"));
			}
		} catch (Exception e)
		{
			System.out.println("Could not copy the McAdminPanel Plugin to the plugins folder...");
		}
	}
	
	public ServerStatus getStatus()
	{
		return status;
	}
	
	public void setStatus(ServerStatus status)
	{
		this.status = status;
	}
	*/
	
	private void setupConfig()
	{
		System.out.println("Loading config file...");
		
		config = new Config();
		
		tinyUrl = new TinyUrl(config.getString("server-ip", "localhost") + ":" + config.getString("web-port", "80"));
		
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
	
	/*
	private void setupBackups()
	{
		if (config.getBoolean("installed", true) && serverJar != null && serverJar.exists())
		{
			System.out.println("Loading backups...");
			
			backupHandler = new BackupHandler(this);
			
			backupHandler.runSchedules();
		}
	}
	
	public BackupHandler getBackupHandler()
	{
		return backupHandler;
	}
	*/
	
	private void startUsages()
	{
		usageMonitor = new UsageMonitor();
		
		timer.scheduleAtFixedRate(usageMonitor, 0L, 750L);
	}
	
	public UsageMonitor getUsages()
	{
		return usageMonitor;
	}
	
	/*
	public List<File> getWorlds()
	{
		List<File> worlds = new ArrayList<File>();
		
		if (getServerJar() != null && getServerJar().exists())
		{
			for (File f : getServerJar().getParentFile().listFiles())
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
	
	public UUIDFetcher getUUIDFetcher()
	{
		return uuidFetcher;
	}
	*/
	
	private void setupDatabases()
	{
		System.out.println("Loading databases...");
		
		//Disable annoying log messages...
		Logger.getLogger(DataSourcePool.class.getName()).setLevel(Level.WARNING);
		Logger.getLogger(SubClassManager.class.getName()).setLevel(Level.WARNING);
		Logger.getLogger(PooledConnection.class.getName()).setLevel(Level.WARNING);
		Logger.getLogger(CreateTableVisitor.class.getName()).setLevel(Level.SEVERE);
		Logger.getLogger(BeanDescriptorManager.class.getName()).setLevel(Level.WARNING);
		Logger.getLogger(DeployCreateProperties.class.getName()).setLevel(Level.WARNING);
		Logger.getLogger("com.avaje.ebean.config.PropertyMapLoader").setLevel(Level.OFF);
		
		ServerConfig db = new ServerConfig();
		
		db.setDefaultServer(false);
		db.setRegister(false);
		db.setClasses(getDatabaseClasses());
		db.setName("McAdminPanel");
		
		DataSourceConfig ds = new DataSourceConfig();
		
		ds.setDriver("org.sqlite.JDBC");
		ds.setUrl("jdbc:sqlite:McAdminPanel/McAdminPanel.db");
		ds.setUsername("adminpanel");
		ds.setPassword("SeaFishRawr");
		ds.setIsolationLevel(TransactionIsolation.getLevel("SERIALIZABLE"));
		
		if (ds.getDriver().contains("sqlite"))
		{
			db.setDatabasePlatform(new SQLitePlatform());
			db.getDatabasePlatform().getDbDdlSyntax().setIdentity("");
		}
		
		db.setDataSourceConfig(ds);
		
		ebean = EbeanServerFactory.create(db);
		
		try
		{
			for (Class<?> c : getDatabaseClasses())
			{
				getDatabase().find(c).findRowCount();
			}
			
			/*
			new Thread(new Runnable() {
				public void run()
				{
					System.out.println("Fetching UUIDs for all users...");
					
					List<UUID> uuids = new ArrayList<UUID>();
					
					for (User u : getDatabase().find(User.class).findList())
					{
						uuids.add(UUID.fromString(u.getUuid()));
					}
					
					uuidFetcher.setUUIDs(uuids).runUUIDs();
					
					System.out.println("Done fetching UUIDs!");
				}
			}).start();
			*/
		} catch (PersistenceException e)
		{
			System.out.println("Initializing database...");
			
			installDDL();
			
			Group g = new Group("Ghost");
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
	}
	
	private void installDDL()
	{
		SpiEbeanServer serv = (SpiEbeanServer) getDatabase();
		DdlGenerator gen = serv.getDdlGenerator();
		
		gen.runScript(false, gen.generateCreateDdl());
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
		return ebean;
	}
	
	/*
	public File getServerJar()
	{
		return serverJar;
	}
	
	public boolean hasDynmap()
	{
		return hasDynmap && status == ServerStatus.STARTED;
	}
	*/
	
	public User getUserFromPlayer(String player)
	{
		// Change to UUID
		return instance.getDatabase().find(User.class).where().ieq("username", player).findUnique();
	}
	
	private void startWebPanel() throws Exception
	{
		System.out.println("Starting web server on port " + config.getString("web-port", "80") + "...");
		
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
		
		webServer.setHandler(context);
		
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
				System.out.println("Goto http://localhost:" + config.getString("web-port", "80") + " in a browser to start the setup.");
				
				Desktop.getDesktop().browse(URI.create("http://localhost"));
			}
		} catch (BindException e)
		{
			System.out.println("McAdminPanel failed to bind to port " + config.getString("web-port", "80"));
			
			System.exit(-1);
		}
	}
	
	/*
	public void updateServerJar()
	{
		serverJar = new File(config.getValue("server-jar"));
	}
	
	public void startBukkitServer()
	{
		if (status == ServerStatus.STOPPED || status == ServerStatus.RESTARTING)
		{
			updateServerJar();
			
			if (config.getBoolean("installed", true))
			{
				if (serverJar != null && serverJar.exists())
				{
					console.clear();
					
					System.out.println("Starting Minecraft server...");
					
					status = status == ServerStatus.RESTARTING ? ServerStatus.RESTARTING : ServerStatus.STARTING;
					
					ProcessBuilder processBuilder = new ProcessBuilder(new String[] { 
						"java",
						"-Djline.terminal=jline.UnsupportedTerminal",
						"-Xms" + config.getValue("min-memory"),
						"-Xmx" + config.getValue("max-memory"),
						"-jar", serverJar.getAbsolutePath()
					});
					
					processBuilder.directory(serverJar.getParentFile());
					
					startBukkitProcess(processBuilder);
				} else
				{
					System.out.println("No or invalid server jar file specified in config...");
				}
			} else
			{
				System.out.println("Navigate to " + config.getString("server-ip", "localhost") + ":" + config.getString("web-port", "80") + " to begin install!");
			}
		}
	}
	
	private void startBukkitProcess(ProcessBuilder processBuilder)
	{
		try
		{
			bukkitProcess = processBuilder.start();
			
			writer = new PrintStream(bukkitProcess.getOutputStream());
			
			outReader = new BufferedReader(new InputStreamReader(bukkitProcess.getInputStream()));
			errReader = new BufferedReader(new InputStreamReader(bukkitProcess.getErrorStream()));
			
			setShutdownHook();
			
			new Thread(new BukkitConsoleReader(false)).start();
			new Thread(new BukkitConsoleReader(true)).start();
		} catch (IOException ex)
		{
			System.out.println("Error starting server... Please try again.");
		}
	}
	*/
	
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
	
	/*
	public void stopBukkitServer(final boolean restart)
	{
		new Thread(new Runnable()
		{
			public void run()
			{
				stopBukkitServerRaw(restart);
			}
		}).start();
	}
	
	public void stopBukkitServerRaw(boolean restart)
	{
		if (status == ServerStatus.STARTED)
		{
			if (restart)
				status = ServerStatus.RESTARTING;
			else
				status = ServerStatus.STOPPING;
			
			getPluginConnector().setConnected(false);
			
			try
			{
				writer.write("stop\n".getBytes());
				writer.flush();
				
				bukkitProcess.waitFor();
				
				writer.close();
				
				outReader.close();
				errReader.close();
				
				if (restart)
					startBukkitServer();
				else
					status = ServerStatus.STOPPED;
			} catch (IOException e)
			{
				e.printStackTrace();
			} catch (InterruptedException e)
			{
				e.printStackTrace();
			}
		}
	}
	
	public void restartBukkitServer()
	{
		stopBukkitServer(true);
	}
	
	public void reloadBukkitServer()
	{
		status = ServerStatus.RELOADING;
		
		try
		{
			writer.write("reload\n".getBytes());
			writer.flush();
		} catch (IOException e)
		{
			e.printStackTrace();
		}
	}
	
	public PluginConnector getPluginConnector()
	{
		return pluginConnector;
	}
	
	public List<String> getConsole()
	{
		return console;
	}
	
	public ReadWriteLock getConsoleLock()
	{
		return consoleLock;
	}
	
	public OutputStream getWriter()
	{
		return writer;
	}
	*/
	
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
					//Check if it's a command for us
					
					if (parseCommand(line))
						continue;
					
					for (BukkitServer serv : servers.values())
					{
						if (serv.hasConsoleFocus() && serv.getWriter() != null)
						{
							serv.getWriter().write((line + "\n").getBytes());
							serv.getWriter().flush();
						}
					}
					
					//writer.write((line + "\n").getBytes());
					//writer.flush();
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
			//startBukkitServer();
			
			return true;
		} else if (cmd.equalsIgnoreCase("restart"))
		{
			//restartBukkitServer();
			
			return true;
		} else if (cmd.equalsIgnoreCase("stop-all"))
		{
			System.exit(0);
			
			return true;
		}
		
		return false;
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