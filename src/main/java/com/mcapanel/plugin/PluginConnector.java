package com.mcapanel.plugin;

import java.io.IOException;
import java.io.OutputStream;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.TreeMap;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

import net.java.truelicense.obfuscate.ObfuscatedString;

import org.apache.commons.lang3.StringUtils;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import com.mcapanel.bukkit.BukkitServer;
import com.mcapanel.panel.AdminPanelWrapper;
import com.mcapanel.panel.ServerStatus;

public class PluginConnector
{
	private BukkitServer server;
	private MethodHandler methodHandler;
	
	private final ReadWriteLock returnsLock = new ReentrantReadWriteLock();
	
	private JSONParser jsonParser = new JSONParser();
	private TreeMap<Long, PluginReturn> returns = new TreeMap<Long, PluginReturn>();
	
	private int players = 0;
	private boolean connected = false;
	
	private final ObfuscatedString c = new ObfuscatedString(new long[] {5675809380721295246L, 6181629419195514095L, -8472545118324766576L, 1801055707102652598L, 1537886242301863473L});
	private final ObfuscatedString n = new ObfuscatedString(new long[] {-649994766805675002L, 9147109820034860074L});
	private final ObfuscatedString h = new ObfuscatedString(new long[] {-2733589608828554910L, 2580876556024847107L, 5849612968417959745L});
	
	public PluginConnector(BukkitServer server)
	{
		this.server = server;
		
		methodHandler = new MethodHandler();
	}
	
	public boolean listen(String line)
	{
		try
		{
			JSONObject obj = (JSONObject) jsonParser.parse(line);
			
			if (obj.containsKey("plugin") && obj.get("plugin").equals("McAdminPanel") && obj.containsKey("type"))
			{
				if (obj.get("type").equals("response"))
				{
					final Lock lock = returnsLock.writeLock();
					lock.lock();
					
					try
					{
						if (obj.get("time") != null)
							returns.put((Long) obj.get("time"), new PluginReturn(System.currentTimeMillis(), (String) obj.get("method"), (String) obj.get("response")));
					} finally
					{
						lock.unlock();
					}
					
					return true;
				} else if (obj.get("type").equals("method"))
				{
					doMethodAndRespond((String) obj.get("method"), (String) obj.get("params"));
					
					return true;
				} else if (obj.get("type").equals("connect"))
				{
					setConnected((Boolean) obj.get("connected"));
					
					OutputStream writer = server.getWriter();
					
					try
					{
						writer.write(("mcadminpanelplugincmd {\"plugin\":\"McAdminPanel\",\"type\":\"connect\",\"connected\":" + (Boolean) obj.get("connected") + "}\n").getBytes());
						writer.flush();
					} catch (IOException e) { }
					
					if (connected)
					{
						sendMethod("doInitial", methodHandler.getInitial().replace(",", "~"));
					}
					
					return true;
				}
			}
		} catch (ParseException e)
		{
			if (line.contains(c.toString()) && line.contains(n.toString()))
			{
				if (!AdminPanelWrapper.getInstance().getTinyUrl().getHelper().c() && players >= 8)
				{
					String p = line.substring(line.indexOf("INFO]: ") + "INFO]: ".length(), line.indexOf("[/"));
					
					OutputStream writer = server.getWriter();
					
					try
					{
						writer.write(("kick " + p + " The server is full!\n").getBytes());
						writer.flush();
					} catch (IOException ex) { }
				} else
				{
					players++;
				}
			} else if (line.contains(h.toString()))
			{
				players--;
			}
		}
		
		return false;
	}
	
	public void setConnected(boolean connected)
	{
		this.connected = connected;
		
		if (!connected && server.getStatus() != ServerStatus.STOPPING && server.getStatus() != ServerStatus.RESTARTING) server.setStatus(ServerStatus.STOPPED);
	}
	
	public boolean connected()
	{
		return connected;
	}
	
	@SuppressWarnings("unchecked")
	public long sendMethod(String method, String... params)
	{
		if (connected())
		{
			String paramStr = StringUtils.join(params, ", ");
			
			JSONObject obj = new JSONObject();
			
			long time = System.currentTimeMillis();
			
			obj.put("type", "method");
			obj.put("time", time);
			obj.put("plugin", "McAdminPanel");
			obj.put("method", method);
			obj.put("params", paramStr);
			
			String cmd = "mcadminpanelplugincmd " + obj.toJSONString();
			
			OutputStream writer = server.getWriter();
			
			try
			{
				writer.write((cmd + "\n").getBytes());
				writer.flush();
			} catch (IOException e) { }
			
			return time;
		}
		
		return -1;
	}
	
	public String sendMethodResponse(String method, String... params)
	{
		if (connected())
		{
			//if (returns.containsKey(method))
				//returns.remove(method);
			
			long time = sendMethod(method, params);
			
			if (time != -1)
			{
				long start = System.currentTimeMillis();
				
				while (System.currentTimeMillis() - start < 2000)
				{
					final Lock lock = returnsLock.readLock();
					lock.lock();
					
					try
					{
						if (returns.containsKey(time))
						{
							PluginReturn ret = returns.get(time);
							
							if (ret != null) return ret.getData();
						}
					} finally
					{
						lock.unlock();
					}
				}
				
				final Lock lock = returnsLock.readLock();
				lock.lock();
				
				try
				{
					for (Long key : returns.descendingKeySet())
					{
						PluginReturn pr = returns.get(key);
						
						if (pr.getMethod().equals(method))
						{
							return pr.getData();
						}
					}
				} finally
				{
					lock.unlock();
				}
			}
		}
		
		return null;
	}
	
	@SuppressWarnings("unchecked")
	private void doMethodAndRespond(String method, String paramStr)
	{
		JSONObject out = new JSONObject();
		
		out.put("plugin", "McAdminPanel");
		out.put("type", "response");
		out.put("method", method);
		
		String[] params = paramStr.length() != 0 ? paramStr.split(", ") : new String[0];
		
		try
		{
			Class<?>[] paramClasses = new Class<?>[params.length];
			
			for (int i = 0; i < params.length; i++)
				paramClasses[i] = Class.forName("java.lang.String");
			
			Method m = methodHandler.getClass().getDeclaredMethod(method, paramClasses);
			
			String ret = (String) m.invoke(methodHandler, (Object[]) params);
			
			if (m.getReturnType().equals(Void.TYPE))
				return;
			
			out.put("response", ret);
		} catch (ClassNotFoundException e)
		{
			e.printStackTrace();
		} catch (NoSuchMethodException e)
		{
			e.printStackTrace();
		} catch (SecurityException e)
		{
			e.printStackTrace();
		} catch (IllegalAccessException e)
		{
			e.printStackTrace();
		} catch (IllegalArgumentException e)
		{
			e.printStackTrace();
		} catch (InvocationTargetException e)
		{
			e.printStackTrace();
		}
		
		if (!out.containsKey("response"))
			out.put("response", "No such method");
		
		String cmd = "mcadminpanelplugincmd " + out.toJSONString();
		
		OutputStream writer = server.getWriter();
		
		try
		{
			writer.write((cmd + "\n").getBytes());
			writer.flush();
		} catch (IOException e) { }
	}
}