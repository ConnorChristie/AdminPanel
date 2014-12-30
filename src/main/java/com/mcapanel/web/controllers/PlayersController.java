package com.mcapanel.web.controllers;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.ParseException;

import com.mcapanel.web.database.User;
import com.mcapanel.web.handlers.Controller;

@SuppressWarnings("unchecked")
public class PlayersController extends Controller
{
	public boolean canView()
	{
		return ((!isLoggedIn() && ap.getGlobalGroup().hasPermission("server.players.view")) || (isLoggedIn() && user.getGroup().hasPermission("server.players.view"))) && bukkitServer.getPluginConnector().connected();
	}
	
	public boolean index()
	{
		request.setAttribute("playerlist", arrayToString(getPListJson(true)));
		
		return renderView();
	}
	
	protected JSONArray getPListJson(boolean raw)
	{
		JSONArray out = new JSONArray();
		
		String pluginStr = bukkitServer.getPluginConnector().sendMethodResponse("getPListJson", "" + isLoggedIn(), "" + (user != null ? user.getGroup().hasPermission("editplayer") : false));
		
		if (pluginStr != null)
		{
			try
			{
				JSONArray s = (JSONArray) jsonParser.parse(pluginStr);
				
				String b = raw ? "<td>" : "";
				String e = raw ? "</td>" : "";
				
				for (Object a : s)
				{
					JSONObject obj = (JSONObject) a;
					
					JSONArray ar = new JSONArray();
					
					User u = ap.getUserFromPlayer(obj.get("uuid"));
					
					if (raw) ar.add("<tr>");
					ar.add(b + obj.get("uuid") + e);
					ar.add(b + "<img src=\"https://crafatar.com/avatars/" + obj.get("name") + "?size=15&helm\" />" + e);
					ar.add(b + obj.get("name") + e);
					ar.add(b + "<span class=\"label label-" + ((Boolean) obj.get("online") ? "success\">Online" : "danger\">Offline") + "</span>" + e);
					ar.add(b + ((Boolean) obj.get("banned") ? "<span style=\"color: red;\">Banned</span>" : (u != null ? u.getGroup().getGroupName() : "<span style=\"color: #AFAFAF;\">Not Registered</span>")) + e);
					ar.add(b + ((Boolean) obj.get("online") ? obj.get("world") : "none") + e);
					ar.add(b + obj.get("health") + "%" + e);
					ar.add(b + obj.get("food") + "%" + e);
					if (raw) ar.add("</tr>");
					
					out.add(ar);
				}
			} catch (ParseException e) { }
		}
		
		return out;
	}
}