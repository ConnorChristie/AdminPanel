package com.mcapanel.web.servlets;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.CountDownLatch;

import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.mcapanel.bukkit.BukkitServer;
import com.mcapanel.panel.AdminPanelWrapper;
import com.mcapanel.utils.Utils;
import com.mcapanel.web.database.User;
import com.mcapanel.web.handlers.ControllerHandler;

public class AppServlet extends HttpServlet
{
	private static final long serialVersionUID = 1L;
	
	@Override
	public void service(final HttpServletRequest request, final HttpServletResponse response)
	{
		final CountDownLatch latch = new CountDownLatch(1);
		
		AdminPanelWrapper.executeMain(new Runnable() {
			public void run()
			{
				if (request == null || request.getPathInfo() == null) return;
				
				if (!request.getPathInfo().contains("install") && !AdminPanelWrapper.getInstance().getConfig().getBoolean("installed", false))
				{
					try
					{
						response.getWriter().print("<script>document.location = '/install/';</script>");
					} catch (IOException e) { e.printStackTrace(); }
					
					latch.countDown();
					
					return;
				}
				
				int id = 1;
				
				try
				{
					id = (Integer) request.getSession().getAttribute("chosenServer");
				} catch (Exception e) { id = 1; }
				
				BukkitServer bukkitServer = AdminPanelWrapper.getInstance().getServer(id);
				
				if (bukkitServer == null)
				{
					if (AdminPanelWrapper.getInstance().servers.size() > 0)
					{
						bukkitServer = AdminPanelWrapper.getInstance().getServer(id = AdminPanelWrapper.getInstance().servers.keySet().toArray(new Integer[AdminPanelWrapper.getInstance().servers.keySet().size()])[0]);
					} else if (!request.getPathInfo().contains("install"))
					{
						AdminPanelWrapper.getInstance().getConfig().setValue("installed", "false");
						AdminPanelWrapper.getInstance().getConfig().saveConfig();
						
						try
						{
							response.getWriter().print("<script>document.location = '/install/';</script>");
						} catch (IOException e) { e.printStackTrace(); }
						
						latch.countDown();
						
						return;
					}
				}
				request.getSession().setAttribute("chosenServer", id);
				
				request.setAttribute("ap", AdminPanelWrapper.getInstance());
				request.setAttribute("user", getUser(request, response));
				request.setAttribute("connected", bukkitServer != null ? bukkitServer.getPluginConnector().connected() : false);
				request.setAttribute("bukkitServer", bukkitServer);
				
				try
				{
					boolean[] retBools = callController(bukkitServer, request, response);
					
					if (retBools.length == 2)
					{
						Object page = request.getAttribute("page");
						
						if (!retBools[0])
							handleError("404", bukkitServer, request, response);
						
						if (retBools[1])
							request.getRequestDispatcher("/index.jsp").include(request, response);
						else if (page != null)
							request.getRequestDispatcher((String) page).include(request, response);
					} else
						request.getRequestDispatcher("/index.jsp").include(request, response);
				} catch (Exception e) { e.printStackTrace(); }
				
				latch.countDown();
			}
		});
		
		try
		{
			latch.await();
		} catch (InterruptedException e) { }
	}
	
	private boolean[] callController(BukkitServer bukkitServer, HttpServletRequest request, HttpServletResponse response) throws IOException
	{
		List<String> path = new ArrayList<String>(Arrays.asList(request.getPathInfo().split("/")));
		path.removeAll(Arrays.asList("", null));
		
		String cont;
		String method;
		
		if (path.size() >= 1)
		{
			cont = path.get(0);
			method = "index";
			
			if (path.size() > 1 && !cont.equalsIgnoreCase("element"))
			{
				method = path.get(1);
				
				path.remove(1);
			}
			
			path.remove(0);
		} else
		{
			cont = "home";
			method = "index";
		}
		
		return ControllerHandler.callController(cont, method, path, request, response, bukkitServer);
	}
	
	private void handleError(String err, BukkitServer bukkitServer, HttpServletRequest request, HttpServletResponse response)
	{
		request.setAttribute("page", err);
		
		ControllerHandler.callController("index", "index", new ArrayList<String>(), request, response, bukkitServer);
	}
	
	private User getUser(HttpServletRequest request, HttpServletResponse response)
	{
		if (request.getSession() == null || request.getSession().getAttribute("userId") == null)
			return null;
		
		User u = AdminPanelWrapper.getInstance().getDatabase().find(User.class, request.getSession().getAttribute("userId"));
		
		if (u != null)
		{
			String userHash = (String) request.getSession().getAttribute("userHash");
			
			if (userHash.equals(Utils.md5(u.getId() + u.getPassSalt())))
			{
				request.setAttribute("loggedIn", true);
				
				return u;
			}
		}
		
		return null;
	}
}