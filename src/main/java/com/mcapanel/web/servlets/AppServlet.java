package com.mcapanel.web.servlets;

import java.io.IOException;
import java.math.BigInteger;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.mcapanel.bukkit.BukkitServer;
import com.mcapanel.panel.AdminPanelWrapper;
import com.mcapanel.web.database.User;
import com.mcapanel.web.handlers.ControllerHandler;

public class AppServlet extends HttpServlet
{
	private static final long serialVersionUID = 1L;
	
	private HttpServletRequest request = null;
	private HttpServletResponse response = null;
	
	@Override
	public void service(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException
	{
		if (request == null || request.getPathInfo() == null) return;
		
		if (!request.getPathInfo().contains("install") && !AdminPanelWrapper.getInstance().getConfig().getBoolean("installed", false))
		{
			response.getWriter().print("<script>document.location = '/install/';</script>");
			
			return;
		}
		
		this.request = request;
		this.response = response;
		
		int id = 1;
		
		try
		{
			id = (Integer) request.getSession().getAttribute("chosenServer");
		} catch (Exception e) { }
		
		BukkitServer bukkitServer = AdminPanelWrapper.getInstance().getServer(id);
		
		request.setAttribute("ap", AdminPanelWrapper.getInstance());
		request.setAttribute("user", getUser());
		request.setAttribute("connected", bukkitServer.getPluginConnector().connected());
		
		boolean[] retBools = callController(bukkitServer);
		
		if (retBools.length == 2)
		{
			Object page = request.getAttribute("page");
			
			if (!retBools[0])
				handleError("404", bukkitServer);
			
			if (retBools[1])
				request.getRequestDispatcher("/index.jsp").include(request, response);
			else if (page != null)
				request.getRequestDispatcher((String) page).include(request, response);
		} else
			request.getRequestDispatcher("/index.jsp").include(request, response);
	}
	
	private boolean[] callController(BukkitServer bukkitServer) throws IOException
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
	
	private void handleError(String err, BukkitServer bukkitServer)
	{
		request.setAttribute("page", err);
		
		ControllerHandler.callController("index", "index", new ArrayList<String>(), request, response, bukkitServer);
	}
	
	private User getUser()
	{
		String ip = request.getRemoteAddr().equals("0:0:0:0:0:0:0:1") ? "127.0.0.1" : request.getRemoteAddr();
		
		if (request.getSession() == null || request.getSession().getAttribute("userId") == null)
			return null;
		
		User u = AdminPanelWrapper.getInstance().getDatabase().find(User.class, request.getSession().getAttribute("userId"));
		
		if (u != null)
		{
			String userHash = (String) request.getSession().getAttribute("userHash");
			
			if (userHash.equals(md5(u.getId() + ip)))
			{
				request.setAttribute("loggedIn", true);
				
				return u;
			}
		}
		
		return null;
	}
	
	public String md5(String text)
	{
		String md5 = "";
		
		try
		{
			MessageDigest messageDigest = MessageDigest.getInstance("MD5");
			
			messageDigest.reset();
			messageDigest.update(text.getBytes());
			
			md5 = new BigInteger(1, messageDigest.digest()).toString(16);
		} catch (NoSuchAlgorithmException e)
		{
			e.printStackTrace();
		}
		
		return md5;
	}
}