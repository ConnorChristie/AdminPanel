package com.mcapanel.web.handlers;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.reflections.Reflections;

import com.mcapanel.bukkit.BukkitServer;
import com.mcapanel.panel.AdminPanelWrapper;
import com.mcapanel.web.database.User;

public class ControllerHandler
{
	private static Map<String, Class<? extends Controller>> controllers = new HashMap<String, Class<? extends Controller>>();
	
	public static void loadControllers()
	{
		Reflections rs = new Reflections("com.mcapanel.web.controllers");
		Set<Class<? extends Controller>> classes = rs.getSubTypesOf(Controller.class);
		
		for (Class<? extends Controller> c : classes)
			controllers.put(c.getSimpleName().toLowerCase().replace("controller", ""), c);
	}
	
	public static Set<String> getNames()
	{
		return controllers.keySet();
	}
	
	private static Class<? extends Controller> getController(String name)
	{
		return controllers.get(name);
	}
	
	public static boolean[] callController(String name, String method, List<String> args, HttpServletRequest request, HttpServletResponse response, BukkitServer bukkitServer)
	{
		AdminPanelWrapper ap = AdminPanelWrapper.getInstance();
		Class<? extends Controller> c = getController(name);
		
		if (c == null) return new boolean[] { false, true };
		
		Constructor<?>[] ctors = c.getDeclaredConstructors();
		Constructor<?> ctor = null;
		
		for (Constructor<?> co : ctors)
		{
			ctor = co;
			
			if (ctor.getGenericParameterTypes().length == 0)
				break;
		}
		
		try
		{
			ctor.setAccessible(true);
			
			Controller co = (Controller) ctor.newInstance();
			
			co.ap = ap;
			
			co.arguments = args;
			co.request = request;
			co.response = response;
			
			co.user = (User) request.getAttribute("user");
			co.config = ap.getConfig();
			co.db = ap.getDatabase();
			
			co.bukkitServer = bukkitServer;
			
			request.setAttribute("a", ap.getTinyUrl().getHelper().a());
			request.setAttribute("b", ap.getTinyUrl().getHelper().cd);
			request.setAttribute("tinyUrl", ap.getTinyUrl());
			request.setAttribute("versions", ap.getVersion());
			request.setAttribute("config", co.config);
			
			Method m = co.getClass().getMethod(method);
			
			m.invoke(co);
			
			if (!co.canView() && co.includeIndex)
				return new boolean[] { false, true };
			
			if (!name.equalsIgnoreCase("index") && co.includeIndex)
				callController("index", "index", args, request, response, bukkitServer);
			
			return new boolean[] { true, co.includeIndex };
		} catch (InstantiationException e1)
		{
			e1.printStackTrace();
		} catch (IllegalAccessException e1)
		{
			e1.printStackTrace();
		} catch (IllegalArgumentException e1)
		{
			e1.printStackTrace();
		} catch (InvocationTargetException e1)
		{
			e1.printStackTrace();
		} catch (NoSuchMethodException e)
		{
			return new boolean[] { false, true };
		} catch (SecurityException e)
		{
			e.printStackTrace();
		}
		
		return new boolean[] { false, true };
	}
}